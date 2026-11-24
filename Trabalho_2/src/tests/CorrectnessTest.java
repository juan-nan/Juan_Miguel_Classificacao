package tests;

import algorithms.BellmanFord;
import algorithms.Dijkstra;
import algorithms.FloydWarshall;
import algorithms.IndexedMinPQ;
import algorithms.ShortestPathResult;
import graph.AdjListGraph;
import graph.AdjMatrixGraph;
import graph.Edge;
import graph.GraphReader;
import graph.GraphReader.GrData;
import graph.GraphType;
import graph.IGraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.IntFunction;

/**
 * Testes de corretude das estruturas de grafo e dos algoritmos de caminho mínimo.
 * Execução (a partir da pasta Trabalho_2):
 * <pre>java -cp out tests.CorrectnessTest [pastaDosGrafos]</pre>
 */
public class CorrectnessTest {

    private static final double EPS = 1e-9;
    private static int passed = 0;
    private static int failed = 0;

    private static final IntFunction<IGraph> LA = n -> new AdjListGraph(n, GraphType.DIRECTED);
    private static final IntFunction<IGraph> MA = n -> new AdjMatrixGraph(n, GraphType.DIRECTED);

    /** Arestas distintas esperadas em cada arquivo do professor (tabela "Cuidados com o GTgraph"). */
    private static final Map<String, Integer> EXPECTED_DISTINCT = new TreeMap<>(Map.ofEntries(
            Map.entry("sample100-1980.gr", 1773), Map.entry("sample100-3960.gr", 3266),
            Map.entry("sample100-5940.gr", 4438), Map.entry("sample100-7920.gr", 5391),
            Map.entry("sample100-9900.gr", 6215), Map.entry("sample200-7960.gr", 7205),
            Map.entry("sample200-15920.gr", 13072), Map.entry("sample200-23880.gr", 17929),
            Map.entry("sample200-31840.gr", 21907), Map.entry("sample200-39800.gr", 25167),
            Map.entry("sample500-49900.gr", 45108), Map.entry("sample500-99800.gr", 82141),
            Map.entry("sample500-149700.gr", 112525), Map.entry("sample500-199600.gr", 137330),
            Map.entry("sample500-249500.gr", 157800)));

