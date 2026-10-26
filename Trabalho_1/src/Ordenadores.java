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

    public static <T extends Comparable<T>> void SelectionSort(T[] a, int n){
        for(int i = 0; i < n-1; i++){
            int min = i;
            for(int j = i+1; j<n; j++){
                if(a[j].compareTo(a[min])<0){
                    min = j;
                }
            }
            troca(a, i, min);
        }
    }

    public static <T extends Comparable<T>> void ShellSort(T[] a, int n) {
        int h = 1;
        do { h = 3 * h + 1; } while (h < n); 
        do {
            h /= 3;
            for (int i = h; i < n; i++) {
                T x = a[i];
                int j = i;
                while (j >= h && a[j - h].compareTo(x) > 0) {
                    a[j] = a[j - h];
                    j -= h;
                }
                a[j] = x;
            }
        } while (h != 1);
    }

    public static <T extends Comparable<T>> void QuickSort(int p, int r, T[] a) {
        T c = a[(p+r)/2];
        int i = p;
        int j = r;
        if (p < r){
            while(i<=j){
                while(a[i].compareTo(c)<0) i++;
               while(c.compareTo(a[j])<0) j--;
                if(i<=j){
                    troca(a,i,j);
                    i++;
                    j--;
                }
            }
            QuickSort(p, j, a);
            QuickSort(i, r, a);
        }
    }

    public static void BucketSort(Integer[] a) {
        int n = a.length;
        if (n <= 1) return;

        int min = a[0], max = a[0];
        for (int i = 0; i < n; i++) {
            if (a[i] < min) min = a[i];
            if (a[i] > max) max = a[i];
        }
        long faixa = (long) max - min + 1;

        int[] tam = new int[n];
        for (int i = 0; i < n; i++) {
            int idx = (int) ((long) (a[i] - min) * n / faixa);
            tam[idx]++;
        }

        Integer[][] b = new Integer[n][];
        for (int i = 0; i < n; i++) b[i] = new Integer[tam[i]];

        int[] pos = new int[n];
        for (int i = 0; i < n; i++) {
            int idx = (int) ((long) (a[i] - min) * n / faixa);
            b[idx][pos[idx]] = a[i];
            pos[idx]++;
        }

        int k = 0;
        for (int i = 0; i < n; i++) {
            InsertionSort(b[i], b[i].length);
            for (int j = 0; j < b[i].length; j++) {
                a[k] = b[i][j];
                k++;
            }
        }
    }

    // counting sort conta quantas vezes cada valor aparece e usa essas contagens para colocar cada elemento direto na posição final
    public static void CountingSort(Integer[] a) {
        int n = a.length;
        if (n <= 1) return;

        int min = a[0], max = a[0];
        for (int i = 0; i < n; i++) {
            if (a[i] < min) min = a[i];
            if (a[i] > max) max = a[i];
        }
        int k = max - min + 1; // tamanho da faixa de valores

        int[] c = new int[k];
        for (int i = 0; i < n; i++) {
            c[a[i] - min]++;
        }
        // soma acumulada: c[v] passa a ser quantos elementos são <= v
        for (int v = 1; v < k; v++) {
            c[v] += c[v - 1];
        }

        // percorre de trás pra frente para manter a ordem dos iguais (estável)
        Integer[] b = new Integer[n];
        for (int i = n - 1; i >= 0; i--) {
            c[a[i] - min]--;
            b[c[a[i] - min]] = a[i];
        }
        for (int i = 0; i < n; i++) {
            a[i] = b[i];
        }
    }

    // radix sort ordena dígito por dígito, do menos significativo para o mais, usando um counting sort estável em cada dígito
    public static void RadixSort(Integer[] a) {
        int n = a.length;
        if (n <= 1) return;

        // desloca pelo menor valor para funcionar também com negativos
        int min = a[0], max = a[0];
        for (int i = 0; i < n; i++) {
            if (a[i] < min) min = a[i];
            if (a[i] > max) max = a[i];
        }
        long maior = (long) max - min;

        Integer[] b = new Integer[n];
        for (long exp = 1; maior / exp > 0; exp *= 10) {
            countingPorDigito(a, b, min, exp);
        }
    }

    // counting sort estável em a usando só o dígito (a[i] - min) / exp % 10
    private static void countingPorDigito(Integer[] a, Integer[] b, int min, long exp) {
        int n = a.length;
        int[] c = new int[10];
        for (int i = 0; i < n; i++) {
            c[digito(a[i], min, exp)]++;
        }
        for (int d = 1; d < 10; d++) {
            c[d] += c[d - 1];
        }
        for (int i = n - 1; i >= 0; i--) {
            int d = digito(a[i], min, exp);
            c[d]--;
            b[c[d]] = a[i];
        }
        for (int i = 0; i < n; i++) {
            a[i] = b[i];
        }
    }

    private static int digito(int x, int min, long exp) {
        return (int) (((long) x - min) / exp % 10);
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
