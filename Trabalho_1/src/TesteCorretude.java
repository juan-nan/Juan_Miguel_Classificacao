import java.util.Arrays;

public class TesteCorretude {

    interface Ordenador {
        void ordenar(Integer[] v);
    }

    // cada algoritmo recebe uma cópia de cada vetor de VetoresTeste e é verificado
    static boolean testar(String nomeAlgoritmo, Ordenador sort) {

        boolean tudoOk = true;

        System.out.println(nomeAlgoritmo);

        for (int k = 0; k < VetoresTeste.quantidade(); k++) {

            Integer[] v = VetoresTeste.copiaInteger(k);
            Integer[] original = VetoresTeste.copiaInteger(k);
            sort.ordenar(v);
            boolean ok = Ordenadores.estaOrdenado(v);
            tudoOk &= ok;
            System.out.printf("  %-11s (n=%2d) %s%n", VetoresTeste.NOMES[k], v.length, ok ? "OK" : "FALHOU");

                System.out.println("antes:");
            for(int j =0; j<v.length; j++){
                    System.out.print((original[j])+",");
                }
                System.out.println("");
                System.out.println("depois: ");
                
            
            for(int j =0; j<v.length; j++){
                    System.out.print(v[j]+",");
            }
            System.out.println();
        }
        return tudoOk;
    }

    public static void main(String[] args) {
        testar("Bubble Sort", v -> Ordenadores.bubbleSort(v, v.length));
        // depois quando a gente for testar os outros algoritmos com vetores pequenos pra validacao 
        // só mudar o bubble ai e trocar os parametros
    }
}
