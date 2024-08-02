/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.qrReader

import ch.nevis.exampleapp.common.error.ErrorHandler
import ch.nevis.exampleapp.common.settings.Settings
import ch.nevis.exampleapp.dagger.ApplicationModule
import ch.nevis.exampleapp.domain.client.ClientProvider
import ch.nevis.exampleapp.domain.deviceInformation.DeviceInformationFactory
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.outOfBand.OutOfBandViewModel
import ch.nevis.mobile.sdk.api.operation.password.PasswordEnroller
import ch.nevis.mobile.sdk.api.operation.pin.PinEnroller
import ch.nevis.mobile.sdk.api.operation.selection.AccountSelector
import ch.nevis.mobile.sdk.api.operation.selection.AuthenticatorSelector
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.DevicePasscodeUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.PasswordUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.PinUserVerifier
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

/**
 * View model implementation of QR Reader view.
 *
 * @constructor Creates a new instance.
 * @param clientProvider An instance of a [ClientProvider] implementation.
 * @param navigationDispatcher An instance of a [NavigationDispatcher] implementation.
 * @param settings An instance of a [Settings] implementation.
 * @param deviceInformationFactory An instance of a [DeviceInformationFactory] implementation.
 * @param accountSelector An instance of a [AccountSelector] implementation.
 * @param registrationAuthenticatorSelector An instance of an [AuthenticatorSelector] interface implementation
 *      used during registration.
 * @param authenticationAuthenticatorSelector An instance of an [AuthenticatorSelector] interface implementation
 *      used during authentication.
 * @param pinEnroller An instance of a [PinEnroller] implementation.
 * @param pinUserVerifier An instance of a [PinUserVerifier] implementation.
 * @param passwordUserVerifier An instance of a [PasswordUserVerifier] implementation.
 * @param biometricUserVerifier An instance of a [BiometricUserVerifier] implementation.
 * @param devicePasscodeUserVerifier An instance of a [DevicePasscodeUserVerifier] implementation.
 * @param fingerprintUserVerifier An instance of a [FingerprintUserVerifier] implementation.
 * @param errorHandler An instance of a [ErrorHandler] implementation. */
@HiltViewModel
class QrReaderViewModel @Inject constructor(
    clientProvider: ClientProvider,
    navigationDispatcher: NavigationDispatcher,
    settings: Settings,
    deviceInformationFactory: DeviceInformationFactory,
    accountSelector: AccountSelector,
    @Named(ApplicationModule.REGISTRATION_AUTHENTICATOR_SELECTOR)
    registrationAuthenticatorSelector: AuthenticatorSelector,
    @Named(ApplicationModule.AUTHENTICATION_AUTHENTICATOR_SELECTOR)
    authenticationAuthenticatorSelector: AuthenticatorSelector,
    pinEnroller: PinEnroller,
    passwordEnroller: PasswordEnroller,
    pinUserVerifier: PinUserVerifier,
    passwordUserVerifier: PasswordUserVerifier,
    biometricUserVerifier: BiometricUserVerifier,
    devicePasscodeUserVerifier: DevicePasscodeUserVerifier,
    fingerprintUserVerifier: FingerprintUserVerifier,
    errorHandler: ErrorHandler
) : OutOfBandViewModel(
    clientProvider,
    deviceInformationFactory,
    navigationDispatcher,
    settings,
    accountSelector,
    registrationAuthenticatorSelector,
    authenticationAuthenticatorSelector,
    pinEnroller,
    passwordEnroller,
    pinUserVerifier,
    passwordUserVerifier,
    biometricUserVerifier,
    devicePasscodeUserVerifier,
    fingerprintUserVerifier,
    errorHandler
)
