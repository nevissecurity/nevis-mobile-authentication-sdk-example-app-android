/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.common.error

/**
 * Main implementation of the [ErrorHandler] interface that can delegate the handling of the errors to
 * other configured [ErrorHandler] instances.
 */
class ChainErrorHandlerImpl(

    /**
     * A [List] of [ErrorHandler] instances those can handle or bypass the given error when
     * [ErrorHandler.handle] is called. If the error is handled by an [ErrorHandler], the error
     * processing will be ended, the error will not be delegated to further elements of the chain.
     */
    private val errorHandlerChain: List<ErrorHandler>
) : ErrorHandler {

    //region ErrorHandler
    override fun handle(error: Throwable): Boolean {
        for (errorHandler in errorHandlerChain) {
            if (errorHandler.handle(error)) {
                // The error was handled by the current errorHandler. Breaking the chain.
                break
            }
        }
        return true
    }
    //endregion
}