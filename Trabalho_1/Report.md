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
resultados_experimento.csv guarda os tempos medidos, no formato Algoritmo,Tamanho,Caso,TempoMs

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
- Radix Sort: no caso aleatório d = 7 dígitos. Nos casos crescente e decrescente d depende de n (ex.: n = 1.000.000 → 6 dígitos).
- Bucket Sort: a distribuição uniforme do caso aleatório é o melhor cenário para ele. Nos casos crescente e decrescente cada balde recebe exatamente um elemento.

Por isso a comparação dos lineares com os algoritmos de comparação só é justa nessas condições (inteiros, faixa de valores conhecida e limitada). Com uma faixa bem maior, o Counting Sort ficaria inviável de memória, e o Radix Sort ficaria mais lento com mais dígitos.

## Metodologia do experimento ##

O VetoresExperimento roda os tamanhos 1.000, 10.000, 100.000 e 1.000.000. Para cada tamanho são gerados três vetores:
- crescente: 0, 1, 2, ..., n-1
- decrescente: n, n-1, ..., 1
- aleatório: n valores sorteados entre 0 e 9.999.999

Todos os algoritmos recebem um clone do mesmo vetor, então dentro de uma execução do programa todos ordenam exatamente a mesma entrada.

O tempo é medido com System.nanoTime() só em volta da chamada de ordenação, então a geração dos vetores, a cópia e a impressão não entram na medição. O tempo é convertido para milissegundos e salvo no CSV.

Como os algoritmos O(n²) demoram muito nos vetores grandes, o programa salva cada resultado no CSV logo que termina, e antes de rodar uma combinação ele verifica se ela já está no arquivo e pula se estiver. Assim dá pra parar e continuar o experimento depois sem perder o que já rodou.

Hardware e software utilizados: (preencher: processador, memória RAM, sistema operacional, versão do Java)

## Pendências ##

Coisas que ainda faltam para atender a especificação do trabalho:

- Repetições: hoje cada combinação algoritmo × tamanho × caso roda uma vez só. A especificação pede pelo menos 10 execuções (30 recomendadas), guardando todos os tempos individuais. A verificação "já existe no CSV" teria que considerar o número da repetição.
- Aquecimento da JVM: a especificação recomenda rodar algumas execuções antes da coleta, que não entram nos resultados. Dá pra ver o efeito disso nos dados atuais: o Bubble Sort no vetor crescente levou 1,65 ms com n = 1.000 e só 0,06 ms com n = 10.000, ou seja, a primeira execução foi mais lenta mesmo com 10 vezes menos elementos.
- Mesma entrada entre execuções: o vetor aleatório é gerado com new Random() sem semente. Se o programa for interrompido e rodado de novo, os algoritmos que faltavam recebem um vetor aleatório diferente dos que já tinham rodado. Usar uma semente fixa (ex.: new Random(42)) resolve isso.
- O experimento parou no Selection Sort com n = 100.000 (falta o caso aleatório e os algoritmos seguintes, e o tamanho 1.000.000 inteiro). Para os O(n²) com n = 1.000.000 o tempo esperado é de horas; a especificação permite usar só um dos tamanhos maiores nos casos crescente e decrescente se isso bastar para a análise.
- Estatísticas (média, mediana, mínimo, máximo, desvio-padrão) e gráficos.
- Preencher hardware e software.

## Observações preliminares ##

Com base em uma única execução por combinação (então os números ainda podem variar):

- Os algoritmos O(n²) crescem mais ou menos 100 vezes quando n cresce 10 vezes, como esperado. Caso aleatório de 10.000 para 100.000: Bubble 318 ms → 39.065 ms, Insertion 88 ms → 14.025 ms, Selection 164 ms → 16.190 ms.
- O Merge Sort cresce mais ou menos 11 vezes no mesmo intervalo (8,6 ms → 97,8 ms), perto do esperado para n log n (10 · log(100.000)/log(10.000) ≈ 12,5).
- O Selection Sort leva praticamente o mesmo tempo nos três casos (≈ 163 ms com n = 10.000), confirmando que ele não depende da ordem da entrada.
- Bubble Sort (com a flag) e Insertion Sort são quase instantâneos no vetor crescente (menos de 1,2 ms com n = 100.000), que é o melhor caso O(n) deles.
- O Quick Sort com pivô no meio foi mais rápido nos vetores crescente e decrescente do que no aleatório, como esperado pela escolha do pivô.
