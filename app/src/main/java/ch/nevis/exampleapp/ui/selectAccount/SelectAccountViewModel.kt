/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.selectAccount

import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.common.error.ErrorHandler
import ch.nevis.exampleapp.dagger.ApplicationModule
import ch.nevis.exampleapp.domain.client.ClientProvider
import ch.nevis.exampleapp.domain.interaction.OnErrorImpl
import ch.nevis.exampleapp.domain.model.error.BusinessException
import ch.nevis.exampleapp.domain.model.operation.Operation
import ch.nevis.exampleapp.ui.base.CancellableOperationViewModel
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.result.parameter.ResultNavigationParameter
import ch.nevis.exampleapp.ui.selectAccount.parameter.SelectAccountNavigationParameter
import ch.nevis.mobile.sdk.api.operation.password.PasswordChanger
import ch.nevis.mobile.sdk.api.operation.pin.PinChanger
import ch.nevis.mobile.sdk.api.operation.selection.AccountSelectionHandler
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
 * View model implementation for Select Account view.
 *
 * @constructor Creates a new instance.
 * @param clientProvider An instance of a [ClientProvider] implementation.
 * @param navigationDispatcher An instance of a [NavigationDispatcher] implementation.
 * @param authenticatorSelector An instance of an [AuthenticatorSelector] interface implementation.
 * @param pinUserVerifier An instance of a [PinUserVerifier] implementation.
 * @param passwordUserVerifier An instance of a [PasswordUserVerifier] implementation.
 * @param biometricUserVerifier An instance of a [BiometricUserVerifier] implementation.
 * @param devicePasscodeUserVerifier An instance of a [DevicePasscodeUserVerifier] implementation.
 * @param fingerprintUserVerifier An instance of a [FingerprintUserVerifier] implementation.
 * @param pinChanger An instance of a [PinChanger] implementation.
 * @param passwordChanger An instance of a [PasswordChanger] implementation.
 * @param errorHandler An instance of a [ErrorHandler] implementation. */
