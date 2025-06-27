/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.R
import ch.nevis.exampleapp.databinding.FragmentHomeBinding
import ch.nevis.exampleapp.ui.base.BaseFragment
import ch.nevis.exampleapp.ui.base.model.ViewData
import ch.nevis.exampleapp.ui.home.model.HomeViewData
import ch.nevis.exampleapp.ui.util.handleDispatchTokenResponse
import ch.nevis.mobile.sdk.api.localdata.Authenticator
import dagger.hilt.android.AndroidEntryPoint

/**
 * [androidx.fragment.app.Fragment] implementation of home view. This view is the start/home view of the application.
 * It can handle an out-of-band payload or Auth Cloud API registration, in-band authentication,
 * in-band registration. change PIN, change device information, or deregistration can be started from here.
 *
 * @constructor Creates a new instance.
 */
@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    //region Properties
    /**
     * UI component bindings.
     */
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    /**
     * View model for this view.
     */
    override val viewModel: HomeViewModel by viewModels()
    //endregion

    //region Fragment
    /** @suppress */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** @suppress */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()

        binding.qrCodeButton.setOnClickListener {
            val action = NavigationGraphDirections.actionGlobalQrReaderFragment()
            navController.navigate(action)
        }

        binding.inBandAuthenticationButton.setOnClickListener {
            viewModel.inBandAuthentication()
        }

        binding.deregisterButton.setOnClickListener {
            viewModel.deregister()
        }

        binding.changePinButton.setOnClickListener {
            viewModel.changeCredential(Authenticator.PIN_AUTHENTICATOR_AAID)
        }

        binding.changePasswordButton.setOnClickListener {
            viewModel.changeCredential(Authenticator.PASSWORD_AUTHENTICATOR_AAID)
        }

        binding.changeDeviceInformationButton.setOnClickListener {
            val action = NavigationGraphDirections.actionGlobalChangeDeviceInformationFragment()
            navController.navigate(action)
        }

        binding.authCloudRegistrationButton.setOnClickListener {
            val action = NavigationGraphDirections.actionGlobalAuthCloudRegistrationFragment()
            navController.navigate(action)
        }

        binding.deleteAuthenticatorsButton.setOnClickListener {
            viewModel.deleteAuthenticators()
        }

        binding.inBandRegistrationButton.setOnClickListener {
            val action = NavigationGraphDirections.actionGlobalUserNamePasswordLoginFragment()
            navController.navigate(action)
        }
    }

    /** @suppress */
    override fun onStart() {
        super.onStart()
        viewModel.initClient()
    }

    /** @suppress */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //region BaseFragment
    override fun updateView(viewData: ViewData) {
        super.updateView(viewData)

        if (viewData is HomeViewData) {
            updateHomeView(viewData)
        } else {
            binding.sdkVersionValueTextView.text = context?.getString(R.string.home_unknown)
            binding.facetIdValueTextView.text = context?.getString(R.string.home_unknown)
            binding.certFingerprintValueTextView.text = context?.getString(R.string.home_unknown)
        }

        // This is the point when we can be sure the client successfully initialized and
        // the dispatch token handling can be started.
        handleDispatchTokenResponse {
            viewModel.decodeOutOfBandPayload(it)
        }
    }
    //endregion

    //region Private Interface
    private fun updateHomeView(viewData: HomeViewData) {
        binding.titleTextView.text = context?.getString(
            R.string.home_registered_accounts,
            viewData.numberOfRegisteredAccounts
        )
        binding.titleTextView.visibility = View.VISIBLE
        binding.sdkVersionValueTextView.text = viewData.sdkVersion
        binding.facetIdValueTextView.text = viewData.facetId
        binding.certFingerprintValueTextView.text = viewData.certificateFingerprint

        val context = context ?: return
        val sdkAttestationInformation = viewData.sdkAttestationInformation ?: return

        binding.attestationValueTextView.visibility = View.GONE

        val successIcon = ContextCompat.getDrawable(context, R.drawable.success_icon)
        val errorIcon = ContextCompat.getDrawable(context, R.drawable.error_icon)

        val surrogateBasicIcon = if (sdkAttestationInformation.onlySurrogateBasicSupported) successIcon else errorIcon
        binding.surrogateBasicTextView.setCompoundDrawablesWithIntrinsicBounds(surrogateBasicIcon, null, null, null)
        binding.surrogateBasicTextView.visibility = View.VISIBLE

        val fullBasicDefaultIcon = if (sdkAttestationInformation.onlyDefaultMode) successIcon else errorIcon
        binding.fullBasicDefaultTextView.setCompoundDrawablesWithIntrinsicBounds(fullBasicDefaultIcon, null, null, null)
        binding.fullBasicDefaultTextView.visibility = View.VISIBLE

        val strictModeIcon = if (sdkAttestationInformation.strictMode) successIcon else errorIcon
        binding.fullBasicStrictTextView.setCompoundDrawablesWithIntrinsicBounds(strictModeIcon, null, null, null)
        binding.fullBasicStrictTextView.visibility = View.VISIBLE
    }
    //endregion
}
