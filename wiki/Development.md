# Development

## Repository Rules

- Keep code, docs, and commit messages in English.
- Avoid hardcoded content where a JSON definition is sufficient.
- Prefer small, testable changes.
- Keep optional integrations optional.

## Testing

Run:

```bash
npm test
```

Use the Gradle client/server tasks only when you need Minecraft runtime verification.

## Commit Style

Use concise English commit messages:

```text
feat: add natural spawn rules
fix: freeze boss during spell cast
docs: add wiki pages
```

## Suggested Workflow

1. Update or add JSON schema support.
2. Add or adjust unit tests.
3. Verify with `npm test`.
4. Run Minecraft runtime checks only for behavior that needs the game engine.
