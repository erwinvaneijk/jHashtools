# CLAUDE.md

Behavioral and project guidelines for working on jHashtools.

**Tradeoff:** These guidelines bias toward caution over speed. For trivial tasks, use judgment.

## 0. Project Overview

jHashtools is a command-line tool, built with Maven, for computing and verifying digests of files and directory trees.

- Root package: `nl.minjus.nfi.dt.jhashtools`; entry point: `App`
- `hashers`: serial, concurrent and actor-based (jetlang) file and directory hashers
- `persistence`: reading/writing digest files (JSON via Jackson, and the "old style" format parsed with ANTLR from `src/main/antlr4`)
- `utils`, `exceptions`: helpers and the project's exception types

Building requires JDK 25 or newer; `./mvnw` uses `JAVA_HOME`, so point it at that JDK. See section 11 for the toolchain.

## 1. Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:

- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them - don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

## 2. Simplicity First

**Minimum code that solves the problem. Nothing speculative.**

- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

## 3. Surgical Changes

**Touch only what you must. Clean up only your own mess.**

When editing existing code:

- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things that aren't broken.
- Match existing style, even if you'd do it differently.
- If you notice unrelated dead code, mention it - don't delete it.

When your changes create orphans:

- Remove imports/variables/methods that YOUR changes made unused.
- Don't remove pre-existing dead code unless asked.

The test: Every changed line should trace directly to the user's request.

**Modernisation is not an exception to this rule.** Upgrades, migrations and reformatting happen in
dedicated commits, one concern each (e.g. `chore: migrate coverage to JaCoCo`). Never upgrade a
dependency, migrate a test, or reformat a file opportunistically inside a feature or bugfix change.

## 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:

- "Add validation" → "Write tests for invalid inputs, then make them pass"
- "Fix the bug" → "Write a test that reproduces it, then make it pass"
- "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:

