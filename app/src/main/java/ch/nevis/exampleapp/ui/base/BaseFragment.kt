/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.base

import android.os.Build
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import ch.nevis.exampleapp.ui.base.model.NavigationParameter
import ch.nevis.exampleapp.ui.base.model.ViewData
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import javax.inject.Inject

/**
 * Abstract, base [androidx.fragment.app.Fragment] implementation for all views of the example
 * application, it provides navigation and view updating features those are crucial for the proper
 * working of this example application.
 *
 * - _Navigation_: The [ch.nevis.exampleapp.ui.base.BaseFragment] subscribes to [NavigationDispatcher]
 * (that is injected as a singleton by Dagger Hilt) in the [androidx.fragment.app.Fragment.onResume]
 * method and unsubscribes in [androidx.fragment.app.Fragment.onPause] method and handles the navigation
 * requests. This way it is ensured only one, the currently visible view is subscribed to
 * the [NavigationDispatcher].
 * - _View updates_: All implementation of this class must provide a view model instance that is a
 * sub-class of [BaseViewModel]. [BaseViewModel] provides an [androidx.lifecycle.LiveData] property called
 * `viewData`. [ch.nevis.exampleapp.ui.base.BaseFragment] starts observing this property in
 * [androidx.fragment.app.Fragment.onResume] method and stops observing it in
 * [androidx.fragment.app.Fragment.onPause]. This way the view models can indicate to the views that
 * the view data was changed and the view must be updated. Once a [ViewData] is posted by the view model
 * the [BaseFragment.updateView] method is called and the view may run the necessary code that updates
 * the view.
 */
abstract class BaseFragment : Fragment() {

    //region Companion Object
    companion object {
        /**
         * Commonly used argument name that is used for navigation parameters during navigation.
         */
        private const val ARGUMENT_NAME_PARAMETER = "parameter"
    }
    //endregion

    //region Properties
    /**
     * The abstract declaration of a view model. The implementations have to provide their own view model.
     */
    protected abstract val viewModel: BaseViewModel

    /**
     * An instance of a [NavigationDispatcher] interface implementation.
     */
    @Inject
    lateinit var navigationDispatcher: NavigationDispatcher
    //endregion

    //region Fragment
    override fun onResume() {
        super.onResume()

        navigationDispatcher.subscribe(viewLifecycleOwner) {
            handleNavigation(it)
        }

        viewModel.viewData.observe(viewLifecycleOwner) {
            updateView(it)
        }
    }

    override fun onPause() {
        super.onPause()
        navigationDispatcher.unsubscribe(viewLifecycleOwner)
        viewModel.viewData.removeObservers(viewLifecycleOwner)
    }
    //endregion

    //region Public Interface
    /**
     * Updates the view based on the given [ViewData] implementation.
     *
     * The default implementation does nothing it is up to the [BaseFragment] implementations to
     * override this method and run the actual view and [ViewData] implementation specific code.
     *
     * @param viewData The [ViewData] to be used for view updating.
     */
    open fun updateView(viewData: ViewData) {}

    /**
     * Updates the [NavigationParameter] of the view. There are two ways a view can receive a
     * [NavigationParameter] object:
     * - The default way is when we navigate to a view using a [NavDirections]
     * object that contains the [NavigationParameter] object. In this case the parameter object is
     * available via a SafeArgs property of the actual view that can be process in e.g.:
     * [androidx.fragment.app.Fragment.onViewCreated] method.
     * - The another way is when the view receives a [NavigationParameter] object when this method is
     * called. Calling of this method depends on the [NavigationDispatcher] object this [BaseFragment]
     * subscribed for. When the [BaseFragment] receives a navigation request from the [NavigationDispatcher]
     * the [BaseFragment.handleNavigation] method is called. It extracts the [NavigationParameter] from the
     * navigation arguments and calls this method. Depending on the current implementation of this method
     * and the returned value the [BaseFragment.handleNavigation] method decides if the parameter is
     * handled by the current view or a navigation is needed when the [NavigationParameter] object is
     * forwarded to another view.
     * This feature is used to avoid multiple navigation to a view. If the returned value is `true` then
     * that means we are already on the view that can use the parameter, no navigation is needed.
     *
     * @param parameter The new [NavigationParameter] instance for the view.
     * @return A [Boolean] flag that tells if the view handled the given [NavigationParameter]
     * object or not. Returns `true` if the view was able to handle the parameter, otherwise the view
     * must return `false` to indicate that the parameter is not acceptable for the current view and
     * it must be forwarded to another view.
     */
    open fun updateViewParameter(parameter: NavigationParameter): Boolean = false
    //endregion

    //region Private Interface
    /**
     * Handles the navigation request received via the [NavigationDispatcher] object this [BaseFragment]
     * subscribed for.
     * It extracts the [NavigationParameter] from the navigation arguments and calls [BaseFragment.updateViewParameter] method.
     * Depending on the current implementation of [BaseFragment.updateViewParameter] method
     * and its returned value this method decides if the parameter is handled by the current view or
     * a navigation is needed when the [NavigationParameter] object is forwarded to another view.
     * This feature is used to avoid multiple navigation to a view. If the returned value of
     * [BaseFragment.updateViewParameter] is `true` the that means we are already on the view that can
     * use the parameter, no navigation is needed.
     *
     * @param navDirections The [NavDirections] object that describes the requested navigation.
     */
    private fun handleNavigation(navDirections: NavDirections) {
        val parameter: NavigationParameter? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                navDirections.arguments.getParcelable(
                    ARGUMENT_NAME_PARAMETER,
                    NavigationParameter::class.java
                )
            } else {
                @Suppress("DEPRECATION")
                navDirections.arguments.getParcelable(ARGUMENT_NAME_PARAMETER)
            }

        var navigate = true
        parameter?.let {
            if (updateViewParameter(it)) {
                navigate = false
            }
        }

        if (navigate) {
            findNavController().navigate(navDirections)
        }
    }
    //endregion
}
