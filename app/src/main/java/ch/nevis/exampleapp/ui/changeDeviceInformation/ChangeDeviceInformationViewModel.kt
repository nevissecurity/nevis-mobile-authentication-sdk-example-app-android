/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.changeDeviceInformation

import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.common.error.ErrorHandler
import ch.nevis.exampleapp.domain.client.ClientProvider
import ch.nevis.exampleapp.domain.interaction.OnErrorImpl
import ch.nevis.exampleapp.domain.model.error.BusinessException
import ch.nevis.exampleapp.domain.model.operation.Operation
import ch.nevis.exampleapp.ui.base.BaseViewModel
import ch.nevis.exampleapp.ui.changeDeviceInformation.model.ChangeDeviceInformationViewData
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.result.parameter.ResultNavigationParameter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * View model implementation of Change Device Information view.
 *
 * @constructor Creates a new instance.
 * @param clientProvider An instance of a [ClientProvider] interface implementation.
 * @param errorHandler An instance of an [ErrorHandler] interface implementation.
 * @param navigationDispatcher An instance of a [NavigationDispatcher] interface implementation.
 */
@HiltViewModel
class ChangeDeviceInformationViewModel @Inject constructor(
    private val clientProvider: ClientProvider,
    private val errorHandler: ErrorHandler,
    private val navigationDispatcher: NavigationDispatcher
) : BaseViewModel() {

    //region Public Interface
    /**
     * Gets the current device information stored by the client and requests a view update to
     * display it to the user.
     */
    fun displayDeviceInformation() {
        try {
            val client = clientProvider.get() ?: throw BusinessException.clientNotInitialized()
            val deviceInformation = client.localData().deviceInformation().orElse(null)
                ?: throw BusinessException.deviceInformationNotFound()
            requestViewUpdate(ChangeDeviceInformationViewData(deviceInformation))
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }

    /**
     * Starts change device information operation.
     *
     * @param name The new name of the device information.
     * @param fcmRegistrationToken The FCM registration token if it is known, otherwise null.
     * @param disablePushNotifications A flag that tells push notifications have to be disabled or not.
     */
    fun changeDeviceInformation(
        name: String,
        fcmRegistrationToken: String? = null,
        disablePushNotifications: Boolean = false
    ) {
        if (name.isBlank()) {
            return
        }

        try {
            val client = clientProvider.get() ?: throw BusinessException.clientNotInitialized()
            val operation = client.operations().deviceInformationChange()
                .name(name)
                .onSuccess {
                    navigationDispatcher.requestNavigation(
                        NavigationGraphDirections.actionGlobalResultFragment(
                            ResultNavigationParameter.forSuccessfulOperation(Operation.CHANGE_DEVICE_INFORMATION)
                        )
                    )
                }
                .onError(OnErrorImpl(Operation.CHANGE_DEVICE_INFORMATION, errorHandler))

            fcmRegistrationToken?.let {
                operation.fcmRegistrationToken(it)
            }

            if (disablePushNotifications) {
                operation.disablePushNotifications()
            }

            operation.execute()
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }
    //endregion
}
