package ch.nevis.exampleapp.ui.credential.parameter

import ch.nevis.exampleapp.ui.base.model.NavigationParameter
import ch.nevis.exampleapp.ui.credential.model.CredentialViewMode
import ch.nevis.mobile.sdk.api.operation.RecoverableError

/**
 * Interface for Credential view navigation parameter.
 */
abstract class CredentialNavigationParameter : NavigationParameter {

    //region Properties
    /**
     * The mode, the Credential view intend to be used/initialized.
     */
    abstract val credentialViewMode: CredentialViewMode

    /**
     * The last recoverable error. It exists only if there was already a failed PIN/Password operation
     * attempt.
     */
    abstract val lastRecoverableError: RecoverableError?
    //endregion
}
