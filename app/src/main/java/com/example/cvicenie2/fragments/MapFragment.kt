package com.example.cvicenie2.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.cvicenie2.BottomBar
import com.example.cvicenie2.R
import com.mapbox.maps.MapView
import com.mapbox.maps.Style

class MapFragment : Fragment(R.layout.fragment_map) {
    private var mapView: MapView? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<BottomBar>(R.id.bottom_bar).setActive(BottomBar.MAP)

        mapView = view.findViewById(R.id.mapView)
        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)
    }

}