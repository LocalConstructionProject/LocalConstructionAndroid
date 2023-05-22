package com.chillminds.local_construction.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chillminds.local_construction.R
import com.chillminds.local_construction.databinding.FragmentProjectDetailsBinding
import com.chillminds.local_construction.databinding.FragmentSettingsBinding

class ProjectDetailsFragment : Fragment() {

    lateinit var binding: FragmentProjectDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectDetailsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

}