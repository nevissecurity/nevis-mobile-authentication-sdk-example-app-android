/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.changeDeviceInformation.model

import ch.nevis.exampleapp.ui.base.model.ViewData
import ch.nevis.mobile.sdk.api.localdata.DeviceInformation

/**
 * [ViewData] implementation for Change Device Information view and its view model. The view model composes
 * an instance of this [ChangeDeviceInformationViewData] class after the current [DeviceInformation] was queried
 * from the [ch.nevis.mobile.sdk.api.MobileAuthenticationClient] and posts it to the Change Device Information
 * view to indicate that the view related data changed the view should be updated.
 */
data class ChangeDeviceInformationViewData(

    /**
     * The current [DeviceInformation] was queried from the [ch.nevis.mobile.sdk.api.MobileAuthenticationClient].
     */
    val deviceInformation: DeviceInformation
) : ViewData