    public static void main(String[] args) throws IOException {
        Path dir = Paths.get(args.length > 0 ? args[0] : "RandomGraphSamples");

        section("Estruturas (UML)");
        testExceptions();
        testDirectedBasics(LA, "LA");
        testDirectedBasics(MA, "MA");
        testUndirected(n -> new AdjListGraph(n, GraphType.UNDIRECTED), "LA");
        testUndirected(n -> new AdjMatrixGraph(n, GraphType.UNDIRECTED), "MA");
        testDuplicates(LA, "LA");
        testDuplicates(MA, "MA");
        testDuplicates(n -> new AdjListGraph(n, GraphType.UNDIRECTED), "LA não-dirigido");
        testDuplicates(n -> new AdjMatrixGraph(n, GraphType.UNDIRECTED), "MA não-dirigido");

        section("Fila de prioridade indexada");
        testIndexedMinPQ();

        section("Caminhos mínimos — exemplos do Cormen (slides)");
        testBellmanFordCormen(LA, "LA");
        testBellmanFordCormen(MA, "MA");
        testDijkstraCormen(LA, "LA");
        testDijkstraCormen(MA, "MA");
        testNegativeCycle(LA, "LA");
        testNegativeCycle(MA, "MA");
        testDijkstraRejectsNegative();
        testUnreachable();

        section("Comparação cruzada em grafos aleatórios pequenos");
        testRandomCrossCheck();

        section("Arquivos do professor (" + dir + ")");
        testProfessorFiles(dir);

        System.out.printf("%nResultado: %d testes OK, %d falharam%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ------------------------------------------------------------------ estruturas

    private static void testExceptions() {
        check("AdjListGraph(-1) lança IllegalArgumentException",
                throwsType(() -> new AdjListGraph(-1, GraphType.DIRECTED), IllegalArgumentException.class));
        check("AdjMatrixGraph(-1) lança IllegalArgumentException",
                throwsType(() -> new AdjMatrixGraph(-1, GraphType.DIRECTED), IllegalArgumentException.class));
        check("LA addEdge com vértice inválido lança IndexOutOfBoundsException",
                throwsType(() -> new AdjListGraph(3, GraphType.DIRECTED).addEdge(0, 3), IndexOutOfBoundsException.class));
        check("MA addEdge com vértice inválido lança IndexOutOfBoundsException",
                throwsType(() -> new AdjMatrixGraph(3, GraphType.DIRECTED).addEdge(-1, 0), IndexOutOfBoundsException.class));
        check("LA adj(u) com vértice inválido lança IndexOutOfBoundsException",
                throwsType(() -> new AdjListGraph(2, GraphType.DIRECTED).adj(2), IndexOutOfBoundsException.class));
        check("MA getEdge com vértice inválido lança IndexOutOfBoundsException",
                throwsType(() -> new AdjMatrixGraph(2, GraphType.DIRECTED).getEdge(0, 5), IndexOutOfBoundsException.class));
        check("grafo com 0 vértices é válido",
                new AdjListGraph(0, GraphType.DIRECTED).numVertices() == 0
                        && new AdjMatrixGraph(0, GraphType.DIRECTED).numEdges() == 0);
    }

    private static void testDirectedBasics(IntFunction<IGraph> f, String rep) {
        IGraph g = f.apply(4);
        g.addEdge(0, 1);
        g.addEdge(0, 2, 5.5);
        g.addEdge(2, 3, -2);
        check(rep + ": numEdges dirigido", g.numEdges() == 3);
        check(rep + ": peso padrão = 1", g.getEdge(0, 1).getWeight() == 1.0);
        check(rep + ": dirigido (u,v) != (v,u)", g.getEdge(1, 0) == null && g.getEdge(0, 2) != null);
        check(rep + ": adj(0) tem 2 arestas com getU()==0",
                g.adj(0).size() == 2 && g.adj(0).stream().allMatch(e -> e.getU().getId() == 0));
        check(rep + ": adj(3) vazia", g.adj(3).isEmpty());
        check(rep + ": type DIRECTED", g.type() == GraphType.DIRECTED);
    }

    private static void testUndirected(IntFunction<IGraph> f, String rep) {
        IGraph g = f.apply(3);
        g.addEdge(0, 1, 4);
        g.addEdge(2, 1, 7);
        check(rep + " não-dirigido: numEdges conta 1 por aresta", g.numEdges() == 2 && g.edges().size() == 2);
        check(rep + " não-dirigido: (u,v) == (v,u)",
                g.getEdge(1, 0) != null && g.getEdge(1, 0).getWeight() == 4 && g.getEdge(1, 2).getWeight() == 7);
        check(rep + " não-dirigido: vizinho sempre em getV()",
                g.adj(1).size() == 2 && g.adj(1).stream().allMatch(e -> e.getU().getId() == 1));
    }

    private static void testDuplicates(IntFunction<IGraph> f, String rep) {
        IGraph g = f.apply(3);
        g.addEdge(0, 1, 5);
        g.addEdge(1, 2, 8);
        g.addEdge(0, 1, 3);                 // duplicata: a última vale
        boolean ok = g.numEdges() == 2 && g.getEdge(0, 1).getWeight() == 3
                && g.adj(0).size() == 1 && g.edges().stream().anyMatch(e -> e.getWeight() == 3)
                && g.edges().stream().noneMatch(e -> e.getWeight() == 5);
        if (g.type() == GraphType.UNDIRECTED) {
            g.addEdge(1, 0, 9);             // mesma aresta não-dirigida, escrita ao contrário
            ok &= g.numEdges() == 2 && g.getEdge(0, 1).getWeight() == 9 && g.getEdge(1, 0).getWeight() == 9
                    && g.adj(1).size() == 2;
        }
        check(rep + ": duplicata substitui (última lida vale)", ok);
    }

    // ------------------------------------------------------------------ fila de prioridade

    private static void testIndexedMinPQ() {
        IndexedMinPQ q = new IndexedMinPQ(6);
        double[] keys = {5, 3, 8, 1, 9, 7};
        for (int v = 0; v < keys.length; v++) {
            q.insert(v, keys[v]);
        }
        q.decreaseKey(4, 0);   // 9 -> 0
        q.decreaseKey(2, 2);   // 8 -> 2
        int[] order = new int[6];
        for (int i = 0; i < 6; i++) {
            order[i] = q.extractMin();
        }
        check("IndexedMinPQ: ordem de EXTRAI-MIN com DIMINUI-CHAVE",
                Arrays.equals(order, new int[]{4, 3, 2, 1, 0, 5}) && q.isEmpty());
    }

    // ------------------------------------------------------------------ caminhos mínimos

    /** Cormen, fig. 22.4 (exemplo do slide de Bellman-Ford): s=0, t=1, x=2, y=3, z=4. */
    private static void testBellmanFordCormen(IntFunction<IGraph> f, String rep) {
        IGraph g = f.apply(5);
        int s = 0, t = 1, x = 2, y = 3, z = 4;
        g.addEdge(s, t, 6);
        g.addEdge(s, y, 7);
        g.addEdge(t, x, 5);
        g.addEdge(t, y, 8);
        g.addEdge(t, z, -4);
        g.addEdge(x, t, -2);
        g.addEdge(y, x, -3);
        g.addEdge(y, z, 9);
        g.addEdge(z, s, 2);
        g.addEdge(z, x, 7);
        ShortestPathResult r = BellmanFord.run(g, s);
        check(rep + ": Bellman-Ford fig. 22.4 (d = 0, 2, 4, 7, -2)",
                r.noNegativeCycle() && sameDist(r.distances(), new double[]{0, 2, 4, 7, -2}));
        check(rep + ": Bellman-Ford fig. 22.4 caminho s->z = s, y, x, t, z",
                r.pathTo(z).equals(java.util.List.of(s, y, x, t, z)));
    }

    /** Cormen, fig. 22.6 (exemplo do slide de Dijkstra): s=0, t=1, x=2, y=3, z=4. */
    private static void testDijkstraCormen(IntFunction<IGraph> f, String rep) {
        IGraph g = f.apply(5);
        int s = 0, t = 1, x = 2, y = 3, z = 4;
        g.addEdge(s, t, 10);
        g.addEdge(s, y, 5);
        g.addEdge(t, x, 1);
        g.addEdge(t, y, 2);
        g.addEdge(y, t, 3);
        g.addEdge(y, x, 9);
        g.addEdge(y, z, 2);
        g.addEdge(x, z, 4);
        g.addEdge(z, x, 6);
        g.addEdge(z, s, 7);
        ShortestPathResult r = Dijkstra.run(g, s);
        double[] expected = {0, 8, 9, 5, 7};
        check(rep + ": Dijkstra fig. 22.6 (d = 0, 8, 9, 5, 7)", sameDist(r.distances(), expected));
        check(rep + ": Dijkstra fig. 22.6 caminho s->x = s, y, t, x", r.pathTo(x).equals(java.util.List.of(s, y, t, x)));
        check(rep + ": Bellman-Ford fig. 22.6 = Dijkstra", sameDist(BellmanFord.run(g, s).distances(), expected));
        FloydWarshall.Result fw = FloydWarshall.run(g);
        boolean fwOk = true;
        for (int v = 0; v < 5; v++) {
            fwOk &= Math.abs(fw.dist(s, v) - expected[v]) < EPS;
        }
        check(rep + ": Floyd-Warshall fig. 22.6 linha s = Dijkstra", fwOk && !fw.hasNegativeCycle());
        check(rep + ": Floyd-Warshall IMPRIME-CAMINHO s->x", fw.formatPath(s, x).equals("0 -> 3 -> 1 -> 2"));
    }

    private static void testNegativeCycle(IntFunction<IGraph> f, String rep) {
        IGraph g = f.apply(4);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, -3);
        g.addEdge(2, 1, 1);    // ciclo 1 -> 2 -> 1 com peso -2
        g.addEdge(2, 3, 2);
        check(rep + ": Bellman-Ford detecta ciclo negativo (retorna FALSO)", !BellmanFord.run(g, 0).noNegativeCycle());
        check(rep + ": Floyd-Warshall detecta ciclo negativo", FloydWarshall.run(g).hasNegativeCycle());
        IGraph h = f.apply(4);
        h.addEdge(0, 1, 1);
        h.addEdge(2, 3, -3);
        h.addEdge(3, 2, 1);    // ciclo negativo NÃO alcançável a partir de 0
        check(rep + ": Bellman-Ford ignora ciclo negativo inalcançável", BellmanFord.run(h, 0).noNegativeCycle());
    }

    private static void testDijkstraRejectsNegative() {
        IGraph g = new AdjListGraph(2, GraphType.DIRECTED);
        g.addEdge(0, 1, -1);
        check("Dijkstra rejeita peso negativo", throwsType(() -> Dijkstra.run(g, 0), IllegalArgumentException.class));
    }

    private static void testUnreachable() {
        for (IntFunction<IGraph> f : java.util.List.of(LA, MA)) {
            IGraph g = f.apply(3);
            g.addEdge(0, 1, 2);
            ShortestPathResult d = Dijkstra.run(g, 0);
            ShortestPathResult b = BellmanFord.run(g, 0);
            boolean ok = d.dist(2) == Double.POSITIVE_INFINITY && b.dist(2) == Double.POSITIVE_INFINITY
                    && d.pred(2) == ShortestPathResult.NIL && d.pathTo(2).isEmpty()
                    && d.formatPath(2).equals("sem caminho");
            check(repName(g) + ": vértice inalcançável tem d = ∞ e π = NIL", ok);
        }
    }

    private static void testRandomCrossCheck() {
        Random rnd = new Random(42);
        boolean ok = true;
        int graphs = 200;
        for (int t = 0; t < graphs && ok; t++) {
            int n = 2 + rnd.nextInt(12);
            int m = rnd.nextInt(n * n);
            IGraph la = LA.apply(n);
            IGraph ma = MA.apply(n);
            for (int i = 0; i < m; i++) {
                int u = rnd.nextInt(n), v = rnd.nextInt(n);
                double w = rnd.nextInt(20);
                la.addEdge(u, v, w);
                ma.addEdge(u, v, w);
            }
            ok &= la.numEdges() == ma.numEdges();
            FloydWarshall.Result fw = FloydWarshall.run(ma);
            for (int s = 0; s < n && ok; s++) {
                double[] ref = Dijkstra.run(la, s).distances();
                ok &= sameDist(ref, Dijkstra.run(ma, s).distances());
                ok &= sameDist(ref, BellmanFord.run(la, s).distances());
                ok &= sameDist(ref, BellmanFord.run(ma, s).distances());
                for (int v = 0; v < n; v++) {
                    ok &= equal(ref[v], fw.dist(s, v));
                }
            }
        }
        check(graphs + " grafos aleatórios: Dijkstra = Bellman-Ford = Floyd-Warshall, LA = MA", ok);
    }

    // ------------------------------------------------------------------ arquivos do professor

    private static void testProfessorFiles(Path dir) throws IOException {
        if (!Files.isDirectory(dir)) {
            check("pasta " + dir + " existe", false);
            return;
        }
        for (Map.Entry<String, Integer> entry : EXPECTED_DISTINCT.entrySet()) {
            Path p = dir.resolve(entry.getKey());
            if (!Files.exists(p)) {
                check(entry.getKey() + " existe", false);
                continue;
            }
            GrData data = GraphReader.parse(p);
            IGraph la = data.build(LA);
            IGraph ma = data.build(MA);
            int expected = entry.getValue();
            boolean edgesOk = data.distinctEdges() == expected && la.numEdges() == expected
                    && ma.numEdges() == expected && data.arcLines() == data.declaredEdges()
                    && data.selfLoops() == 0;
            boolean sameStructure = sameEdges(la, ma);

            double[] ref = BellmanFord.run(la, 0).distances();
            boolean spOk = sameDist(ref, BellmanFord.run(ma, 0).distances())
                    && sameDist(ref, Dijkstra.run(la, 0).distances())
                    && sameDist(ref, Dijkstra.run(ma, 0).distances());
            check(String.format("%-20s |E| distintas = %6d, LA ≡ MA, BF = Dijkstra (LA e MA)", entry.getKey(), expected),
                    edgesOk && sameStructure && spOk);
        }
    }

    /** Mesmo conjunto de arestas (u, v, w) nas duas representações. */
    private static boolean sameEdges(IGraph a, IGraph b) {
        if (a.numEdges() != b.numEdges()) {
            return false;
        }
        for (Edge e : a.edges()) {
            Edge o = b.getEdge(e.getU().getId(), e.getV().getId());
            if (o == null || o.getWeight() != e.getWeight()) {
                return false;
            }
        }
        for (int u = 0; u < a.numVertices(); u++) {
            if (a.adj(u).size() != b.adj(u).size()) {
                return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------------------ utilitários

    private static String repName(IGraph g) {
        return g instanceof AdjListGraph ? "LA" : "MA";
    }

    private static boolean sameDist(double[] a, double[] b) {
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (!equal(a[i], b[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean equal(double a, double b) {
        return a == b || Math.abs(a - b) < EPS;
    }

    private static boolean throwsType(Runnable r, Class<? extends Throwable> type) {
        try {
            r.run();
            return false;
        } catch (Throwable t) {
            return type.isInstance(t);
        }
    }

    private static void section(String name) {
        System.out.println("\n== " + name);
    }

    private static void check(String name, boolean ok) {
        if (ok) {
            passed++;
        } else {
            failed++;
        }
        System.out.printf("  [%s] %s%n", ok ? " OK " : "FALHOU", name);
    }
}
