/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.outOfBand

import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.common.error.ErrorHandler
import ch.nevis.exampleapp.common.settings.Settings
import ch.nevis.exampleapp.domain.client.ClientProvider
import ch.nevis.exampleapp.domain.deviceInformation.DeviceInformationFactory
import ch.nevis.exampleapp.domain.interaction.OnErrorImpl
import ch.nevis.exampleapp.domain.model.error.BusinessException
import ch.nevis.exampleapp.domain.model.operation.Operation
import ch.nevis.exampleapp.ui.base.BaseViewModel
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.result.parameter.ResultNavigationParameter
import ch.nevis.mobile.sdk.api.operation.outofband.OutOfBandAuthentication
import ch.nevis.mobile.sdk.api.operation.outofband.OutOfBandPayload
import ch.nevis.mobile.sdk.api.operation.outofband.OutOfBandRegistration
import ch.nevis.mobile.sdk.api.operation.pin.PinEnroller
import ch.nevis.mobile.sdk.api.operation.selection.AccountSelector
import ch.nevis.mobile.sdk.api.operation.selection.AuthenticatorSelector
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.DevicePasscodeUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.PinUserVerifier

/**
 * Abstract, base class that provides out-of-band payload decoding, processing and out-of-band
 * authentication, registration operation execution related common code for view model sub-classes
 * those deal with out-of-band operations.
 */
abstract class OutOfBandViewModel(
    /**
     * An instance of a [ClientProvider] interface implementation.
     */
    private val clientProvider: ClientProvider,

    /**
     * An instance of a [DeviceInformationFactory] interface implementation.
     */
    private val deviceInformationFactory: DeviceInformationFactory,

    /**
     * An instance of a [NavigationDispatcher] interface implementation.
     */
    private val navigationDispatcher: NavigationDispatcher,

    /**
     * An instance of a [Settings] interface implementation.
     */
    private val settings: Settings,

    /**
     * An instance of a [AccountSelector] interface implementation.
     */
    private val accountSelector: AccountSelector,

    /**
     * An instance of a [AuthenticatorSelector] interface implementation.
     */
    private val registrationAuthenticatorSelector: AuthenticatorSelector,

    /**
     * An instance of a [AuthenticatorSelector] interface implementation.
     */
    private val authenticationAuthenticatorSelector: AuthenticatorSelector,

    /**
     * An instance of a [PinEnroller] interface implementation.
     */
    private val pinEnroller: PinEnroller,

    /**
     * An instance of a [PinUserVerifier] interface implementation.
     */
    private val pinUserVerifier: PinUserVerifier,

    /**
     * An instance of a [BiometricUserVerifier] interface implementation.
     */
    private val biometricUserVerifier: BiometricUserVerifier,

    /**
     * An instance of a [DevicePasscodeUserVerifier] interface implementation.
     */
    private val devicePasscodeUserVerifier: DevicePasscodeUserVerifier,

    /**
     * An instance of a [FingerprintUserVerifier] interface implementation.
     */
    private val fingerprintUserVerifier: FingerprintUserVerifier,

    /**
     * An instance of an [ErrorHandler] interface implementation. Received errors will be passed to this error
     * handler instance.
     */
    private val errorHandler: ErrorHandler
) : BaseViewModel() {

    //region Public Interface
    /**
     * Starts decoding of out-of-band payload from the given dispatch token response.
     *
     * @param dispatchTokenResponse The dispatch token response to decode out-of-band payload from.
     */
    fun decodeOutOfBandPayload(dispatchTokenResponse: String) {
        try {
            val client = clientProvider.get() ?: throw BusinessException.clientNotInitialized()
            client.operations().outOfBandPayloadDecode()
                .base64UrlEncoded(dispatchTokenResponse)
                .onSuccess {
                    processOutOfBandPayload(it)
                }
                .onError(OnErrorImpl(Operation.DECODE_OUT_OF_BAND_PAYLOAD, errorHandler))
                .execute()
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }
    //endregion

    //region Private Interface
    /**
     * Starts processing of out-of-band payload.
     *
     * @param payload The [OutOfBandPayload] object to be processed.
     */
    private fun processOutOfBandPayload(payload: OutOfBandPayload) {
        try {
            val client = clientProvider.get() ?: throw BusinessException.clientNotInitialized()
            client.operations().outOfBandOperation()
                .payload(payload)
                .onAuthentication {
                    authenticate(it)
                }
                .onRegistration {
                    register(it)
                }
                .onError(OnErrorImpl(Operation.PROCESS_OUT_OF_BAND_PAYLOAD, errorHandler))
                .execute()
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }

    /**
     * Sets-up and executes the given out-of-band authentication.
     *
     * @param outOfBandAuthentication The [OutOfBandAuthentication] operation to be executed.
     */
    private fun authenticate(outOfBandAuthentication: OutOfBandAuthentication) {
        outOfBandAuthentication
            .accountSelector(accountSelector)
            .authenticatorSelector(authenticationAuthenticatorSelector)
            .pinUserVerifier(pinUserVerifier)
            .fingerprintUserVerifier(fingerprintUserVerifier)
            .biometricUserVerifier(biometricUserVerifier)
            .devicePasscodeUserVerifier(devicePasscodeUserVerifier)
            .onSuccess {
                navigationDispatcher.requestNavigation(
                    NavigationGraphDirections.actionGlobalResultFragment(
                        ResultNavigationParameter.forSuccessfulOperation(Operation.OUT_OF_BAND_AUTHENTICATION)
                    )
                )
            }
            .onError(OnErrorImpl(Operation.OUT_OF_BAND_AUTHENTICATION, errorHandler))
            .execute()
    }

    /**
     * Sets-up and executes the given out-of-band registration.
     *
     * @param outOfBandRegistration The [OutOfBandRegistration] operation to be executed.
     */
    private fun register(
        outOfBandRegistration: OutOfBandRegistration
    ) {
        outOfBandRegistration
            .deviceInformation(deviceInformationFactory.create())
            .allowClass2Sensors(settings.allowClass2Sensors)
            .authenticatorSelector(registrationAuthenticatorSelector)
            .pinEnroller(pinEnroller)
            .biometricUserVerifier(biometricUserVerifier)
            .devicePasscodeUserVerifier(devicePasscodeUserVerifier)
            .fingerprintUserVerifier(fingerprintUserVerifier)
            .onSuccess {
                navigationDispatcher.requestNavigation(
                    NavigationGraphDirections.actionGlobalResultFragment(
                        ResultNavigationParameter.forSuccessfulOperation(Operation.OUT_OF_BAND_REGISTRATION)
                    )
                )
            }
            .onError(OnErrorImpl(Operation.OUT_OF_BAND_REGISTRATION, errorHandler))
            .execute()
    }
    //endregion
}
