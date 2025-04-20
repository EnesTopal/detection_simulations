package com.example.p2p_error.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.p2p_error.Functions.getLocalIpAddress
import com.example.p2p_error.Functions.DataReceiver
import com.example.p2p_error.R

@Composable
fun ReceiveScreen(navController: NavController) {
    val context = LocalContext.current
    val socketNumber = remember { mutableStateOf("") } // Socket numarasını tutacak
    val ipAddress = remember { mutableStateOf(getLocalIpAddress(context)) } // IP adresi
    val showInfo = remember { mutableStateOf(false) } // Butona basıldığında gösterim
    val serverStatus = remember { mutableStateOf("") } // Sunucu durumu mesajı
    val receivedMessage = remember { mutableStateOf("") } // Gelen mesajı tutacak

    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { navController.navigate("SendScreen") },
            content = {
                Icon(
                    painter = painterResource(id = R.drawable.send_screen),
                    contentDescription = ""
                )
            }
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Socket numarası TextField
            TextField(
                value = socketNumber.value,
                onValueChange = { socketNumber.value = it },
                label = { Text("Socket Numarası") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Başlat butonu
            Button(
                onClick = {
                    if (socketNumber.value.isNotEmpty()) {
                        showInfo.value = true
                        val port = socketNumber.value.toIntOrNull() ?: 12345
                        startServer(port, serverStatus, receivedMessage) // Server başlatma işlemi
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Başlat")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // IP:Socket bilgisi gösterimi
            if (showInfo.value) {
                Text(
                    text = "IP: ${ipAddress.value} - Socket: ${socketNumber.value}",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Gelen mesajın gösterimi
            if (receivedMessage.value.isNotEmpty()) {
                Text(
                    text = "Gelen Veri: ${receivedMessage.value}",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Sunucu durumu bilgisi
            Text(
                text = "Sunucu Durumu: ${serverStatus.value}",
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// Sunucu başlatma fonksiyonu
fun startServer(port: Int, serverStatus: MutableState<String>, receivedMessage: MutableState<String>) {
    Thread {
        try {
            val dataReceiver = DataReceiver(port)
            serverStatus.value = "Sunucu başlatılıyor..."
            dataReceiver.startServer(receivedMessage) // Server başlatılıyor ve gelen veri burada alınacak
            serverStatus.value = "Sunucu çalışıyor: Port $port"
        } catch (e: Exception) {
            serverStatus.value = "Sunucu başlatılamadı: ${e.message}"
        }
    }.start()
}