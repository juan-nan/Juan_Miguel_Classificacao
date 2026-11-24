package algorithms;

import graph.Edge;
import graph.IGraph;

import java.util.Arrays;

/**
 * Algoritmo de Bellman-Ford: caminhos mínimos de origem única com pesos possivelmente negativos.
 * Segue o pseudocódigo da aula (Cormen, cap. 22):
 * <pre>
 * BELLMAN-FORD(G, w, s)
 * 1  INICIALIZA-ORIGEM-ÚNICA(G, s)
 * 2  for i = 1 to |G.V| - 1
 * 3      for cada aresta (u, v) ∈ G.E
 * 4          RELAXA(u, v, w)
 * 5  for cada aresta (u, v) ∈ G.E
 * 6      if v.d &gt; u.d + w(u, v)
 * 7          return FALSO
 * 8  return VERDADE
 * </pre>
 * "Para cada aresta (u, v) ∈ G.E" é percorrido como "para cada u ∈ V, para cada aresta em adj(u)".
 * Assim o mesmo código roda sobre qualquer {@link IGraph}, e só o custo de percorrer as arestas muda:
 * <ul>
 *   <li>Lista de Adjacência: Θ(V + E) por passada → O(V² + VE) no total (como no slide).</li>
 *   <li>Matriz de Adjacência: Θ(V²) por passada (adj(u) varre a linha u) → O(V³) no total.</li>
 * </ul>
 * O laço da linha 2 sempre executa as |V|-1 passadas (sem parada antecipada), fiel ao pseudocódigo.
 */
public final class BellmanFord {

    public static final String NAME = "Bellman-Ford";

    private BellmanFord() {
    }

    public static ShortestPathResult run(IGraph g, int s) {
        int n = g.numVertices();
        if (s < 0 || s >= n) {
            throw new IndexOutOfBoundsException("origem " + s + " fora de [0, " + (n - 1) + "]");
        }

        // INICIALIZA-ORIGEM-ÚNICA(G, s)
        double[] d = new double[n];
        int[] pi = new int[n];
        Arrays.fill(d, Double.POSITIVE_INFINITY);
        Arrays.fill(pi, ShortestPathResult.NIL);
        d[s] = 0.0;

        for (int i = 1; i <= n - 1; i++) {
            for (int u = 0; u < n; u++) {
                relaxOutgoing(g, u, d, pi);
            }
        }

        boolean ok = true;
        for (int u = 0; u < n && ok; u++) {
            for (Edge e : g.adj(u)) {
                if (d[e.getV().getId()] > d[u] + e.getWeight()) {
                    ok = false;
                    break;
                }
            }
        }
        return new ShortestPathResult(NAME, g, s, d, pi, ok);
    }

    /**
     * RELAXA(u, v, w) para cada aresta (u, v) que sai de u
     * (corpo do laço "para cada aresta (u, v) ∈ G.E", agrupado por vértice de origem).
     */
    private static void relaxOutgoing(IGraph g, int u, double[] d, int[] pi) {
        for (Edge e : g.adj(u)) {
            int v = e.getV().getId();
            double nd = d[u] + e.getWeight();   // RELAXA(u, v, w)
            if (d[v] > nd) {
                d[v] = nd;
                pi[v] = u;
            }
        }
    }
}
