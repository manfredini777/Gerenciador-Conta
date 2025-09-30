public class GeradorPrimos {

    /**
     * Verifica se um número é primo de forma otimizada.
     * @param numero O número (long) a ser verificado.
     * @return true se o número for primo, false caso contrário.
     */
    public static boolean isPrimo(long numero) {
        // 1. Casos base: números <= 1 não são primos.
        if (numero <= 1) {
            return false;
        }
        // 2. O número 2 é o único primo par.
        if (numero == 2) {
            return true;
        }
        // 3. Todos os outros números pares não são primos (otimização).
        if (numero % 2 == 0) {
            return false;
        }

        // 4. Verifica divisores ímpares a partir de 3.
        // A verificação só precisa ir até a raiz quadrada do número (grande otimização).
        // Se um número N tem um divisor maior que sua raiz quadrada,
        // ele obrigatoriamente terá um divisor menor.
        for (long i = 3; i <= Math.sqrt(numero); i += 2) {
            if (numero % i == 0) {
                // Encontrou um divisor, então não é primo.
                return false;
            }
        }

        // 5. Se o loop terminar sem encontrar divisores, o número é primo.
        return true;
    }

    public static void main(String[] args) {
        System.out.println("Iniciando a busca infinita por números primos...");
        System.out.println("Pressione Ctrl+C para parar a execução.");

        long numeroAtual = 2; // Começamos a busca a partir do primeiro número primo.

        // Loop infinito para testar todos os números.
        while (true) {
            if (isPrimo(numeroAtual)) {
                // Se o número for primo, exiba-o no console.
                System.out.println(numeroAtual);
            }

            // Vai para o próximo número.
            numeroAtual++;

            // Condição de parada de segurança: se o long estourar e virar negativo, pare.
            // Isso acontece depois de passar de Long.MAX_VALUE (9,223,372,036,854,775,807).
            if (numeroAtual < 0) {
                System.out.println("Atingido o valor máximo para long. Encerrando.");
                break;
            }
        }
    }
}