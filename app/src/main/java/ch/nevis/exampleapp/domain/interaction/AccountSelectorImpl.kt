/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.interaction

import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.common.error.ErrorHandler
import ch.nevis.exampleapp.domain.model.error.BusinessException
import ch.nevis.exampleapp.domain.model.operation.Operation
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.selectAccount.parameter.SelectAccountNavigationParameter
import ch.nevis.exampleapp.ui.transactionConfirmation.parameter.TransactionConfirmationNavigationParameter
import ch.nevis.mobile.sdk.api.operation.selection.AccountSelectionContext
import ch.nevis.mobile.sdk.api.operation.selection.AccountSelectionHandler
import ch.nevis.mobile.sdk.api.operation.selection.AccountSelector

/**
 * Default implementation of [AccountSelector] interface. It checks the <Account> set and
 * transaction confirmation data in received [AccountSelectionContext] object and decides
 * if the next step is transaction confirmation or account selection. As an addition it also showcases
 * how to skip account selection if the received account list set only one element.
 */
class AccountSelectorImpl(

    /**
     * An instance of a [NavigationDispatcher] interface implementation.
     */
    private val navigationDispatcher: NavigationDispatcher,

    /**
     * An instance of an [ErrorHandler] interface implementation. Received errors will be passed to this error
     * handler instance.
     */
    private val errorHandler: ErrorHandler
) : AccountSelector {

    //region AccountSelector
    override fun selectAccount(
        accountSelectionContext: AccountSelectionContext,
        accountSelectionHandler: AccountSelectionHandler
    ) {
        try {
            val accounts = accountSelectionContext.accounts()
            if (accounts.isEmpty()) {
                throw BusinessException.accountsNotFound()
            }

            val transactionConfirmationData =
                accountSelectionContext.transactionConfirmationData().orElse(null)

            transactionConfirmationData?.also {
                navigationDispatcher.requestNavigation(
                    NavigationGraphDirections.actionGlobalTransactionConfirmationFragment(
                        TransactionConfirmationNavigationParameter(
                            Operation.OUT_OF_BAND_AUTHENTICATION,
                            accountSelectionContext.accounts(),
                            it.decodeToString(),
                            accountSelectionHandler
                        )
                    )
                )
            } ?: run {
                if (accounts.size == 1) {
                    accountSelectionHandler.username(accounts.first().username())
                } else {
                    navigationDispatcher.requestNavigation(
                        NavigationGraphDirections.actionGlobalSelectAccountFragment(
                            SelectAccountNavigationParameter(
                                Operation.OUT_OF_BAND_AUTHENTICATION,
                                accountSelectionContext.accounts(),
                                accountSelectionHandler
                            )
                        )
                    )
                }
            }
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }
    //endregion
}
