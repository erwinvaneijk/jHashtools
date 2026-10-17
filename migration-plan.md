# Migration Plan: Modern Tooling

This plan moves jHashtools from its current build (Java 8, Cobertura, JUnit 4, log4j 1.x bindings, Checkstyle
reporting) to the target setup described in `CLAUDE.md` (section 11).

## Status

Completed on branch `chore/tooling-migration`. Every step passed `./mvnw clean verify`, and the CLI output
matched the baseline fixture (stdout, written files and exit codes) except for the intended changes listed below.

Deviations from the plan:

- **JUnit 6** (6.1.3) instead of JUnit 5: the current Jupiter release, with the same API.
  `DirHasherTest` uses `@ParameterizedClass`.
- **ANTLR upgrade after the Java bump.** ANTLR 4.13 requires Java 11+. `src/main/generated-sources` turned out to be
  gitignored local output, not committed.
- **jetlang** was updated to 0.2.23 (latest release, 2019) rather than left alone.
- **Coverage thresholds:** the old Cobertura limits (85/90/80%) were far above the measured coverage. JaCoCo uses floors
  at the measured level instead (bundle line 0.81 / branch 0.77; per package 0.72 / 0.63), and the per-class rule was
  dropped.
- **Error Prone:** style and modernisation checks are switched off. Correctness findings were fixed, including one real
  bug (`DigestMask` case mapping under a Turkish locale), which was committed separately with a test.
- **`-Werror`** is enabled in a `ci` Maven profile, not always.
- **Not done:** Dependabot (optional in the plan), and running the GitHub Actions workflow. Running it needs a push.

Intended behaviour changes:

- Warnings and errors are logged to stderr. Before, all log output was silently dropped.
- commons-cli `DefaultParser`: `-o=file` uses `file` instead of `=file`, and `--sha-1=foo` is rejected.
- The buildnumber plugin no longer runs SCM check/update during the build.

Found but not fixed (out of scope):

- Verifying old-style digest files fails with a `NullPointerException` (see baseline).
- `DirHasherResultVerifierTest` reads back a file it has not closed and never flushes the writer it asserts on, so its
  assertions always pass and its coverage varies between runs.
- `FileNameGenerator` logs through `java.util.logging` rather than SLF4J.

## Ground rules

- **One concern per step, one commit (or small series) per step.** No feature work mixed in (CLAUDE.md section 3).
- **Behaviour must not change.** The CLI output, the JSON and old-style file formats, and the set of supported
  algorithms stay identical. The existing test suite is the safety net; it must pass after every step.
- Each step lists a **verify** check. A step is done only when that check passes.
- Commit messages use Conventional Commits, as given per step.
- Language-level modernisation of production code (records, `java.nio.file`, sealed types, etc.) is **out of
  scope** here. It happens incrementally when code is touched for other reasons, following CLAUDE.md.

## Findings that shape the plan

- **BouncyCastle is still needed.** `DigestMask` registers `BouncyCastleProvider` for `ripemd160`, which the JDK does
  not provide. So it moves to `bcprov-jdk18on`; it is not removed.
- **commons-cli 1.2 APIs used in `App`** (`PosixParser`, `OptionBuilder`) are deprecated in 1.3+ and need
  rewriting to `DefaultParser` and `Option.builder()`.
- **Logging only uses the SLF4J API** (14 files import `org.slf4j.Logger`/`LoggerFactory`). Swapping the binding needs
  no code changes. There is no `log4j.properties` in the repo.
- **Cobertura cannot instrument modern bytecode.** It has to be replaced *before* the Java version bump.
- **Custom JUnit 4 rules exist** (`utils/ExpectedFailure` + `utils/Optional` annotation), plus `TemporaryFolder`,
  `Parameterized` and some wildcard imports. These need JUnit 5 equivalents.
- **ANTLR output is committed** under `src/main/generated-sources` (grammar: `OldStyleHashes.g4`).
- **jetlang 0.2.9** (actor hashers) has not been maintained for years. It works on modern JDKs, and replacing it is out
  of scope. It is noted as a risk only.
