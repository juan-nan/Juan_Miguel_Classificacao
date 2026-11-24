package graph;

/**
 * Tipo do grafo (conforme o modelo de classes UML).
 * <ul>
 *   <li>DIRECTED   - (u, v) != (v, u): dígrafo (grafo orientado)</li>
 *   <li>UNDIRECTED - (u, v) == (v, u): grafo não-orientado</li>
 * </ul>
 */
public enum GraphType {
    DIRECTED,
    UNDIRECTED
}
