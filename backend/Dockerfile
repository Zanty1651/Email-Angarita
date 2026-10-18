# Etapa 1: Compilación de la aplicación con Maven
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar configuración y código fuente
COPY pom.xml .
COPY src ./src

# Compilar y empaquetar el JAR omitiendo tests para agilizar el despliegue
RUN mvn clean package -DskipTests

# Etapa 2: Entorno de ejecución con imagen JRE ligera
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el artefacto generado desde la etapa de compilación
COPY --from=build /app/target/*.jar app.jar

# Render asigna dinámicamente la variable de entorno PORT (por defecto 8080 si es local)
ENV PORT=8080
EXPOSE 8080

# Iniciar la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
