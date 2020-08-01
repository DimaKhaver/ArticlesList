package com.example.articleslist.data.remote.network

import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData


object NetworkEvents : LiveData<Event>() {
    internal fun notify(event: Event) { postValue(event) }
}

sealed class Event {
    class ConnectivityEvent(val isConnected: Boolean) : Event()
}

sealed class NetworkEvent {
    abstract val state: NetworkState

    class AvailabilityEvent(
        override val state: NetworkState,
        val oldNetworkAvailability: Boolean,
        val oldConnectivity:Boolean): NetworkEvent()

    class NetworkCapabilityEvent(
        override val state: NetworkState,
        val old: NetworkCapabilities?): NetworkEvent()

    class LinkPropertiesEvent(
        override val state: NetworkState,
        val old: LinkProperties?): NetworkEvent()
}


interface ConnectivityState {

    val isConnected: Boolean
        get() = networkStats.any {
            it.isAvailable
        }

    val networkStats: Iterable<NetworkState>
}


interface NetworkState {

    var isAvailable: Boolean

    val network: Network?

    val networkCapabilities: NetworkCapabilities?

    val linkProperties: LinkProperties?

    val isWifi: Boolean
        get() = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false

    val isMobile: Boolean
        get() = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ?: false

    val interfaceName: String?
        get() = linkProperties?.interfaceName
}