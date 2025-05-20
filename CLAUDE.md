# BND Library Build & Style Guide

## Build Commands
- Full build: `mvn clean install`
- Run all tests: `mvn test`
- Run single test: `mvn test -Dtest=TestClassName` or `mvn test -Dtest=TestClassName#testMethodName`
- Run tests in specific module: `cd Module-Name && mvn test`
- Skip tests: `mvn install -DskipTests`

## Code Style Guidelines
- **Imports**: Use explicit imports, avoid wildcards except for Java collections (`import scala.collection.JavaConverters._`)
- **Formatting**: Use 2-space indentation, no trailing whitespace
- **Types**: Use explicit type annotations for public methods/fields
- **Naming**: 
  - Classes: PascalCase with `Ac` prefix for chemistry
  - Methods: camelCase
  - Variables: camelCase
  - Constants: ALL_CAPS
- **Error Handling**: Use exception hierarchies (`BndRuntimeException`/`BndChemistryException`), wrap Java exceptions
- **Scala Idioms**: Use `Option` instead of null, functional patterns over imperative
- **Testing**: JUnit annotated test classes, methods prefixed with 'test'

Scala version: 2.13