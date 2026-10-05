# Lab 1 Git Race -- Project Report

This note uses the same disclosure fields as the group-project **AI use (10%)** slice. Lab 1 is still **limited**: assistive GenAI only — not a full or substantial generated solution. The project will later expect agents plus `AGENTS.md` and one skill; you do **not** need those here.

Do not invent a percentage of “AI vs original” lines. Empty or fake disclosure fails this lab.

## What I specified

I wanted to enhance the greeting system by making it aware of both the user's geographical timezone and their preferred language, eliminating the hardcoded English default. I specified that the application should receive two new parameters (`language` and `timezone`) and calculate the exact local time in that specific part of the world to return the correct contextual greeting (morning, afternoon, or night) translated into one of five supported languages.

I knew it would work if passing `?language=es&timezone=Asia/Tokyo` correctly returned "Buenas noches" (or the appropriate time) based on the actual time in Japan, translated to Spanish. Furthermore, the frontend UI needed to be updated to allow selecting these options via dropdown menus instead of typing them manually in the URL.

Bonus:
I wanted to implement an in-memory relational database to persist the HTTP greeting requests. The system needed to provide a paginated CRUD API using Spring Data REST to view the historical logs, along with custom aggregation queries to see summarized statistics grouped by language and timezone. It also required End-to-End (E2E) testing to guarantee the database logic worked correctly.

## What I changed

- **`GreetingController.kt`**: Replaced the simple local time logic with an immutable nested `Map` containing translations for EN, ES, FR, IT, and DE. Integrated Java's `ZoneId` to calculate the current time dynamically based on the timezone string. Added professional KDoc comments.
- **`HelloController.kt` & `HelloApiController.kt`**: Added `language` and `timezone` as `@RequestParam` variables with default fallback values ("en" and "Europe/Madrid"). Added KDoc block tags (`@param`, `@return`, `@author`, `@since`).
- **`welcome.html`**: Modified the API testing card to replace the simple text input with `<select>` dropdowns for both languages and timezones, improving the client-side tool.
- **`http-debug.js`**: Updated the asynchronous `fetch` logic to read the values from the new dropdowns and append them correctly to the `/api/hello` URL string.
- **Test Suite (`HelloControllerUnitTests.kt`, `HelloControllerMVCTests.kt`, `IntegrationTest.kt`)**: Updated assertions (using `containsString` and `endsWith` instead of `equalTo`) to handle dynamic greetings. Added specific unit tests for invalid timezones and fallback languages.

Bonus:
- **`GreetingLog.kt` & `GreetingLogRepository.kt`**: Created a JPA entity to represent the database table and a repository interface extending `PagingAndSortingRepository` with `@RepositoryRestResource` to automatically generate the paginated API.
- **`HelloApiController.kt`**: Added the repository injection to persist each request upon a `GET /api/hello` call. Added two new manual endpoints (`/api/stats/...`) to expose the aggregation queries.
- **`HelloControllerE2ETests.kt`**: Created a completely new test suite using `TestRestTemplate` with a real server port to simulate HTTP requests, populate the database, and assert the pagination and aggregation outputs.
- **`HelloControllerMVCTests.kt` & `HelloControllerUnitTests.kt`**: Refactored the previous tests to inject a mocked `GreetingLogRepository` using Mockito, ensuring the old tests wouldn't crash due to the new database dependency.

## Technical decisions

- **Data Structure**: I chose to use an immutable nested `Map` in Kotlin for the translations rather than setting up an external database. This removes the need for long `if/else` chains.
- **Error Handling**: Instead of letting the application crash with a 500 Internal Server Error if a user inputs a fake timezone (e.g., "Mordor"), I implemented a `try-catch` block around `ZoneId.of(timezone)` to gracefully catch the `Exception` and return a clean, descriptive error message.
- **Testing Strategy**: To avoid flaky tests caused by the time of day, I tested the fallback behavior for unknown languages and the exception handling for invalid timezones. I also relaxed strict exact-match assertions to partial matches checking for the correct name appending.
- **Documentation**: I adhered to the standard KDoc format (`@since`, `@author`, `@param`) to ensure professional code maintainability.

