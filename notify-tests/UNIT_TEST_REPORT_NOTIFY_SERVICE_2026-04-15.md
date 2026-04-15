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

## 1.4 Project Link
- Repository URL: https://github.com/trungtoto/CVConnect-Unit-Test.git
- Module path: `CVConnect-Unit-Test/notify-tests`

## 1.5 Execution Report

### Command
```powershell
mvn -f "D:\CVConnect\CVConnect-Unit-Test\notify-tests\pom.xml" test
```

### Result summary (current environment)
- Local check in this environment could not execute Maven because `mvn` was not available in PATH.
- Please run the command above on your machine to collect final pass/fail counts.

### Screenshot evidence to attach
- Console output of Maven test run
- `target/surefire-reports/` files after execution

## 1.6 Code Coverage Report

### Recommended command
```powershell
mvn -f "D:\CVConnect\CVConnect-Unit-Test\notify-tests\pom.xml" test jacoco:report
```

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

