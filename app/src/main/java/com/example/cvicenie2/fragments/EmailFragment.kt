package com.example.cvicenie2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cvicenie2.R
import com.example.cvicenie2.data.DataRepository
import com.example.cvicenie2.databinding.FragmentEmailBinding
import com.example.cvicenie2.databinding.FragmentLoginBinding
import com.example.cvicenie2.viewmodels.AuthViewModel

class EmailFragment : Fragment() {
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: FragmentEmailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AuthViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmailBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.submitButton).apply {
            setOnClickListener {
               // it.findNavController().navigate(R.id.action_to_reset_password)
            }
        }
    }

    private fun login(username: String, password: String) {

    }
}