/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.interaction

import ch.nevis.mobile.sdk.api.operation.selection.AuthenticatorSelector

/**
 * Marker interface declaration for [AuthenticatorSelector] implementations those are used during
 * registration operations. This interface is created to ease Dagger Hilt injection of [AuthenticatorSelector]
 * implementations.
 */
interface RegistrationAuthenticatorSelector : AuthenticatorSelector