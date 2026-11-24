package graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.IntFunction;

/**
 * Leitor de grafos no formato DIMACS gerado pelo GTgraph (.gr):
 * <pre>
 *   c &lt;comentário&gt;
 *   p sp &lt;|V|&gt; &lt;|E|&gt;
 *   a &lt;u&gt; &lt;v&gt; &lt;w&gt;      (arco dirigido u -&gt; v com peso w; vértices de 1 a |V|)
 * </pre>
 *
 * <ul>
 *   <li>Os vértices são convertidos de 1..|V| para 0..|V|-1; o label de cada vértice
 *       mantém a numeração original do arquivo.</li>
 *   <li>Arestas duplicadas (mesmo par u, v): a última lida vale (decisão D2). A deduplicação é
 *       feita aqui, na leitura, para que a LA e a MA recebam exatamente o mesmo conjunto de arestas,
 *       na mesma ordem, e para a carga não ficar O(V·E) na LA.</li>
 * </ul>
 *
 * Uso típico: {@code GrData d = GraphReader.parse(path); IGraph la = d.build(n -> new AdjListGraph(n, GraphType.DIRECTED));}
 */
public final class GraphReader {

    private GraphReader() {
    }

    /** Lê o arquivo e constrói o grafo com a representação fornecida pela fábrica. */
    public static IGraph read(Path path, IntFunction<IGraph> factory) throws IOException {
        return parse(path).build(factory);
    }

    /** Lê e valida o arquivo, sem construir o grafo. */
    public static GrData parse(Path path) throws IOException {
        int n = -1;
        int declared = -1;
        int arcLines = 0;
        int selfLoops = 0;
        // chave u*n+v -> posição nos vetores; LinkedHashMap mantém a ordem da primeira ocorrência
        Map<Long, Integer> index = null;
        int[] us = null;
        int[] vs = null;
        double[] ws = null;
        int size = 0;

        try (BufferedReader in = Files.newBufferedReader(path, StandardCharsets.US_ASCII)) {
            String line;
            int lineNo = 0;
            while ((line = in.readLine()) != null) {
                lineNo++;
                line = line.trim();
                if (line.isEmpty() || line.charAt(0) == 'c') {
                    continue;
                }
                String[] t = line.split("\\s+");
                switch (t[0]) {
                    case "p" -> {
                        if (n >= 0) {
                            throw error(path, lineNo, "linha 'p' repetida");
                        }
                        if (t.length != 4 || !t[1].equals("sp")) {
                            throw error(path, lineNo, "esperado 'p sp <V> <E>'");
                        }
                        n = Integer.parseInt(t[2]);
                        declared = Integer.parseInt(t[3]);
                        if (n < 0 || declared < 0) {
                            throw error(path, lineNo, "|V| e |E| não podem ser negativos");
                        }
                        index = new LinkedHashMap<>(Math.max(16, declared * 2));
                        us = new int[declared];
                        vs = new int[declared];
                        ws = new double[declared];
                    }
                    case "a" -> {
                        if (n < 0) {
                            throw error(path, lineNo, "linha 'a' antes da linha 'p'");
                        }
                        if (t.length != 4) {
                            throw error(path, lineNo, "esperado 'a <u> <v> <w>'");
                        }
                        int u = Integer.parseInt(t[1]) - 1;
                        int v = Integer.parseInt(t[2]) - 1;
                        double w = Double.parseDouble(t[3]);
                        if (u < 0 || u >= n || v < 0 || v >= n) {
                            throw error(path, lineNo, "vértice fora de 1.." + n);
                        }
                        arcLines++;
                        if (u == v) {
                            selfLoops++;
                        }
                        long key = (long) u * n + v;
                        Integer pos = index.get(key);
                        if (pos != null) {
                            ws[pos] = w;                // duplicata: a última lida vale
                        } else {
                            if (size == us.length) {    // arquivo com mais linhas que o declarado
                                int cap = Math.max(16, size * 2);
                                us = Arrays.copyOf(us, cap);
                                vs = Arrays.copyOf(vs, cap);
                                ws = Arrays.copyOf(ws, cap);
                            }
                            index.put(key, size);
                            us[size] = u;
                            vs[size] = v;
                            ws[size] = w;
                            size++;
                        }
                    }
                    default -> throw error(path, lineNo, "linha desconhecida: " + line);
                }
            }
        } catch (NumberFormatException e) {
            throw new IOException(path + ": número inválido (" + e.getMessage() + ")", e);
        }
        if (n < 0) {
            throw new IOException(path + ": linha 'p sp <V> <E>' não encontrada");
        }
        return new GrData(path.getFileName().toString(), n, declared, arcLines, selfLoops,
                Arrays.copyOf(us, size), Arrays.copyOf(vs, size),
                Arrays.copyOf(ws, size));
    }

    private static IOException error(Path path, int lineNo, String msg) {
        return new IOException(path + ":" + lineNo + ": " + msg);
    }

    /** Conteúdo de um arquivo .gr já lido, com as arestas sem duplicatas (vértices 0-based). */
    public static final class GrData {
        private final String fileName;
        private final int numVertices;
        private final int declaredEdges;
        private final int arcLines;
        private final int selfLoops;
        private final int[] us;
        private final int[] vs;
        private final double[] ws;

        private GrData(String fileName, int numVertices, int declaredEdges, int arcLines, int selfLoops,
                       int[] us, int[] vs, double[] ws) {
            this.fileName = fileName;
            this.numVertices = numVertices;
            this.declaredEdges = declaredEdges;
            this.arcLines = arcLines;
            this.selfLoops = selfLoops;
            this.us = us;
            this.vs = vs;
            this.ws = ws;
        }

        /** Constrói o grafo (DIRECTED, como no arquivo) na representação criada pela fábrica. */
        public IGraph build(IntFunction<IGraph> factory) {
            IGraph g = factory.apply(numVertices);
            if (g.numVertices() != numVertices) {
                throw new IllegalArgumentException("a fábrica criou um grafo com |V| diferente do arquivo");
            }
            for (Vertex x : g.vertices()) {
                x.setLabel(String.valueOf(x.getId() + 1));   // mantém a numeração do arquivo
            }
            for (int i = 0; i < us.length; i++) {
                g.addEdge(us[i], vs[i], ws[i]);
            }
            return g;
        }

        public String fileName() {
            return fileName;
        }

        public int numVertices() {
            return numVertices;
        }

        /** |E| declarado na linha 'p'. */
        public int declaredEdges() {
            return declaredEdges;
        }

        /** Quantidade de linhas 'a' lidas (inclui duplicatas). */
        public int arcLines() {
            return arcLines;
        }

        public int distinctEdges() {
            return us.length;
        }

        public int duplicates() {
            return arcLines - us.length;
        }

        public int selfLoops() {
            return selfLoops;
        }

        /** D = |E| / (|V|(|V|-1)) usando o |E| declarado no arquivo. */
        public double nominalDensity() {
            return density(declaredEdges);
        }

        /** D = |E| / (|V|(|V|-1)) usando as arestas distintas efetivamente carregadas. */
        public double realDensity() {
            return density(us.length);
        }

        private double density(int e) {
            long n = numVertices;
            return n < 2 ? 0.0 : e / (double) (n * (n - 1));
        }
    }
}
