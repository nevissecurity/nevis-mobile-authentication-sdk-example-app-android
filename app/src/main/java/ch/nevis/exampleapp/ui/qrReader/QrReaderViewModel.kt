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
import ch.nevis.mobile.sdk.api.operation.pin.PinEnroller
import ch.nevis.mobile.sdk.api.operation.selection.AccountSelector
import ch.nevis.mobile.sdk.api.operation.selection.AuthenticatorSelector
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.DevicePasscodeUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.PinUserVerifier
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

/**
 * View model implementation of QR Reader view.
 */
@HiltViewModel
class QrReaderViewModel @Inject constructor(

    /**
     * An instance of a [ClientProvider] interface implementation.
     */
    clientProvider: ClientProvider,

    /**
     * An instance of a [NavigationDispatcher] interface implementation.
     */
    navigationDispatcher: NavigationDispatcher,

    /**
     * An instance of a [Settings] interface implementation.
     */
    settings: Settings,

    /**
     * An instance of a [DeviceInformationFactory] interface implementation.
     */
    deviceInformationFactory: DeviceInformationFactory,

    /**
     * An instance of an [AccountSelector] interface implementation.
     */
    accountSelector: AccountSelector,

    /**
     * An instance of an [AuthenticatorSelector] interface implementation used during registration.
     */
    @Named(ApplicationModule.REGISTRATION_AUTHENTICATOR_SELECTOR)
    registrationAuthenticatorSelector: AuthenticatorSelector,

    /**
     * An instance of a [AuthenticatorSelector] interface implementation used during authentication.
     */
    @Named(ApplicationModule.AUTHENTICATION_AUTHENTICATOR_SELECTOR)
    authenticationAuthenticatorSelector: AuthenticatorSelector,

    /**
     * An instance of a [PinEnroller] interface implementation.
     */
    pinEnroller: PinEnroller,

    /**
     * An instance of a [PinUserVerifier] interface implementation.
     */
    pinUserVerifier: PinUserVerifier,

    /**
     * An instance of a [BiometricUserVerifier] interface implementation.
     */
    biometricUserVerifier: BiometricUserVerifier,

    /**
     * An instance of a [DevicePasscodeUserVerifier] interface implementation.
     */
    devicePasscodeUserVerifier: DevicePasscodeUserVerifier,

    /**
     * An instance of a [FingerprintUserVerifier] interface implementation.
     */
    fingerprintUserVerifier: FingerprintUserVerifier,

    /**
     * An instance of an [ErrorHandler] implementation. Received errors will be passed to this error
     * handler instance.
     */
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
    pinUserVerifier,
    biometricUserVerifier,
    devicePasscodeUserVerifier,
    fingerprintUserVerifier,
    errorHandler
)
