/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.log

/**
 * An interface declaration of an SDK log receiver. Implement this interface and add and instance of the implementation to a [SdkLogger] instance to be able to
 * receive new SDK log items/messages.
 */
interface SdkLogReceiver {

    /**
     * Triggered when a new log item/message logged.
     *
     * @param logItem The recently logged item.
     */
    fun newLogItem(logItem: LogItem)
}
