/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2024. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.model.sdk

import ch.nevis.mobile.sdk.api.operation.password.PasswordAuthenticatorProtectionStatus

/**
 * Implementation of [PasswordAuthenticatorProtectionStatus.LastAttemptFailed] interface.
 */
data class PasswordAuthenticatorProtectionStatusLastAttemptFailedImpl(
    val remainingRetries: Int,
    val coolDownTimeInSeconds: Long
) : PasswordAuthenticatorProtectionStatus.LastAttemptFailed {
    override fun remainingRetries(): Int = remainingRetries

    override fun coolDownTimeInSeconds(): Long = coolDownTimeInSeconds
}
