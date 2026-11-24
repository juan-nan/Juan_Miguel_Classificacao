package graph;

/**
 * Vértice de um grafo (conforme o modelo de classes UML).
 * O id é o índice do vértice na estrutura (0 .. |V|-1); o label é o nome exibido.
 */
public class Vertex {

    private final int id;
    private String label;

    public Vertex(int id) {
        this(id, String.valueOf(id));
    }

    public Vertex(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
