package experiment;

import algorithms.BellmanFord;
import algorithms.Dijkstra;
import algorithms.ShortestPathResult;
import graph.AdjListGraph;
import graph.AdjMatrixGraph;
import graph.GraphReader;
import graph.GraphReader.GrData;
import graph.GraphType;
import graph.IGraph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Experimento: tempo de execução de Bellman-Ford e Dijkstra sobre Lista de Adjacência (LA)
 * e Matriz de Adjacência (MA), para cada um dos grafos de entrada (.gr).
 *
 * <h2>Metodologia</h2>
 * <ul>
 *   <li>Cada bloco (arquivo × algoritmo × representação) roda numa <b>JVM nova</b>. Se LA e MA rodassem na
 *       mesma JVM, a JIT deixaria de especializar a chamada {@code g.adj(u)} para uma única classe e o tempo
 *       de uma representação passaria a depender da outra (medido: a LA ficava ~2x mais lenta).</li>
 *   <li>Em cada bloco: leitura do arquivo e construção do grafo (fora da medição); aquecimento da JIT com
 *       no mínimo {@code --warmup} execuções e {@code --warmup-ms} milissegundos; depois {@code --runs}
 *       execuções medidas com {@code System.nanoTime()}.</li>
 *   <li>Os blocos rodam em ordem aleatória (semente fixa {@code --seed}), para que variações de carga da
 *       máquina não afetem sistematicamente um grupo de grafos.</li>
 *   <li>As distâncias calculadas pelos 4 blocos de cada arquivo são comparadas (devem ser idênticas).</li>
 *   <li>Cada execução medida vira uma linha do CSV, gravada imediatamente.</li>
 * </ul>
 *
 * <p>Uso (a partir da pasta Trabalho_2):</p>
 * <pre>
 * java -Xms2g -Xmx2g -cp out experiment.ExperimentRunner [--dir RandomGraphSamples]
 *      [--out resultados/resultados_experimento.csv] [--runs 10] [--warmup 5] [--warmup-ms 1000]
 *      [--source 1] [--seed 2026]
 * </pre>
 * As opções de JVM do processo principal (ex.: -Xms2g -Xmx2g) são repassadas para cada bloco.
 * {@code --source} usa a numeração do arquivo (1..|V|).
 */
public class ExperimentRunner {

    static final String HEADER = "arquivo,vertices,arestas_arquivo,arestas_distintas,densidade_nominal,"
            + "densidade_real,grupo,algoritmo,representacao,ordem_bloco,execucao,tempo_ns,tempo_ms";

    private static final String ROW_PREFIX = "ROW,";
    private static final String HASH_PREFIX = "HASH,";

    static final List<String> ALGORITHMS = List.of(BellmanFord.NAME, Dijkstra.NAME);
    static final List<String> REPRESENTATIONS = List.of("LA", "MA");

    /** Evita que a JIT descarte as chamadas cujo resultado não é usado. */
    private static long sink = 0;

    public static void main(String[] args) throws Exception {
        if (Arrays.asList(args).contains("--block")) {
            runBlock(args);
        } else {
            runAll(args);
        }
    }

    // ===================================================================== processo principal

    private record Block(Path file, String algorithm, String representation) {
    }

