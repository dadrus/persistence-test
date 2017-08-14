# Partial Matches

Username searches return partial matches, i.e. all usernames containing the search string are returned.

### [Example](- "beatles")

Given these users:

| [ ][setup] [Username][user]|
|------------------------------------------|
| john.lennon |
| ringo.starr |
| george.harrison |
| paul.mccartney |

[setup]: - "setUpUser(#username)"
[user]:   - "#username"

Searching for *"[arr](- "#searchString")"* will return:

| [ ][search] [Matching Usernames][match]|
|------------------------------------------|
| george.harrison |
| ringo.starr |

[search]: - "c:verify-rows=#username:getSearchResultsFor(#searchString)"
[match]: - "?=#username"

