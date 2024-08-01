/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ch.nevis.exampleapp.ui.base.model.ViewData
import ch.nevis.exampleapp.ui.util.SingleLiveEvent

/**
 * Abstract, base class for all view models implemented in this example application.
 * It provides a mechanism that ease the way how a view model may indicate to its owner view
 * that the view data was changed and the view should be updated based on the new [ViewData] object
 * that posted by the view model.
 *
 * @constructor Creates a new instance.
 */
abstract class BaseViewModel : ViewModel() {

    //region Properties
    /**
     * Private, mutable backing property of the public [LiveData] property.
     */
    private val _viewData = SingleLiveEvent<ViewData>()

    /**
     * The [LiveData] object the owner view observes. The owner view will be notified via this
     * [LiveData] if a new [ViewData] object is available.
     */
    val viewData: LiveData<ViewData> get() = _viewData
    //endregion

    //region Public Interface
    /**
     * Requests a view update by posting a new [ViewData] object to the owner view of the view model.
     *
     * @param viewData The new [ViewData] object that should be used to update the owner view.
     */
    fun requestViewUpdate(viewData: ViewData) {
        _viewData.postValue(viewData)
    }
    //endregion
}
