package graph;

import java.util.List;

/**
 * Interface comum às representações de grafo (conforme o modelo de classes UML).
 *
 * <p>Convenções adotadas (ver DIVISAO_TAREFAS.md, decisões D1 e D2):</p>
 * <ul>
 *   <li>Vértices numerados de 0 a numVertices()-1.</li>
 *   <li>{@code addEdge(u, v)} cria aresta com peso padrão 1 (grafo não ponderado).</li>
 *   <li>Toda aresta devolvida por {@code adj(u)} tem {@code getU() == u}; o vizinho é sempre {@code getV()}.
 *       Em grafos UNDIRECTED, a estrutura guarda internamente o "espelho" (v, u) de cada aresta.</li>
 *   <li>{@code edges()} lista cada aresta uma única vez (também em grafos UNDIRECTED);
 *       {@code numEdges() == edges().size()}.</li>
 *   <li>Arestas duplicadas: se (u, v) já existe, {@code addEdge} a substitui ("a última lida vale").</li>
 * </ul>
 */
public interface IGraph {

    GraphType type();

    List<Vertex> vertices();

    List<Edge> edges();

    /** Insere a aresta (u, v) com peso padrão 1 (grafo não ponderado). */
    void addEdge(int u, int v);

    /** Insere a aresta (u, v) com o peso informado (grafo ponderado). */
    void addEdge(int u, int v, double weight);

    /** Devolve a aresta (u, v) ou {@code null} se ela não existir. */
    Edge getEdge(int u, int v);

    /**
     * Arestas que saem de u (lista de adjacência de u). A lista devolvida é somente para leitura:
     * o chamador não deve modificá-la (para inserir arestas, use addEdge).
     */
    List<Edge> adj(int u);

    int numVertices();

    int numEdges();
}
