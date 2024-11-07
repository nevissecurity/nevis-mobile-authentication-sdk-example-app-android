/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023-2024. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.transactionConfirmation.parameter

import ch.nevis.exampleapp.domain.model.operation.Operation
import ch.nevis.exampleapp.ui.base.model.NavigationParameter
import ch.nevis.mobile.sdk.api.localdata.Account
import ch.nevis.mobile.sdk.api.operation.selection.AccountSelectionHandler
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Navigation parameter class for Transaction Confirmation view.
 *
 * **IMPORTANT**: Transaction confirmation data handling is part of the account selection process of
 * an out-of-band authentication operation. This is the reason some account selection property must be
 * passed to transaction confirmation as well view to be able to continue the operation with the account
 * selection. Select Account view itself is not out-of-band authentication specific and supports
 * multiple operations.
 *
 * @constructor Creates a new instance.
 * @param account The previously selected account.
 * @param transactionConfirmationMessage The transaction confirmation data/message that should be displayed
 *      on Transaction Confirmation view.
 * @param accountSelectionHandler An instance of an [AccountSelectionHandler] implementation.
 */
@Parcelize
data class TransactionConfirmationNavigationParameter(

    /**
     * The previously selected account.
     */
    @IgnoredOnParcel
    val account: Account? = null,

    /**
     * The transaction confirmation data/message that should be displayed on Transaction Confirmation view.
     */
    val transactionConfirmationMessage: String,

    /**
     * An instance of an [AccountSelectionHandler]. Transaction confirmation data received only in case an out-of-band authentication is started
     * and we navigate to Transaction Confirmation view to ask the user to confirm or deny the operation based on the transaction confirmation data
     * before we continue the operation with account selection.
     * [TransactionConfirmationNavigationParameter.operation] will always be [Operation.OUT_OF_BAND_AUTHENTICATION]
     */
    @IgnoredOnParcel
    val accountSelectionHandler: AccountSelectionHandler? = null
) : NavigationParameter
