FROM eclipse-temurin:21-jdk-jammy
COPY . .
RUN ./mvnw clean package -DskipTests
ENTRYPOINT ["java", "-jar", "target/PoojaMart-0.0.1-SNAPSHOT.jar"]
