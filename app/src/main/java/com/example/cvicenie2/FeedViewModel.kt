package com.example.cvicenie2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cvicenie2.adapters.MyItem

class FeedViewModel : ViewModel() {
    private val _feed_items = MutableLiveData<List<MyItem>>()
    val feed_items: LiveData<List<MyItem>> get() = _feed_items

    fun updateItems(items: List<MyItem>) {
        _feed_items.value = items
    }
}