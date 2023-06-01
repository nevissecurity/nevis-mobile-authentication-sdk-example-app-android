package ch.nevis.exampleapp.common.settings

import android.content.Context
import ch.nevis.exampleapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Default implementation of [Settings] interface.
 */
class SettingsImpl @Inject constructor(
    @ApplicationContext
    context: Context
) : Settings {
    override val allowClass2Sensors = context.resources.getBoolean(R.bool.allow_class2_sensors)
}