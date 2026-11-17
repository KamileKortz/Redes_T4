import java.util.Scanner;

public class CalculadoraIPv4 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("==========================================");
        System.out.println("       CALCULADORA IPv4      ");
        System.out.println("==========================================");

        while (true) {
            System.out.println("\nDigite 'sair' se quiser encerrar.");

            // Leitura e validação do endereço IPv4
            Integer ipInt = null;
            while (ipInt == null) {
                System.out.print("Endereço IPv4 (ex: 172.16.45.200): ");
                String entradaIp = scanner.nextLine().trim();

                if (entradaIp.equalsIgnoreCase("sair")) {
                    System.out.println("Encerrando aplicação...");
                    scanner.close();
                    return;
                }

                ipInt = parseIpParaInteiro(entradaIp);
                if (ipInt == null) {
                    System.out
                            .println("[ERRO] Endereço IPv4 inválido. Use 4 octetos entre 0 e 255 (ex: 192.168.1.10).");
                }
            }

            // Leitura e validação da máscara de rede (/prefixo ou decimal pontuada)
            Integer mascaraInt = null;
            while (mascaraInt == null) {
                System.out.print("Máscara de rede (ex: /20 ou 255.255.240.0): ");
                String entradaMascara = scanner.nextLine().trim();

                if (entradaMascara.equalsIgnoreCase("sair")) {
                    System.out.println("Encerrando aplicação...");
                    scanner.close();
                    return;
                }

                mascaraInt = parseMascaraParaInteiro(entradaMascara);
                if (mascaraInt == null) {
                    System.out.println(
                            "[ERRO] Máscara inválida. Informe um prefixo (/0 a /32) ou máscara com bits 1 contíguos.");
                }
            }

            // Processamento dos cálculos e exibição dos resultados
            exibirResultados(ipInt, mascaraInt);
        }
    }

    private static Integer parseIpParaInteiro(String ipStr) {
        // O parâmetro -1 garante que pontos extras no final (ex: "10.0.0.1.") não sejam
        // ignorados
        String[] octetos = ipStr.split("\\.", -1);
        if (octetos.length != 4) {
            return null;
        }

        int resultado = 0;
        for (String octetoStr : octetos) {
            // Garante que contém apenas dígitos (evita sinais como +1 ou -1)
            if (!octetoStr.matches("\\d{1,3}")) {
                return null;
            }

            int valor = Integer.parseInt(octetoStr);
            if (valor < 0 || valor > 255) {
                return null;
            }

            // Desloca os bits anteriores 8 posições à esquerda e insere o novo octeto
            resultado = (resultado << 8) | valor;
        }

        return resultado;
    }

    /**
     * Converte a máscara (em notação /prefixo ou decimal pontuada) para um inteiro
     * de 32 bits.
     * Valida se os bits 1 são contíguos da esquerda para a direita.
     */
    private static Integer parseMascaraParaInteiro(String mascaraStr) {
        // Caso 1: Notação de prefixo (ex: /20)
        if (mascaraStr.startsWith("/")) {
            String prefixoStr = mascaraStr.substring(1);
            if (!prefixoStr.matches("\\d{1,2}")) {
                return null;
            }

            int prefixo = Integer.parseInt(prefixoStr);
            if (prefixo < 0 || prefixo > 32) {
                return null;
            }

            // Em Java, deslocar 32 bits em um int (<< 32) não zera o número; por isso
            // tratamos /0 à parte
            if (prefixo == 0) {
                return 0;
            }
            return 0xFFFFFFFF << (32 - prefixo);
        }

        // Caso 2: Notação decimal pontuada (ex: 255.255.240.0)
        Integer mascaraCandidata = parseIpParaInteiro(mascaraStr);
        if (mascaraCandidata == null) {
            return null;
        }

        // Validação de bits 1 contíguos:
        // Se a máscara for válida, sua negação (~mascara) terá apenas zeros à esquerda
        // e uns à direita (ex: 00001111).
        // Somando 1 à negação, obtemos uma potência de 2 (ex: 00010000). O AND entre
        // eles deve ser 0.
        int inversa = ~mascaraCandidata;
        if ((inversa & (inversa + 1)) != 0) {
            return null;
        }

        return mascaraCandidata;
    }

    /**
     * Conta quantos bits '1' existem na máscara para obter o tamanho do prefixo
     * CIDR.
     */
    private static int calcularPrefixo(int mascara) {
        int prefixo = 0;
        int temp = mascara;
        while (temp != 0) {
            prefixo += (temp & 1);
            temp >>>= 1; // Deslocamento lógico à direita (preenche com 0 à esquerda)
        }
        return prefixo;
    }

    /**
     * Formata um inteiro de 32 bits na notação decimal pontuada (ex: 172.16.32.0).
     */
    private static String formatarDecimalPontuado(int valor32Bits) {
        int oct1 = (valor32Bits >>> 24) & 0xFF;
        int oct2 = (valor32Bits >>> 16) & 0xFF;
        int oct3 = (valor32Bits >>> 8) & 0xFF;
        int oct4 = valor32Bits & 0xFF;
        return oct1 + "." + oct2 + "." + oct3 + "." + oct4;
    }

    /**
     * Formata um inteiro de 32 bits em 4 octetos binários separados por ponto.
     * Utiliza Integer.toBinaryString e String.format apenas para representação
     * visual.
     */
    private static String formatarBinarioPontuado(int valor32Bits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 24; i >= 0; i -= 8) {
            int octeto = (valor32Bits >>> i) & 0xFF;
            String binario8Bits = String.format("%8s", Integer.toBinaryString(octeto)).replace(' ', '0');
            sb.append(binario8Bits);
            if (i > 0) {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    /**
     * Realiza os cálculos de rede, broadcast, quantidade e faixa de hosts, exibindo
     * no terminal.
     */
    private static void exibirResultados(int ip, int mascara) {
        int prefixo = calcularPrefixo(mascara);

        // Operações bitwise fundamentais
        int rede = ip & mascara;
        int broadcast = rede | (~mascara);

        String redeStr;
        String broadcastStr;
        long totalHosts;
        String faixaHosts;

        if (prefixo == 31) {
            // Exceção RFC 3021: enlace ponto a ponto (2 hosts, sem rede nem broadcast)
            redeStr = "não há";
            broadcastStr = "não há";
            totalHosts = 2;
            faixaHosts = formatarDecimalPontuado(rede) + " a " + formatarDecimalPontuado(broadcast);
        } else if (prefixo == 32) {
            // Exceção host único (/32)
            redeStr = "não há";
            broadcastStr = "não há";
            totalHosts = 1;
            faixaHosts = formatarDecimalPontuado(ip);
        } else {
            // Regra geral (/0 a /30)
            redeStr = formatarDecimalPontuado(rede);
            broadcastStr = formatarDecimalPontuado(broadcast);

            int bitsHost = 32 - prefixo;
            // Usa 1L (long) para evitar estouro de inteiro quando prefixo for /0 (2^32 - 2)
            totalHosts = (1L << bitsHost) - 2;

            int primeiroHost = rede + 1;
            int ultimoHost = broadcast - 1;
            faixaHosts = formatarDecimalPontuado(primeiroHost) + " a " + formatarDecimalPontuado(ultimoHost);
        }

        System.out.println("\n------------------------------------------");
        System.out.printf("%-24s : %s%n", "Endereço IP (binário)", formatarBinarioPontuado(ip));
        System.out.printf("%-24s : /%d%n", "Máscara (prefixo)", prefixo);
        System.out.printf("%-24s : %s%n", "Máscara (decimal)", formatarDecimalPontuado(mascara));
        System.out.printf("%-24s : %s%n", "Máscara (binário)", formatarBinarioPontuado(mascara));
        System.out.printf("%-24s : %s%n", "Endereço da rede", redeStr);
        System.out.printf("%-24s : %s%n", "Endereço de broadcast", broadcastStr);
        System.out.printf("%-24s : %d%n", "Quantidade de hosts", totalHosts);
        System.out.printf("%-24s : %s%n", "Faixa de hosts", faixaHosts);
        System.out.println("------------------------------------------");
    }
}