/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.changeDeviceInformation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import ch.nevis.exampleapp.databinding.FragmentChangeDeviceInformationBinding
import ch.nevis.exampleapp.ui.base.BaseFragment
import ch.nevis.exampleapp.ui.base.model.ViewData
import ch.nevis.exampleapp.ui.changeDeviceInformation.model.ChangeDeviceInformationViewData
import dagger.hilt.android.AndroidEntryPoint

/**
 * [androidx.fragment.app.Fragment] implementation of Change Device Information view where the user
 * can see the current device information and she/he cna enter a new device information name and send it
 * to the client as input for a change device information operation.
 */
@AndroidEntryPoint
class ChangeDeviceInformationFragment : BaseFragment() {

    //region Properties
    /**
     * UI component bindings.
     */
    private var _binding: FragmentChangeDeviceInformationBinding? = null
    private val binding get() = _binding!!

    /**
     * The view model instance for this view.
     */
    override val viewModel: ChangeDeviceInformationViewModel by viewModels()
    //endregion

    //region Fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeDeviceInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.displayDeviceInformation()

        binding.confirmButton.setOnClickListener {
            viewModel.changeDeviceInformation(binding.newNameTextInputEditText.text.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //region BaseFragment
    override fun updateView(viewData: ViewData) {
        super.updateView(viewData)

        val changeDeviceInformationViewData = viewData as? ChangeDeviceInformationViewData ?: return
        binding.currentNameTextView.text = changeDeviceInformationViewData.deviceInformation.name()
    }
    //endregion
}
