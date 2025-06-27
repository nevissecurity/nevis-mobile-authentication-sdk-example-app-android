/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2025. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.home.model

/**
 * Class to indicate the type of the supported attestation mode.
 *
 * @constructor Creates a new instance.
 * @param onlySurrogateBasicSupported Indicates if only surrogate basic attestation is supported.
 * @param onlyDefaultMode Indicates if only default mode is supported.
 * @param strictMode Indicates if strict mode is supported.
 */
sealed class SdkAttestationInformation(
    val onlySurrogateBasicSupported: Boolean,
    val onlyDefaultMode: Boolean,
    val strictMode: Boolean
) {
    /**
     * Represents the case when only surrogate basic attestation is supported by the device.
     */
    class OnlySurrogateBasicSupported : SdkAttestationInformation(
        onlySurrogateBasicSupported = true,
        onlyDefaultMode = false,
        strictMode = false
    )

    /**
     * Represents the case when both surrogate basic and default attestation modes are supported by the device.
     */
    class OnlyDefaultMode : SdkAttestationInformation(
        onlySurrogateBasicSupported = true,
        onlyDefaultMode = true,
        strictMode = false
    )

    /**
     * Represents the case when surrogate basic, default, and strict attestation modes are all supported by the device.
     */
    class StrictMode : SdkAttestationInformation(
        onlySurrogateBasicSupported = true,
        onlyDefaultMode = true,
        strictMode = true
    )
}
