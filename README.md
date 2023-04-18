# Switchy Proxy
An addon for [Switchy](https://modrinth.com/mod/switchy) that adds per-message nickname swapping using pattern matching.

Works with [Drogstyle](https://modrinth.com/mod/drogstyle), [Styled Nicknames](https://modrinth.com/mod/styled-nicknames), and [Drogtor](https://modrinth.com/mod/drogtor). Requires [Styled Chat](https://modrinth.com/mod/styled-chat).

### Usage

`/switchy proxy [preset] add [pattern]` - adds a proxy. Pattern should include "`text`" as a placeholder for the message text. This allows both prefixes and suffixes to be used.

`/switchy proxy [preset] remove [pattern]` - removes a proxy.

When typing a chat message, `/me`, or `/say` command that matches your pattern, the nickname for that preset will be used.
