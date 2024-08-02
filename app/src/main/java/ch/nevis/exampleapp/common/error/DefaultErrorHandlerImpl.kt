/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.common.error

import android.content.Context
import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.domain.model.error.BusinessException
import ch.nevis.exampleapp.domain.model.error.MobileAuthenticationClientException
import ch.nevis.exampleapp.ui.error.parameter.ErrorNavigationParameter
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.mobile.sdk.api.MobileAuthenticationClientError
import ch.nevis.mobile.sdk.api.operation.OperationError
import timber.log.Timber

/**
 * Default implementation of [ErrorHandler] interface.
 *
 * @constructor Creates a new instance.
 * @param context An Android [Context] object for [String] resource resolving.
 * @param navigationDispatcher An instance of a [NavigationDispatcher] interface implementation.
 */
class DefaultErrorHandlerImpl(
    private val context: Context,
    private val navigationDispatcher: NavigationDispatcher
) : ErrorHandler {

    //region ErrorHandler
    override fun handle(error: Throwable): Boolean {
        Timber.e(error)
        val errorMessage = when (error) {
            is MobileAuthenticationClientException -> {
                getErrorMessage(error.error)
            }
            is BusinessException -> {
                context.getString(error.type.resId)
            }
            else -> getErrorMessage(error)
        }

        val action = NavigationGraphDirections.actionGlobalErrorFragment(
            ErrorNavigationParameter(errorMessage)
        )
        navigationDispatcher.requestNavigation(action)

        return true
    }
    //endregion

    //region Private Interface
    /**
     * Gets/composes an error message based on the received error response.
     *
     * @param error The received client error.
     * @return The error message.
     */
    private fun getErrorMessage(error: MobileAuthenticationClientError): String {
        return when (error) {
            is OperationError -> error.errorCode().description()
            is ch.nevis.mobile.sdk.api.operation.authcloudapi.AuthCloudApiError.OperationError -> error.errorCode()
                .description()
            else -> error.description()
        }
    }

    private fun getErrorMessage(throwable: Throwable): String {
        return throwable.message ?: throwable.stackTraceToString()
    }
    //endregion
}
