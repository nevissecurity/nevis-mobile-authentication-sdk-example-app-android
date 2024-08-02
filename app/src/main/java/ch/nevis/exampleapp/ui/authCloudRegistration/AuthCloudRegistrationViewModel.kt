/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.authCloudRegistration

import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.common.error.ErrorHandler
import ch.nevis.exampleapp.common.settings.Settings
import ch.nevis.exampleapp.dagger.ApplicationModule
import ch.nevis.exampleapp.domain.client.ClientProvider
import ch.nevis.exampleapp.domain.deviceInformation.DeviceInformationFactory
import ch.nevis.exampleapp.domain.interaction.OnErrorImpl
import ch.nevis.exampleapp.domain.model.error.BusinessException
import ch.nevis.exampleapp.domain.model.operation.Operation
import ch.nevis.exampleapp.ui.base.BaseViewModel
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.result.parameter.ResultNavigationParameter
import ch.nevis.mobile.sdk.api.operation.password.PasswordEnroller
import ch.nevis.mobile.sdk.api.operation.pin.PinEnroller
import ch.nevis.mobile.sdk.api.operation.selection.AuthenticatorSelector
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.DevicePasscodeUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerifier
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

/**
 * View model implementation of Auth Cloud API Registration view.
 *
 * @constructor Creates a new instance.
 * @param clientProvider An instance of a [ClientProvider] implementation.
 * @param deviceInformationFactory An instance of a [DeviceInformationFactory] implementation.
 * @param navigationDispatcher An instance of a [NavigationDispatcher] implementation.
 * @param settings An instance of a [Settings] implementation.
 * @param authenticatorSelector An instance of an [AuthenticatorSelector] interface implementation
 *      used during registration.
 * @param pinEnroller An instance of a [PinEnroller] implementation.
 * @param passwordEnroller An instance of a [PasswordEnroller] implementation.
 * @param biometricUserVerifier An instance of a [BiometricUserVerifier] implementation.
 * @param devicePasscodeUserVerifier An instance of a [DevicePasscodeUserVerifier] implementation.
 * @param fingerprintUserVerifier An instance of a [FingerprintUserVerifier] implementation.
 * @param errorHandler An instance of a [ErrorHandler] implementation.
 */
@HiltViewModel
class AuthCloudRegistrationViewModel @Inject constructor(
    private val clientProvider: ClientProvider,
    private val deviceInformationFactory: DeviceInformationFactory,
    private val navigationDispatcher: NavigationDispatcher,
    private val settings: Settings,
    @Named(ApplicationModule.REGISTRATION_AUTHENTICATOR_SELECTOR)
    private val authenticatorSelector: AuthenticatorSelector,
    private val pinEnroller: PinEnroller,
    private val passwordEnroller: PasswordEnroller,
    private val biometricUserVerifier: BiometricUserVerifier,
    private val devicePasscodeUserVerifier: DevicePasscodeUserVerifier,
    private val fingerprintUserVerifier: FingerprintUserVerifier,
    private val errorHandler: ErrorHandler
) : BaseViewModel() {

    //region Public Interface
    /**
     * Starts Auth Cloud API registration operation.
     *
     * @param enrollResponse The response of the Cloud HTTP API to the enroll endpoint.
     * @param appLinkUri The value of the `appLinkUri` attribute in the enroll response sent by the server.
     */
    fun authCloudRegistration(enrollResponse: String, appLinkUri: String) {
        val optionalEnrollResponse = enrollResponse.ifBlank {
            null
        }

        val optionalAppLinkUri = appLinkUri.ifBlank {
            null
        }

        try {
            val client = clientProvider.get() ?: throw BusinessException.clientNotInitialized()
            val deviceInformation = deviceInformationFactory.create()

            val operation = client.operations().authCloudApiRegistration()
                .authenticatorSelector(authenticatorSelector)
                .allowClass2Sensors(settings.allowClass2Sensors)
                .deviceInformation(deviceInformation)
                .pinEnroller(pinEnroller)
                .passwordEnroller(passwordEnroller)
                .fingerprintUserVerifier(fingerprintUserVerifier)
                .biometricUserVerifier(biometricUserVerifier)
                .devicePasscodeUserVerifier(devicePasscodeUserVerifier)
                .onSuccess {
                    navigationDispatcher.requestNavigation(
                        NavigationGraphDirections.actionGlobalResultFragment(
                            ResultNavigationParameter.forSuccessfulOperation(Operation.REGISTRATION)
                        )
                    )
                }
                .onError(OnErrorImpl(Operation.REGISTRATION, errorHandler))

            optionalAppLinkUri?.let {
                operation.appLinkUri(it)
            }

            optionalEnrollResponse?.let {
                operation.enrollResponse(it)
            }

            operation.execute()
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }
    //endregion
}
