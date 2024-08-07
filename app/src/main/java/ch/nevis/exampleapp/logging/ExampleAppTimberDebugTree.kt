/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.logging

import android.util.Log
import ch.nevis.exampleapp.domain.log.SdkLogger
import timber.log.Timber

/**
 * An example application specific sub-class of [Timber.DebugTree] that logs SDK events at a specific priority level.
 *
 * @constructor Creates a new instance.
 * @param sdkLogger An instance of an implementation of [SdkLogger] interface.
 */
class ExampleAppTimberDebugTree(
    private val sdkLogger: SdkLogger
): Timber.DebugTree() {

    //region Constants
    /**
     * Constants.
     */
    companion object {
        /**
         * Priority level constant for SDK events.
         */
        internal const val PRIORITY_SDK_EVENT = 100
    }
    //endregion

    //region Timber.DebugTree
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == PRIORITY_SDK_EVENT) {
            sdkLogger.log(message)
        }
        super.log(Log.DEBUG, tag, message, t)
    }
    //endregion
}