    private static void runAll(String[] args) throws Exception {
        Path dir = Paths.get(option(args, "--dir", "RandomGraphSamples"));
        Path out = Paths.get(option(args, "--out", "resultados/resultados_experimento.csv"));
        int runs = Integer.parseInt(option(args, "--runs", "10"));
        int warmup = Integer.parseInt(option(args, "--warmup", "5"));
        int warmupMs = Integer.parseInt(option(args, "--warmup-ms", "1000"));
        int source = Integer.parseInt(option(args, "--source", "1"));
        long seed = Long.parseLong(option(args, "--seed", "2026"));
        if (runs < 1 || warmup < 0 || warmupMs < 0) {
            throw new IllegalArgumentException("--runs deve ser >= 1; --warmup e --warmup-ms >= 0");
        }

        List<Path> files = listGraphFiles(dir);
        if (files.isEmpty()) {
            throw new IOException("nenhum arquivo .gr em " + dir);
        }
        if (out.getParent() != null) {
            Files.createDirectories(out.getParent());
        }

        List<Block> blocks = new ArrayList<>();
        for (Path f : files) {
            for (String alg : ALGORITHMS) {
                for (String rep : REPRESENTATIONS) {
                    blocks.add(new Block(f, alg, rep));
                }
            }
        }
        Collections.shuffle(blocks, new Random(seed));

        List<String> jvmOptions = ManagementFactory.getRuntimeMXBean().getInputArguments();
        String java = ProcessHandle.current().info().command().orElse("java");
        String classpath = System.getProperty("java.class.path");

        System.out.printf("Experimento: %d grafos × %d algoritmos × %d representações = %d blocos; "
                        + "%d execuções medidas por bloco (aquecimento: >= %d execuções e >= %d ms)%n",
                files.size(), ALGORITHMS.size(), REPRESENTATIONS.size(), blocks.size(), runs, warmup, warmupMs);
        System.out.printf("Cada bloco em JVM nova; ordem aleatória (semente %d); origem = vértice %d%n",
                seed, source);
        System.out.printf("Java %s (%s), %d núcleos, opções JVM %s%n%n", System.getProperty("java.version"),
                java, Runtime.getRuntime().availableProcessors(), jvmOptions);

        Map<Path, String> hashByFile = new HashMap<>();
        long start = System.nanoTime();
        try (PrintWriter csv = new PrintWriter(Files.newBufferedWriter(out, StandardCharsets.UTF_8))) {
            csv.println(HEADER);
            csv.flush();
            int order = 0;
            for (Block b : blocks) {
                order++;
                List<String> cmd = new ArrayList<>();
                cmd.add(java);
                cmd.addAll(jvmOptions);
                cmd.addAll(List.of("-cp", classpath, ExperimentRunner.class.getName(), "--block",
                        "--file", b.file().toString(), "--alg", b.algorithm(), "--rep", b.representation(),
                        "--runs", String.valueOf(runs), "--warmup", String.valueOf(warmup),
                        "--warmup-ms", String.valueOf(warmupMs), "--source", String.valueOf(source),
                        "--order", String.valueOf(order)));
                Process p = new ProcessBuilder(cmd).redirectError(ProcessBuilder.Redirect.INHERIT).start();

                List<Double> times = new ArrayList<>();
                String hash = null;
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.startsWith(ROW_PREFIX)) {
                            String row = line.substring(ROW_PREFIX.length());
                            csv.println(row);
                            csv.flush();
                            String[] c = row.split(",");
                            times.add(Double.parseDouble(c[c.length - 1]));
                        } else if (line.startsWith(HASH_PREFIX)) {
                            hash = line.substring(HASH_PREFIX.length());
                        }
                    }
                }
                int exit = p.waitFor();
                if (exit != 0 || hash == null || times.size() != runs) {
                    throw new IllegalStateException("bloco falhou (código " + exit + "): " + b);
                }
                String previous = hashByFile.putIfAbsent(b.file(), hash);
                if (previous != null && !previous.equals(hash)) {
                    throw new IllegalStateException("distâncias divergentes em " + b.file().getFileName()
                            + " (" + b.algorithm() + ", " + b.representation() + ")");
                }
                double[] t = times.stream().mapToDouble(Double::doubleValue).sorted().toArray();
                System.out.printf(Locale.ROOT, "[%2d/%d] %-20s %-12s %s  média %10.3f ms  mediana %10.3f ms%n",
                        order, blocks.size(), b.file().getFileName(), b.algorithm(), b.representation(),
                        Arrays.stream(t).average().orElse(0), median(t));
            }
        }
        System.out.printf("%nConcluído em %.1f s. CSV: %s%n", (System.nanoTime() - start) / 1e9, out);
    }

    // ===================================================================== bloco (JVM filha)

    private static void runBlock(String[] args) throws IOException {
        Path file = Paths.get(option(args, "--file", null));
        String alg = option(args, "--alg", null);
        String rep = option(args, "--rep", null);
        int runs = Integer.parseInt(option(args, "--runs", "10"));
        int warmup = Integer.parseInt(option(args, "--warmup", "5"));
        long warmupNs = Long.parseLong(option(args, "--warmup-ms", "1000")) * 1_000_000L;
        int source = Integer.parseInt(option(args, "--source", "1")) - 1;
        int order = Integer.parseInt(option(args, "--order", "0"));

        GrData data = GraphReader.parse(file);
        if (source < 0 || source >= data.numVertices()) {
            throw new IllegalArgumentException("--source fora de 1.." + data.numVertices());
        }
        IGraph g = switch (rep) {
            case "LA" -> data.build(n -> new AdjListGraph(n, GraphType.DIRECTED));
            case "MA" -> data.build(n -> new AdjMatrixGraph(n, GraphType.DIRECTED));
            default -> throw new IllegalArgumentException("representação desconhecida: " + rep);
        };
        if (!ALGORITHMS.contains(alg)) {
            throw new IllegalArgumentException("algoritmo desconhecido: " + alg);
        }
        boolean bellmanFord = alg.equals(BellmanFord.NAME);
        String group = group(data.nominalDensity());

        // Aquecimento da JIT: no mínimo `warmup` execuções e `warmupNs` nanossegundos.
        long t0 = System.nanoTime();
        for (int i = 0; i < warmup || System.nanoTime() - t0 < warmupNs; i++) {
            sink += run(bellmanFord, g, source).reachableCount();
        }

        System.gc();
        ShortestPathResult last = null;
        StringBuilder rows = new StringBuilder();
        for (int i = 1; i <= runs; i++) {
            long a = System.nanoTime();
            last = run(bellmanFord, g, source);
            long b = System.nanoTime();
            sink += last.reachableCount();
            long ns = b - a;
            rows.append(String.format(Locale.ROOT, "%s%s,%d,%d,%d,%.1f,%.4f,%s,%s,%s,%d,%d,%d,%.6f%n",
                    ROW_PREFIX, data.fileName(), data.numVertices(), data.declaredEdges(), data.distinctEdges(),
                    data.nominalDensity(), data.realDensity(), group, alg, rep, order, i, ns, ns / 1e6));
        }
        // A saída é impressa só depois das medições, para não interferir nos tempos.
        System.out.print(rows);
        System.out.println(HASH_PREFIX + Arrays.hashCode(last.distances()) + "/" + last.reachableCount());
        System.out.flush();
        if (sink == Long.MIN_VALUE) {
            System.err.println("sink");
        }
    }

    private static ShortestPathResult run(boolean bellmanFord, IGraph g, int source) {
        return bellmanFord ? BellmanFord.run(g, source) : Dijkstra.run(g, source);
    }

    // ===================================================================== utilitários

    /** Ɛ = densidades nominais 0,2 e 0,4; Ɗ = 0,8 e 1,0; 0,6 = intermediário (decisão D4). */
    static String group(double nominalDensity) {
        double d = Math.round(nominalDensity * 10) / 10.0;
        if (d <= 0.4) {
            return "esparso";
        }
        if (d >= 0.8) {
            return "denso";
        }
        return "intermediario";
    }

    /** Arquivos .gr da pasta, ordenados por |V| e depois por |E| declarado. */
    private static List<Path> listGraphFiles(Path dir) throws IOException {
        List<Path> files = new ArrayList<>();
        try (Stream<Path> s = Files.list(dir)) {
            s.filter(p -> p.getFileName().toString().endsWith(".gr")).forEach(files::add);
        }
        Map<Path, long[]> keys = new HashMap<>();
        for (Path p : files) {
            GrData d = GraphReader.parse(p);
            keys.put(p, new long[]{d.numVertices(), d.declaredEdges()});
        }
        files.sort(Comparator.<Path>comparingLong(p -> keys.get(p)[0]).thenComparingLong(p -> keys.get(p)[1]));
        return files;
    }

    private static double median(double[] sorted) {
        int n = sorted.length;
        return n % 2 == 1 ? sorted[n / 2] : (sorted[n / 2 - 1] + sorted[n / 2]) / 2.0;
    }

    private static String option(String[] args, String name, String def) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(name)) {
                return args[i + 1];
            }
        }
        if (def == null) {
            throw new IllegalArgumentException("opção obrigatória ausente: " + name);
        }
        return def;
    }
}
