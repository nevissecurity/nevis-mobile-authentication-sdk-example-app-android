/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.dagger

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import ch.nevis.exampleapp.common.configuration.ConfigurationProvider
import ch.nevis.exampleapp.common.configuration.ConfigurationProviderImpl
import ch.nevis.exampleapp.common.configuration.Environment
import ch.nevis.exampleapp.common.error.*
import ch.nevis.exampleapp.common.settings.Settings
import ch.nevis.exampleapp.common.settings.SettingsImpl
import ch.nevis.exampleapp.domain.client.ClientProvider
import ch.nevis.exampleapp.domain.client.ClientProviderImpl
import ch.nevis.exampleapp.domain.deviceInformation.DeviceInformationFactory
import ch.nevis.exampleapp.domain.deviceInformation.DeviceInformationFactoryImpl
import ch.nevis.exampleapp.domain.interaction.*
import ch.nevis.exampleapp.domain.log.SdkLogger
import ch.nevis.exampleapp.domain.log.SdkLoggerImpl
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcherImpl
import ch.nevis.mobile.sdk.api.Configuration
import ch.nevis.mobile.sdk.api.operation.pin.PinChanger
import ch.nevis.mobile.sdk.api.operation.pin.PinEnroller
import ch.nevis.mobile.sdk.api.operation.selection.AccountSelector
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.DevicePasscodeUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerifier
import ch.nevis.mobile.sdk.api.operation.userverification.PinUserVerifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URI
import javax.inject.Singleton

/**
 * Main Dagger Hilt configuration module of the example application.
 */
@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    //region Configuration
    @Suppress("DEPRECATION")
    @SuppressLint("PackageManagerGetSignatures")
    @Provides
    @Singleton
    fun provideAuthenticationCloudConfiguration(application: Application): Configuration {
        val packageInfo = application.packageManager.getPackageInfo(
            application.packageName,
            PackageManager.GET_SIGNATURES
        )
        return Configuration.authCloudBuilder()
            .packageInfo(packageInfo)
            .hostname("myinstance.mauth.nevis.cloud")
            .facetId("android:apk-key-hash:ch.nevis.mobile.authentication.sdk.android.example")
            .authenticationRetryIntervalInSeconds(15L)
            .build()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("PackageManagerGetSignatures")
    @Provides
    @Singleton
    fun provideIdentitySuiteConfiguration(application: Application): Configuration {
        val packageInfo = application.packageManager.getPackageInfo(
            application.packageName,
            PackageManager.GET_SIGNATURES
        )
        return Configuration.builder()
            .packageInfo(packageInfo)
            .baseUrl(URI.create("https://mycompany.com/"))
            .registrationRequestPath("/nevisfido/uaf/1.1/request/registration/")
            .registrationResponsePath("/nevisfido/uaf/1.1/registration/")
            .authenticationRequestPath("/auth/fidouaf")
            .authenticationResponsePath("/auth/fidouaf/authenticationresponse/")
            .deregistrationRequestPath("/nevisfido/uaf/1.1/request/deregistration/")
            .dispatchTargetResourcePath("/nevisfido/token/dispatch/targets/")
            .authenticationRetryIntervalInSeconds(15L)
            .build()
    }

    @Provides
    @Singleton
    fun provideConfigurationProvider(application: Application): ConfigurationProvider =
        ConfigurationProviderImpl(
            Environment.AUTHENTICATION_CLOUD,
            provideAuthenticationCloudConfiguration(application)
        )
    //endregion

    //region Client
    @Provides
    @Singleton
    fun provideClientProvider(): ClientProvider = ClientProviderImpl()
    //endregion

    //region Error Handling
    @Provides
    @Singleton
    fun provideErrorHandler(
        @ApplicationContext context: Context,
        navigationDispatcher: NavigationDispatcher
    ): ErrorHandler = ChainErrorHandlerImpl(
        listOf(
            CancelErrorHandlerImpl(navigationDispatcher),
            DefaultErrorHandlerImpl(context, navigationDispatcher)
        )
    )
    //endregion

    //region Navigator
    @Provides
    @Singleton
    fun provideNavigator(): NavigationDispatcher = NavigationDispatcherImpl()
    //endregion

    //region Settings
    @Provides
    @Singleton
    fun provideSettings(@ApplicationContext context: Context): Settings = SettingsImpl(context)
    //endregion

    //region Interaction
    @Provides
    fun provideBiometricUserVerifier(navigationDispatcher: NavigationDispatcher): BiometricUserVerifier =
        BiometricUserVerifierImpl(navigationDispatcher)

    @Provides
    fun provideDevicePasscodeUserVerifier(navigationDispatcher: NavigationDispatcher): DevicePasscodeUserVerifier =
        DevicePasscodeUserVerifierImpl(navigationDispatcher)

    @Provides
    fun provideFingerprintUserVerifier(navigationDispatcher: NavigationDispatcher): FingerprintUserVerifier =
        FingerprintUserVerifierImpl(navigationDispatcher)

    @Provides
    fun provideAccountSelector(
        navigationDispatcher: NavigationDispatcher,
        errorHandler: ErrorHandler
    ): AccountSelector =
        AccountSelectorImpl(navigationDispatcher, errorHandler)

    @Provides
    fun provideAuthenticationAuthenticatorSelector(
        navigationDispatcher: NavigationDispatcher,
        settings: Settings
    ): AuthenticationAuthenticatorSelector =
        AuthenticationAuthenticatorSelectorImpl(
            navigationDispatcher,
            settings
        )

    @Provides
    fun provideRegistrationAuthenticatorSelector(
        navigationDispatcher: NavigationDispatcher,
        settings: Settings
    ): RegistrationAuthenticatorSelector =
        RegistrationAuthenticatorSelectorImpl(
            navigationDispatcher,
            settings
        )

    @Provides
    fun providePinChanger(navigationDispatcher: NavigationDispatcher): PinChanger =
        PinChangerImpl(navigationDispatcher)

    @Provides
    fun providePinEnroller(navigationDispatcher: NavigationDispatcher): PinEnroller =
        PinEnrollerImpl(navigationDispatcher)

    @Provides
    fun providePinUserVerifier(navigationDispatcher: NavigationDispatcher): PinUserVerifier =
        PinUserVerifierImpl(navigationDispatcher)
    //endregion

    //region Logger
    @Provides
    @Singleton
    fun provideSdkLogger(): SdkLogger = SdkLoggerImpl()
    //endregion

    //region Factory
    @Provides
    @Singleton
    fun provideDeviceInformationFactory(@ApplicationContext context: Context): DeviceInformationFactory =
        DeviceInformationFactoryImpl(context)
    //endregion

    //region Retrofit
    @Provides
    fun provideRetrofit(configurationProvider: ConfigurationProvider): Retrofit =
        Retrofit.Builder().baseUrl(configurationProvider.configuration.baseUrl().toString())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    //endregion
}