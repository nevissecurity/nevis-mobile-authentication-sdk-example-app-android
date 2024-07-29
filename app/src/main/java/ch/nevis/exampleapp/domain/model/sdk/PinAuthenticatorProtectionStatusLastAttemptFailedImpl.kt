/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2024. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.model.sdk

import ch.nevis.mobile.sdk.api.operation.pin.PinAuthenticatorProtectionStatus

/**
 * Implementation of [PinAuthenticatorProtectionStatus.LastAttemptFailed] interface.
 */
data class PinAuthenticatorProtectionStatusLastAttemptFailedImpl(
    val remainingRetries: Int,
    val coolDownTimeInSeconds: Long
) : PinAuthenticatorProtectionStatus.LastAttemptFailed {
    override fun remainingRetries(): Int = remainingRetries

    override fun coolDownTimeInSeconds(): Long = coolDownTimeInSeconds
}
