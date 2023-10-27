package com.example.cvicenie2.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.cvicenie2.R
import com.example.cvicenie2.databinding.FragmentIntroBinding

class IntroFragment : Fragment(R.layout.fragment_intro) {
    private var binding: FragmentIntroBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentIntroBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->
            bnd.login.apply {
                setOnClickListener {
                    it.findNavController().navigate(R.id.action_intro_login)
                }
            }
            bnd.signup.apply {
                setOnClickListener {
                    it.findNavController().navigate(R.id.action_intro_signup)
                }
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}