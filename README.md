# Calculadora de Sub-redes IPv4

Aplicação de linha de comando desenvolvida em Java para cálculo de parâmetros de redes IPv4 (endereço de rede, broadcast, representação binária, quantidade e faixa de hosts utilizáveis) por meio de operações bit a bit.

## Pré-requisitos

- [Docker](https://www.docker.com/) instalado na máquina.

## Execução via Docker Hub

Para baixar e executar a imagem pública diretamente pelo terminal (necessário o uso das flags `-it` para interação via teclado):

```bash
docker run -it --rm SEU_USUARIO_DOCKERHUB/calculadora-ipv4:latest