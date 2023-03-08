/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.qrReader

import ch.nevis.exampleapp.common.error.ErrorHandler
import ch.nevis.exampleapp.domain.client.ClientProvider
import ch.nevis.exampleapp.domain.deviceInformation.DeviceInformationFactory
import ch.nevis.exampleapp.domain.interaction.AuthenticationAuthenticatorSelector
import ch.nevis.exampleapp.domain.interaction.RegistrationAuthenticatorSelector
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.outOfBand.OutOfBandViewModel
import ch.nevis.mobile.sdk.api.operation.pin.PinEnroller
import ch.nevis.mobile.sdk.api.operation.selection.AccountSelector
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.PinUserVerifier
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

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
     * An instance of a [DeviceInformationFactory] interface implementation.
     */
    deviceInformationFactory: DeviceInformationFactory,

    /**
     * An instance of a [AccountSelector] interface implementation.
     */
    accountSelector: AccountSelector,

    /**
     * An instance of a [BiometricUserVerifier] interface implementation.
     */
    biometricUserVerifier: BiometricUserVerifier,

    /**
     * An instance of a [FingerprintUserVerifier] interface implementation.
     */
    fingerprintUserVerifier: FingerprintUserVerifier,

    /**
     * An instance of a [PinUserVerifier] interface implementation.
     */
    pinUserVerifier: PinUserVerifier,

    /**
     * An instance of a [PinEnroller] interface implementation.
     */
    pinEnroller: PinEnroller,

    /**
     * An instance of a [AuthenticationAuthenticatorSelector] interface implementation.
     */
    authenticationAuthenticatorSelector: AuthenticationAuthenticatorSelector,

    /**
     * An instance of a [RegistrationAuthenticatorSelector] interface implementation.
     */
    registrationAuthenticatorSelector: RegistrationAuthenticatorSelector,

    /**
     * An instance of a [ErrorHandler] implementation. Received errors will be passed to this error
     * handler instance.
     */
    errorHandler: ErrorHandler
) : OutOfBandViewModel(
    clientProvider,
    deviceInformationFactory,
    navigationDispatcher,
    accountSelector,
    biometricUserVerifier,
    fingerprintUserVerifier,
    pinUserVerifier,
    pinEnroller,
    authenticationAuthenticatorSelector,
    registrationAuthenticatorSelector,
    errorHandler
)