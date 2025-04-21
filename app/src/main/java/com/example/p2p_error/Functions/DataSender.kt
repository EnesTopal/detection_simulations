package com.example.p2p_error.Functions

import android.util.Log
import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.BufferedWriter

class DataSender(val serverIp: String, val port: Int) {
    private var socket: Socket? = null
    private var reader: BufferedReader? = null
    private var writer: BufferedWriter? = null

    fun sendData(message: String) {
        Thread {
            Log.e("Server", "Sending data to ${serverIp}:${port}")
            try {
                Log.e("Server", "Entered the try block to send data to the server")
                socket = Socket(serverIp, port)
                Log.e("Server", "Connected server: $serverIp:$port")

                reader = BufferedReader(InputStreamReader(socket?.getInputStream()))
                writer = BufferedWriter(OutputStreamWriter(socket?.getOutputStream()))


                writer?.write(message + "\n")
                writer?.flush()
                Log.e("Server", "Sending message: $message")

                val response = reader?.readLine()
                Log.e("Server", "Response from server: $response")
            } catch (e: Exception) {
                Log.e("Server", "Error: ${e.message}")
                e.printStackTrace()
            } finally {
                closeConnection()
            }
        }.start()
    }

    fun closeConnection() {
        Thread {
            try {
                reader?.close()
                writer?.close()
                socket?.close()
                println("Bağlantı kapatıldı.")
            } catch (e: Exception) {
                Log.e("Trace", "$e.printStackTrace()")
//            e.printStackTrace()
            }
        }.start()
    }
}
