/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2024. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.validation

import android.content.Context
import ch.nevis.exampleapp.R
import ch.nevis.mobile.sdk.api.operation.password.PasswordChangeRecoverableError
import ch.nevis.mobile.sdk.api.operation.password.PasswordEnrollmentError
import ch.nevis.mobile.sdk.api.operation.password.PasswordPolicy
import ch.nevis.mobile.sdk.api.util.Consumer

/**
 * Implementation of [PasswordPolicy] interface.
 * This policy validates the password entered by the user during registration or password changing,
 * and allows only password that are different from the string `password`.
 */
class PasswordPolicyImpl(
    /**
     * An Android [Context] object for [String] resource resolving.
     */
    private val context: Context,
) : PasswordPolicy {

    //region PasswordPolicy
    override fun validatePasswordForEnrollment(
        password: CharArray,
        onSuccess: Runnable,
        errorConsumer: Consumer<PasswordEnrollmentError>
    ) {
        if (isValid(password)) {
            onSuccess.run()
        } else {
            errorConsumer.accept(PasswordEnrollmentError.builder()
                .description(context.getString(R.string.password_policy_error_message))
                .cause(Exception(context.getString(R.string.password_policy_error_cause)))
                .build())
        }
    }

    override fun validatePasswordForPasswordChange(
        password: CharArray,
        onSuccess: Runnable,
        errorConsumer: Consumer<PasswordChangeRecoverableError.CustomValidationError>
    ) {
        if (isValid(password)) {
            onSuccess.run()
        } else {
            errorConsumer.accept(PasswordChangeRecoverableError.CustomValidationError.builder()
                .description(context.getString(R.string.password_policy_error_message))
                .cause(Exception(context.getString(R.string.password_policy_error_cause)))
                .build())
        }
    }
    //endregion

    //region Private Interface

    /**
     * Validates the password.
     *
     * @param password The password to validate.
     */
    private fun isValid(password: CharArray): Boolean {
        return String(password).trim() != "password"
    }
    //endregion
}
