Scenario: when a user do a post to create account API, the server reply with a valid account id.
Given the server is up
When the user submit a post request to [URL] with required payload
Then the server reply with [status_code]
When the user get the account balance of the account
Then the account balance is shown as 1000

Examples:
|URL|status_code|
|/createaccount|200|