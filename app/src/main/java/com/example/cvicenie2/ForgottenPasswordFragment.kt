package com.example.cvicenie2

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

class ForgottenPasswordFragment: Fragment(R.layout.fragment_forgotten_password) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    view.findViewById<Button>(R.id.submitButton).apply {
        setOnClickListener {
            it.findNavController().navigate(R.id.action_intro_login)
        }
    }
}

    private fun login(username: String, password: String) {

    }
}
