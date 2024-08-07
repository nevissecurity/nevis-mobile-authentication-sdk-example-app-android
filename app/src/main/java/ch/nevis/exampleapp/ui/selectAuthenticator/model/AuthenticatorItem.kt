/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.selectAuthenticator.model

import androidx.annotation.StringRes
import ch.nevis.mobile.sdk.api.localdata.Authenticator

/**
 * Represents an authenticator that is listed and can be selected by the user on select authenticator view.
 *
 * @constructor Creates a new instance.
 * @param aaid The AAID of the authenticator.
 * @param isPolicyCompliant The flag that tells whether the authenticator is server policy compliant.
 * @param isUserEnrolled The flag that tells whether the user already enrolled the authenticator.
 * @param titleResId String resource identifier of the title of the authenticator.
 */
data class AuthenticatorItem(
    /**
     * The AAID of the authenticator.
     */
    val aaid: String,

    /**
     * The flag that tells whether the authenticator is server policy compliant.
     */
    val isPolicyCompliant: Boolean,

    /**
     * The flag that tells whether the user already enrolled the authenticator.
     */
    val isUserEnrolled: Boolean,

    /**
     * String resource identifier of the title of the authenticator.
     */
    @StringRes
    val titleResId: Int
) {
    /**
     * Tells that if this authenticator item is selectable on select authenticator view or not.
     * The value is calculated based on [AuthenticatorItem.isPolicyCompliant] and [AuthenticatorItem.isUserEnrolled]
     * flags.
     *
     * @return A flag that tells whether the item is selectable.
     */
    fun isEnabled(): Boolean {
        return isPolicyCompliant && (
            aaid == Authenticator.PIN_AUTHENTICATOR_AAID ||
            aaid == Authenticator.PASSWORD_AUTHENTICATOR_AAID ||
            isUserEnrolled
        )
    }
}
