/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.transactionConfirmation.model

import ch.nevis.exampleapp.ui.base.model.ViewData

/**
 * [ViewData] implementation for Transaction Confirmation view and its view model. The view model composes
 * an instance of this [TransactionConfirmationViewData] class and posts it to the Transaction Confirmation view to
 * indicate that the view related data changed the view should be updated.
 */
data class TransactionConfirmationViewData(
    /**
     * The transaction confirmation data/message that should be displayed on Transaction Confirmation view.
     */
    val transactionConfirmationData: String
) : ViewData
