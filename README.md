# moneytransfer

## Build and Test the service

    cd <ROOT_DIR>
    mvn clean verify

## Running the service

    cd <ROOT_DIR>
    java -jar release/money-transfer-1.0.jar

The above will start akka-http server at http://localhost:8081/
The default port can be changed in the rev.AccountsModule.java file and recompiled the source code and re package to get it effective.

## Functional tests (JBehave Stories)

### Transfer Money Story

    Scenario: when a user transfers money from AccountA (initial balance 1000)
    to AccountB (initial balance 1000) all possible scenarios.
    Given the server is up
    And the account with name AccountA is created
    And the account with name AccountB is created
    When the user transfer [amount] from AccountA to AccountB
    Then the server response [status_code] and [body]
    And the account named AccountA has [new_balance_A]
    And the account named AccountB has [new_balance_B]

    Examples:
    |amount|status_code|body|new_balance_A|new_balance_B|
    |10|200|Success|990|1010|
    |1000|200|Success|0|2000|
    |0|200|Success|1000|1000|
    |10.10|200|Success|989.90|1010.10|
    |-1000|400|Error: The value must be a positive number|1000|1000|
    |9.342|400|Error: The value must be of decimal scale of 2|1000|1000|
    |10000|400|Error: Insufficient Balance|1000|1000|
    |sdfsd|400|Error: The value must be a number|1000|1000|
    |null|400|Error: The value must be a number|1000|1000|

### Server Status Story

    Scenario: when a user hit the root path, the server must reply saying its up and running.
    Given the server is up
    When the user hit the [URL]
    Then the server reply [status_code] and [body]

    Examples:
    |URL|status_code|body|
    |/|200|Server is up and running!|
    |fsdfds|405|HTTP method not allowed|

### Create Account Story

    Scenario: when a user do a post to create account API, the server reply with a valid account id.
    Given the server is up
    When the user submit a post request to [URL] with required payload
    Then the server reply with [status_code]
    When the user get the account balance of the account
    Then the account balance is shown as 1000

    Examples:
    |URL|status_code|
    |/createaccount|200|

Use the postman collection to test the APIs.

    <ROOT_DIR>/MoneyTransferTests.postman_collection.json

## APIs

### To get to know the balance of an account
    GET: http://localhost:8081/balance/<ACCOUNT_ID>

### To create a new account
    POST: http://localhost:8081/createaccount
    Paylolad:
    {
	"name" : "User1"
    }
### To transfer money from one account to another
    POST: http://localhost:8081/trasfermoney
    Example Payload:
    {
	"from" : "<ACCOUNT_ID>",
	"to" : "<ACCOUNT_ID>",
	"value" : "10"
    }
### To deposit money to an account
    POST: http://localhost:8081/depositmoney
    Example Payload:
    {
	"account_id" : "<ACCOUNT_ID>",
	"amount" : "10000000000"
    }

## Technology stack

This application is developed primarily in Java, but uses some components of scala.

Technologies:
- akka-http - Http Server for restful apis
- Google Guice - Dependecy injection
- Junit - Unit and integration testing
- Mockito - For mocking
- Postman - Functional tests


## Assumptions

### Currencies
Accounts used in the transfer are assumed to be same currencey for now. This can be extended
for a multi currency account by either adding type in Account or AccountManager.

