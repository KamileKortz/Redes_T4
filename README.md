# Calculadora de Sub-redes IPv4

Aplicação de linha de comando desenvolvida em Java para cálculo de parâmetros de redes IPv4 (endereço de rede, broadcast, representação binária, quantidade e faixa de hosts utilizáveis) por meio de operações bit a bit.

## Pré-requisitos

- [Docker](https://www.docker.com/) instalado na máquina.

## Execução via Docker Hub

Para baixar e executar a imagem pública diretamente pelo terminal (necessário o uso das flags `-it` para interação via teclado):


docker run -it --rm kamile02/calculadora-ipv4:latest (endereço /prefixo)

## Casos de teste

A aplicação deve produzir os resultados abaixo, que podem ser utilizados para conferir a implementação:

| Endereço | Máscara | Prefixo | Rede | Broadcast | Hosts | Faixa de Hosts |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `192.168.1.10` | `255.255.255.0` | `/24` | `192.168.1.0` | `192.168.1.255` | 254 | `192.168.1.1` a `192.168.1.254` |
| `10.20.30.40` | `255.0.0.0` | `/8` | `10.0.0.0` | `10.255.255.255` | 16777214 | `10.0.0.1` a `10.255.255.254` |
| `200.135.80.130` | `255.255.255.224` | `/27` | `200.135.80.128` | `200.135.80.159` | 30 | `200.135.80.129` a `200.135.80.158` |
| `172.31.5.9` | `255.255.254.0` | `/23` | `172.31.4.0` | `172.31.5.255` | 510 | `172.31.4.1` a `172.31.5.254` |
| `192.168.100.5` | `255.255.255.254` | `/31` | não há | não há | 2 | `192.168.100.4` a `192.168.100.5` |
| `8.8.8.8` | `255.255.255.255` | `/32` | não há | não há | 1 | `8.8.8.8` |

```bash