Bonus:
- **In-Memory Database**: Chose H2 database because it clears automatically upon shutdown, which perfectly fits the requirement without requiring external MySQL installations.
- **Bypassing HAL Serialization**: Spring Data REST crashed (`MappingException`) when trying to export the aggregation queries (Projections) to HAL format. I solved this by adding `@RestResource(exported = false)` to the repository queries and exposing them manually via `HelloApiController` to return a clean JSON.
- **Test Context Isolation**: Initially, mocking the repository with Spring annotations caused a `BeanDefinitionOverrideException` that crashed all tests. I solved this by dropping `@WebMvcTest` and using `MockMvcBuilders.standaloneSetup()` to instantiate the controllers manually, keeping the test context perfectly isolated and fast.
- **Kotlin JPA Instantiation**: To prevent Hibernate's `No default constructor for entity` exception, I assigned default empty string values (`""`) to the `GreetingLog` primary constructor properties, keeping the Kotlin syntax clean without needing secondary constructors.

## How I verified

I constantly verified the application state by running `./gradlew check`, `./gradlew test`, and `./gradlew bootRun`. 

Bonus:
I manually navigated to `http://localhost:8080/history` to confirm Spring Data REST successfully generated the paginated HAL JSON. I also checked the custom `/api/stats/language` and `/api/stats/timezone` endpoints. Finally, I validated the entire flow programmatically by creating E2E tests (`HelloControllerE2ETests`) that simulate multiple requests and assert the paginated responses and correct SQL grouping counts, running them via `./gradlew check`.

During development, I faced several issues:
1. **Kotlin Compilation Errors**: Failed with `Syntax error: Expecting comma or ')'` and `actual type is 'LocalTime!', but 'String' was expected`. Fixed by correcting missing commas in the parameters and passing the correct variable to the controller.
2. **Spring Context Test Failures**: Encountered `UninitializedPropertyAccessException` and `UnsatisfiedDependencyException` after modifying the service. I fixed this by properly initializing `GreetingController` in unit tests and adding `@Import(GreetingController::class)` to the MVC tests.
3. **Windows File Lock (`AccessDeniedException`)**: Gradle crashed trying to clean up stale outputs in `build/resources/main/templates`. I resolved this by stopping the zombie Gradle daemon (`./gradlew --stop`) and manually deleting the `build` directory to release OS/OneDrive locks.

Finally, I manually verified the client-server interaction using the browser's Network tab.

Bonus:

## AI disclosure

- **Tools / skills:** Gemini (Google).
- **Purpose:** Brainstorming bonus architecture ideas, troubleshooting Gradle `AccessDeniedException` errors on Windows, fixing Spring Boot testing context issues, and formatting KDoc boilerplate.
- **Representative prompts:** 
  - "I get an AccessDeniedException in build/resources/main/templates when running bootRun."
  - "How do I test dynamic time without making the test flaky?"
  - "I get UnsatisfiedDependencyException in my MVC tests after adding the new service."
  - "Format these comments using the official Kotlin KDoc standard."
- **Affected files/sections:** Test files assertions, `http-debug.js` fetch URL construction, and KDoc structure in controllers.
- **Validation steps:** Executed `./gradlew check` to ensure all 15 tests passed. Verified the JavaScript fetch execution manually in the browser.
- **Citations:** Relied on standard `java.time.ZoneId` concepts and official KDoc documentation guidelines.
- **Human-reviewed:** I decided the core architecture (nested maps and timezone logic) and rejected the AI's initial suggestions to implement a database. I also had to manually review and fix a JavaScript context error caused by a misaligned `try/catch` block during the UI update, and manually apply `@Import` when the AI's first test code failed to load the Spring context.

Bonus:
- **Additional Purpose:** Resolving Kotlin JPA instantiation errors, bypassing Spring Data REST HAL projection crashes, and fixing test context pollution during E2E integration.
- **Additional Prompts:**
  - "No default constructor for entity GreetingLog in Kotlin."
  - "Spring Data REST Couldn't find PersistentEntity for type class jdk.proxy4 when using custom @Query."
  - "BeanDefinitionOverrideException when running MVC and E2E tests together in Gradle."
- **Additional Validation:** Carefully reviewed the AI's architectural suggestion to use `standaloneSetup` for tests to decouple them from the Spring context, rejecting earlier attempts that involved overly complex configurations.