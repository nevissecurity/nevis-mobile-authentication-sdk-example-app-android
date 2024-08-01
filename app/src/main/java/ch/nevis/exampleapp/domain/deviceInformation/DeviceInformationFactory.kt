/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.deviceInformation

import ch.nevis.mobile.sdk.api.localdata.DeviceInformation

/**
 * Factory interface for creating a new [DeviceInformation] object.
 */
interface DeviceInformationFactory {

    /**
     * Creates a [DeviceInformation] instance.
     * @return The [DeviceInformation] instance.
     */
    fun create(): DeviceInformation
}
