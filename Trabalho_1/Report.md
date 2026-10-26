Report de atividades


Para a entrada dos algoritmos vamos usar valores inteiros não negativos (o vetor crescente começa em 0 e o aleatório sorteia valores entre 0 e 9.999.999)


Para validar inicialmente os algoritmos de ordenação criamos uma classe VetoresTeste
que tem alguns vetores de exemplo para os algoritmos ordenarem

        {1, 2, 3, 4, 5, 6, 7, 8, 9, 10},                     // crescente (par)
        {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1}, // decrescente (ímpar)
        {7, 3, 12, 1, 9, 5, 11, 2, 8, 4, 10, 6, 13},         // aleatório (ímpar)
        {4, 2, 4, 1, 2, 4, 3, 1, 2, 4, 3, 1},                // repetidos (par)

Deixamos um exemplo crescente, decrescente, aleatório e repetido para validar os algoritmos com amostras diversas. Os vetores têm entre 10 e 15 elementos, com tamanhos pares e ímpares, como pede a especificação.

No TesteCorretude, cada algoritmo vai receber uma cópia de cada vetor do VetoresTeste.java, ordena essa cópia e depois o resultado é verificado pelo método estaOrdenado, que verifica se cada elemento é menor ou igual ao próximo. Para cada vetor é mostrado o vetor antes e depois da ordenação.

Usamos cópias pq os algoritmos modificam os vetores durante a ordenação, assim a execução de um algoritmo não altera os dados usados pelos outros, e todos vao receber a mesma entrada.

Os dez algoritmos passaram nos quatro vetores de teste (todos "OK" no TesteCorretude).

## Organização ##

Os arquivos se encontram na pasta src

Ordenadores.java contém a implementacao dos algoritmos 
VetoresTeste.java contém os vetores pequenos usados para validação
TesteCorretude executa os testes de corretude de cada algoritmo
VetoresExperimento.java gera os vetores grandes, mede o tempo de cada algoritmo e salva os resultados
resultados_experimento.csv guarda o tempo de cada execução, uma por linha, no formato Algoritmo,Tamanho,Caso,Repeticao,TempoMs
resultados_organizados.csv é gerado no fim do experimento: uma linha por algoritmo × tamanho × caso, com os tempos das 10 repetições lado a lado e as estatísticas
resultados_preliminares.csv guarda os primeiros tempos, de antes das repetições e do aquecimento (não entram nos resultados finais)

Os algoritmos baseados em comparação foram implementados de forma genérica usando a interface Comparable, como foi recomendado nas especificações do trabalho.
Counting Sort, Radix Sort e Bucket Sort não comparam os elementos entre si, eles usam o valor do número para decidir a posição. Por isso eles recebem um Integer[] em vez de um T[] genérico.

## Algoritmos ##

### Baseados em comparação ###

BubbleSort: Percorre o vetor comparando pares vizinhos e trocando quando estão fora de ordem, a cada passada o maior elemento vai para o fim. Usamos uma flag "trocou" que encerra o algoritmo quando uma passada nao faz nenhuma troca.
Complexidade: melhor caso O(n) (vetor já ordenado, graças à flag), caso médio e pior caso O(n²). É estável e ordena no próprio vetor.

InsertionSort: Percorre o vetor da esquerda para a direita, pega cada elemento (a "chave") e vai deslocando os maiores que ele uma posição para a direita até achar o lugar certo, como quem organiza cartas na mão.
Complexidade: melhor caso O(n) (vetor crescente, nenhum deslocamento), caso médio e pior caso O(n²) (vetor decrescente). É estável.

SelectionSort: A cada passada procura o menor elemento do trecho que ainda não foi ordenado e troca ele com a primeira posição desse trecho.
Complexidade: O(n²) em todos os casos, porque sempre percorre o resto do vetor inteiro para achar o mínimo, não importa a ordem da entrada. Faz no máximo n-1 trocas. Não é estável.

ShellSort: É uma generalização do Insertion Sort, primeiro ordena elementos que estão a uma distância h uns dos outros e vai diminuindo h até chegar em 1 (que é um Insertion Sort normal, mas num vetor quase ordenado). Usamos a sequência de Knuth (h = 3h + 1: 1, 4, 13, 40, ...).
Complexidade: depende da sequência de h; com a de Knuth o pior caso é O(n^1,5). Não é estável.

MergeSort: Divide o vetor no meio e ordena cada metade recursivamente, e junta as duas metades ordenadas, o vetor auxiliar é criado uma única vez e reaproveitado em todas as intercalações.
Complexidade: O(n log n) em todos os casos, usa O(n) de memória extra. É estável (na intercalação usamos <= para manter a ordem dos iguais).

HeapSort: Monta um heap máximo no próprio vetor e, repetidamente, troca a raiz com o ultimo elemento do heap e refaz o heap no restante.
Complexidade: O(n log n) em todos os casos, sem memória extra. Não é estável.

