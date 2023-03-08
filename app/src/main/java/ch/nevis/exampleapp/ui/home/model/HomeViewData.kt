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
 */
data class HomeViewData(
    /**
     * The number of registered accounts known by [ch.nevis.mobile.sdk.api.MobileAuthenticationClient].
     */
    val numberOfRegisteredAccounts: Int
) : ViewData
