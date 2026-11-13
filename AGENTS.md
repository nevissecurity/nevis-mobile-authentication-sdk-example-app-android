# Nevis Mobile Authentication SDK Android Example App

## Project Overview

A native Android (Kotlin) example app that shows how to integrate the Nevis Mobile Authentication SDK, a FIDO UAF 1.1 based SDK for passwordless authentication. It is meant as a learning resource and a source of copy-paste-able integration code for customer apps. It is not a production-ready app.

Flows demonstrated:

- Client initialization (`MobileAuthenticationClientInitializer`)
- Registration
  - in-app with Authentication Cloud (`authCloudApiRegistration`, using an enrollment response or an `appLinkUri`)
  - in-app with an Identity Suite backend (`registration` with a `CookieAuthorizationProvider` after a username/password login)
  - out-of-band through a QR code, a deep link or pending operations
- Authentication: in-band, out-of-band (QR code / deep link), and transaction confirmation
- Deregistration of one account or all accounts, and deleting local authenticators
- Changing the PIN and the password
- Changing device information
- Fetching pending out-of-band operations
- Reading local data, `MetaData` (SDK version, facet ID, signing certificate) and device capabilities (FIDO UAF attestation support)

Paradigm: classic MVVM (Fragments + Jetpack `ViewModel`) using the SDK's **callback-style builder API** (`.onSuccess { }` / `.onError { }` / `.execute()`), wired up with Dagger Hilt. A few internal helpers wrap callbacks in `suspendCancellableCoroutine`. For an example that is built around Kotlin Coroutines, see the [Coroutines variant](https://github.com/nevissecurity/nevis-mobile-authentication-sdk-example-app-android-coroutines).

The example covers only part of the SDK. For features that are not shown here, use the documentation at https://docs.nevis.net/mobilesdk. Don't infer SDK behavior from memory.

## SDK & Related Resources

How the SDK is consumed:

