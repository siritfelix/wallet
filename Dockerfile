FROM openjdk:17-alpine

EXPOSE 8080
ADD target/wallet-1.0.0.jar /app/wallet-1.0.0.jar
WORKDIR /app
CMD java -jar wallet-1.0.0.jar