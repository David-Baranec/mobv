package com.example.cvicenie2.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cvicenie2.BottomBar
import com.example.cvicenie2.FeedViewModel
import com.example.cvicenie2.R
import com.example.cvicenie2.adapters.FeedAdapter
import com.example.cvicenie2.adapters.MyItem

class FeedFragment : Fragment(R.layout.fragment_feed) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        // Inicializácia ViewModel
        val viewModel = ViewModelProvider(this)[FeedViewModel::class.java]

        view.findViewById<BottomBar>(R.id.bottom_bar).setActive(BottomBar.FEED)
        val recyclerView = view.findViewById<RecyclerView>(R.id.feed_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val feedAdapter = FeedAdapter()
        recyclerView.adapter = feedAdapter
        feedAdapter.updateItems(listOf(
            MyItem(1, R.drawable.baseline_feed_24,"Prvy"),
            MyItem(2, R.drawable.baseline_map_24,"Druhy"),
            MyItem(3, R.drawable.baseline_account_box_24,"Treti"),
            MyItem(4, R.drawable.baseline_feed_24,"Prvy"),
            MyItem(5, R.drawable.baseline_map_24,"Druhy"),
            MyItem(6, R.drawable.baseline_account_box_24,"Treti"),
        ))
        val newItems = mutableListOf<MyItem>()
        for (i in 1 .. 100) {
            newItems.add(MyItem(i, R.drawable.baseline_feed_24, "Text $i"))
        }


        viewModel.sampleString.observe(viewLifecycleOwner){
                new_items -> feedAdapter.updateItems(new_items)
        }
        viewModel.updateString(newItems)
    }
}