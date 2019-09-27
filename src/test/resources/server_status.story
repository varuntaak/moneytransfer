Scenario: when a user hit the root path, the server must reply saying its up and running.
Given the server is up
When the user hit the root URL
Then the server reply saying it is up an running.