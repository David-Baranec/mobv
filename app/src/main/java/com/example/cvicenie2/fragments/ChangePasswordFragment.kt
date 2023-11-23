package com.example.cvicenie2.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.cvicenie2.R
import com.example.cvicenie2.data.DataRepository
import com.example.cvicenie2.databinding.FragmentPasswordBinding
import com.example.cvicenie2.viewmodels.AuthViewModel

class ChangePasswordFragment : Fragment() {
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: FragmentPasswordBinding
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
        binding = FragmentPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            viewModel.passwordResult.observe(viewLifecycleOwner) {

                if (it.equals("success")) {
                    Log.d("ChangePasswordFragment", "success")
                    //PreferenceData.getInstance().clearData(requireContext())
                    findNavController().navigate(R.id.action_password_feed)

                }else
                    Log.d("ChangePasswordFragment", "failure")

            }

        }
    }


}


