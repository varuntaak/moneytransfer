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

## Assumptions

### Currencies
Accounts used in the transfer are assumed to be same currencey for now. This can be extended
for a multi currency account by either adding type in Account or AccountManager.

