Report de atividades


Para a entrada dos algoritmos vamos usar valores inteiros positivos


Para validar inicialmente os algoritmos de ordenação criamos uma classe VetoresTeste
que tem alguns vetores de exemplo para os algoritmos ordenarem

        {1, 2, 3, 4, 5, 6, 7, 8, 9, 10},                     // crescente (par)
        {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1}, // decrescente (ímpar)
        {7, 3, 12, 1, 9, 5, 11, 2, 8, 4, 10, 6, 13},         // aleatório (ímpar)
        {4, 2, 4, 1, 2, 4, 3, 1, 2, 4, 3, 1},                // repetidos (par)

Deixamos um exemplo crescente, decrescente, aleatório e repetido para validar os algoritmos com amostras diversas.

No TesteCorretude, cada algoritmo vai receber uma cópia de cada vetor do VetoresTeste.java, ordena essa cópia e depois o resultado é verificado pelo método estaOrdenado, que verifica se se cada elemento é menor ou igual ao próximo. Para cada vetor é mostrado o vetor antes e depois da ordenação.

Usamos cópias pq os algoritmos modificam os vetores durante a ordenação, assim a execução de um algoritmo não altera os dados usados pelos outros, e todos vao receber a mesma entrada.

## Organização ##

Os arquivos se encontram na pasta src

Ordenadores.java contém a implementacao dos algoritmos 
VetoresTeste.java contém os vetores pequenos usados para validação
TesteCorretude executa os testes de corretude de cada algoritmo

Os algoritmos foram implementados de forma genérica usando a interface Comparable, como foi recomendado nas especificações do trabalho

## Algoritmos ##

BubbleSort: Percorre o vetor comparando pares vizinhos e trocando quando estão fora de ordem, a cada passada o maior elemento vai para o fim. Usamos uma flag "trocou" que encerra o algoritmo quando uma passada nao faz nenhuma troca

MergeSort: Divide o vetor no meio e ordena cada metade recursivamente, e junta as duas metades ordenadas, o vetor auxiliar é criado uma única vez e reaproveitado em todas as intercalações.

HeapSort: Monta um heap máximo no próprio vetor e, repetidamente, troca a raiz com o ultimo elemento do heap e refaz o heap no restante.
