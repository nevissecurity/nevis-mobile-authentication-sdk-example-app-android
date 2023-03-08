/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.application

import android.app.Application
import ch.nevis.exampleapp.domain.log.SdkLogger
import ch.nevis.exampleapp.logging.ExampleAppTimberDebugTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Simple sub-class of [Application] to enable Dagger Hilt capabilities and to initialize logging.
 */
@HiltAndroidApp
class ExampleApplication : Application() {

    /**
     * An injected instance of an implementation of [SdkLogger] interface.
     */
    @Inject
    lateinit var sdkLogger: SdkLogger

    //region Application
    override fun onCreate() {
        super.onCreate()

        Timber.plant(ExampleAppTimberDebugTree(sdkLogger))
    }
    //endregion
}