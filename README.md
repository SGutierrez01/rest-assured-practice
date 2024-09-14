# REST-assured API Testing Project

This project is designed to demonstrate automated testing of RESTful APIs using REST-assured, a Java domain-specific language that simplifies testing of REST-based services. The tests are written using Cucumber, a BDD framework, allowing non-technical stakeholders to understand the tests easily.

## Technology Stack

- **Java**: Core programming language.
- **Maven**: Dependency management and project building.
- **REST-assured**: Used for testing RESTful APIs.
- **Cucumber**: BDD framework for writing test cases in plain English.
- **JUnit**: Used alongside Cucumber for asserting test outcomes.
- **Gson**: Utilized for JSON data manipulation.
- **Log4j2**: Used for logging test actions and outcomes.

## Project Structure

- `src/main/java`
  - `com.testing.api`
    - `models`: Contains POJOs that represent the data structure of API resources.
    - `requests`: Contains classes responsible for sending HTTP requests and handling responses.
    - `utils`: Utility classes, including JSON file readers and other helpers.
- `src/main/resources`
  - `data`: Contains JSON files used for seeding and mocking API responses during testing.
- `src/test/java`
  - `com.testing.api`
    - `runner`: Contains Cucumber runner classes that configure and initiate the tests.
    - `stepDefinitions`: Contains step definition classes that map the steps defined in feature files to executable code.
- `src/test/resources`
  - `features`: Contains Cucumber feature files that describe the test scenarios in Gherkin syntax.
  - `schemas`: Contains JSON schema files used to validate the structure of API responses.
- `log4j2.properties`: Configuration file for Log4j2.

## Key Implementations

### Test Cases

1. **Resource Activation Test**
   - **File**: `ActivateResource.feature`
   - **Description**: Tests the ability to activate an inactive resource and verify the new state.

2. **Client Update and Delete Test**
   - **File**: `ClientUpdateAndDelete.feature`
   - **Description**: Tests updating client details and then deleting the client, ensuring clean up and state validation.

3. **Bulk Resource Update Test**
   - **File**: `BulkResourceUpdate.feature`
   - **Description**: Tests updating multiple resources in a single operation, ensuring transactional integrity and rollback on failures.

### Utilities

- `JsonFileReader`: Used for reading and parsing JSON files from the `data` directory to seed tests with expected data or mock responses.
- `HttpClientHelper`: Wraps REST-assured calls and commonly used setup configurations for API requests.

### Configuration and Setup

- **Maven Dependencies**: All dependencies are managed in the `pom.xml`. This includes libraries like REST-assured, Cucumber, Gson, and Log4j2.
- **Cucumber Configurations**: Configured in the runner classes using annotations to define feature paths, glue code locations, and report generation.

## Running Tests

Tests can be executed from the command line using Maven:

```bash
mvn test

---

**Author**: Santiago Gutiérrez  
**Date**: September 2024
