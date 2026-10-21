public class Ordenadores {
    // bubblesort percorre todo o vetor comparando pares adjacentes e vai trocando até sobrar o maior elemento no fim do vetor
    public static <T extends Comparable<T>> void BubbleSort(T[] a, int n) {
        for (int i = 0; i < n - 1; i++) {
            boolean trocou = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (a[j].compareTo(a[j + 1]) > 0) {
                    troca(a, j, j + 1);
                    trocou = true;
                }
            }
            // nenhuma troca na passada: o vetor já está ordenado
            if (!trocou) {
                break;
            }
        }
    }

    public static <T extends Comparable<T>> void InsertionSort(T[] a, int n){
        for(int i = 1; i < n; i++){
            T chave = a[i];
            int j = i - 1;
            while(j>=0 && a[j].compareTo(chave)>0){
                a[j+1]= a[j];
                j = j-1;
            }
            a[j+1] = chave;
        }
    }

    // mergesort divide o vetor ao meio, ordena cada metade recursivamente e depois intercala as duas metades ordenadas
    public static <T extends Comparable<T>> void MergeSort(T[] a, int n) {
        // vetor auxiliar criado uma vez só e reaproveitado em todas as intercalações
        T[] aux = a.clone();
        mergeSort(a, aux, 0, n - 1);
    }

    private static <T extends Comparable<T>> void mergeSort(T[] a, T[] aux, int lo, int hi) {
        if (lo >= hi) {
            return; // 0 ou 1 elemento: já está ordenado
        }
        int meio = lo + (hi - lo) / 2;
        mergeSort(a, aux, lo, meio);
        mergeSort(a, aux, meio + 1, hi);
        intercala(a, aux, lo, meio, hi);
    }

    // junta a[lo..meio] e a[meio+1..hi], que já estão ordenados, em a[lo..hi]
    private static <T extends Comparable<T>> void intercala(T[] a, T[] aux, int lo, int meio, int hi) {
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k];
        }
        int i = lo;       // início da metade esquerda
        int j = meio + 1; // início da metade direita
        for (int k = lo; k <= hi; k++) {
            if (i > meio) {
                a[k] = aux[j++];      // esquerda acabou
            } else if (j > hi) {
                a[k] = aux[i++];      // direita acabou
            } else if (aux[i].compareTo(aux[j]) <= 0) {
                a[k] = aux[i++];      // <= mantém a ordem dos iguais (estável)
            } else {
                a[k] = aux[j++];
            }
        }
    }

    // heapsort monta um heap máximo no vetor e vai tirando o maior (a raiz) para o fim, refazendo o heap no que sobra
    public static <T extends Comparable<T>> void HeapSort(T[] a, int n) {
        // constrói o heap: começa do último nó que tem filho e desce até a raiz
        for (int i = n / 2 - 1; i >= 0; i--) {
            desce(a, i, n);
        }
        // a raiz a[0] é o maior: troca com o último e refaz o heap no restante
        for (int fim = n - 1; fim > 0; fim--) {
            troca(a, 0, fim);
            desce(a, 0, fim);
        }
    }

    // desce a[i] no heap a[0..tam-1] até ele ficar maior ou igual aos filhos
    private static <T extends Comparable<T>> void desce(T[] a, int i, int tam) {
        while (2 * i + 1 < tam) {
            int filho = 2 * i + 1; // filho da esquerda
            if (filho + 1 < tam && a[filho + 1].compareTo(a[filho]) > 0) {
                filho++;           // filho da direita é maior
            }
            if (a[i].compareTo(a[filho]) >= 0) {
                break;             // já respeita o heap
            }
            troca(a, i, filho);
            i = filho;
        }
    }

    // ---------------- métodos auxiliares ----------------
//troca pra usar no bubble
    private static <T> void troca(T[] a, int i, int j) {
        T tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }

    public static <T extends Comparable<T>> boolean estaOrdenado(T[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i - 1].compareTo(a[i]) > 0) {
                return false;
            }
        }
        return true;
    }
}
