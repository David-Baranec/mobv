package com.example.cvicenie2.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.cvicenie2.BottomBar
import com.example.cvicenie2.R
import com.example.cvicenie2.databinding.FragmentMapBinding
import com.mapbox.maps.Style

class MapFragment : Fragment(R.layout.fragment_map) {
    private var binding: FragmentMapBinding? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMapBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->
            bnd.bottomBar.setActive(BottomBar.MAP)
            bnd.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
        }
    }


}