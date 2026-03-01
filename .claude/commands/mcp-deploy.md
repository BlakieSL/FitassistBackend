Switch to the **deploy** MCP profile (GitHub + AWS).

1. Use the Read tool to read `.mcp-deploy.json`
2. Use the Write tool to write that content to `.mcp.json`
3. Use the Edit tool to set `enabledPlugins.chrome-devtools-mcp@chrome-devtools-plugins` to `false` in `.claude/settings.json`

After all steps succeed, tell the user:
> Profile switched to **deploy** (GitHub + AWS). **Restart Claude Code** to load the new MCP servers.