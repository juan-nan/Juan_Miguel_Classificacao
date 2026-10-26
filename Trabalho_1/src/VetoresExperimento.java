import java.util.Random;

public class VetoresExperimento {

    interface Ordenador {
        void ordenar(Integer[] v);
    }
    
    public static final String[] NOMES_CASOS = {"crescente", "decrescente", "aleatorio"};

    public static Integer[][] CriarCasos(int tamanho) {
        Random random = new Random();
        
        Integer[][] casos = new Integer[3][tamanho];

        for (int i = 0; i < tamanho; i++) {
            casos[0][i] = i;
        }

        for (int i = 0; i < tamanho; i++) {
            casos[1][i] = tamanho - i;
        }

        casos[2] = random.ints(tamanho, 0, 10_000_000)
                                 .boxed()
                                 .toArray(Integer[]::new); 

        return casos;
    }

    public static void RodarTestes(String nomeAlgoritmo, Ordenador sort, Integer[][] Casos){
            for(int j = 0; j < 3; j++){
                Integer[] vetorParaOrdenar = Casos[j].clone(); 

                long inicio = System.nanoTime();
                
                sort.ordenar(vetorParaOrdenar);
                
                long fim = System.nanoTime();
                double tempoMs = (fim - inicio) / 1_000_000.0;

                System.out.println("Tamanho: " + Casos[j].length + 
                                   " | Caso: " + NOMES_CASOS[j] + " | Tempo: " + tempoMs + " ms | "+nomeAlgoritmo);
            }
    }
    public static void main(String[] args) {
        int[] tamanhos = {1000, 10000, 100000, 1000000};

        for (int i = 0; i < tamanhos.length; i++) {
            int tamanhoAtual = tamanhos[i];
            
            Integer[][] Casos = CriarCasos(tamanhoAtual);
        
            RodarTestes("Bubble Sort", v -> Ordenadores.BubbleSort(v, v.length), Casos);
            RodarTestes("Insertion Sort", v -> Ordenadores.InsertionSort(v, v.length), Casos);
            RodarTestes("Merge Sort", v -> Ordenadores.MergeSort(v, v.length), Casos);
            RodarTestes("Heap Sort", v -> Ordenadores.HeapSort(v, v.length), Casos);
            RodarTestes("Selection Sort", v -> Ordenadores.SelectionSort(v, v.length), Casos);
            RodarTestes("Shell Sort", v -> Ordenadores.ShellSort(v, v.length), Casos);
            RodarTestes("Quick Sort", v -> Ordenadores.QuickSort(0, v.length - 1, v), Casos);
            RodarTestes("Bucket Sort", v -> Ordenadores.BucketSort(v), Casos);
        }
    }
}