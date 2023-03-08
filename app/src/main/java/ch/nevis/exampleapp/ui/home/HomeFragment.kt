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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.R
import ch.nevis.exampleapp.databinding.FragmentHomeBinding
import ch.nevis.exampleapp.ui.base.BaseFragment
import ch.nevis.exampleapp.ui.base.model.ViewData
import ch.nevis.exampleapp.ui.home.model.HomeViewData
import ch.nevis.exampleapp.ui.util.handleDispatchTokenResponse
import dagger.hilt.android.AndroidEntryPoint

/**
 * [androidx.fragment.app.Fragment] implementation of home view. This view is the start/home view of the application.
 * It can handle an out-of-band payload or Auth Cloud API registration, in-band authentication,
 * in-band registration. change PIN, change device information, or deregistration can be started from here.
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

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
            viewModel.changePin()
        }

        binding.changeDeviceInformationButton.setOnClickListener {
            val action = NavigationGraphDirections.actionGlobalChangeDeviceInformationFragment()
            navController.navigate(action)
        }

        binding.authCloudRegistrationButton.setOnClickListener {
            val action = NavigationGraphDirections.actionGlobalAuthCloudRegistrationFragment()
            navController.navigate(action)
        }

        binding.inBandRegistrationButton.setOnClickListener {
            val action = NavigationGraphDirections.actionGlobalUserNamePasswordLoginFragment()
            navController.navigate(action)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.initClient()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //region BaseFragment
    override fun updateView(viewData: ViewData) {
        super.updateView(viewData)

        val homeViewData = viewData as? HomeViewData ?: return
        binding.titleTextView.text = context?.getString(
            R.string.home_registered_accounts,
            homeViewData.numberOfRegisteredAccounts
        )

        // This is the point when we can be sure the client successfully initialized and
        // the dispatch token handling can be started.
        handleDispatchTokenResponse {
            viewModel.decodeOutOfBandPayload(it)
        }
    }
    //endregion
}