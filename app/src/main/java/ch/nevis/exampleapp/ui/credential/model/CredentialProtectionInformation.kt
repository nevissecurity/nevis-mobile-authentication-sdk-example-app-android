/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2024. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.credential.model

/**
 * Represents the protection related information of a credential that is used by the Credential view.
 */
data class CredentialProtectionInformation(
    /**
     * Specifies whether the given authenticator is locked.
     */
    val isLocked: Boolean = false,

    /**
     * The number of remaining retries available.
     */
    val remainingRetries: Int? = null,

    /**
     * The time that must be passed before the user can try to provide credentials again.
     */
    val coolDownTime: Long = 0L,

    /**
     * The protection related message.
     */
    val message: String? = null
)
