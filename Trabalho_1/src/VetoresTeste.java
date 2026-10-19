
public class VetoresTeste {

    public static final String[] NOMES = {"crescente", "decrescente", "aleatorio", "repetidos"};

    private static final int[][] CASOS = {
        {1, 2, 3, 4, 5, 6, 7, 8, 9, 10},                     // crescente (par)
        {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1}, // decrescente (ímpar)
        {7, 3, 12, 1, 9, 5, 11, 2, 8, 4, 10, 6, 13},         // aleatório (ímpar)
        {4, 2, 4, 1, 2, 4, 3, 1, 2, 4, 3, 1},                // repetidos (par)
    };

    public static int quantidade() {
        return CASOS.length;
    }

    // cópia como Integer[], para os algoritmos genéricos
    public static Integer[] copiaInteger(int k) {
        Integer[] v = new Integer[CASOS[k].length];
        
        for (int i = 0; i < v.length; i++) {
            v[i] = CASOS[k][i];
        }
        return v;
    }

    // cópia como int[], para Counting, Radix e Bucket Sort
    public static int[] copiaInt(int k) {
        return CASOS[k].clone();
    }
}
