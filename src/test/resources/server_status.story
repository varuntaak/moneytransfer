Scenario: when a user hit the root path, the server must reply saying its up and running.
Given the server is up
When the user hit the [URL]
Then the server reply [status_code] and [body]

Examples:
|URL|status_code|body|
|/|200|Server is up and running!|
|fsdfds|405|HTTP method not allowed|