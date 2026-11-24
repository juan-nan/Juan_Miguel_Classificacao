package algorithms;

import java.util.NoSuchElementException;

/**
 * Fila de prioridade mínima indexada (heap binário de mínimo) sobre os vértices 0..n-1.
 * Implementa as operações usadas nos pseudocódigos de Dijkstra e Prim (Cormen):
 * INSERE, EXTRAI-MIN e DIMINUI-CHAVE, todas em O(lg n); contains e isEmpty em O(1).
 */
public class IndexedMinPQ {

    private final int[] heap;      // heap[i] = vértice na posição i do heap (1-based)
    private final int[] pos;       // pos[v] = posição de v no heap, ou 0 se v não está na fila
    private final double[] key;    // key[v] = chave atual de v
    private int size;

    public IndexedMinPQ(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacidade negativa: " + capacity);
        }
        heap = new int[capacity + 1];
        pos = new int[capacity];
        key = new double[capacity];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public boolean contains(int v) {
        validate(v);
        return pos[v] != 0;
    }

    public double keyOf(int v) {
        if (!contains(v)) {
            throw new NoSuchElementException("vértice " + v + " não está na fila");
        }
        return key[v];
    }

    /** INSERE(Q, v) com a chave k. */
    public void insert(int v, double k) {
        if (contains(v)) {
            throw new IllegalArgumentException("vértice " + v + " já está na fila");
        }
        size++;
        heap[size] = v;
        pos[v] = size;
        key[v] = k;
        siftUp(size);
    }

    /** EXTRAI-MIN(Q): remove e devolve o vértice de menor chave. */
    public int extractMin() {
        if (size == 0) {
            throw new NoSuchElementException("fila vazia");
        }
        int min = heap[1];
        swap(1, size);
        size--;
        pos[min] = 0;
        if (size > 0) {
            siftDown(1);
        }
        return min;
    }

    /** DIMINUI-CHAVE(Q, v, k): k deve ser menor ou igual à chave atual de v. */
    public void decreaseKey(int v, double k) {
        if (!contains(v)) {
            throw new NoSuchElementException("vértice " + v + " não está na fila");
        }
        if (k > key[v]) {
            throw new IllegalArgumentException("nova chave maior que a atual");
        }
        key[v] = k;
        siftUp(pos[v]);
    }

    private void siftUp(int i) {
        while (i > 1 && key[heap[i]] < key[heap[i / 2]]) {
            swap(i, i / 2);
            i /= 2;
        }
    }

    private void siftDown(int i) {
        while (2 * i <= size) {
            int c = 2 * i;
            if (c < size && key[heap[c + 1]] < key[heap[c]]) {
                c++;
            }
            if (key[heap[i]] <= key[heap[c]]) {
                break;
            }
            swap(i, c);
            i = c;
        }
    }

    private void swap(int i, int j) {
        int t = heap[i];
        heap[i] = heap[j];
        heap[j] = t;
        pos[heap[i]] = i;
        pos[heap[j]] = j;
    }

    private void validate(int v) {
        if (v < 0 || v >= pos.length) {
            throw new IndexOutOfBoundsException("vértice " + v + " fora de [0, " + (pos.length - 1) + "]");
        }
    }
}
