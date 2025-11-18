# Dockerfile para el Backend (Java/Spring Boot)

# FASE 1: Construcción (Build)
# Usamos una imagen base de Java para construir el JAR
FROM eclipse-temurin:21-jdk-jammy AS build

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia los archivos de Maven (pom.xml) y los códigos fuente
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Corrección CRLF → LF (necesaria cuando vienes de Windows)
RUN sed -i 's/\r$//' mvnw

# Da permisos de ejecución al wrapper de Maven
RUN chmod +x mvnw

# Descarga dependencias de Maven en modo offline
RUN ./mvnw dependency:go-offline

# Copia el código fuente de tu app
COPY src ./src

# Construye la aplicación y crea el JAR ejecutable
RUN chmod +x mvnw
RUN ./mvnw package -DskipTests

# FASE 2: Ejecución (Run)
# Usamos una imagen más ligera (JRE) para la ejecución final
FROM eclipse-temurin:21-jre-jammy

# Establece un directorio de trabajo
WORKDIR /app

# Expone el puerto por defecto de Spring Boot
EXPOSE 8080

# Copia el JAR construido desde la fase 'build'
COPY --from=build /app/target/*.jar app.jar

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]