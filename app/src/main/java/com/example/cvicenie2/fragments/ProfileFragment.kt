package com.example.cvicenie2.fragments
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.cvicenie2.BottomBar
import com.example.cvicenie2.R

class ProfileFragment : Fragment(R.layout.fragment_profile) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<BottomBar>(R.id.bottom_bar).setActive(BottomBar.PROFILE)


    }
}