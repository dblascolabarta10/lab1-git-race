# Lab 1 Git Race

Individual starter for Web Engineering 2026–27. Stack matches the group project: **Java 25 LTS**, **Kotlin 2.4.0**, **Spring Boot 4.1.0**, **Gradle 9.6.0**, **Bootstrap 5.3.8**.

The assignment, AI rules, and deadline are in [`docs/GUIDE.md`](docs/GUIDE.md). Fill [`REPORT.md`](REPORT.md) before you submit. Delivery is the Moodle zip only (`docs/GUIDE.md`).

## Run

Java 25 is required (`./gradlew` uses the wrapper). GitHub Codespaces is optional (`docs/GUIDE.md`). Clone this course repository; you do not fork it to submit.

```bash
git clone https://github.com/UNIZAR-30246-WebEngineering/lab1-git-race.git
cd lab1-git-race
./gradlew check
./gradlew bootRun
```

- UI: <http://localhost:8080>
- JSON: <http://localhost:8080/api/hello>
- Health: <http://localhost:8080/actuator/health>

```bash
./gradlew test
./gradlew test --tests "HelloControllerUnitTests"
```

## My increment: language- and timezone-aware greetings

The server now builds the greeting from three query parameters. Both the page (`/`) and the JSON endpoint (`/api/hello`) use them.

| Parameter  | Default         | Values                                       |
|------------|-----------------|----------------------------------------------|
| `name`     | `World` (API) / empty (page) | Any text. A blank name becomes `Student`. |
| `language` | `en`            | `en`, `es`, `fr`, `it`, `de`. Unknown codes fall back to English. |
| `timezone` | `Europe/Madrid` | Any IANA zone id, e.g. `Asia/Tokyo`. An invalid id returns an error message instead of a 500. |

The time of day is calculated in the requested timezone: morning 06–11 h, afternoon 12–20 h, night otherwise.

```bash
curl "http://localhost:8080/api/hello?name=Ana&language=es&timezone=Asia/Tokyo"
# {"message":"Buenas tardes, Ana!","timestamp":"..."}
```

The page has dropdowns for language and timezone in the API test card.

### Bonus: greeting history (H2 + Spring Data REST)

Every `GET /api/hello` call is saved in an in-memory H2 database (it is cleared when the app stops).

- History, paginated: <http://localhost:8080/history?page=0&size=5&sort=timestamp,desc>
- Count by language: <http://localhost:8080/api/stats/language>
- Count by timezone: <http://localhost:8080/api/stats/timezone>

Details and design decisions are in [`REPORT.md`](REPORT.md).

## Layout

```
src/main/kotlin/HelloWorld.kt                      # class Application
src/main/kotlin/controller/HelloController.kt      # page + JSON API + stats endpoints
src/main/kotlin/controller/GreetingController.kt   # greeting logic (language, timezone)
src/main/kotlin/domain/GreetingLog.kt              # JPA entity
src/main/kotlin/domain/GreetingLogRepository.kt    # repository exposed at /history
src/main/resources/templates/welcome.html
src/test/kotlin/controller/HelloControllerUnitTests.kt
src/test/kotlin/controller/HelloControllerMVCTests.kt
src/test/kotlin/controller/HelloControllerE2ETests.kt
src/test/kotlin/IntegrationTest.kt
```

## License

MIT — see `LICENSE`.
