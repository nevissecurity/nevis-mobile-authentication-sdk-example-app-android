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
import ch.nevis.exampleapp.logging.sdk
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.selectAccount.parameter.SelectAccountNavigationParameter
import ch.nevis.exampleapp.ui.transactionConfirmation.parameter.TransactionConfirmationNavigationParameter
import ch.nevis.mobile.sdk.api.localdata.Account
import ch.nevis.mobile.sdk.api.operation.selection.AccountSelectionContext
import ch.nevis.mobile.sdk.api.operation.selection.AccountSelectionHandler
import ch.nevis.mobile.sdk.api.operation.selection.AccountSelector
import timber.log.Timber

/**
 * Default implementation of [AccountSelector] interface. It checks the [Account] set and
 * transaction confirmation data received in the [AccountSelectionContext] object and decides
 * if the next step is transaction confirmation or account selection. As an addition it also showcases
 * how to skip account selection if the received account list set has only one element.
 *
 * @constructor Creates a new instance.
 * @param navigationDispatcher An instance of a [NavigationDispatcher] interface implementation.
 * @param errorHandler An instance of an [ErrorHandler] interface implementation.
 */
class AccountSelectorImpl(
    private val navigationDispatcher: NavigationDispatcher,
    private val errorHandler: ErrorHandler
) : AccountSelector {

    //region AccountSelector
    /** @suppress */
    override fun selectAccount(
        context: AccountSelectionContext,
        handler: AccountSelectionHandler
    ) {
        Timber.asTree()
            .sdk("Please select one of the received available accounts!")
        try {
            val accounts = validAccounts(context)
            if (accounts.isEmpty()) {
                throw BusinessException.accountsNotFound()
            }

            val transactionConfirmationData =
                context.transactionConfirmationData().orElse(null)

            transactionConfirmationData?.also {
                navigationDispatcher.requestNavigation(
                    NavigationGraphDirections.actionGlobalTransactionConfirmationFragment(
                        TransactionConfirmationNavigationParameter(
                            Operation.OUT_OF_BAND_AUTHENTICATION,
                            context.accounts(),
                            it.decodeToString(),
                            handler
                        )
                    )
                )
            } ?: run {
                if (accounts.size == 1) {
                    Timber.asTree()
                        .sdk("One account found, performing automatic selection!")
                    handler.username(accounts.first().username())
                } else {
                    navigationDispatcher.requestNavigation(
                        NavigationGraphDirections.actionGlobalSelectAccountFragment(
                            SelectAccountNavigationParameter(
                                Operation.OUT_OF_BAND_AUTHENTICATION,
                                context.accounts(),
                                handler
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

    //region Private Interface
    /**
     * Validates the list of [Account]s.
     *
     * @param context The context containing the list of [Account]s.
     * @return The list of valid [Account]s.
     */
    private fun validAccounts(context: AccountSelectionContext): Set<Account> {
        val validAccounts = mutableSetOf<Account>()
        context.authenticators().forEach { authenticator ->
            if (authenticator.isSupportedByHardware && authenticator.isSupportedByOs) {
                authenticator.registration().registeredAccounts().forEach { account ->
                    if (context.isPolicyCompliant(account.username(), authenticator.aaid())) {
                        validAccounts.add(account)
                    }
                }
            }
        }

        return validAccounts
    }
    //endregion
}
