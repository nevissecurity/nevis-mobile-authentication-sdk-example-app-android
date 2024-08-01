/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.retrofit.model

/**
 * Data class that represents the JSON response of [ch.nevis.exampleapp.retrofit.LoginEndPoint].
 *
 * @constructor Creates a new instance.
 * @param status Status, result of the login end-point call.
 * @param extId The external identifier of the user that logged in.
 */
data class LoginEndPointResponse(
    /**
     * Status, result of the login end-point call.
     */
    val status: String,

    /**
     * The external identifier of the user that logged in.
     */
    val extId: String
)
