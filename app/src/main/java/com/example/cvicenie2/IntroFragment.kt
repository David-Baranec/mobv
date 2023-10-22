package com.example.cvicenie2

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

class IntroFragment : Fragment(R.layout.fragment_intro) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.log_in_button).apply {
            setOnClickListener{
                it.findNavController().navigate(R.id.action_intro_login)
            }
        }

        view.findViewById<Button>(R.id.sign_in_button).apply {
            setOnClickListener{
                it.findNavController().navigate(R.id.action_intro_signup)
            }
        }
    }
}