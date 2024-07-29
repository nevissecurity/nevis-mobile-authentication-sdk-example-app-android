/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.credential

import android.annotation.SuppressLint
import android.content.Context
import ch.nevis.exampleapp.common.error.ErrorHandler
import ch.nevis.exampleapp.domain.model.error.BusinessException
import ch.nevis.exampleapp.domain.util.message
import ch.nevis.exampleapp.logging.sdk
import ch.nevis.exampleapp.ui.base.CancellableOperationViewModel
import ch.nevis.exampleapp.ui.credential.model.CredentialProtectionInformation
import ch.nevis.exampleapp.ui.credential.model.CredentialViewData
import ch.nevis.exampleapp.ui.credential.model.CredentialViewMode
import ch.nevis.exampleapp.ui.credential.parameter.CredentialNavigationParameter
import ch.nevis.exampleapp.ui.credential.parameter.PasswordNavigationParameter
import ch.nevis.exampleapp.ui.credential.parameter.PinNavigationParameter
import ch.nevis.mobile.sdk.api.localdata.Authenticator
import ch.nevis.mobile.sdk.api.operation.password.PasswordAuthenticatorProtectionStatus
import ch.nevis.mobile.sdk.api.operation.password.PasswordChangeHandler
import ch.nevis.mobile.sdk.api.operation.password.PasswordEnrollmentHandler
import ch.nevis.mobile.sdk.api.operation.pin.PinAuthenticatorProtectionStatus
import ch.nevis.mobile.sdk.api.operation.pin.PinChangeHandler
import ch.nevis.mobile.sdk.api.operation.pin.PinEnrollmentHandler
import ch.nevis.mobile.sdk.api.operation.userverification.PasswordUserVerificationHandler
import ch.nevis.mobile.sdk.api.operation.userverification.PinUserVerificationHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

/**
 * View model implementation of Credential view.
 */