- There is no CI configuration in the repository.

## Steps

### Step 0: Baseline

- Record the JDK in use, and the result and test count of `mvn clean verify` (and `mvn test` if Cobertura fails on
  the local JDK).
- Run the shaded jar against a sample directory and save its output (JSON and old-style) as a reference fixture
  outside the repo. It is used later to confirm that output stays identical.
- **Verify:** baseline numbers written down at the bottom of this file.
- No commit (or `docs: record migration baseline` if the numbers are added here).

### Step 1: Maven Wrapper

- Add the Maven Wrapper (`mvn wrapper:wrapper`, current Maven 3.9.x), committing `mvnw`, `mvnw.cmd` and
  `.mvn/wrapper/maven-wrapper.properties`.
- **Verify:** `./mvnw -v` and `./mvnw test` give the same result as the baseline.
- Commit: `chore: add Maven wrapper`

### Step 2: Build plugin housekeeping

- Remove `maven-assembly-plugin` (shade already builds the executable jar).
- Upgrade `buildnumber-maven-plugin` to 3.x and keep the manifest entries working.
- Remove `build-helper-maven-plugin` if it is only used for `src/main/generated-sources` (that is handled in step 5).
  Otherwise upgrade it.
- Update the remaining plugins to current versions (compiler, surefire, shade, resources) and pin all plugin versions.
- Add `maven-enforcer-plugin` with `requireMavenVersion` (and `requireJavaVersion` matching the current release for now).
- **Verify:** `./mvnw clean package` succeeds; the jar manifest still has `Main-Class`, `Implementation-Version` and
  `Build-Number`; `java -jar` runs.
- Commit: `chore: update and prune Maven build plugins`

### Step 3: Cobertura to JaCoCo

- Remove `cobertura-maven-plugin`; add `jacoco-maven-plugin` (a version that supports Java 25, 0.8.14+).
- Carry over the excludes (`App`, `exceptions/*`) and thresholds: 85% overall line/branch, 90% branch / 80% line for
  `nl.minjus.nfi.dt.jhashtools.*`, low thresholds for `exceptions`. Bind `check` to `verify`.
- JaCoCo counts differently from Cobertura. If the existing code falls short, record the actual numbers and set the
  thresholds to the current level (never lower than measured), and note this in the commit message.
- **Verify:** `./mvnw clean verify` runs the coverage check and passes; the report shows up in `target/site/jacoco`.
- Commit: `chore: replace Cobertura with JaCoCo`

### Step 4: Logging

- Remove `com.pyx4j:maven-plugin-log4j` and `slf4j-log4j12`.
- Add `slf4j-api` 2.x and `slf4j-simple` 2.x (enough for a CLI tool; Logback is the alternative if file logging or
  configuration is needed).
- Add `src/main/resources/simplelogger.properties` only if the default level or format needs adjusting to match the
  current console behaviour.
- **Verify:** tests pass; running the jar shows no SLF4J "no providers" warning; `./mvnw dependency:tree` shows no
  log4j 1.x.
- Commit: `chore: move logging to SLF4J 2 with slf4j-simple`

### Step 5: Library upgrades

Each item below is its own commit.

1. **BouncyCastle:** `bouncycastle:bcprov-jdk16:140` to `org.bouncycastle:bcprov-jdk18on` (current).
   Check that the shade plugin does not break the signed provider jar (exclude `META-INF/*.SF`, `*.DSA`, `*.RSA`
   if needed).
   - Verify: `SupportedDigestAlgorithmsTest` and `DigestTest` pass; `ripemd160` hashing works from the shaded jar.
   - Commit: `chore: upgrade BouncyCastle to bcprov-jdk18on`
2. **commons-cli:** 1.2 to 1.9+. Rewrite `App` to use `DefaultParser` and `Option.builder()`. Keep option names,
   short options, arguments and help text identical.
   - Verify: tests pass; `--help` output and each option behave as in the baseline.
   - Commit: `chore: upgrade commons-cli and replace deprecated parser API`
