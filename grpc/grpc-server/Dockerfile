FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY grpc-server/target/grpc-server-*.jar app.jar

EXPOSE 9090


ENTRYPOINT ["java", "-jar", "app.jar"]