/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.userNamePasswordLogin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import ch.nevis.exampleapp.databinding.FragmentUsernamePasswordLoginBinding
import ch.nevis.exampleapp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.net.PasswordAuthentication

/**
 * [androidx.fragment.app.Fragment] implementation of Username and Password Login view where the user
 * can enter a username and password and send it to start a login process. If the login was successful
 * an in-band registration operation is started automatically.
 *
 * @constructor Creates a new instance.
 */
@AndroidEntryPoint
class UserNamePasswordLoginFragment : BaseFragment() {

    //region Properties
    /**
     * UI component bindings.
     */
    private var _binding: FragmentUsernamePasswordLoginBinding? = null
    private val binding get() = _binding!!

    /**
     * View model implementation of this view.
     */
    override val viewModel: UserNamePasswordLoginViewModel by viewModels()
    //endregion

    //region Fragment
    /** @suppress */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsernamePasswordLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** @suppress */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.confirmButton.setOnClickListener {
            val username =
                binding.usernameTextInputEditText.text?.toString() ?: return@setOnClickListener
            val password = binding.passwordTextInputEditText.text?.toList()?.toCharArray()
                ?: return@setOnClickListener
            viewModel.login(PasswordAuthentication(username, password))
        }
    }

    /** @suppress */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion
}