@HiltViewModel
class CredentialViewModel @Inject constructor(
    /**
     * An Android [Context] object for [String] resource resolving.
     */
    @ApplicationContext
    private val context: Context,

    /**
     * An instance of an [ErrorHandler] interface implementation. Received errors will be passed to
     * this error handler instance.
     */
    private val errorHandler: ErrorHandler
) : CancellableOperationViewModel() {

    /**
     * The mode, the Credential view intend to be used/initialized.
     */
    private var credentialViewMode: CredentialViewMode? = null

    /**
     * An instance of a [PinChangeHandler] in case a PIN change operation is started and we navigate
     * to Credential view to ask the user to enter the old and new PINs to be able to continue the operation.
     * [CredentialViewModel.credentialViewMode] must be [CredentialViewMode.CHANGE].
     */
    private var pinChangeHandler: PinChangeHandler? = null

    /**
     * An instance of a [PinEnrollmentHandler] in case a PIN enrollment is started as part of a
     * registration operation and we navigate to Credential view to ask the user to enter, define the
     * PIN to be able to continue the operation.
     * [CredentialViewModel.credentialViewMode] must be [CredentialViewMode.ENROLLMENT].
     */
    private var pinEnrollmentHandler: PinEnrollmentHandler? = null

    /**
     * An instance of a [PinUserVerificationHandler] in case a PIN verification is started as part of
     * an authentication operation and we navigate to Credential view to ask the user to enter the PIN
     * to be able to continue the operation.
     * [CredentialViewModel.credentialViewMode] must be [CredentialViewMode.VERIFICATION].
     */
    private var pinUserVerificationHandler: PinUserVerificationHandler? = null

    /**
     * An instance of a [PasswordChangeHandler] in case a Password change operation is started and we
     * navigate to Credential view to ask the user to enter the old and new passwords to be able to
     * continue the operation.
     * [CredentialViewModel.credentialViewMode] must be [CredentialViewMode.CHANGE].
     */
    private var passwordChangeHandler: PasswordChangeHandler? = null

    /**
     * An instance of a [PasswordEnrollmentHandler] in case a Password enrollment is started as part
     * of a registration operation and we navigate to Credential view to ask the user to enter, define
     * the password to be able to continue the operation.
     * [CredentialViewModel.credentialViewMode] must be [CredentialViewMode.ENROLLMENT].
     */
    private var passwordEnrollmentHandler: PasswordEnrollmentHandler? = null

    /**
     * An instance of a [PasswordUserVerificationHandler] in case a Password verification is started
     * as part of an authentication operation and we navigate to Credential view to ask the user to
     * enter the password to be able to continue the operation.
     * [CredentialViewModel.credentialViewMode] must be [CredentialViewMode.VERIFICATION].
     */
    private var passwordUserVerificationHandler: PasswordUserVerificationHandler? = null

    /**
     * The type of the credential.
     */
    private lateinit var credentialType: String

    //region Public Interface
    /**
     * Updates this view model instance based on the [CredentialNavigationParameter] that was received
     * by the owner [CredentialFragment]. This method must be called by the owner fragment.
     *
     * @param parameter The [CredentialNavigationParameter] that was received by the owner [CredentialFragment].
     */
    fun updateViewModel(parameter: CredentialNavigationParameter) {
        credentialViewMode = parameter.credentialViewMode

        try {
            updateHandlers(parameter)
            val credentialViewData = CredentialViewData(
                parameter.credentialViewMode,
                credentialType,
                getProtectionInfo(parameter),
                parameter.lastRecoverableError
            )
            requestViewUpdate(credentialViewData)
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }

    /**
     * Confirms the given credentials by invoking the corresponding handlers with the received credential.
     *
     * @param oldCredential The text entered into old credential text field. It is only used when the current [CredentialViewMode]
     * is [CredentialViewMode.CHANGE] otherwise it will be ignored.
     * @param credential The text entered into credential text field.
     */
    fun confirm(oldCredential: CharArray, credential: CharArray) {
        try {
            when (credentialViewMode) {
                CredentialViewMode.CHANGE -> {
                    pinChangeHandler?.pins(oldCredential, credential)
                    this.pinChangeHandler = null
                    passwordChangeHandler?.passwords(oldCredential, credential)
                    this.passwordChangeHandler = null
                }
                CredentialViewMode.ENROLLMENT -> {
                    pinEnrollmentHandler?.pin(credential)
                    this.pinEnrollmentHandler = null
                    passwordEnrollmentHandler?.password(credential)
                    this.passwordEnrollmentHandler = null
                }
                CredentialViewMode.VERIFICATION -> {
                    pinUserVerificationHandler?.verifyPin(credential)
                    this.pinUserVerificationHandler = null
                    passwordUserVerificationHandler?.verifyPassword(credential)
                    this.passwordUserVerificationHandler = null
                }
                else -> throw BusinessException.invalidState()
            }
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }
    //endregion

    //region Private Interface

    /**
     * Updates the different handlers based on the navigation parameter.
     *
     * @param parameter The [CredentialNavigationParameter] that was received by the owner [CredentialFragment].
     */
    private fun updateHandlers(parameter: CredentialNavigationParameter) {
        when (parameter) {
            is PinNavigationParameter -> {
                credentialType = Authenticator.PIN_AUTHENTICATOR_AAID
                pinChangeHandler = parameter.pinChangeHandler
                pinEnrollmentHandler = parameter.pinEnrollmentHandler
                pinUserVerificationHandler = parameter.pinUserVerificationHandler
            }
            is PasswordNavigationParameter -> {
                credentialType = Authenticator.PASSWORD_AUTHENTICATOR_AAID
                passwordChangeHandler = parameter.passwordChangeHandler
                passwordEnrollmentHandler = parameter.passwordEnrollmentHandler
                passwordUserVerificationHandler = parameter.passwordUserVerificationHandler
            }
            else -> throw BusinessException.invalidState()
        }
    }

    /**
     * Creates the credential protection information only if authenticator protection status is present.
     *
     * @param parameter The [CredentialNavigationParameter] that was received by the owner [CredentialFragment].
     * @return The created [CredentialProtectionInformation] instance.
     */
    private fun getProtectionInfo(parameter: CredentialNavigationParameter): CredentialProtectionInformation? {
        return when (parameter) {
            is PinNavigationParameter -> {
                parameter.pinAuthenticatorProtectionStatus?.let {
                    getPinProtectionInfo(it)
                }
            }
            is PasswordNavigationParameter -> {
                parameter.passwordAuthenticatorProtectionStatus?.let {
                    getPasswordProtectionInfo(it)
                }
            }
            else -> throw BusinessException.invalidState()
        }
    }

    /**
     * Creates PIN authenticator specific credential protection information.
     *
     * @param protectionStatus The [PinAuthenticatorProtectionStatus] that presented in the received [CredentialNavigationParameter].
     * @return The PIN authenticator specific [CredentialProtectionInformation] instance.
     */
    @SuppressLint("DefaultLocale")
    private fun getPinProtectionInfo(protectionStatus: PinAuthenticatorProtectionStatus): CredentialProtectionInformation {
        return when (protectionStatus) {
            is PinAuthenticatorProtectionStatus.Unlocked -> {
                Timber.asTree().sdk("PIN authenticator is unlocked.")
                CredentialProtectionInformation()
            }
            is PinAuthenticatorProtectionStatus.LastAttemptFailed -> {
                Timber.asTree().sdk("Last attempt failed using the PIN authenticator.")
                Timber.asTree()
                    .sdk(String.format(
                        "Remaining tries: %d, cool down period: %d",
                        protectionStatus.remainingRetries(),
                        protectionStatus.coolDownTimeInSeconds())
                    )
                CredentialProtectionInformation(
                    isLocked = protectionStatus.coolDownTimeInSeconds() > 0,
                    remainingRetries = protectionStatus.remainingRetries(),
                    coolDownTime = protectionStatus.coolDownTimeInSeconds(),
                )
            }
            is PinAuthenticatorProtectionStatus.LockedOut -> {
                Timber.asTree().sdk("PIN authenticator is locked.")
                CredentialProtectionInformation(
                    isLocked = true,
                    message = protectionStatus.message(context)
                )
            }
            else -> throw IllegalStateException("Unsupported PIN authenticator protection status.")
        }
    }

    /**
     * Creates Password authenticator specific credential protection information.
     *
     * @param protectionStatus The [PasswordAuthenticatorProtectionStatus] that presented in the received [CredentialNavigationParameter].
     * @return The Password authenticator specific [CredentialProtectionInformation] instance.
     */
    @SuppressLint("DefaultLocale")
    private fun getPasswordProtectionInfo(protectionStatus: PasswordAuthenticatorProtectionStatus): CredentialProtectionInformation {
        return when (protectionStatus) {
            is PasswordAuthenticatorProtectionStatus.Unlocked -> {
                Timber.asTree().sdk("Password authenticator is unlocked.")
                CredentialProtectionInformation()
            }
            is PasswordAuthenticatorProtectionStatus.LastAttemptFailed -> {
                Timber.asTree().sdk("Last attempt failed using the Password authenticator.")
                Timber.asTree()
                    .sdk(String.format(
                        "Remaining tries: %d, cool down period: %d",
                        protectionStatus.remainingRetries(),
                        protectionStatus.coolDownTimeInSeconds())
                    )
                CredentialProtectionInformation(
                    isLocked = protectionStatus.coolDownTimeInSeconds() > 0,
                    remainingRetries = protectionStatus.remainingRetries(),
                    coolDownTime = protectionStatus.coolDownTimeInSeconds(),
                )
            }
            is PasswordAuthenticatorProtectionStatus.LockedOut -> {
                Timber.asTree().sdk("Password authenticator is locked.")
                CredentialProtectionInformation(
                    isLocked = true,
                    message = protectionStatus.message(context)
                )
            }
            else -> throw IllegalStateException("Unsupported Password authenticator protection status.")
        }
    }
    //endregion

    //region CancellableOperationViewModel
    override fun cancelOperation() {
        pinChangeHandler?.cancel()
        pinChangeHandler = null

        pinEnrollmentHandler?.cancel()
        pinEnrollmentHandler = null

        pinUserVerificationHandler?.cancel()
        pinUserVerificationHandler = null

        passwordChangeHandler?.cancel()
        passwordChangeHandler = null

        passwordEnrollmentHandler?.cancel()
        passwordEnrollmentHandler = null

        passwordUserVerificationHandler?.cancel()
        passwordUserVerificationHandler = null
    }
    //endregion
}
