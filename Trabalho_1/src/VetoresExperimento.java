import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

public class VetoresExperimento {

    interface Ordenador {
        void ordenar(Integer[] v);
    }
    
    public static final String[] NOMES_CASOS = {"crescente", "decrescente", "aleatorio"};

    public static final String ARQUIVO_CSV = "resultados_experimento.csv";

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
            if (experimentoJaExiste(nomeAlgoritmo, Casos[j].length, NOMES_CASOS[j])) {
                    System.out.println("[PULADO] " + nomeAlgoritmo + " | " + Casos[j].length + " | " + NOMES_CASOS[j]);
                    continue;
            }

            Integer[] vetorParaOrdenar = Casos[j].clone(); 

            long inicio = System.nanoTime();
            
            sort.ordenar(vetorParaOrdenar);
            
            long fim = System.nanoTime();
            double tempoMs = (fim - inicio) / 1_000_000.0;

            System.out.println("Tamanho: " + Casos[j].length + 
                                " | Caso: " + NOMES_CASOS[j] + " | Tempo: " + tempoMs + " ms | "+nomeAlgoritmo);
            salvarNoCSV(nomeAlgoritmo, Casos[j].length, NOMES_CASOS[j], tempoMs);
        }
    }

    public static void salvarNoCSV(String algoritmo, int tamanho, String caso, double tempoMs) {
        // O parâmetro 'true' no FileWriter garante que os novos dados sejam ADICIONADOS ao final do arquivo (append)
        try (FileWriter fw = new FileWriter(ARQUIVO_CSV, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            // Escreve os campos separados por vírgula (formato padrão CSV)
            pw.println(algoritmo + "," + tamanho + "," + caso + "," + tempoMs);
            
        } catch (IOException e) {
            System.err.println("Erro ao salvar no arquivo CSV: " + e.getMessage());
        }
    }

    public static boolean experimentoJaExiste(String algoritmo, int tamanho, String caso){
        Path path = Path.of(ARQUIVO_CSV);
        
        if (!Files.exists(path)) {
            return false;
        }

        try {
            List<String> linhas = Files.readAllLines(path);
            
            String chaveBusca = algoritmo + "," + tamanho + "," + caso;

            // Percorre as linhas (pulando o cabeçalho no índice 0)
            for (int i = 1; i < linhas.size(); i++) {
                String linha = linhas.get(i).trim();
                
                // Se a linha começar com a nossa chave, significa que já foi rodada!
                if (linha.startsWith(chaveBusca)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo CSV para verificação: " + e.getMessage());
        }

        return false;
    }

    public static void main(String[] args) {
        int[] tamanhos = {1000, 10000, 100000, 1000000};

        Path path = Path.of(ARQUIVO_CSV);
        if (!Files.exists(path)) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO_CSV, false))) {
                pw.println("Algoritmo,Tamanho,Caso,TempoMs");
            } catch (IOException e) {
                System.err.println("Erro ao criar o cabeçalho do CSV: " + e.getMessage());
            }
        }

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
            RodarTestes("Counting Sort", v -> Ordenadores.CountingSort(v), Casos);
            RodarTestes("Radix Sort", v -> Ordenadores.RadixSort(v), Casos);
        }
    }
}