/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023-2024. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.selectAccount.parameter

import ch.nevis.exampleapp.domain.model.operation.Operation
import ch.nevis.exampleapp.ui.base.model.NavigationParameter
import ch.nevis.mobile.sdk.api.localdata.Account
import ch.nevis.mobile.sdk.api.operation.selection.AccountSelectionHandler
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Navigation parameter class for Select Account view.
 *
 * @constructor Creates a new instance.
 * @param operation The operation type the account selection was requested for.
 * @param accounts The list of available accounts the user can select from.
 * @param accountSelectionHandler An instance of an [AccountSelectionHandler] implementation.
 */
@Parcelize
data class SelectAccountNavigationParameter(

    /**
     * The operation type the account selection was requested for.
     */
    val operation: Operation,

    /**
     * The list of available accounts the user can select from.
     */
    @IgnoredOnParcel
    val accounts: Set<Account>? = null,

    /**
     * An instance of an [AccountSelectionHandler] in case an out-of-band authentication is started and we navigate to
     * Select Account view to ask the user to select one of the available accounts to be able to continue the operation.
     * [SelectAccountNavigationParameter.operation] must be [Operation.OUT_OF_BAND_AUTHENTICATION].
     */
    @IgnoredOnParcel
    val accountSelectionHandler: AccountSelectionHandler? = null,

    /**
     * The message to confirm if there is any.
     */
    val message: String? = null,
) : NavigationParameter
