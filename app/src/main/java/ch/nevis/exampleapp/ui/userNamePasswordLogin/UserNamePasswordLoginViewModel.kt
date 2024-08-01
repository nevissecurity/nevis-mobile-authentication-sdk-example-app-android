/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.userNamePasswordLogin

import androidx.lifecycle.viewModelScope
import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.common.error.ErrorHandler
import ch.nevis.exampleapp.common.settings.Settings
import ch.nevis.exampleapp.dagger.ApplicationModule
import ch.nevis.exampleapp.domain.client.ClientProvider
import ch.nevis.exampleapp.domain.deviceInformation.DeviceInformationFactory
import ch.nevis.exampleapp.domain.interaction.OnErrorImpl
import ch.nevis.exampleapp.domain.model.error.BusinessException
import ch.nevis.exampleapp.domain.model.operation.Operation
import ch.nevis.exampleapp.retrofit.LoginEndPoint
import ch.nevis.exampleapp.ui.base.BaseViewModel
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.result.parameter.ResultNavigationParameter
import ch.nevis.mobile.sdk.api.authorization.AuthorizationProvider.CookieAuthorizationProvider
import ch.nevis.mobile.sdk.api.authorization.Cookie
import ch.nevis.mobile.sdk.api.operation.password.PasswordEnroller
import ch.nevis.mobile.sdk.api.operation.pin.PinEnroller
import ch.nevis.mobile.sdk.api.operation.selection.AuthenticatorSelector
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.DevicePasscodeUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import timber.log.Timber
import java.net.PasswordAuthentication
import javax.inject.Inject
import javax.inject.Named

/**
 * View model implementation of UserName and Password Login view.
 *
 * @constructor Creates a new instance.
 * @param clientProvider An instance of a [ClientProvider] implementation.
 * @param retrofit A [Retrofit] instance.
 * @param deviceInformationFactory An instance of a [DeviceInformationFactory] implementation.
 *      used during authentication.
 * @param navigationDispatcher An instance of a [NavigationDispatcher] implementation.
 * @param settings An instance of a [Settings] implementation.
 * @param authenticatorSelector An instance of an [AuthenticatorSelector] interface implementation
 *      used during registration.
 * @param pinEnroller An instance of a [PinEnroller] implementation.
 * @param biometricUserVerifier An instance of a [BiometricUserVerifier] implementation.
 * @param devicePasscodeUserVerifier An instance of a [DevicePasscodeUserVerifier] implementation.
 * @param fingerprintUserVerifier An instance of a [FingerprintUserVerifier] implementation.
 * @param errorHandler An instance of a [ErrorHandler] implementation. */
@HiltViewModel
class UserNamePasswordLoginViewModel @Inject constructor(
    private val clientProvider: ClientProvider,
    private val retrofit: Retrofit,
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

    //region Companion Object
    /**
     * Constants.
     */
    companion object {
        private const val HEADER_SET_COOKIE = "Set-Cookie"
    }
    //endregion

    //region Public Interface
    /**
     * Starts login process with given username/password pair.
     *
     * @param passwordAuthentication The [PasswordAuthentication] object that holds the username and
     * the password be used to log in.
     */
    fun login(passwordAuthentication: PasswordAuthentication) {
        viewModelScope.launch {
            try {
                val baseUri = retrofit.baseUrl().toUri()
                val loginEndPoint = retrofit.create(LoginEndPoint::class.java)

                val retrofitResponse = loginEndPoint.authenticate(
                    passwordAuthentication.userName,
                    String(passwordAuthentication.password)
                )
                val loginResponse = retrofitResponse.body() ?: throw BusinessException.loginFailed()
                val cookies = mutableListOf<Cookie>()
                for (cookieString in retrofitResponse.headers()
                    .values(HEADER_SET_COOKIE)) {
                    cookies.add(Cookie.create(baseUri, cookieString))
                }
                register(loginResponse.extId, cookies)
            } catch (throwable: Throwable) {
                Timber.e(throwable)
                errorHandler.handle(BusinessException.loginFailed())
            }
        }
    }
    //endregion

    //region Private Interface
    /**
     * Starts in-band registration operation based on data/information received during the login
     * process.
     *
     * @param extId The external identifier of the user to be registered.
     * @param cookies HTTP session cookies to be used for the in-base registration end-point.
     */
    private fun register(extId: String, cookies: List<Cookie>) {
        try {
            val deviceInformation = deviceInformationFactory.create()
            val client = clientProvider.get() ?: throw BusinessException.clientNotInitialized()
            client.operations().registration()
                .username(extId)
                .authorizationProvider(CookieAuthorizationProvider.create(cookies))
                .deviceInformation(deviceInformation)
                .allowClass2Sensors(settings.allowClass2Sensors)
                .authenticatorSelector(authenticatorSelector)
                .pinEnroller(pinEnroller)
                .passwordEnroller(passwordEnroller)
                .biometricUserVerifier(biometricUserVerifier)
                .devicePasscodeUserVerifier(devicePasscodeUserVerifier)
                .fingerprintUserVerifier(fingerprintUserVerifier)
                .onSuccess {
                    navigationDispatcher.requestNavigation(
                        NavigationGraphDirections.actionGlobalResultFragment(
                            ResultNavigationParameter.forSuccessfulOperation(Operation.REGISTRATION)
                        )
                    )
                }
                .onError(OnErrorImpl(Operation.REGISTRATION, errorHandler))
                .execute()
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }
    //endregion
}
