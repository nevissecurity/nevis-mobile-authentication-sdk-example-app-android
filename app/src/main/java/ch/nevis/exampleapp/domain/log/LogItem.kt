/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.log

import java.util.*

/**
 * A data class that represents a log item.
 *
 * @constructor Creates a new instance.
 * @param date Date/timestamp part of the log item.
 * @param message Message part of the log item.
 */
data class LogItem(

    /**
     * Date/timestamp part of the log item.
     */
    val date: Date,

    /**
     * Message part of the log item.
     */
    val message: String
)
