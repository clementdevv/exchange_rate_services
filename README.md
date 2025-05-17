
# Exchange Rate Services.
### A Currency Conversion Microservices Project.

This is a Java Spring Boot-based microservices application that allows real-time currency conversion using exchange rates from Exchange Rates API, the API resource is available at https://exchangeratesapi.io/. It demonstrates inter-service communication, RESTful development, JDBC-based database persistence, and Docker-based deployment.

---

## Project Set up and Testing Instructions.
1. To set up the project, first clone the repository using any of the below commands, within the directory you wish to have the project:
  - Clone via SSH:
    ```
    git clone git@github.com:clementdevv/exchange_rate_services.git
    ```
  - Clone via HTTPS:
    ```
    git clone https://github.com/clementdevv/exchange_rate_services.git
    ```
  - Clone via Github CLI:
    ```
    git clone gh repo clone clementdevv/exchange_rate_services
    ```
2. Open the project in your favourite IDE, preferrably Intellij IDEA.
- You will now be able to run the rate service followed by the main service successfully and test the endpoints via the postman workspace whose link is shared below:
[Exchange Rate Services Workspace](https://karria-team.postman.co/workspace/My-Projects~239a85ae-f249-4dc9-9d4f-2e4d860054bb/request/36678553-611f9db0-e40c-4e47-883d-dc36e087a11f?action=share&creator=36678553&ctx=documentation&active-environment=36678553-5d19a35a-4feb-432c-bd9f-0b56dfa99648)

---

### Running the Application with Docker Compose
- This project is fully containerized using **Docker and Docker Compose**.
- Before moving on, ensure you have **Docker Desktop** installed on your machine and you have a **Dockerhub Account**.
- Follow the steps below to build and run both services along with a **PostgreSQL database**:
   1. First, run the command below when in the project root directory:
      ```
      - docker-compose up --build
      ```
- The command will:
      - Build the rate-service and main-service Docker images.
      - Spin up rate-service, main-service, and a PostgreSQL container.
      - Automatically apply the schema.sql file (for conversions table) through volume mounting or startup script.
   2. Once all containers are running, you can verify the services are up and running by moving on to the next section.
   3. To stop all running containers, run:
      ```
      docker-compose down
      ```
**Key points to note:**
- All configuration values (e.g., database credentials, ports, API base URLs) are externalized using the **.env** file.
- You may inspect the **docker-compose.yml** and **Dockerfile** in each service folder for detailed setup.
- **PostgreSQL** stores conversion records in the conversions table, created using schema.sql.

### Postman Testing instructions
- With both services running in your **IDE**, head over to the **Status folder** and test the GET request named **Rate Service Status**, which is the rate service.
- Now test the main service status endpoint, which is the GET request named **Main Service Status**. These two status endpoints act as **health checks** for both services.
- To test an endpoint, click the blue button indicated **"Send"**. To copy a token, under the Send button, look for a link written **"Authorization"**.
- Click on it, then paste the token in the **Bearer token** section.
- Head over to the Auth folder and use the **Register** endpoint to register then use the **User Login** endpoint to login as a registered user.
- _**COPY THE TOKEN**_. 
- In the Internal folder, Paste the copied token to test the **Fast Exchange Rates** endpoint. Do the same for the **Rate Exchange API** endpoint. 
- Finally, head over to **Convert Currency** endpoint and copy the token there too. Test the endpoint to see the returned converted currency. 
---

## Attained Objective

Build and run two microservices:

1. **rate-service**: Retrieves live exchange rates from [Exchange Rates API](https://exchangeratesapi.io/).
2. **main-service**: Accepts conversion requests, fetches rates via rate-service, calculates the converted amount, saves the record into PostgreSQL using Spring Data JDBC, and returns the result.

---

## Technologies Used

- Java 17.0.12 
- Spring Boot 3.4.5
- Spring Data JDBC
- PostgreSQL
- Docker & Docker Compose
- Jakarta Bean Validation
- Spring WebClient
- RowMapper (for mapping DB rows)
- Postman (for endpoint testing)

---

## Project Structure

### Rate Service

The `rate-service` is responsible for fetching up-to-date exchange rates from an external currency API and providing that data to the `main-service`.

#### Key Responsibilities
- Fetch real-time exchange rates from [Exchange Rates API](https://exchangeratesapi.io/).
- Communicate with external APIs using reactive, non-blocking HTTP calls.
- Expose internal endpoints that serve exchange rates to `main-service`.
- Handle external API errors gracefully and consistently.


---

### Main Service

The `main-service` handles user requests, manages authentication and authorization, and performs the currency conversion using rates fetched from the `rate-service`.

#### Key Responsibilities
- Process currency conversion requests.
- Communicate with the `rate-service` to get current exchange rates.
- Perform currency conversion calculations.
- Persist transaction records to the PostgreSQL database.
- Secure endpoints using JWT authentication.
- Return conversion results to the client.


#### API Endpoints
- `POST /api/auth/login` – User authentication endpoint to issue JWT.
- `POST /api/convert` – Protected endpoint for performing currency conversion.

---

### Authentication Flow

1. Client sends credentials to `/api/auth/login`.
2. System validates credentials and returns a JWT token.
3. Client includes the token in the `Authorization: Bearer <token>` header for protected endpoints.
4. `JwtRequestFilter` validates the token and authorizes the request.

---

### Conversion Process Flow

1. Client sends an authenticated POST request with:
   - Source currency
   - Target currency
   - Amount
2. `main-service` validates the input parameters.
3. `main-service` calls the internal API of `rate-service` to fetch the latest exchange rate.
4. `main-service` calculates the converted amount.
5. Conversion details are saved to the `conversions` table in PostgreSQL.
6. The conversion result is returned to the client.

---


