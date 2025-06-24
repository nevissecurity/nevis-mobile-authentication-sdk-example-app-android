/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.common.configuration.ConfigurationProvider
import ch.nevis.exampleapp.common.configuration.Environment
import ch.nevis.exampleapp.common.error.ErrorHandler
import ch.nevis.exampleapp.common.settings.Settings
import ch.nevis.exampleapp.dagger.ApplicationModule
import ch.nevis.exampleapp.domain.client.ClientProvider
import ch.nevis.exampleapp.domain.deviceInformation.DeviceInformationFactory
import ch.nevis.exampleapp.domain.interaction.OnErrorImpl
import ch.nevis.exampleapp.domain.model.error.BusinessException
import ch.nevis.exampleapp.domain.model.error.MobileAuthenticationClientException
import ch.nevis.exampleapp.domain.model.operation.Operation
import ch.nevis.exampleapp.domain.util.isUserEnrolled
import ch.nevis.exampleapp.logging.sdk
import ch.nevis.exampleapp.ui.home.model.HomeViewData
import ch.nevis.exampleapp.ui.home.model.SdkAttestationInformation
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.outOfBand.OutOfBandViewModel
import ch.nevis.exampleapp.ui.result.parameter.ResultNavigationParameter
import ch.nevis.exampleapp.ui.selectAccount.parameter.SelectAccountNavigationParameter
import ch.nevis.mobile.sdk.api.MobileAuthenticationClient
import ch.nevis.mobile.sdk.api.MobileAuthenticationClientInitializer
import ch.nevis.mobile.sdk.api.devicecapabilities.FidoUafAttestationInformation.OnlySurrogateBasicSupported
import ch.nevis.mobile.sdk.api.devicecapabilities.FidoUafAttestationInformation.OnlyDefaultMode
import ch.nevis.mobile.sdk.api.devicecapabilities.FidoUafAttestationInformation.StrictMode
import ch.nevis.mobile.sdk.api.localdata.Authenticator
import ch.nevis.mobile.sdk.api.metadata.MetaData
import ch.nevis.mobile.sdk.api.operation.OperationError
import ch.nevis.mobile.sdk.api.operation.password.PasswordChanger
import ch.nevis.mobile.sdk.api.operation.password.PasswordEnroller
import ch.nevis.mobile.sdk.api.operation.pin.PinChanger
import ch.nevis.mobile.sdk.api.operation.pin.PinEnroller
import ch.nevis.mobile.sdk.api.operation.selection.AccountSelector
import ch.nevis.mobile.sdk.api.operation.selection.AuthenticatorSelector
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.DevicePasscodeUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.PasswordUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.PinUserVerifier
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.resume

