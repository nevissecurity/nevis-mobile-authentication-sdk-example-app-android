/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.common.configuration

import ch.nevis.mobile.sdk.api.Configuration

/**
 * Interface declaration of a configuration provider that provides information about
 * the configured environment and the related [Configuration] object as well.
 */
interface ConfigurationProvider {

    //region Properties
    /**
     * The selected/configured [Environment] value.
     */
    val environment: Environment

    /**
     * The [Configuration] object related to the environment.
     */
    val configuration: Configuration
    //endregion
}