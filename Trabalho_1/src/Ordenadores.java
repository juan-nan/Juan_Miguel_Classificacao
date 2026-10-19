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
