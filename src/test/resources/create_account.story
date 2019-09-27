Scenario: when a user do a post to createaccount API, the server reply with account id.
Given the server is up
When the user submit a post request to /createaccount with required payload
Then the server reply with status 200

When the user get the account balance of the account
Then the account balance is shown as 1000
