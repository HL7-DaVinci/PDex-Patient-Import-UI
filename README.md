PDex Patient Import UI Reference Implementation
===============

Da Vinci reference implementation for the Patient Import UI use case.

# Implementation Details
Implementation is intended to prove the following functionality:
1. OAuth2 Authorization for data import from multiple Payer systems.
2. Querying of Patient data from a Payer FHIR Server using a token requested during step 1
3. Storing Patient data from multiple Payer systems.
4. Responsive UI to display Patient data.
5. Group data by Encounter.

# Build
```sh
mvn clean install
```

# Try it
Run app with
```sh
java -jar target/patient-ui-1.0-SNAPSHOT.jar
```
Go to http://localhost:8080
Credentials: user/user
