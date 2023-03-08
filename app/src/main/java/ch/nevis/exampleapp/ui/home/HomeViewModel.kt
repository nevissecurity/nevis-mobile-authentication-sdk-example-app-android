/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.common.configuration.ConfigurationProvider
import ch.nevis.exampleapp.common.configuration.Environment
import ch.nevis.exampleapp.common.error.ErrorHandler
import ch.nevis.exampleapp.domain.client.ClientProvider
import ch.nevis.exampleapp.domain.deviceInformation.DeviceInformationFactory
import ch.nevis.exampleapp.domain.interaction.AuthenticationAuthenticatorSelector
import ch.nevis.exampleapp.domain.interaction.OnErrorImpl
import ch.nevis.exampleapp.domain.interaction.RegistrationAuthenticatorSelector
import ch.nevis.exampleapp.domain.model.error.BusinessException
import ch.nevis.exampleapp.domain.model.error.MobileAuthenticationClientException
import ch.nevis.exampleapp.domain.model.operation.Operation
import ch.nevis.exampleapp.ui.home.model.HomeViewData
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.outOfBand.OutOfBandViewModel
import ch.nevis.exampleapp.ui.result.parameter.ResultNavigationParameter
import ch.nevis.exampleapp.ui.selectAccount.parameter.SelectAccountNavigationParameter
import ch.nevis.mobile.sdk.api.MobileAuthenticationClientInitializer
import ch.nevis.mobile.sdk.api.localdata.Account
import ch.nevis.mobile.sdk.api.localdata.Authenticator
import ch.nevis.mobile.sdk.api.operation.OperationError
import ch.nevis.mobile.sdk.api.operation.pin.PinEnroller
import ch.nevis.mobile.sdk.api.operation.selection.AccountSelector
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.PinUserVerifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * View model implementation of Home view.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(

    /**
     * The current Android [Application] instance.
     */
    private val application: Application,

    /**
     * An instance of a [ConfigurationProvider] implementation.
     */
    private val configurationProvider: ConfigurationProvider,

    /**
     * An instance of a [ClientProvider] implementation.
     */
    private val clientProvider: ClientProvider,

    /**
     * An instance of a [NavigationDispatcher] interface implementation.
     */
    private val navigationDispatcher: NavigationDispatcher,

    /**
     * An instance of a [DeviceInformationFactory] implementation.
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
     * An instance of an [ErrorHandler] interface implementation. Received errors will be passed to this error
     * handler instance.
     */
    private val errorHandler: ErrorHandler
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
) {

    //region Public Interface
    /**
     * Starts initialization of [ch.nevis.mobile.sdk.api.MobileAuthenticationClient].
     */
    fun initClient() {
        MobileAuthenticationClientInitializer.initializer()
            .application(application)
            .configuration(configurationProvider.configuration)
            .onSuccess {
                clientProvider.save(it)
                requestViewUpdate(HomeViewData(it.localData().accounts().size))
            }
            .onError(OnErrorImpl(Operation.INIT_CLIENT, errorHandler))
            .execute()
    }

    /**
     * Starts an in-band authentication operation.
     */
    fun inBandAuthentication() {
        try {
            val client = clientProvider.get() ?: throw BusinessException.clientNotInitialized()
            val accounts = client.localData().accounts()
            if (accounts.isEmpty()) {
                throw BusinessException.accountsNotFound()
            }

            navigationDispatcher.requestNavigation(
                NavigationGraphDirections.actionGlobalSelectAccountFragment(
                    SelectAccountNavigationParameter(
                        Operation.AUTHENTICATION,
                        accounts
                    )
                )
            )
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }

    /**
     * Starts a change PIN operation.
     */
    fun changePin() {
        try {
            val client = clientProvider.get() ?: throw BusinessException.clientNotInitialized()
            var accounts: Set<Account>? = null
            client.localData().authenticators().forEach {
                if (it.aaid() == Authenticator.PIN_AUTHENTICATOR_AAID) {
                    accounts = it.registration().registeredAccounts()
                }
            }

            if (accounts.isNullOrEmpty()) {
                throw BusinessException.accountsNotFound()
            }

            accounts?.also {
                navigationDispatcher.requestNavigation(
                    NavigationGraphDirections.actionGlobalSelectAccountFragment(
                        SelectAccountNavigationParameter(
                            Operation.CHANGE_PIN,
                            it
                        )
                    )
                )
            } ?: throw BusinessException.invalidState()
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }

    /**
     * Starts de-registration operation.
     *
     * **NOTE**: The de-registration process is executed differently for Authentication Cloud and
     * Identity Suite environments.
     * - In case of Authentication Cloud all of the registered accounts will be de-registered one by one.
     * - In case of Identity Suite only one account can be de-registered at a time, this is the reason the
     * user will be asked to select the account first she/he wants to de-register.
     */
    fun deregister() {
        try {
            val client = clientProvider.get() ?: throw BusinessException.clientNotInitialized()
            val accounts = client.localData().accounts()
            if (accounts.isEmpty()) {
                throw BusinessException.accountsNotFound()
            }

            when (configurationProvider.environment) {
                Environment.AUTHENTICATION_CLOUD -> {
                    viewModelScope.launch {
                        try {
                            var operationError: OperationError? = null
                            for (account in accounts) {
                                operationError = deregisterAccount(account.username())
                                if (operationError != null) {
                                    break
                                }
                            }

                            operationError?.also {
                                errorHandler.handle(
                                    MobileAuthenticationClientException(
                                        Operation.DEREGISTRATION,
                                        it
                                    )
                                )
                            } ?: navigationDispatcher.requestNavigation(
                                NavigationGraphDirections.actionGlobalResultFragment(
                                    ResultNavigationParameter.forSuccessfulOperation(Operation.DEREGISTRATION)
                                )
                            )
                        } catch (exception: Exception) {
                            errorHandler.handle(exception)
                        }
                    }
                }
                Environment.IDENTITY_SUITE -> {
                    navigationDispatcher.requestNavigation(
                        NavigationGraphDirections.actionGlobalSelectAccountFragment(
                            SelectAccountNavigationParameter(
                                Operation.DEREGISTRATION,
                                accounts
                            )
                        )
                    )
                }
            }
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }
    //endregion

    //region Private Interface
    /**
     * De-registers an account in case of Authentication Cloud environment.
     *
     * @param username The username that identifies the account that must be de-registered.
     * @return A [OperationError] object if the de-registration failed or null if the de-registration
     * was successful.
     */
    private suspend fun deregisterAccount(username: String): OperationError? {
        return suspendCancellableCoroutine { cancellableContinuation ->
            val client = clientProvider.get() ?: throw BusinessException.clientNotInitialized()
            client.operations().deregistration()
                .username(username)
                .onSuccess {
                    cancellableContinuation.resume(null)
                }
                .onError {
                    cancellableContinuation.resume(it)
                }
                .execute()
        }
    }
    //endregion
}