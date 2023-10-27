package com.example.cvicenie2.fragments
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cvicenie2.BottomBar
import com.example.cvicenie2.viewmodels.FeedViewModel
import com.example.cvicenie2.R
import com.example.cvicenie2.adapters.FeedAdapter
import com.example.cvicenie2.databinding.FragmentFeedBinding

class FeedFragment : Fragment(R.layout.fragment_feed) {
    private lateinit var viewModel: FeedViewModel
    private var binding: FragmentFeedBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[FeedViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFeedBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->
            bnd.bottomBar.setActive(BottomBar.FEED)

            bnd.feedRecyclerview.layoutManager = LinearLayoutManager(context)
            val feedAdapter = FeedAdapter()
            bnd.feedRecyclerview.adapter = feedAdapter

            // Pozorovanie zmeny hodnoty
            viewModel.feed_items.observe(viewLifecycleOwner) { items ->
                Log.d("FeedFragment", "nove hodnoty $items")
                feedAdapter.updateItems(items)
            }

            bnd.btnGenerate.setOnClickListener {
                viewModel.updateItems()
            }
        }

    }
}