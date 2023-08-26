<center><img alt="switchy proxy demo" src="https://github.com/sisby-folk/switchy-proxy/assets/55819817/2e1b85d2-9492-4a57-a1dd-5612b406a198"/><br/>
A PluralKit-like switchy addon for pattern-matching, per-message nickname swapping.<br/>
Requires <a href="https://modrinth.com/mod/switchy">Switchy</a> and <a href="https://modrinth.com/mod/styled-chat">Styled Chat</a>.
</center>

---

Works with nicknames from [Styled Nicknames](https://modrinth.com/mod/styled-nicknames) (and (<1.20) [Drogstyle](https://modrinth.com/mod/drogstyle)).

### Usage

`/switchy proxy add [preset] [pattern]` - adds a proxy to a preset. Pattern should include "`text`" as a placeholder for the message text. This allows both prefixes and suffixes to be used.

`/switchy proxy remove [preset] [pattern]` - removes a proxy from a preset.

When typing a chat message, `/me`, or `/say` command that matches your pattern, the nickname for that preset will be used.

### Afterword

All mods are built on the work of many others.

This mod is included in [Tinkerer's Quilt](https://modrinth.com/modpack/tinkerers-quilt) - our modpack about ease of
play and self-expression.

We're open to suggestions for how to implement stuff better - if you see something wonky and have an idea - let us know.
