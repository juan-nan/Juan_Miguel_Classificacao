package algorithms;

import graph.Edge;
import graph.IGraph;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/**
 * Resultado de um algoritmo de caminhos mínimos de origem única:
 * v.d (distância estimada) e v.π (predecessor; -1 representa NIL) para cada vértice.
 */
public class ShortestPathResult {

    public static final int NIL = -1;

    private final String algorithm;
    private final IGraph graph;
    private final int source;
    private final double[] dist;
    private final int[] pred;
    private final boolean noNegativeCycle;

    public ShortestPathResult(String algorithm, IGraph graph, int source,
                              double[] dist, int[] pred, boolean noNegativeCycle) {
        this.algorithm = algorithm;
        this.graph = graph;
        this.source = source;
        this.dist = dist;
        this.pred = pred;
        this.noNegativeCycle = noNegativeCycle;
    }

    public String algorithm() {
        return algorithm;
    }

    public int source() {
        return source;
    }

    /** δ(s, v); {@code Double.POSITIVE_INFINITY} se v não é alcançável a partir de s. */
    public double dist(int v) {
        return dist[v];
    }

    /** v.π; {@link #NIL} para a origem e para vértices não alcançáveis. */
    public int pred(int v) {
        return pred[v];
    }

    public double[] distances() {
        return Arrays.copyOf(dist, dist.length);
    }

    /**
     * Bellman-Ford: false se existe ciclo de peso negativo alcançável a partir da origem
     * (nesse caso as distâncias não são caminhos mínimos). Dijkstra: sempre true.
     */
    public boolean noNegativeCycle() {
        return noNegativeCycle;
    }

    public boolean hasPathTo(int v) {
        return dist[v] != Double.POSITIVE_INFINITY;
    }

    /** Vértices do caminho mínimo s ~> v (vazio se não houver caminho). */
    public List<Integer> pathTo(int v) {
        Deque<Integer> path = new ArrayDeque<>();
        if (!hasPathTo(v)) {
            return List.copyOf(path);
        }
        int steps = 0;
        for (int x = v; x != NIL; x = pred[x]) {
            path.addFirst(x);
            if (++steps > dist.length) {        // proteção contra ciclo nos predecessores
                break;
            }
        }
        return List.copyOf(path);
    }

    public int reachableCount() {
        int c = 0;
        for (double d : dist) {
            if (d != Double.POSITIVE_INFINITY) {
                c++;
            }
        }
        return c;
    }

    /** Tabela "vértice | d | π | caminho" (limitada a maxRows linhas). */
    public String table(int maxRows) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s a partir de %s%s%n", algorithm, label(source),
                noNegativeCycle ? "" : "  [ATENÇÃO: ciclo de peso negativo alcançável]"));
        sb.append(String.format("%8s | %8s | %8s | %s%n", "vértice", "d", "π", "caminho mínimo"));
        int n = Math.min(maxRows, dist.length);
        for (int v = 0; v < n; v++) {
            sb.append(String.format("%8s | %8s | %8s | %s%n", label(v), Edge.formatWeight(dist[v]),
                    pred[v] == NIL ? "NIL" : label(pred[v]), formatPath(v)));
        }
        if (dist.length > n) {
            sb.append(String.format("... (+%d vértices; %d de %d alcançáveis)%n",
                    dist.length - n, reachableCount(), dist.length));
        }
        return sb.toString();
    }

    public String formatPath(int v) {
        if (!hasPathTo(v)) {
            return "sem caminho";
        }
        StringBuilder sb = new StringBuilder();
        for (int x : pathTo(v)) {
            if (sb.length() > 0) {
                sb.append(" -> ");
            }
            sb.append(label(x));
        }
        return sb.toString();
    }

    private String label(int v) {
        return graph.vertices().get(v).getLabel();
    }
}
