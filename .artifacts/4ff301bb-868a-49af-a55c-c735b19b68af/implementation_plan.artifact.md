# Fix JdkImageTransform error with JDK 21

The build error `Execution failed for JdkImageTransform` occurs when using JDK 21 with Android Gradle Plugin (AGP) 8.2.0. AGP 8.2.0 has compatibility issues with how JDK 21's `jlink` handles modular images for the Android SDK.

Updating AGP to 8.4.2 and Gradle to 8.7 resolves this incompatibility.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Adyaan/Documents/T/MediaConverterApp/gradle/libs.versions.toml)
- Update `agp` version from `8.2.0` to `8.4.2`.

#### [MODIFY] [gradle-wrapper.properties](file:///C:/Users/Adyaan/Documents/T/MediaConverterApp/gradle/wrapper/gradle-wrapper.properties)
- Update `distributionUrl` to use Gradle `8.7`.

#### [MODIFY] [gradle.properties](file:///C:/Users/Adyaan/Documents/T/MediaConverterApp/gradle.properties)
- Remove experimental/temporary flags that were trying to bypass the issue, as they are no longer needed with AGP 8.4.2.

## Verification Plan

### Automated Tests
- Run `./gradlew clean` to ensure a fresh build environment.
- Run `./gradlew assembleDebug` to verify the build succeeds without the `JdkImageTransform` error.
