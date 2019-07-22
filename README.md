# jdbc-rmi-server
## Requirements (No Docker)
- Java (+11)
- Maven (+3)

## Install (No Docker)
- Navigate to folder
- config.config file with the following code inside
```
db-url=URL1:PORT1,URL2:PORT2,...
db-username=INSERT PORT HERE
db-password=INSERT PASSWORD HERE
```
- mvn clean install
- mvn dependency:resolve
- mvn verify
- java -jar ./target/rmi-server.jar

*Note: Will listen on port 7654*

## Requirements (Docker)
- Docker

## Install (Docker)
- Navigate to folder
- config.config file with the following code inside
```
db-url=URL1:PORT1,URL2:PORT2,...
db-username=INSERT PORT HERE
db-password=INSERT PASSWORD HERE
```
- docker build -t rmi-server .
- docker run -p 7654:7654 rmi-server

*Note: Will listen on port 7654*

## License
This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Author
[Breno Salles](brenosalles.com)