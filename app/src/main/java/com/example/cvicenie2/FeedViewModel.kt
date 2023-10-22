package com.example.cvicenie2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cvicenie2.adapters.MyItem

class FeedViewModel : ViewModel() {
    private val _items = MutableLiveData<List<MyItem>>()
    val sampleString: LiveData<List<MyItem>> get() = _items

    fun updateString(new_items: List<MyItem>) {
        _items.postValue(new_items)
    }
}