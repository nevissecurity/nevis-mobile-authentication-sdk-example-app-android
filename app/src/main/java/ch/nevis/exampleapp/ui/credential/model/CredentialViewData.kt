/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.credential.model

import android.view.View
import ch.nevis.exampleapp.R
import ch.nevis.exampleapp.ui.base.model.ViewData
import ch.nevis.mobile.sdk.api.localdata.Authenticator
import ch.nevis.mobile.sdk.api.operation.RecoverableError
import kotlinx.parcelize.IgnoredOnParcel

/**
 * [ViewData] implementation for Credential view and its view model. The view model composes an instance
 * of this [CredentialViewData] class and posts it to the Credential view to indicate that the view related
 * data changed the view should be updated.
 */
data class CredentialViewData(
    /**
     * The mode, the Credential view intend to be used/initialized.
     */
    val credentialViewMode: CredentialViewMode,

    /**
     * The type of the credential.
     */
    val credentialType: String,

    /**
     * Authenticator protection information.
     */
    val protectionInformation: CredentialProtectionInformation?,

    /**
     * The last recoverable error. It exists only if there was already a failed PIN operation attempt.
     */
    @IgnoredOnParcel
    val lastRecoverableError: RecoverableError? = null
) : ViewData {

    /**
     * The identifier of String resource that should be used as title on the Credential view.
     * Its value depends on the actual [credentialViewMode] and [credentialType].
     */
    val title: Int
        get() {
            return when (credentialViewMode) {
                CredentialViewMode.CHANGE -> when (credentialType) {
                    Authenticator.PIN_AUTHENTICATOR_AAID -> R.string.pin_title_change
                    Authenticator.PASSWORD_AUTHENTICATOR_AAID -> R.string.password_title_change
                    else -> throw IllegalStateException("Unsupported credential type.")
                }
                CredentialViewMode.ENROLLMENT -> when (credentialType) {
                    Authenticator.PIN_AUTHENTICATOR_AAID -> R.string.pin_title_enrollment
                    Authenticator.PASSWORD_AUTHENTICATOR_AAID -> R.string.password_title_enrollment
                    else -> throw IllegalStateException("Unsupported credential type.")
                }
                CredentialViewMode.VERIFICATION -> when (credentialType) {
                    Authenticator.PIN_AUTHENTICATOR_AAID -> R.string.pin_title_verify
                    Authenticator.PASSWORD_AUTHENTICATOR_AAID -> R.string.password_title_verify
                    else -> throw IllegalStateException("Unsupported credential type.")
                }
            }
        }

    /**
     * The identifier of String resource that should be used as description on Credential view.
     * Its value depends on the actual [credentialViewMode] and [credentialType].
     */
    val description: Int
        get() {
            return when (credentialViewMode) {
                CredentialViewMode.CHANGE -> when (credentialType) {
                    Authenticator.PIN_AUTHENTICATOR_AAID -> R.string.pin_description_change
                    Authenticator.PASSWORD_AUTHENTICATOR_AAID -> R.string.password_description_change
                    else -> throw IllegalStateException("Unsupported credential type.")
                }
                CredentialViewMode.ENROLLMENT -> when (credentialType) {
                    Authenticator.PIN_AUTHENTICATOR_AAID -> R.string.pin_description_enrollment
                    Authenticator.PASSWORD_AUTHENTICATOR_AAID -> R.string.password_description_enrollment
                    else -> throw IllegalStateException("Unsupported credential type.")
                }
                CredentialViewMode.VERIFICATION -> when (credentialType) {
                    Authenticator.PIN_AUTHENTICATOR_AAID -> R.string.pin_description_verify
                    Authenticator.PASSWORD_AUTHENTICATOR_AAID -> R.string.password_description_verify
                    else -> throw IllegalStateException("Unsupported credential type.")
                }
            }
        }

    /**
     * The identifier of String resource that should be used as hint of credential text field on the
     * Credential view. Its value depends on the actual [credentialType].
     */
    val credentialTextFieldHint: Int
        get() {
            return when (credentialType) {
                Authenticator.PIN_AUTHENTICATOR_AAID -> R.string.pin_hint_pin
                Authenticator.PASSWORD_AUTHENTICATOR_AAID -> R.string.password_hint_password
                else -> throw IllegalStateException("Unsupported credential type.")
            }
        }

    /**
     * The identifier of String resource that should be used as hint of old credential text field on
     * the Credential view. Its value depends on the actual [credentialType].
     */
    val oldCredentialTextFieldHint: Int
        get() {
            return when (credentialType) {
                Authenticator.PIN_AUTHENTICATOR_AAID -> R.string.pin_hint_old_pin
                Authenticator.PASSWORD_AUTHENTICATOR_AAID -> R.string.password_hint_old_password
                else -> throw IllegalStateException("Unsupported credential type.")
            }
        }

    /**
     * The visibility value that tells if the old credential text field should be displayed on the
     * Credential view. Its value depends on the actual [credentialViewMode].
     */
    val oldCredentialVisibility: Int
        get() {
            return when (credentialViewMode) {
                CredentialViewMode.CHANGE -> View.VISIBLE
                else -> View.GONE
            }
        }
}
