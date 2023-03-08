/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.transactionConfirmation

import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.common.error.ErrorHandler
import ch.nevis.exampleapp.domain.model.error.BusinessException
import ch.nevis.exampleapp.domain.model.operation.Operation
import ch.nevis.exampleapp.ui.base.CancellableOperationViewModel
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.selectAccount.parameter.SelectAccountNavigationParameter
import ch.nevis.exampleapp.ui.transactionConfirmation.model.TransactionConfirmationViewData
import ch.nevis.exampleapp.ui.transactionConfirmation.parameter.TransactionConfirmationNavigationParameter
import ch.nevis.mobile.sdk.api.localdata.Account
import ch.nevis.mobile.sdk.api.operation.selection.AccountSelectionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * View model implementation of Transaction Confirmation view.
 *
 * **IMPORTANT**: Transaction confirmation data handling is part of the account selection process of
 * an out-of-band authentication operation. This is the reason some account selection property must be
 * passed to transaction confirmation as well view to be able to continue the operation with the account
 * selection. Select Account view itself is not out-of-band authentication specific and supports
 * multiple operations.
 */
@HiltViewModel
class TransactionConfirmationViewModel @Inject constructor(

    /**
     * An instance of a [NavigationDispatcher] interface implementation.
     */
    private val navigationDispatcher: NavigationDispatcher,

    /**
     * An instance of an [ErrorHandler] interface implementation. Received errors will be passed to this error
     * handler instance.
     */
    private val errorHandler: ErrorHandler
) : CancellableOperationViewModel() {

    //region Properties
    /**
     * The operation the account selection was requested for.
     *
     * This value will always be [Operation.OUT_OF_BAND_AUTHENTICATION].
     */
    private var operation: Operation? = null

    /**
     * The list of available accounts the user can select from.
     */
    private var accounts: Set<Account>? = null

    /**
     * An instance of an [AccountSelectionHandler]. Transaction confirmation data received only in case an out-of-band authentication is started
     * and we navigate to Transaction Confirmation view to ask the user to confirm or deny the operation based on the transaction confirmation data
     * before we continue the operation with account selection.
     * [TransactionConfirmationViewModel.operation] will always be [Operation.OUT_OF_BAND_AUTHENTICATION]
     */
    private var accountSelectionHandler: AccountSelectionHandler? = null
    //endregion

    //region Public Interface
    /**
     * Updates this view model instance based on the [SelectAccountNavigationParameter] that was received by
     * the owner [TransactionConfirmationFragment]. This method must be called by the owner fragment.
     *
     * @param parameter The [TransactionConfirmationNavigationParameter] that was received by the owner [TransactionConfirmationFragment].
     */
    fun updateViewModel(parameter: TransactionConfirmationNavigationParameter) {
        this.operation = parameter.operation
        this.accounts = parameter.accounts
        this.accountSelectionHandler = parameter.accountSelectionHandler

        requestViewUpdate(TransactionConfirmationViewData(parameter.transactionConfirmationData))
    }

    /**
     * Confirms the transaction, the operation will be continued.
     */
    fun confirm() {
        try {
            val accounts = this.accounts ?: throw BusinessException.invalidState()
            val operation = this.operation ?: throw BusinessException.invalidState()
            val accountSelectionHandler =
                this.accountSelectionHandler ?: throw BusinessException.invalidState()

            if (accounts.size == 1) {
                accountSelectionHandler.username(accounts.first().username())
                this.accountSelectionHandler = null
            } else {
                navigationDispatcher.requestNavigation(
                    NavigationGraphDirections.actionGlobalSelectAccountFragment(
                        SelectAccountNavigationParameter(
                            operation,
                            accounts,
                            accountSelectionHandler
                        )
                    )
                )
            }
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }

    /**
     * Denies the transaction, the operation will be cancelled.
     */
    fun deny() {
        cancelOperation()
    }
    //endregion

    //region CancellableOperationViewModel
    override fun cancelOperation() {
        accountSelectionHandler?.cancel()
        accountSelectionHandler = null
    }
    //endregion
}