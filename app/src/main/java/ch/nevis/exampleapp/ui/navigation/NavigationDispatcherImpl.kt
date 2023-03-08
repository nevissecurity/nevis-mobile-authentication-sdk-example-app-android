/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.navigation

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.NavDirections
import ch.nevis.exampleapp.ui.util.SingleLiveEvent

/**
 * Default implementation of [NavigationDispatcher] interface.
 */
class NavigationDispatcherImpl : NavigationDispatcher {

    //region Properties
    /**
     * Mutable backing property of the [LiveData] property.
     */
    private val _navigation = SingleLiveEvent<NavDirections>()

    /**
     * The [LiveData] object the subscribers observe. The subscribers view will be notified via this
     * [LiveData] if a new [NavDirections] (navigation request) object is available.
     */
    private val navigation: LiveData<NavDirections> get() = _navigation
    //endregion

    //region NavigationDispatcher
    override fun requestNavigation(navDirections: NavDirections) {
        _navigation.value = navDirections
    }

    override fun subscribe(viewLifecycleOwner: LifecycleOwner, block: (NavDirections) -> Unit) {
        navigation.observe(viewLifecycleOwner) {
            block(it)
        }
    }

    override fun unsubscribe(viewLifecycleOwner: LifecycleOwner) {
        navigation.removeObservers(viewLifecycleOwner)
    }
    //endregion
}