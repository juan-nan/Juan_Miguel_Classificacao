package graph;

import java.util.List;

/**
 * Impressão/visualização textual de um grafo de entrada.
 * <ul>
 *   <li>{@link #summary}: tipo, representação, |V|, |E| e densidade.</li>
 *   <li>{@link #adjacencyLists}: lista de adjacência de cada vértice ("u -> v(peso) ...").</li>
 *   <li>{@link #matrix}: matriz de pesos (para grafos pequenos).</li>
 *   <li>{@link #describe}: escolhe automaticamente o formato conforme o tamanho do grafo.</li>
 * </ul>
 * Os vértices são exibidos pelo label (nos grafos lidos de arquivo, a numeração original 1..|V|).
 */
public final class GraphPrinter {

    /** Até este |V| a matriz de adjacência também é impressa por {@link #describe}. */
    public static final int MATRIX_LIMIT = 15;
    /** Quantidade de vértices/vizinhos exibidos em grafos grandes. */
    public static final int PREVIEW_VERTICES = 10;
    public static final int PREVIEW_NEIGHBORS = 12;

    private GraphPrinter() {
    }

    public static String summary(IGraph g) {
        long n = g.numVertices();
        long possible = g.type() == GraphType.DIRECTED ? n * (n - 1) : n * (n - 1) / 2;
        double d = possible == 0 ? 0.0 : g.numEdges() / (double) possible;
        return String.format("%s | %s | |V| = %d | |E| = %d | densidade = %.3f",
                representation(g), g.type() == GraphType.DIRECTED ? "dirigido" : "não-dirigido",
                g.numVertices(), g.numEdges(), d);
    }

    public static String representation(IGraph g) {
        if (g instanceof AdjListGraph) {
            return "Lista de Adjacência (LA)";
        }
        if (g instanceof AdjMatrixGraph) {
            return "Matriz de Adjacência (MA)";
        }
        return g.getClass().getSimpleName();
    }

    /** Listas de adjacência completas. */
    public static String adjacencyLists(IGraph g) {
        return adjacencyLists(g, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /** Listas de adjacência limitadas a maxVertices linhas e maxNeighbors vizinhos por linha. */
    public static String adjacencyLists(IGraph g, int maxVertices, int maxNeighbors) {
        StringBuilder sb = new StringBuilder();
        boolean weighted = isWeighted(g);
        int width = labelWidth(g);
        int shownV = Math.min(maxVertices, g.numVertices());
        for (int u = 0; u < shownV; u++) {
            List<Edge> list = g.adj(u);
            sb.append(String.format("%" + width + "s ->", g.vertices().get(u).getLabel()));
            int shownN = Math.min(maxNeighbors, list.size());
            for (int i = 0; i < shownN; i++) {
                Edge e = list.get(i);
                sb.append(' ').append(e.getV().getLabel());
                if (weighted) {
                    sb.append('(').append(Edge.formatWeight(e.getWeight())).append(')');
                }
            }
            if (list.size() > shownN) {
                sb.append(" ... (+").append(list.size() - shownN).append(')');
            }
            sb.append('\n');
        }
        if (g.numVertices() > shownV) {
            sb.append("... (+").append(g.numVertices() - shownV).append(" vértices)\n");
        }
        return sb.toString();
    }

    /** Matriz de pesos; "·" indica ausência de aresta. */
    public static String matrix(IGraph g) {
        int n = g.numVertices();
        int width = labelWidth(g);
        for (Edge e : g.edges()) {
            width = Math.max(width, Edge.formatWeight(e.getWeight()).length());
        }
        String cell = "%" + (width + 1) + "s";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(cell, ""));
        for (int v = 0; v < n; v++) {
            sb.append(String.format(cell, g.vertices().get(v).getLabel()));
        }
        sb.append('\n');
        for (int u = 0; u < n; u++) {
            sb.append(String.format(cell, g.vertices().get(u).getLabel()));
            for (int v = 0; v < n; v++) {
                Edge e = g.getEdge(u, v);
                sb.append(String.format(cell, e == null ? "·" : Edge.formatWeight(e.getWeight())));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Resumo + listas de adjacência (+ matriz, se o grafo for pequeno). */
    public static String describe(IGraph g) {
        StringBuilder sb = new StringBuilder();
        sb.append(summary(g)).append('\n');
        if (g.numVertices() <= MATRIX_LIMIT) {
            sb.append("Listas de adjacência:\n").append(adjacencyLists(g));
            sb.append("Matriz de adjacência (pesos):\n").append(matrix(g));
        } else {
            sb.append("Listas de adjacência (prévia):\n")
              .append(adjacencyLists(g, PREVIEW_VERTICES, PREVIEW_NEIGHBORS));
        }
        return sb.toString();
    }

    private static boolean isWeighted(IGraph g) {
        for (Edge e : g.edges()) {
            if (e.getWeight() != Edge.DEFAULT_WEIGHT) {
                return true;
            }
        }
        return false;
    }

    private static int labelWidth(IGraph g) {
        int w = 1;
        for (Vertex x : g.vertices()) {
            w = Math.max(w, x.getLabel().length());
        }
        return w;
    }
}
