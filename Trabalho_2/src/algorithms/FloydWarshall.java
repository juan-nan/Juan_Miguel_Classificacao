package algorithms;

import graph.Edge;
import graph.IGraph;

/**
 * Algoritmo de Floyd-Warshall: caminhos mínimos entre todos os pares (programação dinâmica).
 * Segue o pseudocódigo da aula, FLOYD-WARSHALL-COM-PREDECESSOR(W) (Cormen, cap. 23):
 * a entrada é a matriz de pesos W (W[i, j] = ∞ se não existir aresta de i para j), obtida em O(1)
 * por par com {@code getEdge(i, j)} quando o grafo é um {@code AdjMatrixGraph} (representação da aula).
 * Tempo Θ(V³); memória Θ(V²).
 */
public final class FloydWarshall {

    public static final String NAME = "Floyd-Warshall";
    public static final int NIL = -1;

    private FloydWarshall() {
    }

    public static Result run(IGraph g) {
        int n = g.numVertices();
        double[][] dist = new double[n][n];
        int[][] pi = new int[n][n];

        // D(0) = W e π(0)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Edge e = g.getEdge(i, j);
                if (i == j) {
                    dist[i][j] = 0.0;
                    pi[i][j] = NIL;
                } else if (e == null) {
                    dist[i][j] = Double.POSITIVE_INFINITY;
                    pi[i][j] = NIL;
                } else {
                    dist[i][j] = e.getWeight();
                    pi[i][j] = i;
                }
            }
        }

        // d_ij(k) = min(d_ij(k-1), d_ik(k-1) + d_kj(k-1))
        for (int k = 0; k < n; k++) {
            double[] rowK = dist[k];
            for (int i = 0; i < n; i++) {
                double dik = dist[i][k];
                if (dik == Double.POSITIVE_INFINITY) {
                    continue;                     // nenhum caminho i ~> k: nada a melhorar
                }
                double[] rowI = dist[i];
                for (int j = 0; j < n; j++) {
                    double via = dik + rowK[j];
                    if (via < rowI[j]) {
                        rowI[j] = via;
                        pi[i][j] = pi[k][j];
                    }
                }
            }
        }
        return new Result(g, dist, pi);
    }

    /** Matrizes D (custos mínimos) e π (predecessores) de todos os pares. */
    public static final class Result {
        private final IGraph graph;
        private final double[][] dist;
        private final int[][] pred;

        private Result(IGraph graph, double[][] dist, int[][] pred) {
            this.graph = graph;
            this.dist = dist;
            this.pred = pred;
        }

        public double dist(int i, int j) {
            return dist[i][j];
        }

        public int pred(int i, int j) {
            return pred[i][j];
        }

        /** Há ciclo de peso negativo se algum D[i, i] &lt; 0. */
        public boolean hasNegativeCycle() {
            for (int i = 0; i < dist.length; i++) {
                if (dist[i][i] < 0) {
                    return true;
                }
            }
            return false;
        }

        /** IMPRIME-CAMINHO(π, i, j), como no slide. */
        public String formatPath(int i, int j) {
            if (i == j) {
                return label(i);
            }
            if (pred[i][j] == NIL) {
                return "não existe caminho de " + label(i) + " para " + label(j);
            }
            StringBuilder sb = new StringBuilder();
            appendPath(sb, i, j, 0);
            return sb.toString();
        }

        private void appendPath(StringBuilder sb, int i, int j, int depth) {
            if (i == j || depth > dist.length) {
                sb.append(label(i));
                return;
            }
            appendPath(sb, i, pred[i][j], depth + 1);
            sb.append(" -> ").append(label(j));
        }

        /** Matriz D formatada (limitada a maxSize linhas e colunas). */
        public String distanceMatrix(int maxSize) {
            int n = Math.min(maxSize, dist.length);
            int w = 3;
            for (int i = 0; i < n; i++) {
                w = Math.max(w, label(i).length());
                for (int j = 0; j < n; j++) {
                    w = Math.max(w, Edge.formatWeight(dist[i][j]).length());
                }
            }
            String cell = "%" + (w + 1) + "s";
            StringBuilder sb = new StringBuilder(String.format(cell, "D"));
            for (int j = 0; j < n; j++) {
                sb.append(String.format(cell, label(j)));
            }
            sb.append('\n');
            for (int i = 0; i < n; i++) {
                sb.append(String.format(cell, label(i)));
                for (int j = 0; j < n; j++) {
                    sb.append(String.format(cell, Edge.formatWeight(dist[i][j])));
                }
                sb.append('\n');
            }
            if (dist.length > n) {
                sb.append("... (matriz ").append(dist.length).append('x').append(dist.length)
                  .append(", exibidos ").append(n).append('x').append(n).append(")\n");
            }
            return sb.toString();
        }

        private String label(int v) {
            return graph.vertices().get(v).getLabel();
        }
    }
}
