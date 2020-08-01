package com.example.articleslist.data.remote.network

import android.app.Application
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.articleslist.utils.getConnectivityServiceManager


object ConnectivityStateHolder: ConnectivityState {

    private val mutableSet: MutableSet<NetworkState> = mutableSetOf()

    override val networkStats: Iterable<NetworkState>
        get() = mutableSet


    private fun networkEventHandler(state: NetworkState, event: NetworkEvent) {
        when (event) {
            is NetworkEvent.AvailabilityEvent -> {
                if (isConnected != event.oldNetworkAvailability)
                NetworkEvents.notify(Event.ConnectivityEvent(state.isAvailable))
            }
        }
    }

    fun Application.registerConnectivityBroadcaster() {
        val connectivityManager = getConnectivityServiceManager()

        listOf(
            NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).build(),
            NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()
        ).forEach {
            val stateHolder = NetworkStateImpl { a, b -> networkEventHandler(a, b) }
            mutableSet.add(stateHolder)
            connectivityManager?.registerNetworkCallback(it, NetworkCallbackImpl(stateHolder))
        }
    }
}