<h1 style="text-align:center;">Weather Service API</h1>

## Overview:
The Weather Service API is a Sprint boot based application designed to fetch the weather details of a given city and country


### Key Features:
- **Reactive WebClient Integration**: Utilizes Spring WebFlux's `WebClient` for asynchronous, non-blocking API calls to OpenWeatherMap (the is the backend service to get the realtime weather details)
- **Resilience4j Integration**: Implements circuit breaker and retry patterns to ensure high availability and fault tolerance.
- **ApiKey Rate limiting**: Rate limiting has been implemented on the api key. Each apiKey is valid for 5 attempts in an hour. 
- **Database Persistence**: Caches weather data in an H2 in-memory database.
- **WireMock Integration Tests**: Comprehensive integration tests using Mockito for mocking and WireMock.

## Prerequisites

Before running the project, ensure that you have the following installed:
- Java 17 or later


## Steps to run the service locally
- Clone the repo from github
````bash
git clone https://github.com/gowrichatradi/ms-weather.git
````
- Build and run the service
````bash
cd ms-weather
./gradlew clean build
./gradlew bootRun
````

## Access the service
To test/access the service, please use below curl
````curl
curl -X POST http://localhost:8080/api/weather \
-H "Content-Type: application/json" \
-H "Api-Key: TESTAPIKEY" \
-d '{
  "city": "Menasha",
  "country": "US"
}'
````


## Technical Stack
- Java 17
- Spring Boot 3
- Spring WebFlux
- Resilience4j
- H2 in-memory with R2DBC
- Wiremock for testing


## Contact
For queries and issues, please contact gowrichatradi@gmail.com
