/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.log

/**
 * An interface declaration of a SDK event logger. Implement this interface to be able to log SDK events
 * and to be able to notify receivers about new messages
 */
interface SdkLogger {

    /**
     * Adds a new receiver object to this logger. In case of a new message, all added receivers will be notified.
     *
     * @param logReceiver The log receiver instance.
     */
    fun addLogReceiver(logReceiver: SdkLogReceiver)

    /**
     * Removes a receiver object to this logger. The removed receiver will not be notified anymore about new messages.
     *
     * @param logReceiver The log receiver instance.
     */
    fun removeLogReceiver(logReceiver: SdkLogReceiver)

    /**
     * Logs a new message.
     *
     * @param message The message to be logged.
     */
    fun log(message: String)
}
