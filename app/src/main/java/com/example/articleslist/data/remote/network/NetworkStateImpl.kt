package com.example.articleslist.data.remote.network

import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities


internal class NetworkStateImpl(callback: (NetworkState, NetworkEvent) -> Unit) : NetworkState {

    private var notify: (NetworkEvent) -> Unit

    init {
        this.notify = { event: NetworkEvent -> callback(this, event) }
    }

    override var isAvailable: Boolean = false
        set(value) {
            val old = field
            val oldConnected = ConnectivityStateHolder.isConnected
            field = value
            notify.invoke(NetworkEvent.AvailabilityEvent(this, old, oldConnected))
        }

    override var network: Network? = null

    override var linkProperties: LinkProperties? = null
        set(value) {
            val old = field
            field = value
            notify.invoke(NetworkEvent.LinkPropertiesEvent(this, old))
        }

    override var networkCapabilities: NetworkCapabilities? = null
        set(value) {
            val old = field
            field = value
            notify.invoke(NetworkEvent.NetworkCapabilityEvent(this, old))
        }
}