@HiltViewModel
class SelectAccountViewModel @Inject constructor(
    private val clientProvider: ClientProvider,
    private val navigationDispatcher: NavigationDispatcher,
    @Named(ApplicationModule.AUTHENTICATION_AUTHENTICATOR_SELECTOR)
    private val authenticatorSelector: AuthenticatorSelector,
    private val pinUserVerifier: PinUserVerifier,
    private val passwordUserVerifier: PasswordUserVerifier,
    private val biometricUserVerifier: BiometricUserVerifier,
    private val devicePasscodeUserVerifier: DevicePasscodeUserVerifier,
    private val fingerprintUserVerifier: FingerprintUserVerifier,
    private val pinChanger: PinChanger,
    private val passwordChanger: PasswordChanger,
    private val errorHandler: ErrorHandler
) : CancellableOperationViewModel() {

    //region Properties
    /**
     * An instance of an [AccountSelectionHandler] in case an out-of-band authentication is started and we navigate to
     * Select Account view to ask the user to select one of the available accounts to be able to continue the operation.
     */
    private var accountSelectionHandler: AccountSelectionHandler? = null
    //endregion

    //region Public Interface
    /**
     * Updates this view model instance based on the [SelectAccountNavigationParameter] that was received by
     * the owner [SelectAccountFragment]. This method must be called by the owner fragment.
     *
     * @param parameter The [SelectAccountNavigationParameter] that was received by the owner [SelectAccountFragment].
     */
    fun updateViewModel(parameter: SelectAccountNavigationParameter) {
        accountSelectionHandler = parameter.accountSelectionHandler
    }

    /**
     * Selects account for the given operation and user. The supported operations are
     * [Operation.AUTHENTICATION], [Operation.DEREGISTRATION], [Operation.CHANGE_PIN],
     * [Operation.CHANGE_PASSWORD] and [Operation.OUT_OF_BAND_AUTHENTICATION].
     *
     * @param operation The operation the account selected for.
     * @param username The username assigned to the selected account.
     */
    fun selectAccount(operation: Operation, username: String) {
        try {
            when (operation) {
                Operation.AUTHENTICATION -> inBandAuthentication(username)
                Operation.DEREGISTRATION -> inBandAuthenticationForDeregistration(username)
                Operation.CHANGE_PIN -> changePin(username)
                Operation.CHANGE_PASSWORD -> changePassword(username)
                Operation.OUT_OF_BAND_AUTHENTICATION -> outOfBandAuthentication(username)
                else -> throw BusinessException.invalidState()
            }
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }
    //endregion

    //region Private interface
    /**
     * Starts PIN change.
     *
     * @param username The username that identifies the account the PIN change must be started for.
     */
    private fun changePin(username: String) {
        val client = clientProvider.get() ?: throw BusinessException.clientNotInitialized()
        client.operations().pinChange()
            .username(username)
            .pinChanger(pinChanger)
            .onSuccess {
                navigationDispatcher.requestNavigation(
                    NavigationGraphDirections.actionGlobalResultFragment(
                        ResultNavigationParameter.forSuccessfulOperation(Operation.CHANGE_PIN)
                    )
                )
            }
            .onError(OnErrorImpl(Operation.CHANGE_PIN, errorHandler))
            .execute()
    }

    /**
     * Starts Password change.
     *
     * @param username The username that identifies the account the Password change must be started for.
     */
    private fun changePassword(username: String) {
        val client = clientProvider.get() ?: throw BusinessException.clientNotInitialized()
        client.operations().passwordChange()
            .username(username)
            .passwordChanger(passwordChanger)
            .onSuccess {
                navigationDispatcher.requestNavigation(
                    NavigationGraphDirections.actionGlobalResultFragment(
                        ResultNavigationParameter.forSuccessfulOperation(Operation.CHANGE_PASSWORD)
                    )
                )
            }
            .onError(OnErrorImpl(Operation.CHANGE_PASSWORD, errorHandler))
            .execute()
    }

    /**
     * Starts an in-band authentication.
     *
     * @param username The username that identifies the account the in-band authentication must be started for.
     */
    private fun inBandAuthentication(username: String) {
        val client = clientProvider.get() ?: throw BusinessException.clientNotInitialized()
        client.operations().authentication()
            .username(username)
            .authenticatorSelector(authenticatorSelector)
            .pinUserVerifier(pinUserVerifier)
            .passwordUserVerifier(passwordUserVerifier)
            .biometricUserVerifier(biometricUserVerifier)
            .devicePasscodeUserVerifier(devicePasscodeUserVerifier)
            .fingerprintUserVerifier(fingerprintUserVerifier)
            .onSuccess {
                navigationDispatcher.requestNavigation(
                    NavigationGraphDirections.actionGlobalResultFragment(
                        ResultNavigationParameter.forSuccessfulOperation(Operation.AUTHENTICATION)
                    )
                )
            }
            .onError(OnErrorImpl(Operation.AUTHENTICATION, errorHandler))
            .execute()
    }

    /**
     * Starts an in-band authentication as a pre-step for an identity suite environment de-registration.
     *
     * @param username The username that identifies the account the de-registration must be started for.
     */
    private fun inBandAuthenticationForDeregistration(username: String) {
        val client = clientProvider.get() ?: throw BusinessException.clientNotInitialized()
        client.operations().authentication()
            .username(username)
            .authenticatorSelector(authenticatorSelector)
            .pinUserVerifier(pinUserVerifier)
            .passwordUserVerifier(passwordUserVerifier)
            .biometricUserVerifier(biometricUserVerifier)
            .devicePasscodeUserVerifier(devicePasscodeUserVerifier)
            .fingerprintUserVerifier(fingerprintUserVerifier)
            .onSuccess {
                client.operations().deregistration()
                    .username(username)
                    .authorizationProvider(it)
                    .onSuccess {
                        navigationDispatcher.requestNavigation(
                            NavigationGraphDirections.actionGlobalResultFragment(
                                ResultNavigationParameter.forSuccessfulOperation(Operation.DEREGISTRATION)
                            )
                        )
                    }
                    .onError(OnErrorImpl(Operation.DEREGISTRATION, errorHandler))
                    .execute()
            }
            .onError(OnErrorImpl(Operation.DEREGISTRATION, errorHandler))
            .execute()
    }

    /**
     * Continues an out-of-band authentication.
     *
     * @param username The username that identifies the account the out-of-band authentication must be started for.
     */
    private fun outOfBandAuthentication(username: String) {
        val accountSelectionHandler =
            this.accountSelectionHandler ?: throw BusinessException.invalidState()
        accountSelectionHandler.username(username)
        this.accountSelectionHandler = null
    }
    //endregion

    //region CancellableOperationViewModel
    override fun cancelOperation() {
        accountSelectionHandler?.cancel()
        accountSelectionHandler = null
    }
    //endregion
}
