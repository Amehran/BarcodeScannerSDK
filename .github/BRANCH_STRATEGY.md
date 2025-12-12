# Branch Strategy & CI/CD Workflow

## Branch Structure

```
main (production - protected)
  ↑
  │ PR triggers CI/CD (production release)
  │
stage (staging - protected)
  ↑
  │ PR triggers CI/CD (pre-production)
  │
develop
  ↑
  │ PR triggers CI/CD
  │
refactor/cleanup (feature branch)
  │
  └─ Push triggers CI/CD
```

## CI/CD Trigger Rules


### Android CI Workflow (`android-ci.yml`)

**Triggers on:**
- ✅ **Push** to branches:
  - `main` (production)
  - `stage` (staging)
  - `develop`
  - `refactor/cleanup`
  
- ✅ **Pull Request** to branches:
  - `main` (production releases)
  - `stage` (pre-production)
  - `develop`

**What runs:**
1. Unit Tests (both modules)
2. UI Tests (Android emulators API 29, 33) - Currently disabled
3. Build APK

### Code Coverage Workflow (`coverage.yml`)

**Triggers on:**
- ✅ **Push** to branches:
  - `main`
  - `stage`
  - `develop`
  
- ✅ **Pull Request** to branches:
  - `main`
  - `stage`
  - `develop`

**What runs:**
1. Unit tests with coverage
2. JaCoCo report generation
3. Codecov upload (optional)
4. PR coverage comments

## Example Workflow

### Creating a PR from refactor/cleanup → stage

```bash
# 1. Push your feature branch
git push origin refactor/cleanup

# This triggers:
# ✅ Unit tests
# ✅ UI tests
# ✅ Build APK

# 2. Create PR on GitHub: refactor/cleanup → stage
# This triggers:
# ✅ All CI checks again
# ✅ Code coverage report
# ✅ Coverage comment on PR

# 3. After CI passes and review approved
# Merge PR → stage

# 4. Merge triggers on stage:
# ✅ Unit tests
# ✅ UI tests  
# ✅ Code coverage
# ✅ Build APK
```

## Status Checks

When you create a PR to `stage`, you'll see these checks:

- ✅ **Unit Tests** - Must pass
- ✅ **Instrumentation Tests (API 29)** - Must pass
- ✅ **Instrumentation Tests (API 33)** - Must pass
- ✅ **Build APK** - Must pass
- ✅ **Code Coverage** - Must meet thresholds (70% overall, 80% changed files)

## Branch Protection Setup

### For `stage` branch:

1. Go to: **Settings** → **Branches** → **Add rule**
2. Branch name pattern: `stage`
3. Enable:
   - ✅ Require a pull request before merging
   - ✅ Require status checks to pass before merging
   - ✅ Require branches to be up to date before merging
4. Select required status checks:
   - `Unit Tests`
   - `Instrumentation Tests (API 29)`
   - `Instrumentation Tests (API 33)`
   - `Build APK`
   - `Code Coverage`
5. Enable:
   - ✅ Require conversation resolution before merging
   - ✅ Do not allow bypassing the above settings

### For `develop` branch (optional):

Same settings as `stage` for consistency.

## Workflow Files

- `.github/workflows/android-ci.yml` - Main CI pipeline
- `.github/workflows/coverage.yml` - Code coverage
- `.github/CI_CD_SETUP.md` - Detailed documentation

## Quick Reference

| Action | Triggers CI? | Which Workflows? |
|--------|--------------|------------------|
| Push to `refactor/cleanup` | ✅ Yes | android-ci.yml |
| Push to `develop` | ✅ Yes | android-ci.yml, coverage.yml |
| Push to `stage` | ✅ Yes | android-ci.yml, coverage.yml |
| PR to `stage` | ✅ Yes | android-ci.yml, coverage.yml |
| PR to `develop` | ✅ Yes | android-ci.yml, coverage.yml |
| Push to other branches | ❌ No | None |
