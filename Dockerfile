FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

ENTRYPOINT ["java",
"-Duser.timezone=Asia/Kolkata",
"-Xms128m",
"-Xmx256m",
"-jar",
"target/AnGate-0.0.1-SNAPSHOT.jar"]