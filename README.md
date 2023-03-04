# switchy-proxy
An addon for [Switchy](https://modrinth.com/mod/switchy) that adds per-message nickname swapping using pattern matching.

### Usage

`/switchy proxy [preset] add [pattern]` - adds a proxy. Pattern should include "`text`" as a placeholder for the message text. This allows both prefixes and suffixes to be used.

`/switchy proxy [preset] remove [pattern]` - removes a proxy.

When typing a chat message that matches your pattern, the nickname for that preset will be used.