3. **ANTLR:** 4.7 to 4.13.x, and generate into `target/generated-sources/antlr4` (the plugin default).
   Delete `src/main/generated-sources`, add it to `.gitignore`, and drop the custom `outputDirectory`.
   - Verify: clean build regenerates the parser; `OldStyleHashesTest` and `OldStylePersistenceProviderTest` pass;
     old-style output matches the baseline fixture.
   - Commit: `chore: upgrade ANTLR and stop committing generated sources`
4. **Others:** check `./mvnw versions:display-dependency-updates`, and bring commons-lang3 and Jackson to current
   patch levels if they are behind.
   - Commit: `chore: update remaining dependencies`

### Step 6: Java 25

- Replace `<release>8</release>` with the `maven.compiler.release` property set to `25`. Update enforcer
  `requireJavaVersion` to `[25,)`.
- Enable `-Xlint:all` and fix the warnings it reports. Keep these fixes mechanical: deprecations, raw types,
  missing `serialVersionUID`, try-with-resources where the warning asks for it. Do no other modernisation.
- Fallback: if a plugin or dependency does not work on 25, use 21 and record why.
- **Verify:** `./mvnw clean verify` on JDK 25 passes with zero compiler warnings; jar output matches the baseline
  fixture.
- Commit: `chore: move to Java 25` (plus `fix: resolve -Xlint warnings` if that is a separate commit)

### Step 7: JUnit 5 and AssertJ

1. **Enable the platform:** add the `junit-bom`, `junit-jupiter`, `junit-vintage-engine` and `assertj-core`.
   Remove `hamcrest-integration`. Keep JUnit 4 running through vintage.
   - Verify: the same test count as the baseline runs and passes.
   - Commit: `test: add JUnit 5 platform with vintage engine`
2. **Migrate tests package by package** (`utils`, `persistence`, `hashers`, root), one commit each:
   - `@Before`/`@After`/`@BeforeClass`/`@AfterClass` become `@BeforeEach`/`@AfterEach`/`@BeforeAll`/`@AfterAll`
   - `TemporaryFolder` becomes `@TempDir`, and `Parameterized` becomes `@ParameterizedTest` + `@MethodSource`
   - Hamcrest and `org.junit.Assert` become AssertJ; remove wildcard imports
   - Replace the `ExpectedFailure` rule and `@Optional` annotation with a JUnit 5 `TestExecutionExceptionHandler`
     extension, or with `assertThatThrownBy` at each use site, whichever is simpler. Rename the `Optional`
     annotation (it clashes with `java.util.Optional`)
   - Verify: test count per package unchanged; all pass; JaCoCo check passes.
   - Commit: `test: migrate <package> tests to JUnit 5 and AssertJ`
3. **Remove JUnit 4:** drop `junit:junit`, `junit-vintage-engine` and `hamcrest-core`.
   - Verify: `./mvnw dependency:tree` shows no JUnit 4 or Hamcrest; the full suite passes.
   - Commit: `test: remove JUnit 4 and Hamcrest`

### Step 8: Formatting with Spotless

1. Add `spotless-maven-plugin` with an Eclipse formatter profile (`eclipse-formatter.xml`) tuned to the house style:
   4 spaces, class and constructor braces on their own line, method braces on the same line, import order
   `java|javax`, third-party, `nl.minjus`. Add the BSD license header step. Exclude generated sources.
   - Tune the profile until `spotless:apply` produces the smallest diff, so that the profile matches the code,
     not the other way round.
2. Run `./mvnw spotless:apply` as a single reformat-only commit, and add its hash to `.git-blame-ignore-revs`.
3. Bind `spotless:check` to `verify`. Remove the Checkstyle reporting config and `checkstyle_checks.xml`.
- **Verify:** `./mvnw spotless:check` passes; the reformat commit contains whitespace and formatting changes only
  (`git diff -w --stat` is close to empty); tests pass.
- Commits: `style: add Spotless with house-style formatter`, `style: apply Spotless formatting`,
  `chore: add .git-blame-ignore-revs and remove Checkstyle`

