Execute the full deploy workflow:

1. Run `./gradlew format`
2. Stage all changes, commit if needed, push current branch to `dev`
3. Use GitHub MCP to create a PR from `dev` → `master` with a description based on the commits included
4. Poll PR check status every 2 minutes using GitHub MCP (`pull_request_read` with `get_status`) — CI takes 10-20 minutes, keep polling until all checks complete
5. On failure:
   - Fetch PR diff and failed check details via GitHub MCP
   - Unit test failure → run `./gradlew test --tests "..."` locally to reproduce, fix, re-push to `dev`
   - Integration test failure → fix locally, push to `dev` (PR auto-updates), re-poll
   - Format check failure → run `./gradlew format`, re-push
   - Repeat from step 4
6. On success: merge the PR via GitHub MCP — ECS deploy triggers automatically
7. Post-deploy: get new ECS task public IP and map to API Gateway (manual until AWS MCP is configured)
