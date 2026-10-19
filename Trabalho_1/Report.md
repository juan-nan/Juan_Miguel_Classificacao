Para a entrada dos algoritmos vamos usar valores inteiros positivos



Para validar inicialmente os algoritmos de ordenação criamos uma classe VetoresTeste
que tem alguns vetores de exemplo para os algoritmos ordenarem

        {1, 2, 3, 4, 5, 6, 7, 8, 9, 10},                     // crescente (par)
        {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1}, // decrescente (ímpar)
        {7, 3, 12, 1, 9, 5, 11, 2, 8, 4, 10, 6, 13},         // aleatório (ímpar)
        {4, 2, 4, 1, 2, 4, 3, 1, 2, 4, 3, 1},                // repetidos (par)

Deixamos um exemplo crescente, decrescente, aleatório e repetido para validar os algoritmos com amostras diversas.

No TesteCorretude, 