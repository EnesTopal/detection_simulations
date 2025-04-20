package com.example.p2p_error.Functions

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import java.net.InetAddress

fun getLocalIpAddress(context: Context): String? {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: Network? = connectivityManager.activeNetwork
    val linkProperties = connectivityManager.getLinkProperties(activeNetwork)

    // IPv4 adresi almak için filtreleme yapıyoruz
    return linkProperties?.linkAddresses?.find {
        it.address.hostAddress.contains(".") // IPv4 adresi formatı (noktalarla ayrılmış)
    }?.address?.hostAddress
}

