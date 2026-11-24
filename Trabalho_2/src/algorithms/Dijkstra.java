package algorithms;

import graph.Edge;
import graph.IGraph;

import java.util.Arrays;

/**
 * Algoritmo de Dijkstra: caminhos mínimos de origem única com pesos não negativos.
 * Segue o pseudocódigo da aula (Cormen, cap. 22), com fila de prioridade mínima
 * implementada como heap binário indexado ({@link IndexedMinPQ}):
 * <pre>
 * DIJKSTRA(G, w, s)
 *  1  INICIALIZA-ORIGEM-ÚNICA(G, s)
 *  2  S = Ø
 *  3  Q = Ø
 *  4  for cada vértice u ∈ G.V
 *  5      INSERE(Q, u)
 *  6  while Q ≠ Ø
 *  7      u = EXTRAI-MIN(Q)
 *  8      S = S ∪ {u}
 *  9      for cada vértice v em G.Adj[u]
 * 10          RELAXA(u, v, w)
 * 11          if a chamada de RELAXA diminuiu v.d
 * 12              DIMINUI-CHAVE(Q, v, v.d)
 * </pre>
 * O mesmo código roda sobre qualquer {@link IGraph}; só o custo de G.Adj[u] muda:
 * <ul>
 *   <li>Lista de Adjacência: O((V + E) lg V) (como no slide).</li>
 *   <li>Matriz de Adjacência: O(V² + E lg V), pois cada adj(u) varre a linha u inteira (Θ(V)).</li>
 * </ul>
 */
public final class Dijkstra {

    public static final String NAME = "Dijkstra";

    private Dijkstra() {
    }

    /**
     * @throws IllegalArgumentException se uma aresta com peso negativo for encontrada
     */
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

        boolean[] inS = new boolean[n];               // S
        IndexedMinPQ q = new IndexedMinPQ(n);         // Q
        for (int u = 0; u < n; u++) {
            q.insert(u, d[u]);
        }

        while (!q.isEmpty()) {
            int u = q.extractMin();
            inS[u] = true;
            if (d[u] == Double.POSITIVE_INFINITY) {
                continue;                             // u inalcançável: não há o que relaxar
            }
            for (Edge e : g.adj(u)) {
                double w = e.getWeight();
                if (w < 0) {
                    throw new IllegalArgumentException(
                            "Dijkstra exige pesos não negativos; aresta " + e + " tem peso " + w);
                }
                int v = e.getV().getId();
                // RELAXA(u, v, w)
                double nd = d[u] + w;
                if (d[v] > nd) {
                    d[v] = nd;
                    pi[v] = u;
                    if (!inS[v]) {
                        q.decreaseKey(v, nd);         // DIMINUI-CHAVE(Q, v, v.d)
                    }
                }
            }
        }
        return new ShortestPathResult(NAME, g, s, d, pi, true);
    }
}
