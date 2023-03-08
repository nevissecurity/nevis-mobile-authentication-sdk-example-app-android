/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.selectAuthenticator

import ch.nevis.exampleapp.common.error.ErrorHandler
import ch.nevis.exampleapp.domain.model.error.BusinessException
import ch.nevis.exampleapp.ui.base.CancellableOperationViewModel
import ch.nevis.exampleapp.ui.selectAuthenticator.parameter.SelectAuthenticatorNavigationParameter
import ch.nevis.mobile.sdk.api.operation.selection.AuthenticatorSelectionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * View model implementation for Select Authenticator view.
 */
@HiltViewModel
class SelectAuthenticatorViewModel @Inject constructor(

    /**
     * An instance of an [ErrorHandler] interface implementation. Received errors will be passed to this error
     * handler instance.
     */
    val errorHandler: ErrorHandler
) : CancellableOperationViewModel() {

    //region Properties
    /**
     * An instance of an [AuthenticatorSelectionHandler] to be able to continue the operation that requested
     * the authenticator selection.
     */
    private var authenticatorSelectionHandler: AuthenticatorSelectionHandler? = null
    //endregion

    //region Public Interface
    /**
     * Updates this view model instance based on the [SelectAuthenticatorNavigationParameter] that was received by
     * the owner [SelectAuthenticatorFragment]. This method must be called by the owner fragment.
     *
     * @param parameter The [SelectAuthenticatorNavigationParameter] that was received by the owner [SelectAuthenticatorFragment].
     */
    fun updateViewModel(parameter: SelectAuthenticatorNavigationParameter) {
        authenticatorSelectionHandler = parameter.authenticatorSelectionHandler
    }

    /**
     * Selects the authenticator and continues the operation that requested the authenticator selection.
     *
     * @param aaid The AAID of the authenticator the user selected.
     */
    fun selectAuthenticator(aaid: String) {
        try {
            val authenticatorSelectionHandler =
                this.authenticatorSelectionHandler ?: throw BusinessException.invalidState()
            authenticatorSelectionHandler.aaid(aaid)
            this.authenticatorSelectionHandler = null
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }
    //endregion

    //region CancellableOperationViewModel
    override fun cancelOperation() {
        authenticatorSelectionHandler?.cancel()
        authenticatorSelectionHandler = null
    }
    //endregion
}