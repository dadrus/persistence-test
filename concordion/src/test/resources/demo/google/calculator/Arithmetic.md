# Arithmetic

Entering sums into the Google search bar causes Google to calculate the sum.

## Addition
### [Example](- "addition")
[6 + 9][search] = [15][check].

## Subtraction
### [Example](- "subtraction")
[6 - 9][search] should be [-3][check].

## Multiplication
### [Example](- "multiplication")
[6 * 9][search] should be [54][check].

## Division
### [Example](- "division c:status=ExpectedToFail")
|[searchSum][][sum][]|[expected][check]|
|-------------------:|----------------:|
|9 / 6|1.5|
|0 / 1|0|
|1 / 0|NaN|


[search]: - "searchFor(#TEXT)"
[check]:  - "?=getCalculatorResult()"

[searchSum]: - "searchFor(#sum)"
[sum]:       - "#sum"