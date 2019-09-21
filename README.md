# moneytransfer

## Build and Test the service

    cd <ROOT_DIR>
    mvn clean package

## Running the service

    cd <ROOT_DIR>
    java -jar release/money-transfer-1.0.jar

The above will start akka-http server at http://localhost:8081/
The default port can be changed in the rev.AccountsModule.java file and recompiled the source code and re package to get it effective.

## Functional tests

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

## Assumptions

### Currencies
Accounts used in the transfer are assumed to be same currencey for now. This can be extended
for a multi currency account by either adding type in Account or AccountManager.

