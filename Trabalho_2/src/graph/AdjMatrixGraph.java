package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Grafo representado por Matriz de Adjacência (MA).
 * matrix[u][v] guarda a aresta (u, v) ou {@code null} se ela não existir.
 * Memória Θ(V²); getEdge(u, v) em O(1); adj(u) em Θ(V), pois percorre a linha u inteira.
 */
public class AdjMatrixGraph implements IGraph {

    private final List<Vertex> vertices;
    private final List<Edge> edges;
    private final Edge[][] matrix;
    private final GraphType type;

    public AdjMatrixGraph(int numVertices, GraphType type) {
        if (numVertices < 0) {
            throw new IllegalArgumentException("numVertices não pode ser negativo: " + numVertices);
        }
        if (type == null) {
            throw new IllegalArgumentException("type não pode ser null");
        }
        this.type = type;
        this.vertices = new ArrayList<>(numVertices);
        this.edges = new ArrayList<>();
        this.matrix = new Edge[numVertices][numVertices];
        for (int i = 0; i < numVertices; i++) {
            vertices.add(new Vertex(i));
        }
    }

    @Override
    public GraphType type() {
        return type;
    }

    @Override
    public List<Vertex> vertices() {
        return Collections.unmodifiableList(vertices);
    }

    @Override
    public List<Edge> edges() {
        return Collections.unmodifiableList(edges);
    }

    @Override
    public void addEdge(int u, int v) {
        addEdge(u, v, Edge.DEFAULT_WEIGHT);
    }

    @Override
    public void addEdge(int u, int v, double weight) {
        validateVertex(u);
        validateVertex(v);
        Vertex vu = vertices.get(u);
        Vertex vv = vertices.get(v);
        Edge e = new Edge(vu, vv, weight);
        boolean mirrored = type == GraphType.UNDIRECTED && u != v;

        Edge old = matrix[u][v];
        Edge oldMirror = mirrored ? matrix[v][u] : null;
        matrix[u][v] = e;
        if (mirrored) {
            matrix[v][u] = new Edge(vv, vu, weight);
        }

        if (old == null) {                             // aresta nova
            edges.add(e);
            return;
        }

        // Aresta duplicada: a última lida vale (decisão D2).
        int k = indexOfIdentity(edges, old);
        if (k < 0) {
            k = indexOfIdentity(edges, oldMirror);
        }
        edges.set(k, e);
    }

    @Override
    public Edge getEdge(int u, int v) {
        validateVertex(u);
        validateVertex(v);
        return matrix[u][v];
    }

    /** Percorre a linha u da matriz (Θ(V)) e devolve as arestas existentes. */
    @Override
    public List<Edge> adj(int u) {
        validateVertex(u);
        Edge[] row = matrix[u];
        List<Edge> list = new ArrayList<>();
        for (Edge e : row) {
            if (e != null) {
                list.add(e);
            }
        }
        return list;
    }

    @Override
    public int numVertices() {
        return vertices.size();
    }

    @Override
    public int numEdges() {
        return edges.size();
    }

    private void validateVertex(int u) {
        if (u < 0 || u >= numVertices()) {
            throw new IndexOutOfBoundsException(
                    "vértice " + u + " fora do intervalo [0, " + (numVertices() - 1) + "]");
        }
    }

    private static int indexOfIdentity(List<Edge> list, Edge target) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == target) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return "AdjMatrixGraph[" + type + ", |V|=" + numVertices() + ", |E|=" + numEdges() + "]";
    }
}
