# Module nevis-mobile-authentication-example-app

## Description

The example app demonstrating how to use the Nevis Mobile Authentication SDK in an Android mobile application.
The Nevis Mobile Authentication SDK allows you to integrate passwordless authentication to your existing mobile app, backed by the FIDO UAF 1.1 Standard.

Some SDK features demonstrated in this example app are:

- Using the SDK with the Nevis Authentication Cloud
- Registering with QR code & app link URIs
- Simulating in-band authentication after registration
- Deregistering a registered account
- Changing the PIN of the PIN authenticator
- Changing the device information

Please note that the example app only demonstrates a subset of the available SDK features. The main purpose is to demonstrate how the SDK can be used, not to cover all supported scenarios.

# Package ch.nevis.exampleapp.application

This package contains a sub-class of `Application` to enable Dagger Hilt capabilities and to initialize logging.

# Package ch.nevis.exampleapp.common.configuration

This package contains the configuration possibilities of the Nevis Mobile Authentication SDK.

# Package ch.nevis.exampleapp.common.error

This package contains the error handling related classes.

# Package ch.nevis.exampleapp.common.settings

This package contains the application settings possibilities.

# Package ch.nevis.exampleapp.dagger

This package contains the main Dagger Hilt configuration module.

# Package ch.nevis.exampleapp.domain.client

This package contains the `MobileAuthenticationClient` provider.

# Package ch.nevis.exampleapp.domain.deviceInformation

This package contains the `DeviceInformation` factory.

# Package ch.nevis.exampleapp.domain.interaction

This package contains the default implementation of interaction related interfaces from the Nevis Mobile Authentication SDK, like account and authenticator selection and user verification.

# Package ch.nevis.exampleapp.domain.interaction.password

This package contains the default implementation of password interaction related interfaces from the Nevis Mobile Authentication SDK, like changing, enrolling and verifying the password.

# Package ch.nevis.exampleapp.domain.interaction.pin

This package contains the default implementation of PIN interaction related interfaces from the Nevis Mobile Authentication SDK, like changing, enrolling and verifying the PIN.

# Package ch.nevis.exampleapp.domain.log

This package contains logging related implementations.

# Package ch.nevis.exampleapp.domain.model.error

This package contains error handling related model definitions.

# Package ch.nevis.exampleapp.domain.model.operation

This package contains operation related model definitions.

# Package ch.nevis.exampleapp.domain.model.sdk

This package contains Nevis Mobile Authentication SDK related model definitions.

# Package ch.nevis.exampleapp.domain.util

This package contains Nevis Mobile Authentication SDK related extension definitions.

# Package ch.nevis.exampleapp.domain.validation

This package contains validation related logic.

# Package ch.nevis.exampleapp.logging

This package contains the `Timber` logging framework integration.

# Package ch.nevis.exampleapp.retrofit

This package contains the `Retrofit` networking framework integration.

# Package ch.nevis.exampleapp.retrofit.model

This package contains the `Retrofit` model definitions.

# Package ch.nevis.exampleapp.ui.authCloudRegistration

This package contains the view and view model definitions for the Auth Cloud API Registration operation.

# Package ch.nevis.exampleapp.ui.base

This package contains the base view and view model definitions.

# Package ch.nevis.exampleapp.ui.base.model

This package contains the base navigation parameter and view data definitions.

# Package ch.nevis.exampleapp.ui.changeDeviceInformation

This package contains the view and view model definitions for the Change Device Information operation.

# Package ch.nevis.exampleapp.ui.changeDeviceInformation.model

This package contains the model definitions for the Change Device Information operation.

# Package ch.nevis.exampleapp.ui.credential

This package contains the view and view model definitions for credential related operations, like change, enroll or verify a PIN or password.

# Package ch.nevis.exampleapp.ui.credential.model

This package contains the model definitions for credential related operations.

# Package ch.nevis.exampleapp.ui.credential.parameter

This package contains the navigation parameter definitions for credential related operations.

# Package ch.nevis.exampleapp.ui.error

This package contains the view definition for error handling.

# Package ch.nevis.exampleapp.ui.error.parameter

This package contains the navigation parameter definitions for error handling.

# Package ch.nevis.exampleapp.ui.home

This package contains the view and view model definitions for the Home feature.

# Package ch.nevis.exampleapp.ui.home.model

This package contains the model definitions for the Home feature.

# Package ch.nevis.exampleapp.ui.main

This package contains the main activity and view model definitions.

# Package ch.nevis.exampleapp.ui.navigation

This package contains the navigation implementation.

# Package ch.nevis.exampleapp.ui.outOfBand

This package contains the base view model definition for Out-of-Band operations.

# Package ch.nevis.exampleapp.ui.qrReader

This package contains the view and view model definitions for Qr Code reading.

# Package ch.nevis.exampleapp.ui.result

This package contains the view definition for displaying operation result.

# Package ch.nevis.exampleapp.ui.result.parameter

This package contains the navigation parameter definitions for displaying operation result.

# Package ch.nevis.exampleapp.ui.selectAccount

This package contains the view and view model definitions for account selection.

# Package ch.nevis.exampleapp.ui.selectAccount.parameter

This package contains the navigation parameter definitions for account selection.

# Package ch.nevis.exampleapp.ui.selectAuthenticator

This package contains the view and view model definitions for authenticator selection.

# Package ch.nevis.exampleapp.ui.selectAuthenticator.model

This package contains the model definitions for authenticator selection.

# Package ch.nevis.exampleapp.ui.selectAuthenticator.parameter

This package contains the navigation parameter definitions for authenticator selection.

# Package ch.nevis.exampleapp.ui.transactionConfirmation

This package contains the view and view model definitions for transaction confirmation.

# Package ch.nevis.exampleapp.ui.transactionConfirmation.model

This package contains the model definitions for transaction confirmation.

# Package ch.nevis.exampleapp.ui.transactionConfirmation.parameter

This package contains the navigation parameter definitions for transaction confirmation.

# Package ch.nevis.exampleapp.ui.userNamePasswordLogin

This package contains the view and view model definitions for In-Band Registration operation.

# Package ch.nevis.exampleapp.ui.util

This package contains utility methods.

# Package ch.nevis.exampleapp.ui.verifyUser

This package contains the view and view model definitions for user verification.

# Package ch.nevis.exampleapp.ui.verifyUser.model

This package contains the model definitions for user verification.

# Package ch.nevis.exampleapp.ui.verifyUser.parameter

This package contains the navigation parameter definitions for user verification.
