# 🧪 BET99 Web UI Automation Task (Kotlin + Selenium + Cucumber + Allure)

This project is a **lightweight, maintainable UI automation framework** written in **Kotlin** using:
- **Selenium 4.21** for browser automation  
- **Cucumber 5.7 (JUnit4)** for BDD-style tests  
- **Allure Reports** for step-level execution tracking  
- **Gradle 8.14 (Kotlin DSL)** as the build system  

It was designed as a **demonstration project** simulating the BET99 registration flow with **positive and negative test coverage**.

---

## 📂 Project Structure

```
.
├── build.gradle.kts             # Gradle build script
├── config/
│   └── test.properties          # Environment config (URL, headless mode, etc.)
├── src/test/kotlin/
│   ├── pages/                   # Page Objects (HomePage, JoinStep1Page, JoinStep2Page)
│   ├── steps/                   # Cucumber Step Definitions
│   ├── support/                 # Framework utilities (DriverFactory, Waits, Config, TestData, etc.)
│   ├── runners/                 # CucumberRunner.kt entry point
│   └── resources/
│       └── features/            # Cucumber feature files
└── build/allure-results/        # Allure output after test run
```

---

## ⚙️ Configuration

All runtime settings are read from **`config/test.properties`** or JVM system properties (`-Dkey=value`).

**Example:**
```properties
baseUrl=https://www.bet99.com/
headless=true
lang=EN
```

You can override any property when running tests:
```bash
./gradlew test -DbaseUrl=https://staging.bet99.com/ -Dheadless=false
```

---

## 🚀 Running Tests

Run all Cucumber scenarios:
```bash
./gradlew clean test
```

Run tests with a specific tag (e.g. happy path only):
```bash
./gradlew clean test -Dcucumber.filter.tags="@happy_path"
```

All tests use **JUnit4** under the hood and are executed via `CucumberRunner.kt`.

---

## 📊 Allure Reports

After each test run, Allure results are automatically generated.

### To view the report:
```bash
./gradlew allureReport
```

Then open:
```
build/reports/allure-report/index.html
```

> 💡 Tip: You can also use `allure serve build/allure-results` if the Allure CLI is installed locally.

---

## Key Design Highlights

- **Property-driven config:**  
  Environment URL, headless mode, and language read dynamically via `Config.kt`.

- **Simple page model:**  
  Each page focuses only on user actions and locators—no assertions or logic.

- **Step-level assertions:**  
  Each step definition asserts postconditions for every page action (mirroring real-world validation).

- **Reusable waits and extensions:**  
  Located in `support/Waits.kt` and `support/WebDriverExtensions.kt`, ensuring no hard sleeps and clean driver handling.

- **Automatic reporting:**  
  Allure plugin runs automatically after every `test` task.

---

## 🧱 Example Feature

```gherkin
@registration
Feature: Registration

  Background:
    Given the user is on the home page
    And they dismiss any jurisdiction or cookie popups
    And they tap the Join button

  @happy_path
  Scenario: User completes registration successfully
    Given the user is on Join Step 1
    When they complete Step 1 with valid details
    And they complete Step 2 with a valid address
    And they submit the registration
    Then the account is created successfully

  @negative
  Scenario Outline: User cannot proceed with invalid or missing input
    Given the user is on Join Step 1
    When they enter "<email>" and "<username>"
    And they try to continue
    Then an error message should appear indicating invalid input

    Examples:
      | email              | username  |
      | invalidemail.com   | testuser1 |
      | user@              | testuser2 |
      |                    | testuser3 |
```

---

## Tech Stack

| Component | Version | Purpose |
|------------|----------|----------|
| Kotlin | 2.2.0 | Main language |
| Gradle | 8.14 | Build system |
| Selenium | 4.21.0 | Web automation |
| Cucumber | 5.7.0 | BDD test framework |
| Allure | 2.29.0 | Reporting |
| JUnit | 4.13.2 | Test runner |
| SLF4J | 2.0.12 | Logging |

---

## Author Notes

This project demonstrates:
- Page Object pattern in Kotlin  
- Data-driven & property-based configuration  
- Negative test coverage (invalid input, blocklist, missing mandatory fields)  
- Realistic registration flow assertions  
- Auto-generated Allure reports for traceability  

---

### 🧩 Next Steps (optional improvements)
- Add `JoinStep3Page` when backend is available  
- Integrate with Jenkins for CI  
- Parameterize browser choice (Chrome/Firefox)  
- Add API-level pre-conditions for blocklist validation  

---

**Author:** Ashwin Gavai  
**Last updated:** November 2025
