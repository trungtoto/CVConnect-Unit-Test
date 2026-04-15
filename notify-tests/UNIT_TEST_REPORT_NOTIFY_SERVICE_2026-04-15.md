# Unit Testing Report - notify-service (notify-tests)

Date: 2026-04-15
Scope owner: notify-tests module
Project: CVConnect

## 1.1 Tools and Libraries
- Test framework: JUnit 5 (via `spring-boot-starter-test`)
- Mocking: Mockito (via `spring-boot-starter-test`)
- Data layer test: Spring Data JPA Test (`@DataJpaTest`)
- In-memory DB for repository tests: H2
- Build/test runner: Maven (Surefire)
- Coverage tool (recommended): JaCoCo

## 1.2 Scope of Testing

### Files / Classes that are tested
- `com.cvconnect.notifyservice.service.NotifyService`
- `com.cvconnect.notifyservice.repository.NotifyRepository`
- `com.cvconnect.notifyservice.entity.Notification` (indirectly through persistence assertions)

Test classes implemented:
- `com.cvconnect.notifyservice.service.NotifyServiceTest`
- `com.cvconnect.notifyservice.repository.NotifyRepositoryTest`
- `com.cvconnect.notifyservice.entity.NotificationTest`

### Files / Classes that do not need direct unit tests (for this phase)
- `dto/*`: data carriers, low branching logic.
- `entity/*`: mainly JPA mapping; covered indirectly by repository/integration tests.
- `repository/*` custom query methods (if none): Spring Data generated behavior is framework responsibility.
- `controller/*`: better validated by `@WebMvcTest` / integration tests.
- `config/*`, `*Application.java`: wiring/bootstrap concerns, not core business logic unit tests.

## 1.3 Unit Test Cases
- Matrix file (Excel-ready CSV):
  - `notify-tests/UNIT_TEST_CASE_MATRIX_NOTIFY_SERVICE_2026-04-15.csv`
- Table fields used per requirement:
  - Test Case ID / Test Objective / Input / Expected Output / Notes
- Added test cases:
  - `TC_Notify_03`: default constructor + setters
  - `TC_Notify_04`: all-args constructor mapping

## 1.4 Project Link
- Repository URL: https://github.com/trungtoto/CVConnect-Unit-Test.git
- Module path: `CVConnect-Unit-Test/notify-tests`

## 1.5 Execution Report

### Command
```powershell
mvn -f "D:\CVConnect\CVConnect-Unit-Test\notify-tests\pom.xml" test
```

### Result summary (current environment)
- Verified using local Maven binary (`D:\tools\apache-maven-3.9.9\bin\mvn.cmd`).
- Tests run: 4
- Failures: 0
- Errors: 0
- Skipped: 0
- Status: PASS

### Screenshot evidence to attach
- Console output of Maven test run
- `target/surefire-reports/` files after execution

## 1.6 Code Coverage Report

### Recommended command
```powershell
mvn -f "D:\CVConnect\CVConnect-Unit-Test\notify-tests\pom.xml" test jacoco:report
```

### Coverage gate configuration
- `notify-tests/pom.xml` is configured with `jacoco-maven-plugin` check rule:
  - `LINE` `COVEREDRATIO` minimum `0.70`
- This means Maven test phase will fail if line coverage is below 70%.

### Coverage summary (verified from `notify-service/target/site/jacoco/jacoco.xml`)
- Instruction: 62 / 62 = 100.00%
- Line: 24 / 24 = 100.00%
- Method: 12 / 12 = 100.00%
- Class: 2 / 2 = 100.00%
- Coverage requirement `>= 70%` is satisfied.

### Expected coverage artifacts
- `notify-tests/target/jacoco.exec`
- `notify-tests/target/site/jacoco/index.html`
- `notify-tests/target/site/jacoco/jacoco.xml`

### Screenshot evidence to attach
- Coverage dashboard page from `index.html`

## 1.7 References + Prompts Used

### References
- Course guideline requested by instructor:
  - https://drive.google.com/file/d/1mcGQTYDVWEl2mBprHM6fjk6zQ99kHnCE/view
- Project references:
  - `CVConnect-Unit-Test/UNIT_TEST_ASSESSMENT_2026-04-15.md`
  - `CVConnect-Unit-Test/UNIT_TEST_REFERENCE_COMBINED_2026-04-15.md`

### Prompts used
- "Sinh mã nguồn notify-service từ đầu gồm Entity/Repository/Service + unit tests"
- "Bổ sung report theo mục 1.1 -> 1.7 và rule CheckDB/Rollback/Test Case ID"

## 2. Script Requirement Compliance Checklist
- Every test method includes a Test Case ID comment.
- Naming convention is descriptive (e.g., `testSave_WhenValidData_ShouldReturnSuccess`).
- CheckDB is implemented in repository test by `save()` then `findById()` verification.
- Rollback is enforced with class-level `@Transactional` in repository test.
- Comments are kept concise to explain non-obvious assertions.
- Coverage threshold enforcement is configured at 70% line coverage.

