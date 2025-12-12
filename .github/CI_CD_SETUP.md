# CI/CD and Automated Testing

This project uses GitHub Actions for continuous integration and automated testing.

## Workflows

### 1. Android CI (`android-ci.yml`)

Runs on every push to `main`, `develop`, and `refactor/cleanup` branches, and on pull requests to `main` and `develop`.

#### Jobs:

- **Unit Tests** 
  - Runs all unit tests in both `app` and `scanner` modules
  - Publishes test results as artifacts
  - Generates test reports

- **Instrumentation Tests (UI Tests)**
  - Runs on Android emulators (API levels 29 and 33)
  - Tests Compose UI components
  - Uses AVD caching for faster execution
  - Runs in parallel on multiple API levels

- **Build APK**
  - Builds debug APK after unit tests pass
  - Uploads APK as artifact for download

### 2. Code Coverage (`coverage.yml`)

Runs on pushes and pull requests to `main` and `develop` branches.

#### Features:

- Generates JaCoCo coverage reports
- Uploads reports to Codecov (optional)
- Adds coverage comments to pull requests
- Minimum coverage thresholds:
  - Overall: 70%
  - Changed files: 80%

## Local Testing

### Run Unit Tests

```bash
./gradlew test
```

### Run UI Tests (requires connected device/emulator)

```bash
./gradlew connectedAndroidTest
```

### Generate Code Coverage Report

```bash
./gradlew testDebugUnitTest jacocoTestReport
```

Coverage reports will be generated at:
- `app/build/reports/jacoco/jacocoTestReport/html/index.html`
- `scanner/build/reports/jacoco/jacocoTestReport/html/index.html`

## Test Results

After each CI run, you can download:

- **Unit test results**: HTML and XML reports
- **Instrumentation test results**: Screenshots and test logs
- **Coverage reports**: HTML coverage reports
- **APK**: Debug build artifact

## Setup for Your Repository

### 1. Enable GitHub Actions

GitHub Actions should be enabled by default. The workflows will run automatically on push/PR.

### 2. (Optional) Codecov Integration

To enable Codecov integration:

1. Sign up at [codecov.io](https://codecov.io)
2. Add your repository
3. Add `CODECOV_TOKEN` to your repository secrets (Settings → Secrets → Actions)

### 3. Branch Protection (Recommended)

Set up branch protection rules for `main`:

1. Go to Settings → Branches → Add rule
2. Require status checks to pass before merging:
   - Unit Tests
   - Build APK
   - (Optional) Instrumentation Tests
3. Require pull request reviews

## Troubleshooting

### UI Tests Failing

- Ensure emulator has enough resources
- Check if animations are disabled
- Verify test timeout settings

### Coverage Not Generating

- Ensure tests are running successfully
- Check JaCoCo configuration in `build.gradle.kts`
- Verify file paths in coverage workflow

### Slow CI Builds

- AVD caching is enabled to speed up emulator tests
- Consider reducing matrix API levels if needed
- Use `--continue` flag to run all tests even if some fail

## Test Coverage Goals

Current coverage targets:
- **Overall**: 70% minimum
- **Changed files in PRs**: 80% minimum
- **Critical business logic**: 90%+ recommended

## Contributing

Before submitting a PR:

1. ✅ Run unit tests locally: `./gradlew test`
2. ✅ Run UI tests if UI changes: `./gradlew connectedAndroidTest`
3. ✅ Check coverage: `./gradlew jacocoTestReport`
4. ✅ Ensure all CI checks pass

## Monitoring

- Check the **Actions** tab in GitHub for workflow runs
- Review test reports in workflow artifacts
- Monitor coverage trends over time
