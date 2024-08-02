/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ch.nevis.exampleapp.R
import ch.nevis.exampleapp.databinding.FragmentResultBinding
import ch.nevis.exampleapp.ui.util.navigateToHome
import dagger.hilt.android.AndroidEntryPoint

/**
 * [androidx.fragment.app.Fragment] implementation of Result view.
 *
 * This view shows the result of an operation in case the operation was successfully completed or it
 * was cancelled. In case of failed operations the [ch.nevis.exampleapp.ui.error.ErrorFragment] is
 * used.
 * If the user presses OS back button or the confirm button on this view then the application
 * navigates back to Home view.
 *
 * @constructor Creates a new instance.
 */
@AndroidEntryPoint
class ResultFragment : Fragment() {

    //region Properties
    /**
     * Android UI component bindings.
     */
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    /**
     * Safe Args navigation arguments.
     */
    private val navigationArguments: ResultFragmentArgs by navArgs()
    //endregion

    //region Fragment
    /** @suppress */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** @suppress */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = getString(
            navigationArguments.parameter.titleResId,
            getString(navigationArguments.parameter.operation?.resId ?: R.string.operation_general)
        )
        binding.titleTextView.text = title

        binding.confirmButton.setOnClickListener {
            findNavController().navigateToHome()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateToHome()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /** @suppress */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion
}
