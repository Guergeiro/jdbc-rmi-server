# jdbc-rmi-server
## Requirements (No Docker)
- Java (+11)
- Maven (+3)

## Install (No Docker)
- Navigate to folder
- mvn clean install
- mvn dependency:resolve
- mvn verify
- java -jar ./target/jdbc-rmi-server-0.1.0-SNAPSHOT-jar-with-dependencies.jar

*Note: Will listen on port 4567*

## Requirements (Docker)
- Docker

## Install (Docker)
- Navigate to folder
- docker build -t rmi-server .
- docker run -p 5000:4567 rmi-server

*Note: Will listen on port 5000*

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

MIT © [Breno Salles](brenosalles.com)