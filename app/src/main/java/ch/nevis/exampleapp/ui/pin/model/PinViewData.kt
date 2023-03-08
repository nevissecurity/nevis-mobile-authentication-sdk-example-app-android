/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.pin.model

import android.view.View
import ch.nevis.exampleapp.R
import ch.nevis.exampleapp.ui.base.model.ViewData
import ch.nevis.mobile.sdk.api.operation.RecoverableError
import ch.nevis.mobile.sdk.api.operation.pin.PinAuthenticatorProtectionStatus
import kotlinx.parcelize.IgnoredOnParcel

/**
 * [ViewData] implementation for PIN view and its view model. The view model composes an instance of
 * this [PinViewData] class and posts it to the PIN view to indicate that the view related data changed
 * the view should be updated.
 */
data class PinViewData(
    /**
     * The mode, the PIN view intend to be used/initialized.
     */
    val pinViewMode: PinViewMode,

    /**
     * Status object of the PIN authenticator.
     */
    @IgnoredOnParcel
    val pinAuthenticatorProtectionStatus: PinAuthenticatorProtectionStatus? = null,

    /**
     * The last recoverable error. It exists only if there was already a failed PIN operation attempt.
     */
    @IgnoredOnParcel
    val lastRecoverableError: RecoverableError? = null
) : ViewData {

    /**
     * The identifier of String resource that should be used as title on PIN view.
     * Its value depends on the actual [PinViewData.pinViewMode].
     */
    val title: Int
        get() {
            return when (pinViewMode) {
                PinViewMode.CHANGE_PIN -> R.string.pin_title_change_pin
                PinViewMode.ENROLL_PIN -> R.string.pin_title_create_pin
                PinViewMode.VERIFY_PIN -> R.string.pin_title_verify_pin
            }
        }

    /**
     * The identifier of String resource that should be used as message on PIN view.
     * Its value depends on the actual [PinViewData.pinViewMode].
     */
    val message: Int
        get() {
            return when (pinViewMode) {
                PinViewMode.CHANGE_PIN -> R.string.pin_message_change_pin
                PinViewMode.ENROLL_PIN -> R.string.pin_message_create_pin
                PinViewMode.VERIFY_PIN -> R.string.pin_message_verify_pin
            }
        }

    /**
     * The identifier of String resource that should be used as hint of PIN text field on PIN view.
     * Its value depends on the actual [PinViewData.pinViewMode].
     */
    val pinTextFieldHint: Int
        get() {
            return when (pinViewMode) {
                PinViewMode.CHANGE_PIN -> R.string.pin_hint_new_pin
                else -> R.string.pin_hint_pin
            }
        }


    /**
     * The visibility value that tells if the old PIN text field should be displayed on PIN view or not.
     * Its value depends on the actual [PinViewData.pinViewMode].
     */
    val oldPinVisibility: Int
        get() {
            return when (pinViewMode) {
                PinViewMode.CHANGE_PIN -> View.VISIBLE
                else -> View.GONE
            }
        }
}
