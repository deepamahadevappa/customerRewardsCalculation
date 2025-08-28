\# Rewards Calculation



\## Versions Used



&nbsp; \* \*\*Java:\*\* 1.8

&nbsp; \* \*\*Spring Boot:\*\* 2.7.18



-----



\## Design Details



\### Layers



&nbsp; \* \*\*Controller Layer:\*\* The entry point for all API requests (a REST controller). Created two APIs: Save and Fetch.

&nbsp; \* \*\*Service Layer:\*\* Contains the core business logic. It processes requests, handles data flow, and applies business logic.

&nbsp; \* \*\*Data Access Layer (Repository):\*\* Manages interactions with the in-memory database.



\### Service Layer Design



&nbsp; \* Saves new customers.

&nbsp; \* Fetches customers based on their phone number.

&nbsp; \* Calls the repository layer for database interactions.

&nbsp; \* Uses 'MessageSource' to retrieve localized messages.

&nbsp; \* Handles exceptions using Spring Boot's global exception handler mechanism.

&nbsp; \* Uses 'CompletableFuture' for asynchronous operations.



-----



\## API Details



\### Get Call



'GET http://localhost:8080/api/v1/customers/getDetails/{phoneNumber}'

This API accepts the user's phone number as input.



\### Save Call



'POST http://localhost:8080/api/v1/customers/saveDetails'

This API accepts a 'Customer' object as input.



\*\*Sample Input Format:\*\*



json

{

&nbsp; "firstName": "Abc",

&nbsp; "lastName": "Def",

&nbsp; "orderId": 912,

&nbsp; "price": 120,

&nbsp; "phoneNumber": 1234567890,

&nbsp; "shoppedDate": "2025-07-21"

}





-----



\## Validation Details



&nbsp; \* Phone number cannot be null or empty.

&nbsp; \* Phone number must be exactly 10 digits.

&nbsp; \* Order ID cannot be null or empty.

&nbsp; \* First name cannot be null or empty.

&nbsp; \* Last name cannot be null or empty.

&nbsp; \* Price cannot be zero.

&nbsp; \* Price must be a minimum of $10.



-----



\## Scenarios and Messages



&nbsp; \* \*\*No Recent Shopping:\*\*

&nbsp;   'Rewards are calculated for the last 3 months!! Shop now and earn rewards'

&nbsp; \* \*\*Not Eligible for Rewards:\*\*

&nbsp;   'Sorry, no rewards points!! Please shop for a minimum of $50.'

&nbsp; \* \*\*Rewards Earned:\*\*

&nbsp;   'Congratulations!!!!, you have received a total of 180 points for your order.'



-----



\*Screenshots are attached in 'test-screenshot/'\*

