package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Grafo representado por Listas de Adjacência (LA).
 * Memória Θ(V + E); adj(u) em O(1); getEdge(u, v) em O(grau(u)).
 */
public class AdjListGraph implements IGraph {

    private final List<Vertex> vertices;
    private final List<Edge> edges;
    private final List<List<Edge>> adj;
    private final GraphType type;

    public AdjListGraph(int numVertices, GraphType type) {
        if (numVertices < 0) {
            throw new IllegalArgumentException("numVertices não pode ser negativo: " + numVertices);
        }
        if (type == null) {
            throw new IllegalArgumentException("type não pode ser null");
        }
        this.type = type;
        this.vertices = new ArrayList<>(numVertices);
        this.edges = new ArrayList<>();
        this.adj = new ArrayList<>(numVertices);
        for (int i = 0; i < numVertices; i++) {
            vertices.add(new Vertex(i));
            adj.add(new ArrayList<>());
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

        int i = indexOf(adj.get(u), v);
        if (i < 0) {                                   // aresta nova
            adj.get(u).add(e);
            if (mirrored) {
                adj.get(v).add(new Edge(vv, vu, weight));
            }
            edges.add(e);
            return;
        }

        // Aresta duplicada: a última lida vale (decisão D2).
        Edge old = adj.get(u).get(i);
        adj.get(u).set(i, e);
        Edge oldMirror = null;
        if (mirrored) {
            int j = indexOf(adj.get(v), u);
            oldMirror = adj.get(v).get(j);
            adj.get(v).set(j, new Edge(vv, vu, weight));
        }
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
        int i = indexOf(adj.get(u), v);
        return i < 0 ? null : adj.get(u).get(i);
    }

    /**
     * Devolve a própria lista interna de u, em O(1), SOMENTE PARA LEITURA (não modificar).
     * Não usamos Collections.unmodifiableList de propósito: o iterador desse invólucro é compartilhado
     * por todo o JDK, a JIT nem sempre consegue otimizá-lo, e no experimento isso deixava o
     * Bellman-Ford em LA até 3x mais lento de forma irregular entre execuções.
     */
    @Override
    public List<Edge> adj(int u) {
        validateVertex(u);
        return adj.get(u);
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

    /** Posição da aresta (·, v) na lista, ou -1. */
    private static int indexOf(List<Edge> list, int v) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getV().getId() == v) {
                return i;
            }
        }
        return -1;
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
        return "AdjListGraph[" + type + ", |V|=" + numVertices() + ", |E|=" + numEdges() + "]";
    }
}
