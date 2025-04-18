# IntelliJ Platform Plugin Template Changelog

## [2.1.0] - 2025-03-28

### Added

- Example code – `ProjectActivity`
- Added `opentest4j` test dependency, see: [Missing opentest4j dependency in Test Framework](https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-faq.html#missing-opentest4j-dependency-in-test-framework)

### Removed

- Example code – `MyApplicationActivationListener`
- Remove redundant IntelliJ Platform dependency helpers: `instrumentationTools()`, `pluginVerifier()`, `zipSigner()`
- GitHub Actions: Remove `gradle/actions/wrapper-validation` because validation is performed by default in `gradle/actions/setup-gradle@v4`

### Changed

- Change JVM version to `21`
- Upgrade Gradle Wrapper to `8.13`
- Update `platformVersion` to `2024.2.5`
- Change since/until build to `242-252.*` (2024.2-2025.2.*)
- Dependencies - upgrade `org.jetbrains.intellij.platform` to `2.5.0`
- Dependencies - upgrade `org.jetbrains.kotlin.jvm` to `2.1.20`
- Dependencies - upgrade `org.jetbrains.qodana` to `2024.3.4`
- Dependencies - upgrade `org.jetbrains.kotlinx.kover` to `0.9.1`
- Dependencies (GitHub Actions) - upgrade `gradle/actions/wrapper-validation` to `v4`
- Dependencies (GitHub Actions) - upgrade `codecov/codecov-action` to `v5`

## [2.0.2] - 2024-10-07

### Changed

- Upgrade Gradle Wrapper to `8.10.2`
- Update `platformVersion` to `2023.3.8`
- Dependencies - upgrade `org.jetbrains.intellij.platform` to `2.1.0`
- Dependencies - upgrade `org.jetbrains.qodana` to `2024.2.3`
- Dependencies (GitHub Actions) - upgrade `gradle/actions/setup-gradle` to `v4`
- Add back the `org.gradle.toolchains.foojay-resolver-convention` Gradle settings plugin

### Fixed

- Fixed _Run Plugin_ run configuration logs location

### Removed

- Removed _Run Qodana_ and _Run UI for UI Tests_ run configurations
