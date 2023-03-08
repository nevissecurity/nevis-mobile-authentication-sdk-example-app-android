/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.interaction

import ch.nevis.exampleapp.common.error.ErrorHandler
import ch.nevis.exampleapp.domain.model.error.MobileAuthenticationClientException
import ch.nevis.exampleapp.domain.model.operation.Operation
import ch.nevis.mobile.sdk.api.MobileAuthenticationClientError
import ch.nevis.mobile.sdk.api.util.Consumer

/**
 * A [MobileAuthenticationClientError] specific implementation of [Consumer] interface that is used as
 * a general client error consumer. It accepts [MobileAuthenticationClientError] objects wraps them into
 * [MobileAuthenticationClientException] objects and passes them to the [ErrorHandler].
 */
class OnErrorImpl<E : MobileAuthenticationClientError>(
    private val operation: Operation,

    /**
     * An instance of an [ErrorHandler] interface implementation. Received errors will be passed to this error
     * handler instance.
     */
    private val errorHandler: ErrorHandler
) : Consumer<E> {
    override fun accept(error: E) {
        errorHandler.handle(
            MobileAuthenticationClientException(
                operation,
                error
            )
        )
    }
}