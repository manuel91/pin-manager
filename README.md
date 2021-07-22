
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
$ mvn clean package
```
* Run the service with docker by executing the command:
```bash
$ docker-compose up --build
```
* In order to shutdown docker-compose execution, use the following command:
```bash
$ docker-compose down
```

## Endpoints

**Create PIN for MSISDN**

This endpoint will receive a phone number (MSISDN) in order to generate and retrieve a random 4-digit PIN to then be stored into database.
* **PUT**: http://localhost:8080/pin-service/pin/create
```bash
{
    "MSISDN":"+34999112233",
}
```

**Validate PIN**

This endpoint will receive a phone number along with a pin number (MSISDN) to validate if it matches with one of the gererated pins of the given phone number (MSISDN).
* **PUT**: http://localhost:8080/pin-service/pin/validate
```bash
{
    "MSISDN":"+34999112233",
    "PIN": "1234"
}
```

**Get PIN list for MSISDN**

This endpoint will fetch and retrieve all PINs associated to a phone number (MSISDN).
* **GET**: http://localhost:8080/pin-service/msisdn/pins
```bash
{
    "MSISDN":"+34999112233"
}
```

**Get all MSISDN with PIN list**
This endpoint will fetch and retrieve all MSISDN created with their respective associated PINs.
* **GET**: http://localhost:8080/pin-service/msisdn/all

**Clean expired PINs**
An alternative method that manually calls the CRON function to delete expired PINs after 1 hour of their creation-
* **DELETE**: http://localhost:8080/pin-service/pin/expired
