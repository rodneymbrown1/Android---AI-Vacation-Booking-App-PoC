# Engineering Roadmap

Things intentionally left out of the modularization/CI-CD pass, in rough priority order. Each is a natural next step, not a gap in judgment — scoped out to keep that change reviewable and safe to land without a local Android build available.

## Build & CI

- **`build-logic` convention plugins.** Right now shared ktlint/detekt config lives in a `subprojects {}` block in the root `build.gradle.kts`, and Jacoco is wired per-module via `apply(from = "config/gradle/jacoco.gradle.kts")`. The next step is an included `build-logic` project with real convention plugins (`android-library-convention`, `android-feature-convention`, etc.) so each module's `build.gradle.kts` shrinks to just its dependencies — the approach used by Now in Android and most large multi-module Android codebases.
- **Coverage-floor gate.** `jacocoTestReport` runs today and uploads a report, but nothing fails the build below a threshold. Once there's a real coverage baseline across all modules (right now `core:ui`, `core:network`, `feature:excursion`, and `:app` have zero unit tests), add `jacocoTestCoverageVerification` with a floor and ratchet it up over time.
- **Test coverage for the untested modules.** `core:network` (AiService's retry/timeout/error-mapping logic), `core:ui`, `feature:excursion`'s ViewModel, and `:app`'s nav host have no unit tests yet.
- **Dependency vulnerability scanning.** Dependabot covers version currency; add OWASP Dependency-Check or Snyk for known-CVE scanning of transitive dependencies.
- **Play Store deployment.** Not wired up — this project doesn't have a Play Console account. If it did, the natural addition is the Gradle Play Publisher plugin plus a release-workflow step gated on a `PLAY_SERVICE_ACCOUNT_JSON` secret, publishing the signed AAB to an internal track.

## Scalability & performance

- **Baseline Profiles + Macrobenchmark.** Would demonstrate startup/scroll-jank optimization for the module graph as it grows — not very meaningful yet at this app's current size, but the standard next step for a "scaled" Android app story.
- **Paging 3.** Vacation/excursion lists are small (personal trip planning), so full-table `Flow` queries are fine today; if this became multi-user or synced from a backend, swap `VacationDao.observeAll()`/`ExcursionDao.observeExcursionsForVacation()` for `PagingSource`-backed queries.
- **WorkManager-backed offline sync.** AI excursion generation currently fails visibly (with a typed `AppError`) when offline; a `CoroutineWorker` could queue the request and retry with network-aware constraints instead of requiring the user to retry manually.
- **A real `:core:model` module.** `core:network` currently depends on `core:database` purely to reuse the `Vacation`/`Excursion` data classes for the OpenAI prompt/response mapping — a minor layering compromise made to keep the module count at 8. Extracting a pure `:core:model` (no Room annotations) would fully decouple network from database.
- **Room migrations.** The schema is still version 1, so `fallbackToDestructiveMigration()` is harmless today. Once it changes, replace it with a real `Migration` + `MigrationTestHelper` test — don't carry destructive fallback into a shipped app with user data.

## Product / platform

- **User authentication.** Currently single-user, local-only.
- **Crashlytics + Analytics.** No crash reporting or usage analytics wired up yet.
- **Accessibility testing.** No TalkBack/semantics testing pass has been done on the Compose screens.
- **Feature flags / remote config.** Would matter once there's a release cadence to stage rollouts against.
