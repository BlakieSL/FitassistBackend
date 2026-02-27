Execute the full deploy workflow:

1. Run `./gradlew format`
2. Stage all changes, commit if needed, push current branch to `dev`
3. Use GitHub MCP to create a PR from `dev` → `master` with a description based on the commits included — no test plan checklist, no "Generated with Claude Code" footer. If a PR already exists (422 error), find it with `list_pull_requests` and update its description with `update_pull_request` instead.
4. Poll every 2 minutes: `pull_request_read` with `get` and check `mergeable_state`. Keep polling until `clean` (all checks passed) or `blocked` (a check failed). CI takes 10-20 minutes.
5. On failure:
   - Fetch PR diff and failed check details via GitHub MCP
   - Unit test failure → run `./gradlew test --tests "..."` locally to reproduce, fix, re-push to `dev`
   - Integration test failure → fix locally, push to `dev` (PR auto-updates), re-poll
   - Format check failure → run `./gradlew format`, re-push
   - Repeat from step 4
6. On success: merge the PR via GitHub MCP — ECS deploy triggers automatically
7. Post-deploy: get new ECS task public IP and map to API Gateway (manual until AWS MCP is configured)
