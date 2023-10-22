package com.example.cvicenie2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

class LoginFragment : Fragment(R.layout.fragment_log_in) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.submitButton).apply {
            setOnClickListener {
//                val username: String =
//                    findViewById<EditText>(R.id.username_edit_text).text.toString()
//                val password: String =
//                    findViewById<EditText>(R.id.password_edit_text).text.toString()
                it.findNavController().navigate(R.id.action_to_profile)
            }
        }

        view.findViewById<Button>(R.id.backButton).apply {
            setOnClickListener {
                it.findNavController().navigate(R.id.action_intro)
            }
        }
        view.findViewById<Button>(R.id.resetPasswordButton).apply {
            setOnClickListener {
                it.findNavController().navigate(R.id.action_to_email)
            }
        }

    }

    private fun login(username: String, password: String) {

    }
}