```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you loop independently. Weak criteria ("make it work") require constant clarification.

## 5. Code Style and Formatting

- MUST use meaningful, descriptive variable, method and class names
- MUST use 4 spaces for indentation (never tabs)
- NEVER use emoji, or unicode that emulates emoji (e.g. ✓, ✗) in code.
  The only exception is when writing tests and testing the impact of
  multibyte characters. Emojis in comments and documentation are acceptable,
  but should be used sparingly and only when they add clarity.
- Follow the existing house style:
  - Opening brace on its own line for class and constructor declarations; on the same line for methods and control flow
  - Parameters are `final`; existing code uses `a`/`the` prefixes (`aHasher`, `thePathsToProcess`) - match it in files that use it
  - Import order: `java.*`/`javax.*`, then third-party, then `nl.minjus.*`, separated by blank lines
- New source files start with the EUPL-1.2 license header in `java.header`
- Formatting is enforced by tooling (section 15); the formatter configuration is the source of truth

## 6. Error Handling

- **MUST** use the project's exception types in `exceptions/` (e.g. `PersistenceException`, `AlgorithmNotFoundException`) or add a specific one; do not throw bare `RuntimeException`/`Exception`
- **MUST** preserve the cause when wrapping: `new PersistenceException("Could not read " + path, e)`
- **NEVER** swallow exceptions silently; handle, rethrow, or log with context
- **MUST** use try-with-resources for every stream, reader, channel and executor
- **NEVER** use `printStackTrace()` or `System.err`/`System.out` for diagnostics; use SLF4J. `System.out` is only for actual CLI output (`App`, `DigestOutputCreator`)
- Error messages include the relevant context (file path, algorithm name)

## 7. Method Design

- **MUST** keep methods focused on a single responsibility
- Limit method parameters to 5 or fewer; use a parameter object or record for more
- Return early to reduce nesting
- Prefer streams and collection methods over explicit loops where clearer, but keep loops where checked exceptions or early exit make streams awkward
- Use `Optional` for return values that may be absent, never for fields or parameters

## 8. Class Design

- **MUST** keep classes focused on a single responsibility
- Fields are `private`, and `final` wherever possible; prefer immutable types
- Use `record` for value types (digests, results) instead of hand-written `equals`/`hashCode`/`toString`
- When a class is not a record, override `equals`, `hashCode` and `toString` together or not at all
- Use `enum` for closed sets of constants (e.g. `PersistenceStyle`) and `sealed` interfaces for closed type hierarchies (e.g. directory hashers)
- Prefer composition over inheritance
- Use a builder only for complex construction

## 9. Testing

- **MUST** write unit tests for all new methods and classes
- Test layout mirrors the main package layout under `src/test/java`
- Tests use **JUnit Jupiter** (JUnit 6) with **AssertJ** assertions; parameterised test classes use `@ParameterizedClass`
- Use `@TempDir` for filesystem tests; this is a filesystem tool, so test against real temporary files rather than mocking the filesystem
- Mock other external dependencies where needed
- Reuse helpers in `src/test/java/.../utils` (`KnownDigests`, `OsUtils`, ...)
- Cover OS-specific behaviour explicitly, including symbolic links (which must not be followed when walking directories)
- Classes named `Abstract*` are excluded from test execution; use that prefix for shared test base classes
- Follow the Arrange-Act-Assert pattern
- Coverage is enforced by JaCoCo `check` in `./mvnw verify`. The limits in `pom.xml` are floors at the coverage measured when JaCoCo was introduced (generated ANTLR classes, `App` and `exceptions` excluded); raise them when coverage improves, never lower them
- Do not commit commented-out or `@Ignore`/`@Disabled` tests without a reason in the annotation

## 10. Imports and Dependencies

- **MUST** avoid wildcard imports, including static imports, except for test assertion entry points (e.g. `org.assertj.core.api.Assertions.*`)
- **MUST** declare dependencies in `pom.xml` with explicit versions; share versions via `<properties>` (as with `antlr.version`)
- Ask before adding a new dependency; prefer the JDK when it suffices
- Never add a dependency with known critical CVEs

## 11. Toolchain

| Area | Tool |
|---|---|
| Language | Java 25 (`maven.compiler.release`), enforced by `maven-enforcer-plugin` |
| Build | Maven Wrapper (`./mvnw`, Maven 3.9), `maven-shade-plugin` for the executable jar |
| Tests | JUnit Jupiter 6 + AssertJ |
| Coverage | JaCoCo `check` in `verify` |
| Formatting | Spotless with `eclipse-formatter.xml`, `spotless:check` in `verify` |
| Static analysis | Error Prone (javac plugin, `.mvn/jvm.config`), `-Xlint:all` |
| Logging | SLF4J 2 + `slf4j-simple` (WARN and above to stderr, `simplelogger.properties`) |
| Parsing | ANTLR 4.13, generated into `target/generated-sources/antlr4` |
| CI | GitHub Actions (`.github/workflows/build.yml`), `./mvnw -B -Pci clean verify` |

Known follow-ups, each for its own commit (section 3):

- Error Prone style and modernisation checks are switched off in `pom.xml` (`-Xep:...:OFF`); enable them as the code is modernised
- `HelpFormatter` (commons-cli) is deprecated; its replacement changes the `--help` layout
- jetlang (actor-based hasher) is no longer maintained
- The sources mix LF and CRLF line endings; Spotless preserves them

## 12. Java Best Practices

- Write code for the configured Java release; do not use APIs newer than `maven.compiler.release`
- Once on Java 21+, use modern language features where they improve clarity: records, switch expressions, pattern matching for `instanceof` and records, text blocks, `List.of`/`Map.of`
- Use `var` only when the type is obvious from the right-hand side
- `switch` over enums and sealed types MUST be exhaustive without a `default` branch, so new cases fail to compile
- Use `java.nio.file` (`Path`, `Files`) in new code; do not introduce new `java.io.File` usage
- Never follow symbolic links when walking directories unless explicitly requested
- Always specify charsets explicitly (`StandardCharsets.UTF_8`)
- Concurrency: use `ExecutorService` (closed via try-with-resources on Java 19+) or the existing jetlang setup; never share mutable state without proper synchronisation; do not introduce virtual threads into hashing paths without measurements
- Avoid `null` returns for collections; return empty collections

## 13. Security

- **NEVER** store secrets, API keys, or passwords in code or in the repository
- Treat digest files read for verification as untrusted input (JSON and old-style parsers): validate structure and fail with a clear error
- Guard against path traversal when resolving paths read from a digest file
- Do not introduce MD5 or SHA-1 as a default or recommended algorithm; they remain supported for compatibility only
- Flag outdated dependencies with known vulnerabilities rather than silently upgrading them in unrelated changes
- **NEVER** log sensitive information

## 14. Version Control

- **MUST** write clear commit messages using Conventional Commits (`fix:`, `feat:`, `chore:`, `refactor:`, `test:`)
- **NEVER** commit commented-out code; delete it
- **NEVER** commit debug `System.out.println` statements or `printStackTrace()` calls
- **NEVER** commit credentials or sensitive data

## 15. Tools

- Build: `./mvnw package` produces the shaded executable jar (main class `App`)
- Test: `./mvnw test`; full verification: `./mvnw clean verify`
- Formatting: Spotless with an Eclipse formatter profile matching the house style (section 5); `./mvnw spotless:check` / `./mvnw spotless:apply`. Do not switch to google-java-format: it would reformat every file
- Static analysis: Error Prone at compile time; fix findings rather than suppressing them, and justify any `@SuppressWarnings`
- **MUST** compile without warnings: `-Xlint:all`; the `ci` profile (`./mvnw -Pci ...`) adds `-Werror`
- **NEVER** hand-edit ANTLR-generated parser/lexer classes; change the grammar in `src/main/antlr4` and rebuild
- Dependency updates: `./mvnw versions:display-dependency-updates`
- `maven-enforcer-plugin` pins the required JDK and Maven versions
- Use `./mvnw javadoc:javadoc` to check documentation

## 16. Before Committing

- [ ] `./mvnw -Pci clean verify` passes (tests, coverage, formatting, Error Prone, no compiler warnings)
- [ ] If the ANTLR grammar changed, the build regenerates the parser and its tests pass
- [ ] All public classes and methods have Javadoc
- [ ] No commented-out code or debug statements
- [ ] No hardcoded credentials
