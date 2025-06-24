/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.navigation

import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavDirections

/**
 * Interface declaration of a navigation dispatcher. The purpose of this interface and its implementation
 * is to provide a possibility for [LifecycleOwner] instances, typically [androidx.fragment.app.Fragment]
 * instances to receive navigation requests from outside of their scope. Once a [LifecycleOwner] subscribes,
 * it will receive the [NavDirections] objects requested using the [NavigationDispatcher.requestNavigation]
 * by any object that has the instance of the implementation of [NavigationDispatcher]. Of course the navigation
 * must be requested from the same [NavigationDispatcher] the [LifecycleOwner] object subscribed for.
 *
 * **IMPORTANT:** It is strongly suggested:
 * - The implementation of [NavigationDispatcher] interface is injected as a singleton everywhere it intended to be used.
 * - Only one, the visible [LifecycleOwner], [androidx.fragment.app.Fragment] should be subscribed at a time.
 *
 * This is the way this example application is designed, implemented. The [NavigationDispatcherImpl] is injected as a
 * singleton by Dagger Hilt, each view implements the [ch.nevis.exampleapp.ui.base.BaseFragment] and the
 * [ch.nevis.exampleapp.ui.base.BaseFragment] subscribes to [NavigationDispatcher] in the [androidx.fragment.app.Fragment.onResume]
 * method and unsubscribes in [androidx.fragment.app.Fragment.onPause] method.
 */
interface NavigationDispatcher {

    /**
     * Subscribes for [NavDirections] objects dispatched by this [NavigationDispatcher].
     *
     * @param viewLifecycleOwner The [LifecycleOwner] that subscribes for [NavDirections] objects.
     * @param block The lambda block that will be called with the [NavDirections] objects.
     */
    fun subscribe(viewLifecycleOwner: LifecycleOwner, block: (NavDirections) -> Unit)

    /**
     * Unsubscribes from this this [NavigationDispatcher] instance.
     *
     * @param viewLifecycleOwner The [LifecycleOwner] that unsubscribes from this [NavigationDispatcher] instance.
     */
    fun unsubscribe(viewLifecycleOwner: LifecycleOwner)

    /**
     * Request a navigation. The requested [NavDirections] will be dispatched to the subscribed [LifecycleOwner] instances.
     *
     * @param navDirections The requested [NavDirections] object.
     */
    fun requestNavigation(navDirections: NavDirections)
}