### Step 9: Error Prone

- Add Error Prone to `maven-compiler-plugin` (with the `--add-exports`/`--add-opens` entries required on JDK 17+,
  in `.mvn/jvm.config`).
- Fix the findings. Each one that is a real bug gets its own `fix:` commit with a test. Any suppression needs a
  justification comment.
- **Verify:** `./mvnw clean verify` passes with Error Prone enabled and no warnings.
- Commits: `chore: enable Error Prone`, plus `fix: ...` per real bug found

### Step 10: Strict builds and CI

- Turn on `-Werror` (via a `ci` Maven profile, or always, if the local build stays clean).
- Add a GitHub Actions workflow: JDK 25 (Temurin), `./mvnw -B -Pci clean verify`, cache `~/.m2`, and upload the
  JaCoCo report. Optional: a Dependabot config for Maven and GitHub Actions.
- **Verify:** the workflow is green on a branch.
- Commit: `ci: add GitHub Actions build`

### Step 11: Close out

- Update `CLAUDE.md`: remove the "Current state vs. target" note and the Modernisation Targets table (or mark it
  done), and keep the tool commands.
- Update `README` build instructions (`./mvnw`, JDK 25).
- Commit: `docs: update guidelines and README after tooling migration`

## Order and dependencies

```
0 Baseline
1 Wrapper
2 Plugin housekeeping
3 JaCoCo           (must come before 6: Cobertura breaks on new bytecode)
4 Logging
5 Libraries        (ANTLR 4.13 and bcprov-jdk18on are needed for 6)
6 Java 25          (needed for 8 and 9: current Spotless/Error Prone require JDK 17+)
7 JUnit 5          (independent of 8 and 9; best done before 8 so tests are reformatted once)
8 Spotless
9 Error Prone
10 -Werror + CI
11 Docs
```

## Risks

- **Coverage thresholds:** JaCoCo numbers may differ from Cobertura's. Mitigation: the rule in step 3 (record the
  numbers, never lower thresholds below what is measured).
- **BouncyCastle in a shaded jar:** signature files can cause `SecurityException` at runtime. Mitigation: test the
  shaded jar explicitly in step 5.1.
- **The reformat commit** makes `git blame` noisy. Mitigation: `.git-blame-ignore-revs`.
- **jetlang** is unmaintained. Out of scope here; consider replacing the actor hasher with an `ExecutorService`
  version later, as separate work.
- **Output compatibility:** digest files written by older versions must still be readable. Mitigation: the baseline
  fixtures from step 0 are re-checked after steps 5 and 6.

## Baseline (filled in at step 0)

- JDK: only Homebrew OpenJDK 27 is installed (no JDK 25). It builds `--release 8` (with an "obsolete" warning)
  and is used for all steps; `--release 25` is used as the target from step 6.
- Maven: 3.9.16
- `mvn clean verify` result: compile, tests and shade succeed; the build then fails in `cobertura:check`
  (Cobertura 2.5.1 cannot run on JDK 27). Compiler warnings: `-source/-target 8 obsolete`, and `'yield' may become a
  restricted identifier` in `hashers/actors/FileNameGenerator.java`, which is a compile error at `--release 14+`
  and must be fixed in step 6.
- Test count: 172 run, 0 failures, 0 errors, 0 skipped.
- Reference output fixture: a session-local snapshot (not in the repo) of the `hashers` sources plus a `hello.txt`,
  hashed with the baseline jar. It covers `--help`, JSON and old-style output with `-a`, old-style output with
  `--md5 -1 -2`, JSON verification of fresh output and of a frozen baseline file, and `--single`. Outputs are
  compared after normalising timestamps and object hashes.
- Existing behaviour recorded as-is (not fixed by this migration):
  - Verifying old-style files fails with a `NullPointerException` in `DigestResult.add`, both with `-a` (the
    grammar has no `md2`) and with `--md5 -1 -2`.
  - `--single -2 <dir>` without `-o` exits with status 2.
