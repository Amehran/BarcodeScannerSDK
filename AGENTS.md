# AGENTS.md

## 🚨 Public Release & SDK Mandate: The Gold Standard

The primary role of the agent is to act as a **Lead Architect and Quality Gatekeeper**. The code must adhere to the highest standards, particularly within the `:scanner` module, which is intended as a reusable SDK.

## 🎯 Project Overview & Architecture
* **Project Name:** BarcodeScanner (A Hilt, Compose, CameraX, Clean Architecture project)
* **Primary Language:** Kotlin
* **UI Framework:** Strictly **Jetpack Compose** (Material 3).
* **Architecture:** Must strictly adhere to **Clean Architecture** principles.

[Image of Clean Architecture Diagram]

    * **Dependency Rule:** Dependencies must only flow inward. This is especially critical for the `:scanner` module to ensure it remains highly independent.

## 📦 SDK Fundamentals and Best Practices (The `:scanner` Module)

* **Module Isolation:** The `:scanner` module **must not** have any direct dependencies on the main `:app` module. It should be a standalone, reusable library.
* **Minimal Android Dependency:** Within the `:scanner` module, minimize reliance on Android framework classes (especially in the Domain layer). When Android classes are necessary, use abstraction (interfaces) to manage the dependency.
* **Public API Design:** All classes, methods, and functions intended for public use (consumption by the `:app` module or external clients) must be:
    1.  Designated as `public` (or default visibility in Kotlin).
    2.  Documented with clear, comprehensive KDoc explaining their purpose, parameters, and return values.
* **Error Handling:** The SDK must use consistent and robust error handling. Return results via Kotlin's `Result` type, sealed classes, or exceptions wrapped in a dedicated SDK error type. **Never leak implementation details** (like Retrofit or Room exceptions) to the consumer.

## 🛠️ Testing & Coverage Mandate (TDD & Quality)
* **Methodology:** All development must follow the **Test-Driven Development (TDD)** cycle: RED -> GREEN -> REFACTOR.
* **Test Libraries:** Use **JUnit 5**, **MockK**, **Truth**, and **Turbine**.
* **Test Coverage:** Proactively identify and add missing tests. Coverage is non-negotiable for SDK code.
    * **Logic (Unit Tests):** All **Domain Layer Use Cases** (in both modules) **must** be 100% unit tested.
    * **UI (Compose Tests):** Critical user flows (e.g., successful scan, permission denial) must have corresponding UI tests (`androidx.ui.test.junit4`).

## 🛡️ Security and Public Hygiene Mandate
* **API Keys/Secrets:** **STRICT MANDATE:** Scan all files for hardcoded secrets. Suggest moving them to environment variables or `local.properties`.
* **Logging:** Remove all excessive `Log.d()`, `Log.v()`, or `println()` calls from **production code**, especially within the `:scanner` SDK module.
* **Code Documentation:** All public methods, interfaces, and complex logic blocks must have descriptive **KDoc comments** (`/** ... */`).

## 💉 Dependency Injection (Hilt)
* **Framework:** **Dagger Hilt** is mandatory.
* **Injection Strategy for SDK:** The `:scanner` module must expose its dependencies via **Hilt Modules** that the consuming application (`:app` module) can install. **The SDK should not automatically initialize global state.**

## 📝 Example Action for Agent (SDK Audit)
* **Task:** Review `ScannerRepositoryImpl.kt` in the `:scanner` module.
* **Agent Action:**
    1.  Verify the class implements an interface defined in the **Domain** layer.
    2.  Check for any direct Android framework imports that can be abstracted.
    3.  Confirm all methods have accompanying unit tests.
    4.  Ensure no implementation-specific error types (e.g., Retrofit errors) are exposed in public signatures.