Title: Rewards Calculation


Design Details:

Controller Layer: The entry point for API requests (a REST controller). Created 2 API's Save and Fecth

Service Layer: Contains the core business logic. It processes requests, data flows, and business logic.

Data Access Layer (Repository): Handles interactions with the database. 

Database: in-memory database.

Design Details of Service Layer:

1) Save new customers
2) Get the customers based on their phone number

From Service layer calling the repository layer for databse interaction

MessageSource for the retrieving the localized messages

Exception handled using Spring boot exception techniques. Used Global exception handler mechanism

Loggers - Logback logging system from Sppring boot

Used Async for the asynchronous operation


API Details:

Get Call: http://localhost:8080/api/v1/customers/getDetails/{phoneNumber}

which accepts the phone number input

Scenarios's:

When data are 3 months old then message saying - You haven't shopped with us for more than 3 months. Shop and get rewards!!!
when the price is less than 50 for a month - Sorry no rewards points!! Please shop for minimum of $50
When he/she has met the criteria then - Congratulations!!!!, you have received a total of 180 points for your order of $120, 120 - > where the numbers are replaced wth plcae holders


Save call: http://localhost:8080/api/v1/customers/saveDetails

input is customer object

Validations:
Phone number cannot be null or empty.
Phone number must be exactly 10 digits.
Order Id cannot be null or empty.
First name cannot be null or empty.
Last Name cannot be null or empty.
Price cannot be 0.
Price must be a minimum of $10.
