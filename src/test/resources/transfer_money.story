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