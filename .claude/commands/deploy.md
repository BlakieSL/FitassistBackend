Execute the full deploy workflow:

1. Run `./gradlew format`
2. Stage all changes, commit if needed, push current branch to `dev`
3. Use GitHub MCP to create a PR from `dev` → `master` with a description based on the commits included — no test plan checklist,
no "Generated with Claude Code" footer. When committing, do NOT include a `Co-Authored-By: Claude` trailer. If a PR already exists (422 error), find it with `list_pull_requests` and update its description with `update_pull_request` instead.
4. Poll every 2 minutes (up to 15 polls / 30 min total): `pull_request_read` with `get` and check `mergeable_state`.
The only terminal states are `clean` (proceed to step 6) or `blocked` (proceed to step 5).
Any other state (`unstable`, `unknown`, `behind`, or no checks yet) means CI is still running — do nothing,
wait the full 2 minutes, then poll again. Do NOT call `get_status`, inspect workflows, re-push, or take any other action while waiting. If 15 polls complete without a terminal state, stop and report the PR URL to the user.
5. On failure:
   - Fetch PR diff and failed check details via GitHub MCP
   - Unit test failure → run `./gradlew test --tests "..."` locally to reproduce, fix, re-push to `dev`
   - Integration test failure → fix locally, push to `dev` (PR auto-updates), re-poll
   - Format check failure → run `./gradlew format`, re-push
   - Repeat from step 4
6. On success: merge the PR via GitHub MCP — ECS deploy triggers automatically
7. Post-deploy: update API Gateway integration with the new ECS task public IP:
   - Poll `ecs_resource_management` with `DescribeServices` (cluster: `fitassist-cluster`, services: `fitassist-backend-task-service`) every 30 seconds until `runningCount == desiredCount` and `deployments` has exactly one entry with `rolloutState: COMPLETED` — only then proceed
   - Use `ecs_resource_management` with `ListTasks` (cluster: `fitassist-cluster`, serviceName: `fitassist-backend-task-service`) to get the running task ARN
   - Use `ecs_resource_management` with `DescribeTasks` (cluster: `fitassist-cluster`, tasks: `[<task-arn>]`) to get the ENI ID from `attachments[].details` where `name == networkInterfaceId`
   - Use `call_aws` with `aws ec2 describe-network-interfaces --network-interface-ids <eni-id> --region eu-central-1` to get `NetworkInterfaces[0].Association.PublicIp`
   - Use `call_aws` with `aws apigatewayv2 update-integration --api-id yfhuexj19h --integration-id 8l6c7ro --integration-uri "http://<public-ip>:8000/{proxy}" --region eu-central-1` to update the integration
