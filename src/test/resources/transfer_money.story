Scenario: when a user wants to transfer money from AccountA to AccountB.
Given the server is up
And the account with name AccountA is created
And the account with name AccountB is created
When the user transfer amount of 10 from AccountA to AccountB
Then the server full fill the request as Success
And the account named AccountA is debited of amount 10
And the account named AccountB is credit with amount 10