/**
 * View model implementation of Home view.
 *
 * @constructor Creates a new instance.
 * @param context An Android [Context] object used for initializing [ch.nevis.mobile.sdk.api.MobileAuthenticationClient].
 * @param configurationProvider An instance of a [ConfigurationProvider] implementation.
 * @param clientProvider An instance of a [ClientProvider] implementation.
 * @param pinChanger An instance of a [PinChanger] implementation.
 * @param passwordChanger An instance of a [PasswordChanger] implementation.
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
 * @param errorHandler An instance of a [ErrorHandler] implementation.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val configurationProvider: ConfigurationProvider,
    private val clientProvider: ClientProvider,
    private val pinChanger: PinChanger,
    private val passwordChanger: PasswordChanger,
    private val navigationDispatcher: NavigationDispatcher,
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
    private val errorHandler: ErrorHandler
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
) {

    //region Public Interface
    /**
     * Starts initialization of [MobileAuthenticationClient].
     */
    fun initClient() {
        clientProvider.get()?.let {
            Timber.asTree().sdk("Client already initialized.")
            viewModelScope.launch {
                updateHomeViewData(it)
            }
            return
        }
        MobileAuthenticationClientInitializer.initializer()
            .context(context)
            .configuration(configurationProvider.configuration)
            .onSuccess {
                Timber.asTree().sdk("Client initialization succeeded.")
                clientProvider.save(it)
                viewModelScope.launch {
                    updateHomeViewData(it)
                }
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
     * Starts credential changing.
     *
     * @param authenticatorType The type of the authenticator.
     */
    fun changeCredential(authenticatorType: String) {
        try {
            val client = clientProvider.get() ?: throw BusinessException.clientNotInitialized()
            val accounts = client.localData().accounts()
            if (accounts.isEmpty()) {
                throw BusinessException.accountsNotFound()
            }

            val operation = when (authenticatorType) {
                Authenticator.PIN_AUTHENTICATOR_AAID -> Operation.CHANGE_PIN
                Authenticator.PASSWORD_AUTHENTICATOR_AAID -> Operation.CHANGE_PASSWORD
                else -> throw BusinessException.invalidState()
            }

            val authenticatorNotFoundException = when (authenticatorType) {
                Authenticator.PIN_AUTHENTICATOR_AAID -> BusinessException.pinAuthenticatorNotFound()
                Authenticator.PASSWORD_AUTHENTICATOR_AAID -> BusinessException.passwordAuthenticatorNotFound()
                else -> throw BusinessException.invalidState()
            }

            val authenticators = client.localData().authenticators()
            if (authenticators.isEmpty()) {
                throw authenticatorNotFoundException
            }

            val credentialAuthenticator = authenticators.firstOrNull { it.aaid() == authenticatorType }
            if (credentialAuthenticator == null) {
                throw authenticatorNotFoundException
            }

            val eligibleAccounts = accounts.filter {
                credentialAuthenticator.isUserEnrolled(it.username(), false)
            }.toSet()

            when (eligibleAccounts.size) {
                0 -> throw BusinessException.accountsNotFound()
                1 -> {
                    when (operation) {
                        Operation.CHANGE_PIN -> startPinChange(eligibleAccounts.first().username())
                        Operation.CHANGE_PASSWORD -> startPasswordChange(eligibleAccounts.first().username())
                        else -> throw BusinessException.invalidState()
                    }
                }
                else -> {
                    navigationDispatcher.requestNavigation(
                        NavigationGraphDirections.actionGlobalSelectAccountFragment(
                            SelectAccountNavigationParameter(operation, eligibleAccounts)
                        )
                    )
                }
            }
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
                            withContext(Dispatchers.IO) {
                                for (account in accounts) {
                                    operationError = deregisterAccount(account.username())
                                    if (operationError != null) {
                                        break
                                    }
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

    /**
     * Starts deleting local authenticators of all registered users.
     */
    fun deleteAuthenticators() {
        try {
            val client = clientProvider.get() ?: throw BusinessException.clientNotInitialized()
            val accounts = client.localData().accounts()
            if (accounts.isEmpty()) {
                throw BusinessException.accountsNotFound()
            }

            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    for (account in accounts) {
                        deleteAuthenticators(account.username())
                    }
                }

                navigationDispatcher.requestNavigation(
                    NavigationGraphDirections.actionGlobalResultFragment(
                        ResultNavigationParameter.forSuccessfulOperation(Operation.LOCAL_DATA)
                    )
                )
            }
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }
    //endregion

    //region Private Interface

    /**
     * Updates the home view data.
     *
     * @param client The [MobileAuthenticationClient] instance.
     */
    private suspend fun updateHomeViewData(client: MobileAuthenticationClient) {
        withContext(Dispatchers.IO) {
            val homeViewData = HomeViewData(
                client.localData().accounts().size,
                MetaData.mobileAuthenticationVersion().toString(),
                MetaData.applicationFacetId(context),
                MetaData.signingCertificateSha256(context),
                getAttestationInformation(client)
            )
            requestViewUpdate(homeViewData)
        }
    }

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

    /**
     * Deletes all local authenticators of an enrolled account.
     *
     * @param username The username that identifies the account whose authenticators must be deleted locally.
     */
    private suspend fun deleteAuthenticators(username: String) {
        return suspendCancellableCoroutine { cancellableContinuation ->
            val client = clientProvider.get() ?: throw BusinessException.clientNotInitialized()
            client.localData().deleteAuthenticator(username)
            cancellableContinuation.resume(Unit)
        }
    }

    /**
     * Starts the PIN change operation.
     *
     * @param username The username of the account whose PIN must be changed.
     */
    private fun startPinChange(username: String) {
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
     * Starts the Password change operation.
     *
     * @param username The username of the account whose Password must be changed.
     */
    private fun startPasswordChange(username: String) {
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
     * Retrieves the FIDO UAF attestation information.
     *
     * @param client The [MobileAuthenticationClient] instance.
     * @return An instance of [SdkAttestationInformation] or null if the information could not be retrieved.
     */
    private suspend fun getAttestationInformation(client: MobileAuthenticationClient): SdkAttestationInformation? {
        return suspendCancellableCoroutine { cancellableContinuation ->
            client.deviceCapabilities().fidoUafAttestationInformationGetter()
                .onSuccess { information ->
                    when (information) {
                        is OnlySurrogateBasicSupported -> {
                            Timber.asTree()
                                .sdk("Only surrogate basic attestation supported.")
                            if (information.cause() != null) {
                                Timber.asTree().sdk("Cause: ${information.cause()}")
                            }
                            cancellableContinuation.resume(SdkAttestationInformation.OnlySurrogateBasicSupported())
                        }
                        is OnlyDefaultMode -> {
                            Timber.asTree().sdk("Full basic default attestation mode supported.")
                            if (information.cause() != null) {
                                Timber.asTree().sdk("Cause: ${information.cause()}")
                            }
                            cancellableContinuation.resume(SdkAttestationInformation.OnlyDefaultMode())
                        }
                        is StrictMode -> {
                            Timber.asTree().sdk("Full basic strict attestation mode supported.")
                            cancellableContinuation.resume(SdkAttestationInformation.StrictMode())
                        }
                        else -> throw IllegalStateException("Unsupported attestation information type.")
                    }
                }
                .onError {
                    Timber.asTree()
                        .sdk("Getting FIDO UAF attestation information failed. Error: ${it.description()}")
                    cancellableContinuation.resume(null)
                }
                .execute()
        }
    }
    //endregion
}
