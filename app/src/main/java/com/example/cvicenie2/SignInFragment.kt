package com.example.cvicenie2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.submitButton).apply {
            setOnClickListener {
                findNavController().navigate(R.id.action_intro_login)
            }
        }

        view.findViewById<Button>(R.id.backButton).apply {
            setOnClickListener {
                it.findNavController().navigate(R.id.action_intro)
            }
        }
    }


}