/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.error.parameter

import ch.nevis.exampleapp.ui.base.model.NavigationParameter
import kotlinx.parcelize.Parcelize

/**
 * Navigation parameter data class for Error view.
 *
 * @constructor Creates a new instance.
 * @param message The error message to be displayed on Error view.
 */
@Parcelize
data class ErrorNavigationParameter(

    /**
     * The error message to be displayed on Error view.
     */
    val message: String?
) : NavigationParameter
