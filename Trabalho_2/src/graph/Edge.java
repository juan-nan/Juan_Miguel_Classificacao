package graph;

/**
 * Aresta (u, v) com peso (conforme o modelo de classes UML).
 * Arestas criadas sem peso recebem o peso padrão 1.
 * Todos os campos são imutáveis.
 */
public class Edge {

    public static final double DEFAULT_WEIGHT = 1.0;

    private final Vertex u;
    private final Vertex v;
    private final double weight;

    public Edge(Vertex u, Vertex v) {
        this(u, v, DEFAULT_WEIGHT);
    }

    public Edge(Vertex u, Vertex v, double weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }

    public Vertex getU() {
        return u;
    }

    public Vertex getV() {
        return v;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "(" + u + ", " + v + ", " + formatWeight(weight) + ")";
    }

    /** Escreve pesos inteiros sem casas decimais (ex.: 66 em vez de 66.0). */
    public static String formatWeight(double w) {
        if (Double.isInfinite(w)) {
            return w > 0 ? "∞" : "-∞";
        }
        if (w == Math.rint(w) && Math.abs(w) < 1e15) {
            return String.valueOf((long) w);
        }
        return String.valueOf(w);
    }
}