QuickSort: Escolhe um pivô (usamos o elemento do meio), separa o vetor em uma parte com os menores que o pivô e outra com os maiores, e ordena cada parte recursivamente.
Complexidade: melhor caso e caso médio O(n log n), pior caso O(n²) quando o pivô divide o vetor de forma muito desigual. Como o pivô é o elemento do meio, os vetores crescente e decrescente são casos bons (o pivô cai bem no meio dos valores), diferente da versão que usa o primeiro ou o último elemento como pivô. Não é estável.

### Tempo linear ###

BucketSort: Calcula o mínimo e o máximo, cria n baldes e distribui cada elemento no balde proporcional ao seu valor ((valor - min) * n / faixa). Depois ordena cada balde com Insertion Sort e junta os baldes em ordem.
Complexidade: caso médio O(n) quando os valores estão bem distribuídos na faixa (cada balde fica com poucos elementos), pior caso O(n²) quando muitos elementos caem no mesmo balde. Usa O(n) de memória extra.

CountingSort: Calcula o mínimo e o máximo, conta quantas vezes cada valor aparece em um vetor de contagem de tamanho k = max - min + 1, faz a soma acumulada (assim cada posição diz quantos elementos são menores ou iguais àquele valor) e coloca cada elemento direto na posição final em um vetor auxiliar. O vetor é percorrido de trás pra frente nessa última etapa para o algoritmo ser estável.
Complexidade: O(n + k) de tempo e de memória. Só vale a pena quando k não é muito maior que n.

RadixSort: Ordena os números dígito por dígito em base 10, do menos significativo para o mais significativo. Em cada dígito usa um Counting Sort estável com só 10 posições (dígitos 0 a 9). A estabilidade é o que garante que a ordem dos dígitos anteriores não é perdida.
Complexidade: O(d · (n + 10)), onde d é a quantidade de dígitos do maior valor (max - min). Usa O(n) de memória extra.

Nos três algoritmos lineares os valores são deslocados pelo mínimo (valor - min), então eles também funcionariam com números negativos, mesmo a gente só usando valores não negativos.

## Condições dos algoritmos de tempo linear ##

Esses algoritmos dependem das propriedades dos dados, então é importante deixar claro em que condições eles rodaram:

- Os dados são inteiros. Nenhum dos três funciona direto com qualquer tipo Comparable.
- No caso aleatório os valores estão entre 0 e 9.999.999, sorteados de forma uniforme.
- Counting Sort: no caso aleatório k ≈ 10.000.000 para qualquer tamanho de vetor. Para n = 1.000 isso significa um vetor de contagem 10.000 vezes maior que o vetor de entrada (≈ 40 MB), então o custo é dominado por k e não por n. Nos casos crescente e decrescente k ≈ n, que é a situação ideal.
- Radix Sort: no caso aleatório d = 7 dígitos. Nos casos crescente e decrescente d depende de n (ex.: n = 100.000 → 5 dígitos).
- Bucket Sort: a distribuição uniforme do caso aleatório é o melhor cenário para ele. Nos casos crescente e decrescente cada balde recebe exatamente um elemento.

Por isso a comparação dos lineares com os algoritmos de comparação só é justa nessas condições (inteiros, faixa de valores conhecida e limitada). Com uma faixa bem maior, o Counting Sort ficaria inviável de memória, e o Radix Sort ficaria mais lento com mais dígitos.

## Metodologia do experimento ##

O VetoresExperimento roda os tamanhos 1.000, 10.000 e 100.000 (constante TAMANHOS). Para cada tamanho são gerados três vetores:
- crescente: 0, 1, 2, ..., n-1
- decrescente: n, n-1, ..., 1
- aleatório: n valores sorteados entre 0 e 9.999.999

Os algoritmos ficam em dois vetores (NOMES_ALGORITMOS e ALGORITMOS), e o main percorre eles para cada tamanho. Assim os dez algoritmos rodam sempre na mesma ordem e com o mesmo código de medição.

O tempo é medido com System.nanoTime() só em volta da chamada de ordenação, então a geração dos vetores, a cópia e a impressão não entram na medição. O tempo é convertido para milissegundos e salvo no CSV.

Como os algoritmos O(n²) demoram muito nos vetores grandes, o programa salva cada tempo no CSV logo que termina. Antes de rodar uma repetição, ele verifica se ela já está no arquivo e pula se estiver. Assim dá pra parar e continuar o experimento depois sem perder o que já rodou.

No fim, o método GerarCsvOrganizado lê o resultados_experimento.csv e monta o resultados_organizados.csv, no formato Algoritmo,Tamanho,Caso,Rep1,...,Rep10,Media,Mediana,Minimo,Maximo,DesvioPadrao. O desvio-padrão é o amostral (divide por n - 1). Uma configuração só entra nesse arquivo quando as 10 repetições já rodaram, para uma repetição faltando não entrar como zero na média.

### Por que não usamos 1.000.000 de elementos ###

