public class CalculadoraIPv4 {

    public static void main(String[] args) {
        try {
            if (args.length != 2)
                throw new Exception("Uso: <ip> <mascara>  (ex: 172.16.45.200 /20)");

            int ip = lerIp(args[0]);
            int mascara = lerMascara(args[1]);

            int prefixo = Integer.bitCount(mascara);
            int rede = ip & mascara; // AND zera os bits de host
            int broadcast = rede | ~mascara; // OR liga todos os bits de host

            String redeStr, bcStr, faixa;
            long hosts;

            if (prefixo == 32) { // host único
                redeStr = bcStr = "não há";
                hosts = 1;
                faixa = decimal(ip);
            } else if (prefixo == 31) { // ponto a ponto (RFC 3021)
                redeStr = bcStr = "não há";
                hosts = 2;
                faixa = decimal(rede) + " a " + decimal(broadcast);
            } else { // caso geral
                redeStr = decimal(rede);
                bcStr = decimal(broadcast);
                hosts = (1L << (32 - prefixo)) - 2;
                faixa = decimal(rede + 1) + " a " + decimal(broadcast - 1);
            }

            System.out.println("Endereço IP (binário) : " + binario(ip));
            System.out.println("Máscara (prefixo)     : /" + prefixo);
            System.out.println("Máscara (decimal)     : " + decimal(mascara));
            System.out.println("Máscara (binário)     : " + binario(mascara));
            System.out.println("Endereço da rede      : " + redeStr);
            System.out.println("Endereço de broadcast : " + bcStr);
            System.out.println("Quantidade de hosts   : " + hosts);
            System.out.println("Faixa de hosts        : " + faixa);

        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    // "192.168.1.10" -> inteiro de 32 bits
    static int lerIp(String s) throws Exception {
        String[] partes = s.split("\\.", -1);
        if (partes.length != 4)
            throw new Exception("Endereço inválido: precisa de 4 octetos.");
        int valor = 0;
        for (String p : partes) {
            if (!p.matches("\\d{1,3}") || Integer.parseInt(p) > 255)
                throw new Exception("Octeto inválido (use 0 a 255): " + p);
            valor = (valor << 8) | Integer.parseInt(p);
        }
        return valor;
    }

    // "/20" ou "255.255.240.0" -> inteiro de 32 bits
    static int lerMascara(String s) throws Exception {
        if (s.startsWith("/")) {
            if (!s.matches("/\\d{1,2}") || Integer.parseInt(s.substring(1)) > 32)
                throw new Exception("Prefixo inválido (use /0 a /32).");
            int p = Integer.parseInt(s.substring(1));
            return p == 0 ? 0 : 0xFFFFFFFF << (32 - p);
        }
        int m = lerIp(s);
        int inversa = ~m; // máscara válida: inversa = 000...111
        if ((inversa & (inversa + 1)) != 0) // 0001111 & 0010000 == 0
            throw new Exception("Máscara inválida: bits 1 não contíguos.");
        return m;
    }

    // inteiro -> "a.b.c.d"
    static String decimal(int v) {
        return (v >>> 24) + "." + ((v >>> 16) & 255) + "." + ((v >>> 8) & 255) + "." + (v & 255);
    }

    // inteiro -> "11111111.00000000.00000000.00000000"
    static String binario(int v) {
        String b = String.format("%32s", Integer.toBinaryString(v)).replace(' ', '0');
        return b.substring(0, 8) + "." + b.substring(8, 16) + "." + b.substring(16, 24) + "." + b.substring(24);
    }
}
