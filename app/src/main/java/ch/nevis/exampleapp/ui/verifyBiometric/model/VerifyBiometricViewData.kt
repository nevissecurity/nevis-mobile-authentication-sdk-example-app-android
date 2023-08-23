/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.verifyBiometric.model

import ch.nevis.exampleapp.ui.base.model.ViewData

/**
 * [ViewData] implementation for Verify User view and its view model. The view model composes
 * an instance of this [VerifyBiometricViewData] class and posts it to the Verify User view to
 * indicate that the view related data changed the view should be updated.
 */
data class VerifyBiometricViewData(

    /**
     * The error message that should be displayed on Verify User view.
     */
    val errorMessage: String
) : ViewData
