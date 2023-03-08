/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.selectAuthenticator.model

import androidx.annotation.StringRes

/**
 * Simple item descriptor of an authenticator.
 */
data class AuthenticatorItemDescriptor(
    /**
     * String resource identifier of the title of the authenticator.
     */
    @StringRes val titleResId: Int
)
