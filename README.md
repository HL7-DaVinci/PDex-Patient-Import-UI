PDex Patient Import UI Reference Implementation
===============
[![Build Status](https://travis-ci.org/HL7-DaVinci/PDex-Patient-Import-UI.svg?branch=master)](https://travis-ci.org/github/HL7-DaVinci/PDex-Patient-Import-UI)

Da Vinci reference implementation for the Patient Import UI use case.

# Implementation Details
Implementation is intended to prove the following functionality:
1. OAuth2 Authorization for data import from multiple Payer systems.
1. Querying of Patient data from a Payer FHIR Server using a token requested during step 1.
1. Storing Patient data from multiple Payer systems.
1. Responsive UI to display Patient data.

# Build
Go to root project's folder and run:
```sh
mvn clean install
```

# Try it
Run app with:
```sh
java -jar target/patient-ui-2.0-SNAPSHOT.jar
```
Go to http://localhost:8080

Credentials: user/user