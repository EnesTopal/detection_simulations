package com.example.p2p_error.Functions

import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.BufferedWriter

class DataSender(private val serverIp: String, private val port: Int) {
    private var socket: Socket? = null
    private var reader: BufferedReader? = null
    private var writer: BufferedWriter? = null

    // Sunucuya veri gönderme
    fun sendData(message: String) {
        try {
            socket = Socket(serverIp, port)
            println("Sunucuya bağlanıldı: $serverIp:$port")

            reader = BufferedReader(InputStreamReader(socket?.getInputStream()))
            writer = BufferedWriter(OutputStreamWriter(socket?.getOutputStream()))

            // Mesajı gönder
            writer?.write(message + "\n")
            writer?.flush()
            println("Gönderilen veri: $message")

            // Sunucudan gelen cevabı al
            val response = reader?.readLine()
            println("Sunucudan gelen cevap: $response")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            closeConnection()
        }
    }

    // Bağlantıyı kapatma
    fun closeConnection() {
        try {
            reader?.close()
            writer?.close()
            socket?.close()
            println("Bağlantı kapatıldı.")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
