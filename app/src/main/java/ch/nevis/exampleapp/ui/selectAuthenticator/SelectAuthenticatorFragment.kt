/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.selectAuthenticator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import ch.nevis.exampleapp.databinding.FragmentSelectAuthenticatorBinding
import ch.nevis.exampleapp.ui.base.BaseFragment
import ch.nevis.exampleapp.ui.base.CancelOperationOnBackPressedCallback
import ch.nevis.exampleapp.ui.base.model.NavigationParameter
import ch.nevis.exampleapp.ui.selectAuthenticator.parameter.SelectAuthenticatorNavigationParameter
import dagger.hilt.android.AndroidEntryPoint

/**
 * [androidx.fragment.app.Fragment] implementation of Select Authenticator view.
 *
 * This view renders the available authenticators as a list and the user
 * can select one of them.
 *
 * @constructor Creates a new instance.
 */
@AndroidEntryPoint
class SelectAuthenticatorFragment : BaseFragment(), AuthenticatorSelectedListener {

    //region Properties
    /**
     * UI component bindings.
     */
    private var _binding: FragmentSelectAuthenticatorBinding? = null
    private val binding get() = _binding!!

    /**
     * Safe Args navigation arguments.
     */
    private val navigationArguments: SelectAuthenticatorFragmentArgs by navArgs()

    /**
     * An [AuthenticatorsRecyclerViewAdapter] instance that is used to render authenticators in a [androidx.recyclerview.widget.RecyclerView].
     */
    private lateinit var authenticatorsRecyclerViewAdapter: AuthenticatorsRecyclerViewAdapter

    /**
     * The view model instance for this view.
     */
    override val viewModel: SelectAuthenticatorViewModel by viewModels()
    //endregion

    //region Fragment
    /** @suppress */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectAuthenticatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** @suppress */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = context ?: return

        viewModel.updateViewModel(navigationArguments.parameter)

        authenticatorsRecyclerViewAdapter = AuthenticatorsRecyclerViewAdapter(
            navigationArguments.parameter.authenticatorItems?.toTypedArray() ?: arrayOf(),
            this
        )
        binding.authenticatorsRecyclerView.adapter = authenticatorsRecyclerViewAdapter
        binding.authenticatorsRecyclerView.layoutManager = LinearLayoutManager(context)

        // Override back button handling.
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            CancelOperationOnBackPressedCallback(viewModel)
        )
    }

    /** @suppress */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //region AuthenticatorsRecyclerViewAdapterDelegate
    override fun onAuthenticatorSelected(aaid: String) {
        viewModel.selectAuthenticator(aaid)
    }
    //endregion

    //region BaseFragment
    override fun updateViewParameter(parameter: NavigationParameter): Boolean {
        if (parameter is SelectAuthenticatorNavigationParameter) {
            viewModel.updateViewModel(parameter)
            return true
        }
        return false
    }
    //endregion
}
