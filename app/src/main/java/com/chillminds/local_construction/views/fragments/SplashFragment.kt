package com.chillminds.local_construction.views.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chillminds.local_construction.R
import com.chillminds.local_construction.common.Actions
import com.chillminds.local_construction.databinding.FragmentSplashBinding
import com.chillminds.local_construction.utils.isNullOrEmptyOrBlank
import com.chillminds.local_construction.view_models.SplashViewModel
import org.koin.android.ext.android.inject

class SplashFragment : Fragment() {

    lateinit var binding: FragmentSplashBinding

    private val viewModel by inject<SplashViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.commonModel.actionListener.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmptyOrBlank() && it == Actions.GOTO_HOME_PAGE_ACTIVITY) {
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigate(R.id.action_splashFragment_to_homeActivity)
                }, 3000)
            }
        }
    }
}
