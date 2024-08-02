/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.selectAccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import ch.nevis.exampleapp.databinding.FragmentSelectAccountBinding
import ch.nevis.exampleapp.domain.model.operation.Operation
import ch.nevis.exampleapp.ui.base.BaseFragment
import ch.nevis.exampleapp.ui.base.CancelOperationOnBackPressedCallback
import ch.nevis.exampleapp.ui.base.model.NavigationParameter
import ch.nevis.exampleapp.ui.selectAccount.parameter.SelectAccountNavigationParameter
import ch.nevis.mobile.sdk.api.localdata.Account
import dagger.hilt.android.AndroidEntryPoint

/**
 * [androidx.fragment.app.Fragment] implementation of Select Account view where the user
 * can one of her/his registered accounts for an operation.
 *
 * @constructor Creates a new instance.
 */
@AndroidEntryPoint
class SelectAccountFragment : BaseFragment(),
    AccountSelectedListener {

    //region Properties
    /**
     * UI component bindings.
     */
    private var _binding: FragmentSelectAccountBinding? = null
    private val binding get() = _binding!!

    /**
     * The view model instance for this view.
     */
    override val viewModel: SelectAccountViewModel by viewModels()

    /**
     * Safe Args navigation arguments.
     */
    private val navigationArguments: SelectAccountFragmentArgs by navArgs()

    /**
     * An [AccountsRecyclerViewAdapter] instance that is used to render accounts in a [androidx.recyclerview.widget.RecyclerView].
     */
    private lateinit var accountsRecyclerViewAdapter: AccountsRecyclerViewAdapter
    //endregion

    //region Fragment
    /** @suppress */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** @suppress */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = context ?: return

        viewModel.updateViewModel(navigationArguments.parameter)

        accountsRecyclerViewAdapter = AccountsRecyclerViewAdapter(
            navigationArguments.parameter.accounts?.toTypedArray() ?: arrayOf(),
            this
        )
        binding.accountsRecyclerView.adapter = accountsRecyclerViewAdapter
        binding.accountsRecyclerView.layoutManager = LinearLayoutManager(context)

        // Override back button handling.
        if (navigationArguments.parameter.operation == Operation.OUT_OF_BAND_AUTHENTICATION) {
            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                CancelOperationOnBackPressedCallback(viewModel)
            )
        }
    }

    /** @suppress */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //region AccountSelectedListener
    override fun onAccountSelected(account: Account) {
        viewModel.selectAccount(
            navigationArguments.parameter.operation,
            account.username()
        )
    }
    //endregion

    //region BaseFragment
    override fun updateViewParameter(parameter: NavigationParameter): Boolean {
        if (parameter is SelectAccountNavigationParameter) {
            viewModel.updateViewModel(parameter)
            return true
        }
        return false
    }
    //endregion
}
