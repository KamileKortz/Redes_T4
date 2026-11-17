# Etapa 1: Compilação
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY src/CalculadoraIPv4.java .
RUN javac CalculadoraIPv4.java

# Etapa 2: Execução
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/CalculadoraIPv4.class .

ENTRYPOINT ["java", "CalculadoraIPv4"]