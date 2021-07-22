
# Implement a PIN manager REST service:

The solution should provide a standalone service that provides PIN generation and validation functionality.

Concepts:
    - MSISDN: Full length phone number. (ex. +34999112233)
    - PIN: Any 4 digit numeric combination.

Mandatory
  - implementation in spring-boot
  - Has to provide an operation to generate 4-digit PINs associated with MSISDNs
  - Has to allow a maximum of 3 PINs per MSISDN awaiting validation
  - Has to provide an operation to validate PINs for a given MSISDN 
  - If not validated, PINs should be removed 1h after their creation
  - Has to control the number of validation attempts and allow only 3 before discarding the PIN
  - Has to use a DB to keep its state
Bonus
  - The component can scale horizontally by deploying multiple instances
  - runs as a docker container
  - runs inside a docker-compose along with the DB


*The provided solution should be published and accessible on a GIT repository and accessible. It should contain clear instructions on how to run it.*

## Setup and Execution

The minimum requirements to run the project are listed below:
- Java 11
- Apache Maven
- Docker
- Docker Compose

In order to compile and run this service by console, the commands to be executed on the project's folder are:

* Clean and Install the project by using Maven:
```bash
$ mvn clean install
```
* Run the service with docker by executing the command:
```bash
$ docker-compose up --build
```
* In order to shutdown docker-compose execution, use the following command:
```bash
$ docker-compose down
```
