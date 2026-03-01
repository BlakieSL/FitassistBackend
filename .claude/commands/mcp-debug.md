Switch to the **debug** MCP profile (GitHub + Chrome DevTools plugin).

1. Use the Read tool to read `.mcp-debug.json`
2. Use the Write tool to write that content to `.mcp.json`
3. Use the Edit tool to set `enabledPlugins.chrome-devtools-mcp@chrome-devtools-plugins` to `true` in `.claude/settings.json`

After all steps succeed, tell the user:
> Profile switched to **debug** (GitHub + Chrome DevTools). **Restart Claude Code** to load the new MCP servers. Make sure Chrome is running with remote debugging enabled on port 9222.