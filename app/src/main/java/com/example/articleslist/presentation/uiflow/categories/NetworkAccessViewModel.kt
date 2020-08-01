package com.example.articleslist.presentation.uiflow.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.articleslist.data.remote.network.Event
import com.example.articleslist.data.remote.network.NetworkEvents

// just a temp wrapper
open class NetworkAccessViewModel: ViewModel() {

    protected val connectivityLiveData: LiveData<Boolean>
        get() = _connectivityLiveData

    private val _connectivityLiveData = MutableLiveData<Boolean>()

    init {
        NetworkEvents.observeForever {
            if (it is Event.ConnectivityEvent)
                _connectivityLiveData.postValue(it.isConnected)
        }
    }

}