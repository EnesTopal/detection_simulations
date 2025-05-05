package com.example.p2p_error.Functions

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ServerSocket
import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.BufferedWriter


class DataReceiver(private val port: Int) {

    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null
    private var reader: BufferedReader? = null
    private var writer: BufferedWriter? = null

    fun startServer(receivedMessage: MutableState<String>, errorState: MutableState<Boolean>) {
        Thread {
            try {
                // Sunucu başlatılıyor
                serverSocket = ServerSocket(port)
                Log.e("Server", "Server başlatıldı, bağlantı bekleniyor...")

                // Bağlantıyı kabul et
                clientSocket = serverSocket?.accept()
                Log.e("Server", "Client bağlandı: ${clientSocket?.inetAddress}")

                // Gelen veriyi okuma ve gönderme
                reader = BufferedReader(InputStreamReader(clientSocket?.getInputStream()))
                writer = BufferedWriter(OutputStreamWriter(clientSocket?.getOutputStream()))

                var message: String?
                while (true) {
                    message = reader?.readLine()
                    if (message == null) break
                    Log.e("Server", "Gelen veri: $message")
                    // Gelen veriyi UI'ya aktar
                    receivedMessage.value = message

                    // Gelen veriye bir cevap gönder (isteğe bağlı)
                    writer?.write("Veri alındı: $message\n")
                    writer?.flush()
                }
            } catch (e: Exception) {
                errorState.value = true
                e.printStackTrace()
            } finally {
                stopServer()
            }
        }.start()
    }

    // Server'ı durdurma
    private fun stopServer() {
        Thread {
            try {
                reader?.close()
                writer?.close()
                clientSocket?.close()
                serverSocket?.close()
                Log.e("Server", "Server kapatıldı.")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()

    }
}
