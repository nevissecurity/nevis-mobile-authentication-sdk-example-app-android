/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.authCloudRegistration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import ch.nevis.exampleapp.databinding.FragmentAuthCloudRegistrationBinding
import ch.nevis.exampleapp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * [androidx.fragment.app.Fragment] implementation of Auth Cloud API Registration view where the user
 * can enter an enroll response or an app link URI and send it to the Auth Cloud API as input for
 * a registration operation.
 *
 * @constructor Creates a new instance.
 */
@AndroidEntryPoint
class AuthCloudRegistrationFragment : BaseFragment() {

    //region Properties
    /**
     * UI component bindings.
     */
    private var _binding: FragmentAuthCloudRegistrationBinding? = null
    private val binding get() = _binding!!

    /**
     * View model implementation of this view.
     */
    override val viewModel: AuthCloudRegistrationViewModel by viewModels()
    //endregion

    //region Fragment
    /** @suppress */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthCloudRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** @suppress */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.confirmButton.setOnClickListener {
            viewModel.authCloudRegistration(
                binding.enrollResponseTextInputEditText.text.toString(),
                binding.appLinkUriTextInputEditText.text.toString()
            )
        }
    }

    /** @suppress */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion
}