A especificação sugere tamanhos da ordem de 1.000 a 1.000.000, mas permite usar tamanhos diferentes conforme o tempo e os computadores disponíveis (seção 2.2). Pelos dados preliminares, o tempo dos algoritmos O(n²) cresce cerca de 100 vezes quando n cresce 10 vezes. Com n = 100.000, o Bubble Sort já levou cerca de 39 s numa execução do caso aleatório, então com n = 1.000.000 seria perto de 1 hora por execução. Com 10 repetições nos casos decrescente e aleatório, só o Bubble Sort levaria cerca de 20 horas, e o Insertion e o Selection somariam mais de 20 horas. Por isso o maior tamanho do experimento é 100.000.

Hardware e software utilizados: (preencher: processador, memória RAM, sistema operacional, versão do Java)

## Requisitos da especificação e como cumprimos ##

### Mesmos dados de entrada para todos os algoritmos (seções 2.3 e 5) ###

A especificação pede que todos os algoritmos de uma configuração recebam exatamente a mesma entrada, e que cada execução use uma cópia do vetor original.

Como cumprimos:
- Os três vetores de cada tamanho são gerados uma vez só (CriarCasos) e cada execução recebe um clone (Casos[j].clone()). A cópia é feita antes do nanoTime, então não entra no tempo.
- O vetor aleatório é gerado com uma semente fixa (SEMENTE = 42, new Random(SEMENTE)). Antes usávamos new Random() sem semente, e se o programa fosse interrompido e rodado de novo, os algoritmos que faltavam recebiam um vetor aleatório diferente dos que já tinham rodado. Com a semente fixa, o vetor aleatório de cada tamanho é sempre o mesmo, em qualquer execução do programa e em qualquer computador.

### Repetições (seção 5) ###

A especificação pede pelo menos 10 execuções de cada combinação algoritmo × tamanho × caso, com todos os tempos individuais registrados.

Como cumprimos:
- A constante REPETICOES = 10 define quantas vezes cada combinação roda. No RodarTestes, para cada caso tem um laço de 1 a REPETICOES, e cada repetição recebe uma cópia nova do vetor.
- Cada tempo individual é salvo numa linha do CSV, junto com o número da repetição. O formato agora é Algoritmo,Tamanho,Caso,Repeticao,TempoMs.
- A verificação de "já existe no CSV" também considera a repetição, então se o programa parar no meio ele continua da repetição em que parou. A chave de busca termina com vírgula para a repetição 1 não ser confundida com a 10.
- Se der tempo, dá pra aumentar para 30 repetições (o recomendado) só mudando a constante.

### Aquecimento da JVM (seção 4) ###

A especificação recomenda fazer algumas execuções antes da coleta, para diminuir a influência das primeiras execuções na JVM, e diz que esses tempos não devem entrar nos resultados.

Como cumprimos:
- Antes de começar o experimento, o método Aquecer roda cada um dos dez algoritmos 5 vezes (EXECUCOES_AQUECIMENTO) num vetor aleatório de 10.000 elementos (TAMANHO_AQUECIMENTO). Ele usa outra semente, então não é o mesmo vetor das medições.
- Esses tempos não são medidos nem salvos.
- O motivo: no começo a JVM interpreta o código e só depois de ele ser executado várias vezes o compilador JIT gera código nativo otimizado. Sem o aquecimento, as primeiras medições pegam o código ainda não otimizado. Nos dados preliminares, sem aquecimento, o Bubble Sort no vetor crescente levou 1,65 ms com n = 1.000 e só 0,06 ms com n = 10.000. Num teste rápido com o aquecimento, o mesmo caso com n = 1.000 caiu para cerca de 0,03 ms.

### Medição só da ordenação (seção 4) ###

O System.nanoTime() é chamado logo antes e logo depois do sort.ordenar(...). A geração dos vetores, as cópias, a impressão e a gravação no CSV ficam fora do intervalo medido.

## Pendências ##

- Rodar o experimento completo com as 10 repetições.
- Gráficos (as estatísticas já saem no resultados_organizados.csv).
- Preencher hardware e software.

## Observações preliminares ##

Os primeiros tempos, de antes das repetições e do aquecimento, ficaram guardados em src/resultados_preliminares.csv. Eles têm uma execução só por combinação e não devem entrar nos resultados finais, mas já mostram algumas tendências:

- Os algoritmos O(n²) crescem mais ou menos 100 vezes quando n cresce 10 vezes, como esperado. Caso aleatório de 10.000 para 100.000: Bubble 318 ms → 39.065 ms, Insertion 88 ms → 14.025 ms. O Selection Sort não chegou a rodar o aleatório com 100.000, mas no crescente foi de 163 ms → 16.190 ms.
- O Merge Sort cresce mais ou menos 11 vezes no mesmo intervalo (8,6 ms → 97,8 ms), perto do esperado para n log n (10 · log(100.000)/log(10.000) ≈ 12,5).
- O Selection Sort leva praticamente o mesmo tempo nos três casos (≈ 163 ms com n = 10.000), confirmando que ele não depende da ordem da entrada.
- Bubble Sort (com a flag) e Insertion Sort são quase instantâneos no vetor crescente (menos de 1,2 ms com n = 100.000), que é o melhor caso O(n) deles.
- O Quick Sort com pivô no meio foi mais rápido nos vetores crescente e decrescente do que no aleatório, como esperado pela escolha do pivô.
