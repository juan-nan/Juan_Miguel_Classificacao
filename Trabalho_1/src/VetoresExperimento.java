import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class VetoresExperimento {

    interface Ordenador {
        void ordenar(Integer[] v);
    }
    
    public static final String[] NOMES_CASOS = {"crescente", "decrescente", "aleatorio"};

    public static final String ARQUIVO_CSV = "resultados_experimento.csv";

    // uma linha por algoritmo x tamanho x caso, com o tempo de cada repetição lado a lado
    public static final String ARQUIVO_CSV_ORGANIZADO = "resultados_organizados.csv";

    // 1.000.000 ficou de fora: os algoritmos O(n²) levariam horas por execução
    public static final int[] TAMANHOS = {1000, 10000, 100000};

    // cada combinação algoritmo x tamanho x caso roda essa quantidade de vezes (a especificação pede no mínimo 10)
    public static final int REPETICOES = 10;

    // semente fixa: o vetor aleatório de cada tamanho é sempre o mesmo, mesmo parando e rodando o programa de novo
    public static final long SEMENTE = 42;

    // execuções de aquecimento da JVM antes da coleta, os tempos delas não são salvos
    public static final int EXECUCOES_AQUECIMENTO = 5;
    public static final int TAMANHO_AQUECIMENTO = 10_000;

    public static final String[] NOMES_ALGORITMOS = {
        "Bubble Sort", "Insertion Sort", "Merge Sort", "Heap Sort", "Selection Sort",
        "Shell Sort", "Quick Sort", "Bucket Sort", "Counting Sort", "Radix Sort"
    };

    public static final Ordenador[] ALGORITMOS = {
        v -> Ordenadores.BubbleSort(v, v.length),
        v -> Ordenadores.InsertionSort(v, v.length),
        v -> Ordenadores.MergeSort(v, v.length),
        v -> Ordenadores.HeapSort(v, v.length),
        v -> Ordenadores.SelectionSort(v, v.length),
        v -> Ordenadores.ShellSort(v, v.length),
        v -> Ordenadores.QuickSort(0, v.length - 1, v),
        v -> Ordenadores.BucketSort(v),
        v -> Ordenadores.CountingSort(v),
        v -> Ordenadores.RadixSort(v)
    };

    public static Integer[][] CriarCasos(int tamanho) {
        Random random = new Random(SEMENTE);
        
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
            for (int rep = 1; rep <= REPETICOES; rep++) {
                if (experimentoJaExiste(nomeAlgoritmo, Casos[j].length, NOMES_CASOS[j], rep)) {
                        System.out.println("[PULADO] " + nomeAlgoritmo + " | " + Casos[j].length + " | " + NOMES_CASOS[j] + " | rep " + rep);
                        continue;
                }

                // cópia nova a cada repetição, feita fora da medição
                Integer[] vetorParaOrdenar = Casos[j].clone(); 

                long inicio = System.nanoTime();
                
                sort.ordenar(vetorParaOrdenar);
                
                long fim = System.nanoTime();
                double tempoMs = (fim - inicio) / 1_000_000.0;

                System.out.println("Tamanho: " + Casos[j].length + 
                                    " | Caso: " + NOMES_CASOS[j] + " | Rep: " + rep + " | Tempo: " + tempoMs + " ms | "+nomeAlgoritmo);
                salvarNoCSV(nomeAlgoritmo, Casos[j].length, NOMES_CASOS[j], rep, tempoMs);
            }
        }
    }

    // roda cada algoritmo algumas vezes num vetor aleatório para a JVM compilar o código (JIT) antes da coleta
    public static void Aquecer() {
        Integer[] base = new Random(SEMENTE + 1).ints(TAMANHO_AQUECIMENTO, 0, 10_000_000)
                                                .boxed()
                                                .toArray(Integer[]::new);
        for (int a = 0; a < ALGORITMOS.length; a++) {
            System.out.println("[AQUECIMENTO] " + NOMES_ALGORITMOS[a]);
            for (int e = 0; e < EXECUCOES_AQUECIMENTO; e++) {
                ALGORITMOS[a].ordenar(base.clone());
            }
        }
    }

    public static void salvarNoCSV(String algoritmo, int tamanho, String caso, int repeticao, double tempoMs) {
        // O parâmetro 'true' no FileWriter garante que os novos dados sejam ADICIONADOS ao final do arquivo (append)
        try (FileWriter fw = new FileWriter(ARQUIVO_CSV, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            // Escreve os campos separados por vírgula (formato padrão CSV)
            pw.println(algoritmo + "," + tamanho + "," + caso + "," + repeticao + "," + tempoMs);
            
        } catch (IOException e) {
            System.err.println("Erro ao salvar no arquivo CSV: " + e.getMessage());
        }
    }

    public static boolean experimentoJaExiste(String algoritmo, int tamanho, String caso, int repeticao){
        Path path = Path.of(ARQUIVO_CSV);
        
        if (!Files.exists(path)) {
            return false;
        }

        try {
            List<String> linhas = Files.readAllLines(path);
            
            // a vírgula no final evita que a repetição 1 case com a 10
            String chaveBusca = algoritmo + "," + tamanho + "," + caso + "," + repeticao + ",";

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
        Path path = Path.of(ARQUIVO_CSV);
        if (!Files.exists(path)) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO_CSV, false))) {
                pw.println("Algoritmo,Tamanho,Caso,Repeticao,TempoMs");
            } catch (IOException e) {
                System.err.println("Erro ao criar o cabeçalho do CSV: " + e.getMessage());
            }
        }

        Aquecer();

        for (int i = 0; i < TAMANHOS.length; i++) {
            int tamanhoAtual = TAMANHOS[i];
            
            Integer[][] Casos = CriarCasos(tamanhoAtual);
        
            for (int a = 0; a < ALGORITMOS.length; a++) {
                RodarTestes(NOMES_ALGORITMOS[a], ALGORITMOS[a], Casos);
            }
        }

        GerarCsvOrganizado();
    }

    // lê o CSV com uma linha por execução e monta o CSV organizado, com as repetições em colunas e as estatísticas no final
    public static void GerarCsvOrganizado() {
        Map<String, double[]> tempos = new HashMap<>();
        try {
            List<String> linhas = Files.readAllLines(Path.of(ARQUIVO_CSV));
            for (int i = 1; i < linhas.size(); i++) {
                String[] campos = linhas.get(i).trim().split(",");
                if (campos.length != 5) continue;
                String chave = campos[0] + "," + campos[1] + "," + campos[2];
                int rep = Integer.parseInt(campos[3]);
                if (rep < 1 || rep > REPETICOES) continue;
                tempos.computeIfAbsent(chave, k -> {
                    double[] t = new double[REPETICOES];
                    Arrays.fill(t, Double.NaN); // NaN marca repetição que ainda não rodou
                    return t;
                })[rep - 1] = Double.parseDouble(campos[4]);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo CSV: " + e.getMessage());
            return;
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO_CSV_ORGANIZADO, false))) {
            StringBuilder cabecalho = new StringBuilder("Algoritmo,Tamanho,Caso");
            for (int rep = 1; rep <= REPETICOES; rep++) {
                cabecalho.append(",Rep").append(rep);
            }
            cabecalho.append(",Media,Mediana,Minimo,Maximo,DesvioPadrao");
            pw.println(cabecalho);

            // mesma ordem do experimento: algoritmo, depois tamanho, depois caso
            for (String algoritmo : NOMES_ALGORITMOS) {
                for (int tamanho : TAMANHOS) {
                    for (String caso : NOMES_CASOS) {
                        String chave = algoritmo + "," + tamanho + "," + caso;
                        double[] t = tempos.get(chave);
                        // só entra no CSV organizado quando as REPETICOES execuções já rodaram
                        if (t == null || Arrays.stream(t).anyMatch(Double::isNaN)) continue;

                        StringBuilder linha = new StringBuilder(chave);
                        for (double x : t) {
                            linha.append(",").append(x);
                        }

                        double soma = 0;
                        for (double x : t) soma += x;
                        double media = soma / t.length;

                        double somaQuadrados = 0;
                        for (double x : t) somaQuadrados += (x - media) * (x - media);
                        double desvio = Math.sqrt(somaQuadrados / (t.length - 1)); // desvio-padrão amostral

                        double[] ordenado = t.clone();
                        Arrays.sort(ordenado); // só para a mediana, não faz parte do experimento
                        int meio = ordenado.length / 2;
                        double mediana = ordenado.length % 2 == 0 ? (ordenado[meio - 1] + ordenado[meio]) / 2 : ordenado[meio];

                        linha.append(String.format(Locale.US, ",%.6f,%.6f,%.6f,%.6f,%.6f",
                                media, mediana, ordenado[0], ordenado[ordenado.length - 1], desvio));
                        pw.println(linha);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar o CSV organizado: " + e.getMessage());
            return;
        }
        System.out.println("CSV organizado salvo em " + ARQUIVO_CSV_ORGANIZADO);
    }
}