/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.common.configuration

import ch.nevis.mobile.sdk.api.Configuration

/**
 * Default implementation of [ConfigurationProvider] interface.
 */
class ConfigurationProviderImpl(
    override val environment: Environment,
    override val configuration: Configuration,
    override val authenticatorAllowlist: List<String>
) : ConfigurationProvider
