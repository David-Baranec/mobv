package com.example.cvicenie2.fragments
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.cvicenie2.BottomBar
import com.example.cvicenie2.R
import com.example.cvicenie2.databinding.FragmentProfileBinding
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var binding: FragmentProfileBinding? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProfileBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->
            bnd.bottomBar.setActive(BottomBar.PROFILE)
        }
    }
}