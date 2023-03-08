/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.transactionConfirmation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import ch.nevis.exampleapp.databinding.FragmentTransactionConfirmationBinding
import ch.nevis.exampleapp.ui.base.BaseFragment
import ch.nevis.exampleapp.ui.base.CancelOperationOnBackPressedCallback
import ch.nevis.exampleapp.ui.base.model.NavigationParameter
import ch.nevis.exampleapp.ui.base.model.ViewData
import ch.nevis.exampleapp.ui.transactionConfirmation.model.TransactionConfirmationViewData
import ch.nevis.exampleapp.ui.transactionConfirmation.parameter.TransactionConfirmationNavigationParameter
import dagger.hilt.android.AndroidEntryPoint

/**
 * [androidx.fragment.app.Fragment] implementation of Transaction Confirmation view where the user
 * can confirm or deny further processing of the operation based on the displayed transaction data.
 *
 * **IMPORTANT**: Transaction confirmation data handling is part of the account selection process of
 * an out-of-band authentication operation. This is the reason some account selection property must be
 * passed to transaction confirmation as well view to be able to continue the operation with the account
 * selection. Select Account view itself is not out-of-band authentication specific and supports
 * multiple operations.
 */
@AndroidEntryPoint
class TransactionConfirmationFragment : BaseFragment() {

    //region Properties
    /**
     * UI component bindings.
     */
    private var _binding: FragmentTransactionConfirmationBinding? = null
    private val binding get() = _binding!!

    /**
     * View model implementation of this view.
     */
    override val viewModel: TransactionConfirmationViewModel by viewModels()

    /**
     * Safe Args navigation arguments.
     */
    private val navigationArguments: TransactionConfirmationFragmentArgs by navArgs()
    //endregion

    //region Fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.updateViewModel(navigationArguments.parameter)

        binding.confirmButton.setOnClickListener {
            viewModel.confirm()
        }

        binding.cancelButton.setOnClickListener {
            viewModel.deny()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            CancelOperationOnBackPressedCallback(viewModel)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //region BaseFragment
    override fun updateViewParameter(parameter: NavigationParameter): Boolean {
        if (parameter is TransactionConfirmationNavigationParameter) {
            viewModel.updateViewModel(parameter)
            return true
        }
        return false
    }

    override fun updateView(viewData: ViewData) {
        super.updateView(viewData)
        if (viewData is TransactionConfirmationViewData) {
            binding.transactionConfirmationDataTextView.text = viewData.transactionConfirmationData
        }
    }
    //endregion
}