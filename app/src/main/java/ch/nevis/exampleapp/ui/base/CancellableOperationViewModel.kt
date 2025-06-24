/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.base

/**
 * Base abstract view model class for view models those run/handle operations those can be cancelled.
 * An implementation of this abstract class typically owns, stores one or more instances of subclasses of
 * [ch.nevis.mobile.sdk.api.operation.CancelableHandler] and the `cancel()` method of these handlers will be
 * called in actual implementation of abstract `cancelOperation()` method of this class.
 */
abstract class CancellableOperationViewModel : BaseViewModel() {

    /**
     * Cancels the current, running operation.
     */
    abstract fun cancelOperation()
}