- Maven coordinates: `ch.nevis:nevis-mobile-authentication-sdk-android-debug`. The version is set in `gradle/libs.versions.toml` (`nevis-mobile-authentication-sdk-version`, a dynamic `4.6.+` range).
- Repository: GitHub Packages at `https://maven.pkg.github.com/nevissecurity/nevis-mobile-authentication-sdk-android-package` (declared in the root `build.gradle.kts`). It needs `GH_USERNAME` and `GH_PERSONAL_ACCESS_TOKEN` (a token with the `read:packages` scope) in `~/.gradle/gradle.properties`, `local.properties` or the environment.
- That public repository only hosts the **`debug` flavor**, which has no tamper guards (no emulator, debugger or root detection). Production apps must use the **`release` flavor** (the same artifact without the `-debug` suffix), which is distributed separately. See the [Android installation guide](https://docs.nevis.net/mobilesdk/guide/installation/android-installation) and [SDK flavors](https://docs.nevis.net/mobilesdk/guide/introduction#sdk-flavors).

Documentation (https://docs.nevis.net):

- [Quickstart: build the example and use it](https://docs.nevis.net/mobilesdk/quickstart)
- [Requirements](https://docs.nevis.net/mobilesdk/guide/requirements) and [device support](https://docs.nevis.net/mobilesdk/guide/device-support)
- [Configuration](https://docs.nevis.net/mobilesdk/guide/configuration) (Authentication Cloud, Identity Suite, custom backend, facet ID)
- [Initialization](https://docs.nevis.net/mobilesdk/guide/initialization)
- Operations: [registration](https://docs.nevis.net/mobilesdk/guide/operation/registration), [authentication](https://docs.nevis.net/mobilesdk/guide/operation/authentication), [deregistration](https://docs.nevis.net/mobilesdk/guide/operation/deregistration), [other operations](https://docs.nevis.net/mobilesdk/guide/operation/other-operations)
- [Error handling](https://docs.nevis.net/mobilesdk/guide/error-handling), [best practices](https://docs.nevis.net/mobilesdk/guide/best-practices), [troubleshooting](https://docs.nevis.net/mobilesdk/appendixes/troubleshooting)
- Integration scenarios: [Authentication Cloud](https://docs.nevis.net/mobilesdk/integration-scenarios/authentication-cloud), [Identity Suite](https://docs.nevis.net/mobilesdk/integration-scenarios/identity-suite)
- [Push support](https://docs.nevis.net/mobilesdk/guide/push_support) (not implemented in this example)
- [Android API reference (Javadoc)](https://docs.nevis.net/mobilesdk/api-references/javadoc/), [all API references](https://docs.nevis.net/mobilesdk/reference/api-references)
- [Android release notes](https://docs.nevis.net/mobilesdk/release_notes/android-release-notes)
- [Authentication Cloud documentation](https://docs.nevis.net/authcloud/)

Example apps on other platforms (same feature set, useful for comparison):

- Android, Kotlin Coroutines: https://github.com/nevissecurity/nevis-mobile-authentication-sdk-example-app-android-coroutines
- iOS: https://github.com/nevissecurity/nevis-mobile-authentication-sdk-example-app-ios
- iOS, RxSwift: https://github.com/nevissecurity/nevis-mobile-authentication-sdk-example-app-ios-rx
- Flutter: https://github.com/nevissecurity/nevis-mobile-authentication-sdk-example-app-flutter
- React Native: https://github.com/nevissecurity/nevis-mobile-authentication-sdk-example-app-react

## Prerequisites & Configuration

Backend: either a **Nevis Authentication Cloud** instance (with an access key to start enrollments), or an **Identity Suite** setup (nevisFIDO behind nevisProxy/nevisAuth). `README.md` § "Getting Started" explains how to set up either one.

All configuration is **build-time only**. To point the app at your own environment:

1. **Backend host.** Edit `app/src/main/java/ch/nevis/exampleapp/dagger/ApplicationModule.kt`:
   - Authentication Cloud: replace `myinstance.mauth.nevis.cloud` in `provideAuthenticationCloudConfiguration()` (`Configuration.authCloudBuilder().hostname(...)`).
   - Identity Suite: set the host in `provideIdentitySuiteConfiguration()` (`Configuration.admin4PatternBuilder().hostname(...)`). Then switch `provideConfigurationProvider()` to `Environment.IDENTITY_SUITE` with `provideIdentitySuiteConfiguration()`. The constructor also takes `provideAuthenticatorAllowlist()` as its third argument; keep it there, even though the snippet in `README.md` omits it.
   - Custom endpoint paths: use `Configuration.builder()`. See [Configuration](https://docs.nevis.net/mobilesdk/guide/configuration).
2. **Identity Suite login (in-app registration only).** `retrofit/LoginEndPoint.kt` posts to `/auth/pwd` using nevisAuth form fields (`isiwebuserid` / `isiwebpasswd`). Adapt it to your login flow. The base URL comes from the SDK `Configuration`.
3. **Deep links.** In `app/src/main/AndroidManifest.xml`, change the `myaccessapp` custom scheme in `MainActivity`'s `VIEW` intent filter to your scheme. Only URIs that carry a `dispatchTokenResponse` query parameter get processed (`MainActivity.processIntent`). For verified HTTPS App Links, add an `autoVerify` intent filter and host `assetlinks.json` on your domain.
4. **Facet ID.** nevisFIDO / Authentication Cloud must know the app's facet ID (`android:apk-key-hash:<base64 SHA-256 of the signing cert>`), or operations fail with `UNTRUSTED_FACET_ID`. The Home screen shows the current facet ID (from `MetaData`). Note that **every build type is signed with the local `debug` keystore**, so each developer machine produces a different facet ID. See [Facet ID](https://docs.nevis.net/mobilesdk/guide/configuration#facet-id).
5. **Package name.** `applicationId` / `namespace` is `ch.nevis.exampleapp` (`app/build.gradle.kts`). Change it when you adapt the app.
6. **Authenticators.** `provideAuthenticatorAllowlist()` in `ApplicationModule` sets which AAIDs (PIN, password, fingerprint, biometric, device passcode) can be offered. `res/values/settings.xml` → `allow_class2_sensors` controls `allowClass2Sensors` (face unlock on Class 2 devices).

## Build & Run Commands

`README.md` has the full setup (JDK, GitHub token, configuration). Quick reference:

```bash
./gradlew assembleDebug        # build
./gradlew installDebug         # build and install on a running emulator / connected device
./gradlew ktlintCheck          # Kotlin lint (rules in .editorconfig, version in ktlint-plugins.properties)
./gradlew ktlintFormat         # auto-fix most lint issues
./gradlew :dokkaGenerate       # KDoc → build/dokka
```

Requirements: JDK 17 and the Gradle wrapper in the repo. The build fails right away if `GH_USERNAME` / `GH_PERSONAL_ACCESS_TOKEN` can't be resolved. The repo has no meaningful unit or instrumentation tests; to verify changes, run the app against a backend.

## Architecture

### Source layout (`app/src/main/java/ch/nevis/exampleapp/`)

```
application/       ExampleApplication (@HiltAndroidApp, Timber setup)
dagger/            ApplicationModule: SDK Configuration, environment, allowlist, all SDK interaction impls (DI)
common/
  configuration/   ConfigurationProvider(+Impl), Environment (AUTHENTICATION_CLOUD / IDENTITY_SUITE)
  error/           ErrorHandler chain (cancel → default error screen)
  settings/        Settings (allowClass2Sensors from resources)
domain/
  client/          ClientProvider(+Impl): holds the initialized MobileAuthenticationClient
  interaction/     SDK callback implementations: AccountSelector, AuthenticatorSelector,
                   Biometric/DevicePasscode/Fingerprint user verifiers, OnErrorImpl,
                   pin/ (PinEnroller, PinUserVerifier, PinChanger), password/ (same for password)
  validation/      AuthenticatorValidator (filters authenticators by allowlist, OS support, enrollment), PasswordPolicyImpl
  deviceInformation/ DeviceInformationFactory: builds DeviceInformation (device name)
  log/, model/, util/  SDK log forwarding to the UI, error/operation models, authenticator helpers
retrofit/          LoginEndPoint: Identity Suite username/password login (demo only)
ui/                One package per screen: Fragment + ViewModel (+ model/, parameter/)
  main/            MainActivity: nav host, deep link intake, on-screen log
  home/            HomeViewModel: client init, in-band auth, deregistration, PIN/password change, pending ops, metadata
  outOfBand/       OutOfBandViewModel: base class that decodes and executes out-of-band payloads
  qrReader/        CameraX + ML Kit QR scanning (subclass of OutOfBandViewModel)
  authCloudRegistration/, userNamePasswordLogin/   in-app registration for each backend type
  selectAccount/, selectAuthenticator/, credential/, verifyUser/, transactionConfirmation/   screens the SDK callbacks navigate to
  changeDeviceInformation/, result/, error/
  navigation/      NavigationDispatcher: lets non-UI interaction impls request navigation
```

Navigation lives in `app/src/main/res/navigation/navigation_graph.xml` (Jetpack Navigation + Safe Args).

### How SDK calls are wired

- **Initialization**: `HomeViewModel.initClient()` → `MobileAuthenticationClientInitializer.initializer().context(...).configuration(...).execute()`. The client is stored in `ClientProviderImpl` (a singleton), and every other ViewModel gets it from `ClientProvider.get()`.
- **Operations** start in ViewModels as `client.operations().<op>()` and are configured with the interaction objects that Hilt injects:
  - registration: `UserNamePasswordLoginViewModel`, `AuthCloudRegistrationViewModel`, `OutOfBandViewModel.register`
  - authentication: `SelectAccountViewModel.inBandAuthenticate`, `OutOfBandViewModel.authenticate`
  - deregistration: `HomeViewModel.deregister`, `SelectAccountViewModel`
  - PIN/password change: `HomeViewModel`, `SelectAccountViewModel`
  - out-of-band decoding: `OutOfBandViewModel.decodeOutOfBandPayload`; pending operations: `HomeViewModel.fetchPendingOperations`
- **SDK interaction callbacks** (`domain/interaction/`) never show UI themselves. They receive the SDK context and handler, then call `NavigationDispatcher.requestNavigation(...)` with a parameter object that carries the **SDK handler** (for example `PinNavigationParameter(pinEnrollmentHandler = handler)`). The target screen's ViewModel completes the handler, for example `CredentialViewModel.confirm` → `handler.pin(...)`, `SelectAuthenticatorViewModel` → `handler.aaid(...)`.
- **Two `AuthenticatorSelector` instances** are provided, distinguished by `@Named` (`REGISTRATION_AUTHENTICATOR_SELECTOR` / `AUTHENTICATION_AUTHENTICATOR_SELECTOR`). They filter differently through `AuthenticatorValidator`.
- **Errors**: `OnErrorImpl` → `ErrorHandler` chain → `ErrorFragment`. Cancellations are handled separately (`CancelErrorHandlerImpl`). Screens that run an operation extend `CancellableOperationViewModel`, so the back button cancels the pending SDK handler.

## Integration Guidance

When moving code into your own app:

| If you want to… | Look at | Notes |
|---|---|---|
| Add the SDK dependency | root `build.gradle.kts`, `gradle/libs.versions.toml` | Use the `release` flavor for production builds; see [installation](https://docs.nevis.net/mobilesdk/guide/installation/android-installation). |
| Configure the backend | `ApplicationModule` (`provide*Configuration`) | [Configuration](https://docs.nevis.net/mobilesdk/guide/configuration) |
| Initialize the SDK once | `HomeViewModel.initClient`, `ClientProviderImpl` | Reusable pattern: initialize once, keep a single client. |
| Register with Authentication Cloud | `AuthCloudRegistrationViewModel` | Your backend gets the enrollment response / `appLinkUri` from Authentication Cloud. |
| Register with Identity Suite | `UserNamePasswordLoginViewModel`, `LoginEndPoint` | Replace the login call with your own; keep the `AuthorizationProvider` idea. |
| Handle QR codes / deep links | `OutOfBandViewModel`, `MainActivity.processIntent` | The decode → `outOfBandOperation()` → register/authenticate logic is reusable. QR scanning (`qrReader/`) is demo scaffolding. |
| Pick the authenticator | `AuthenticatorSelectorImpl`, `AuthenticatorValidatorImpl` | Reusable filtering logic; replace the UI. |
| Enroll or verify a PIN/password | `domain/interaction/pin/`, `password/`, `CredentialViewModel` | Recoverable errors and protection status (lockout/cool-down) are handled here. Password policy: `PasswordPolicyImpl`. |
| Biometric / device passcode / fingerprint | `BiometricUserVerifierImpl`, `DevicePasscodeUserVerifierImpl`, `FingerprintUserVerifierImpl`, `VerifyUserViewModel` | Prompt texts and fallbacks are customizable. |
| Transaction confirmation | `AccountSelectorImpl`, `TransactionConfirmationViewModel` | [Transaction confirmation](https://docs.nevis.net/mobilesdk/concept/operations/transaction-confirmation) |
| Deregister / change credentials / device info | `HomeViewModel`, `SelectAccountViewModel`, `ChangeDeviceInformationViewModel` | |
| Push notifications | Not implemented | See [Push support](https://docs.nevis.net/mobilesdk/guide/push_support). |

Demo-only code that should not be copied as is:

- the on-screen log (`domain/log/`, `LogRecyclerViewAdapter`)
- showing raw technical errors to users (see [Error handling](https://docs.nevis.net/mobilesdk/guide/error-handling))
- the hard-coded `LoginEndPoint`
- the device-name generator
- the placeholder deep link scheme and host names

If something isn't covered here, check https://docs.nevis.net (guide + Javadoc) before you write code. Don't guess the API.

## Conventions & Gotchas

- **Minimum OS**: `minSdk` 24 (Android 7). The biometric authenticator needs API 29+, and the device passcode authenticator needs API 30+. `AuthenticatorValidator` hides authenticators that aren't supported by hardware/OS or aren't policy-compliant, and prefers the biometric authenticator over the legacy fingerprint one.
- **Device lock and enrollment**: biometric, fingerprint and device passcode authenticators only work if the user has a secure lock screen and enrolled biometrics. Otherwise the authenticator shows up as not enrolled, or the operation fails.
- **Emulators** work only because the `debug` SDK flavor is used. The `release` flavor detects emulators, debuggers and root, and refuses to run there. On an emulator, fingerprints can be simulated in the extended controls. QR scanning needs a camera (a virtual scene or a webcam).
- **Facet ID mismatch** (`UNTRUSTED_FACET_ID`) is the most common setup error. The facet ID changes with the signing key: debug keystore, release key, Play App Signing.
- **SDK version alignment**: the SDK version range in `libs.versions.toml` and `VERSION_NAME` in `gradle.properties` follow the SDK's major.minor version. When you upgrade, check the [release notes](https://docs.nevis.net/mobilesdk/release_notes/android-release-notes) for API changes.
- **Handlers are single-use**: every SDK handler (`PinEnrollmentHandler`, `AuthenticatorSelectionHandler`, …) must be completed or cancelled exactly once. The ViewModels set them to `null` after use. Keep that pattern, or the operation hangs or fails.
- **Credentials as `CharArray`**: PINs and passwords are passed as `CharArray`, not `String`.
- **Code style**: Kotlin, ktlint enforced (`./gradlew ktlintCheck` must pass). KDoc on public members (Dokka `reportUndocumented`). Copyright header on new files.
- **Never commit secrets**: `keystore.properties` and `local.properties` are git-ignored and must stay that way. Don't commit GitHub tokens or keystores that hold real signing keys either.
