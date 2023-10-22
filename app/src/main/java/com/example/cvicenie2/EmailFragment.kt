package com.example.cvicenie2

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

class EmailFragment : Fragment(R.layout.fragment_email) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.submitButton).apply {
            setOnClickListener {
                it.findNavController().navigate(R.id.action_to_reset_password)
            }
        }
    }

    private fun login(username: String, password: String) {

    }
}