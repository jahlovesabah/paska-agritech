# =================================================================
#  Dockerfile multi-etapa para Paska Agritech (despliegue en Render)
# =================================================================

# ---------- Etapa 1: compilacion con Maven ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cachea dependencias primero (acelera builds posteriores)
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copia el codigo y genera el .jar (se omiten los tests en el build de imagen)
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Etapa 2: imagen de ejecucion (solo JRE) ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copia el artefacto generado
COPY --from=build /app/target/paska-agritech.jar app.jar

# Render inyecta la variable PORT; la app la lee en application.properties.
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
