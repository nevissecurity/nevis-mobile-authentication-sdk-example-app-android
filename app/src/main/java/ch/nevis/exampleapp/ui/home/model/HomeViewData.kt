/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.home.model

import ch.nevis.exampleapp.ui.base.model.ViewData

/**
 * [ViewData] implementation for Home view and its view model. The view model composes
 * an instance of this [HomeViewData] class after the [ch.nevis.mobile.sdk.api.MobileAuthenticationClient]
 * initialization and posts it to the Home view to indicate that the view related data changed the view
 * should be updated.
 *
 * @constructor Creates a new instance.
 * @param numberOfRegisteredAccounts The number of registered accounts known by [ch.nevis.mobile.sdk.api.MobileAuthenticationClient].
 * @param sdkVersion The version of Nevis Mobile Authentication SDK.
 * @param facetId The application facet identifier.
 * @param certificateFingerprint The certificate fingerprint.
 * @param sdkAttestationInformation The attestation information.
 */
data class HomeViewData(
    /**
     * The number of registered accounts known by [ch.nevis.mobile.sdk.api.MobileAuthenticationClient].
     */
    val numberOfRegisteredAccounts: Int,
    /**
     * The version of Nevis Mobile Authentication SDK.
     */
    val sdkVersion: String,
    /**
     * The application facet identifier.
     */
    val facetId: String,
    /**
     * The certificate fingerprint.
     */
    val certificateFingerprint: String,
    /**
     * The attestation information.
     */
    val sdkAttestationInformation: SdkAttestationInformation?
) : ViewData
