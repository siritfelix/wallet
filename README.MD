# Code Assessment

By Felix Antonio Sirit Murillo siritfelix@gmail.com

`Code available on github github`: https://github.com/siritfelix/wallet.git

Necessary technologies

`Java 17` `Maven` `Spring-Boot` `h2` `JPA`

Steps to launch the application:
    `Prerequisites: having installed JDk 17, and maven`
1) Run the command:
> mvn clean package
2) Locally you can launch it with:
> mvn spring-boot:run
3) The documentation to test http://localhost:8080/swagger-ui/index.htm

To solve the exam, it was developed with the following characteristics:

1) A layered architecture was used, with definition of domain objects, DTO pattern for controllers and an embedded database as a demonstration.
2) To always keep the service active, a deployment was implemented using Docker, so that in the event of a failure or blockage, it can restart the application.
3) In order to be able to trace the operations, unique shared transaction identifiers were added, for example for a transfer the transactionId of the wallet that is debited is the same transactionId of the wallet that is credited, in the same way each operation maintains an id and a transactionId.
4) The test coverage was 80%, the generated report is located at **/target/site/jacoco/index.html, to generate it
> mvn clean package verify
5) Added API documentation, which is available when starting the service at http://localhost:8080/swagger-ui/index.html
6) Dockerfile and docker-compose.yml files were created to launch the application
> docker-compose up -d --build

7) Depending on the time spent, to improve the application I would recommend using an event-oriented architecture (EDA) with a saga pattern, using a queue such as Kafka or activeMp, in order to be able to handle transactions in a distributed manner, which allows for improved data consistency and to operate with low concurrency when performing operations on the database. However, synchronized methods and Pessimistic Locking were applied in the development to maintain consistency of debit and credit operations in the wallet.
