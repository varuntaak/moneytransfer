# moneytransfer

## Build the service

    mvn clean package

## Running the service

    java -jar release/money-transfer-1.0.jar

## Assumptions

### Currencies
Accounts used in the transfer are assumed to be same currencey for now. This can be extended
for a multi currency account by either adding type in Account or AccountManager.

