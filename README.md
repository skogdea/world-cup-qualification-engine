# World Cup Qualification Engine

Spring Boot service for **FIFA World Cup 2026** group-stage standings and Round of 32 qualification (48 teams, groups A–L).

Top two in each group plus the **eight best third-placed** teams advance. The API also exposes per-team status (`QUALIFIED` / `STILL_ALIVE` / `ELIMINATED`) from current standings.

## Stack

| | |
|---|---|
| Java | 21 |
| Spring Boot | 4.1 |
| Build | Gradle |
| Persistence | In-memory (`MatchRepository`) — no database |
| DTOs | Immutables |

## Architecture

```
MatchAndCardsProvider
  ├─ FifaMatchAndCardsClient      (live FIFA HTTP — preferred)
  └─ ManualMatchAndCardsProvider  (request body / classpath seed)
           ↓
     MatchService → InMemoryMatchRepository
           ↓
     StandingCalculator → GroupStageStandingsService
                              ├──────────────────────────────┐
                              ↓                              │
                    QualificationCalculator                  │
                              ↓                              │
                        RoundOf32Service                     │
                              │                              │
                              └──────────┬───────────────────┘
                                         ↓
                                 TeamStatusService
                    (group rank + best-third rank → status)
```

Persistence is in-memory only. Match results (with home/away stats) are the source of truth; standings apply FIFA-style ordering including fair-play / team conduct and FIFA world ranking for remaining ties.

On the `dev` profile, startup tries live FIFA first-stage import, then falls back to `seed/fifa/wc2026_first_stage_discipline_stats.json` if the repository is still empty. `seed/fifa/fifa_mens_world_ranking.json` is loaded on every profile for remaining tie-breakers.

## Quick Start

```bash
./gradlew bootRun
# Windows: gradlew.bat bootRun
```

- Default profile is `dev` (`spring.profiles.default=dev`), which enables seed loading (`app.seed.enabled=true`).
- Override with `--spring.profiles.active=…` if needed.
- Server: [http://localhost:8080](http://localhost:8080)

```bash
./gradlew build
./gradlew test
./gradlew spotlessApply
./gradlew spotlessCheck checkstyleMain checkstyleTest
```

## API (`/api/v1`)

Team **inputs** (paths and JSON) accept **enum names or FIFA 3-letter codes** (`MEXICO` / `MEX`, `IR_IRAN` / `IRN`, case-insensitive). JSON **responses** always use enum names (`MEXICO`, `IR_IRAN`). Groups are `A`–`L` (case-insensitive). Match JSON uses snake_case fields (`match_id`, `home_score`, `match_status`).

Controllers return DTOs (`200`). Error statuses come from `ApiExceptionHandler` and `ResponseStatusException`.

### Matches

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/matches` | List all matches |
| `GET` | `/api/v1/matches/{matchId}` | Get one match (`404` if missing) |
| `PUT` | `/api/v1/matches/result` | Update a match. Prefers live FIFA; if that fails, persists the request body |

### Standings

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/standings` | Standings for all groups |
| `GET` | `/api/v1/standings/groups/{group}` | Standings for one group |

### Qualification

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/qualification` | Full qualification snapshot |
| `GET` | `/api/v1/qualification/round-of-32` | Teams advancing to the Round of 32 |
| `GET` | `/api/v1/qualification/best-third-place` | Advancing best third-place sides |

### Status

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/status/teams/{team}` | Status for one team (e.g. `/api/v1/status/teams/IR_IRAN` or `/IRN`) |

`ApiExceptionHandler` returns `400` for unknown values — e.g. `/standings/groups/Z` (no such group; groups are only `A`–`L`) vs `/status/teams/NOT_A_TEAM` (not an enum name or FIFA code). Unreadable match JSON also returns `400`. Missing matches return `404`. Team failures get a hint to use an enum name or FIFA code.

## Project Layout

```
src/main/java/.../
  api/           REST controllers
  domain/        enums, models, DTOs, constants
  ingestion/     FIFA + manual providers, seed bootstrap
  repository/    in-memory match store
  service/       match, standings, qualification, team status, ranking
src/main/resources/
  application*.properties
  seed/fifa/wc2026_first_stage_discipline_stats.json  # match seed (dev fallback)
  seed/fifa/fifa_mens_world_ranking.json              # tie-break ranks
src/test/resources/fixtures/fifa/
  live_match_400021443.json                           # live FIFA snapshot for tests
linter/checkstyle/
.github/workflows/gradle-build.yaml
```

## CI and Style

GitHub Actions **Java CI** runs `./gradlew build` on push and pull requests to `main` (JDK 21 Temurin).

| Tool | Config |
|------|--------|
| Checkstyle | `linter/checkstyle/checkstyle.xml` (`maxWarnings = 0`) |
| Spotless | import order `\#, java, javax, jakarta, org, com, tools`; tabs |

## Configuration

| Property | Role |
|----------|------|
| `spring.profiles.default` | Defaults to `dev` for IDE / bare `bootRun` |
| `app.seed.enabled` | Dev match-seed bootstrap (`true` on `dev`; ranking JSON loads regardless) |
| `app.fifa.base-url` | FIFA API base URL |
| `app.fifa.id-competition` / `id-season` / `id-stage-first` | Competition identifiers for live ingest |

## License

Private portfolio project — all rights reserved unless otherwise noted.
