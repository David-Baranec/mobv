package com.example.cvicenie2
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var viewModel: FeedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<BottomBar>(R.id.bottom_bar).setActive(BottomBar.PROFILE)

        viewModel = ViewModelProvider(requireActivity())[FeedViewModel::class.java]

        viewModel.feed_items.observe(viewLifecycleOwner) { items ->
            // Tu môžete aktualizovať UI podľa hodnoty stringValue
            Log.d("Profile", "prvky su $items")
        }

    }
}