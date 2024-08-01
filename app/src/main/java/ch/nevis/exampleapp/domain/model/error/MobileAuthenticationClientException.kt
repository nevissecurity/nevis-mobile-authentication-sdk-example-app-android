/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.model.error

import ch.nevis.exampleapp.domain.model.operation.Operation
import ch.nevis.mobile.sdk.api.MobileAuthenticationClientError

/**
 * A sub-class of [Exception] that hold a [MobileAuthenticationClientError] and an optional [Operation] value.
 *
 * @constructor Creates a new instance.
 * @param operation The [Operation] the error relates to or null if it cannot be determined.
 * @param error The [MobileAuthenticationClientError] object that represents the error.
 */
class MobileAuthenticationClientException(

    /**
     * The [Operation] the error relates to or null if it cannot be determined.
     */
    val operation: Operation? = null,

    /**
     * The [MobileAuthenticationClientError] object that represents the error.
     */
    val error: MobileAuthenticationClientError
) : Exception()
