# Rewards Calculation

## Versions Used

### Java: 1.8

### Spring Boot: 2.7.18

-----

## Design Details

### Layers

#### Controller Layer: The entry point for all API requests. Manages the REST endpoints for saving and fetching customer data.

#### Service Layer: Contains the core business logic, including reward calculation.

#### Data Access Layer (Repository): Handles interactions with the database.

### Service Layer Design

The service layer is built with a focus on business logic and performance:

* Saves new customer purchase details.
* Fetches customer data by phone number.
* Uses `MessageSource` for retrieving localized messages for different scenarios.
* Leverages `CompletableFuture` for asynchronous operations to improve API responsiveness.
* Handles exceptions using Spring Boot's global exception handler.

-----

## API Details

### `GET` Endpoint: Get Customer Rewards

`GET http://localhost:8080/api/v1/customers/getDetails/{phoneNumber}`

This API accepts a user's 10-digit phone number as a path variable and returns their rewards information.

**Sample Response Structure:**

```json
{
  "phoneNumber": 8899223344,
  "purchaseDetailsList": [
    {
      "orderId": 111,
      "month": "JUNE",
      "price": 186,
      "rewardsPoints": 222
    },
    {
      "orderId": 112,
      "month": "JULY",
      "price": 86,
      "rewardsPoints": 36
    },
    {
      "orderId": 113,
      "month": "AUGUST",
      "price": 16,
      "rewardsPoints": 0
    }
  ],
  "totalRewards": 258,
  "message": "Congratulations!!!!, you have received a total of 258 points for your order"
}
```

### `POST` Endpoint: Save Customer Purchase

`POST http://localhost:8080/api/v1/customers/saveDetails`

This API saves a new customer purchase record to the database.

**Sample Request Body:**

```json
{
    "firstName": "Abc",
    "lastName": "Def",
    "orderId": 912,
    "price": 120,
    "phoneNumber": 1234567890,
    "shoppedDate": "2025-07-21"
}
```

-----

## Validation Details

All requests are validated to ensure data integrity:

* **Phone Number**: Must be a 10-digit number.
* **Order ID**: Cannot be null or empty.
* **Names**: First name and last name cannot be null or empty.
* **Price**: Must be a minimum of $10 and cannot be zero.

-----

## Scenarios and API Messages

The application provides clear messages for various reward scenarios:

* **No Recent Shopping**: `Rewards are calculated for the last 3 months!! Shop now and earn rewards`
* **Not Eligible for Rewards**: `Sorry, no rewards points!! Please shop for a minimum of $50.`
* **Rewards Earned**: `Congratulations!!!!, you have received a total of 180 points for your order.`

-----

*Screenshots are attached in `test-screenshot/`*