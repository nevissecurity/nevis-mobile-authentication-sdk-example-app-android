/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.deviceInformation

import android.content.Context
import android.os.Build
import ch.nevis.exampleapp.R
import ch.nevis.mobile.sdk.api.localdata.DeviceInformation
import java.text.DateFormat
import java.util.*

/**
 * Default implementation of [DeviceInformationFactory] interface.
 * It creates a new [DeviceInformation] using Android [Context] and returns it.
 *
 * @constructor Creates a new instance.
 * @param context The Android [Context].
 */
class DeviceInformationFactoryImpl(
    private val context: Context
) : DeviceInformationFactory {

    //region DeviceInformationFactory
    override fun create(): DeviceInformation {
        return DeviceInformation.builder().name(initDeviceInformationName()).build()
    }
    //endregion

    //region Private Interface
    private fun initDeviceInformationName(): String {
        val df = DateFormat.getDateTimeInstance()
        val timestamp = df.format(Date())
        val model =
            getDeviceModel()
                ?: context.resources.getString(R.string.device_information_unknown_model)
        return "Android $model $timestamp"
    }

    private fun getDeviceModel(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL

        if (manufacturer == null) {
            if (model == null) {
                return null
            }
            return model
        } else if (model == null) {
            return manufacturer
        }

        return if (model.lowercase().startsWith(manufacturer.lowercase())) {
            model
        } else {
            "$manufacturer $model"
        }
    }
    //endregion
}
