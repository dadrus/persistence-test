# Splitting Names

To help personalise our mailshots we want to have the first name and last name of the customer. 
Unfortunately the customer data that we are supplied only contains full names.

The system therefore attempts to break a supplied full name into its constituents by splitting 
around whitespace.

### [Example](- "basic")

The full name [Jane Smith](- "#name") is [broken](- "#result = split(#name)") into first name 
[Jane](- "?=#result.firstName") and last name [Smith](- "?=#result.lastName").

### [Examples](- "simple-names")

| Full Name               | First Name      | Last Name |
| ---------------         | --------------- | --------------- |
| [John Smith][split]     | [John][first]   | [Smith][last] |
| [David Peterson][split] | [David][first]  | [Peterson][last] |

[split]: - "#result = split(#TEXT)"
[first]: - "?=#result.firstName"
[last]:  - "?=#result.lastName"

