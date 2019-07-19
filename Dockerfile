FROM maven:3.6.1-jdk-11

COPY . /usr/src/
WORKDIR /usr/src/rmi-server

RUN mvn clean install
RUN mvn dependency:resolve
RUN mvn verify

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.2.1/wait /wait
RUN chmod +x /wait

EXPOSE 7654
CMD /wait && java -jar ./target/jdbc-rmi-server-0.1.0-SNAPSHOT-jar-with-dependencies.jar
