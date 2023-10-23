package com.example.cvicenie2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.cvicenie2.data.api.DataRepository
import com.example.cvicenie2.viewmodels.AuthViewModel
import com.google.android.material.snackbar.Snackbar

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    private lateinit var viewModel: AuthViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance()) as T
            }
        })[AuthViewModel::class.java]

        viewModel.registrationResult.observe(viewLifecycleOwner) {
            if (it.second != null) {
                requireView().findNavController().navigate(R.id.action_signup_feed)
            } else {
                Snackbar.make(
                    view.findViewById(R.id.submit_button),
                    it.first,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        view.findViewById<TextView>(R.id.submitButton).apply {
            setOnClickListener {

                viewModel.registerUser(
                    view.findViewById<EditText>(R.id.username_edit_text).text.toString(),
                    view.findViewById<EditText>(R.id.email_edit_text).text.toString(),
                    view.findViewById<EditText>(R.id.email_edit_text).text.toString()
                )
            }
        }

        view.findViewById<Button>(R.id.backButton).apply {
            setOnClickListener {
                it.findNavController().navigate(R.id.action_intro)
            }
        }
    }


}