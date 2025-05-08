package com.example.p2p_error.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.p2p_error.Functions.DataReceiver
import com.example.p2p_error.Functions.decodeToString
import com.example.p2p_error.Functions.getLocalIpAddress
import com.example.p2p_error.R
import com.example.p2p_error.pages.inner.CheckBoxes

@Composable
fun ReceiveScreen(navController: NavController) {
    val context = LocalContext.current
    val socketNumber = remember { mutableStateOf("") } // Socket numarasını tutacak
    val ipAddress = remember { mutableStateOf(getLocalIpAddress(context)) } // IP adresi
    val showInfo = remember { mutableStateOf(false) } // Butona basıldığında gösterim
    val serverStatus = remember { mutableStateOf("") } // Sunucu durumu mesajı
    val receivedMessage = remember { mutableStateOf("") } // Gelen mesajı tutacak
    val detectionType = remember { mutableIntStateOf(0) }
    val realData = remember { mutableStateOf("") }
    val errorState = remember { mutableStateOf(false) } // Hata durumu

    LaunchedEffect(detectionType.value) {
        if (receivedMessage.value.isNotEmpty()) {
            realData.value = decodeToString(receivedMessage, detectionType)
        }
    }

    val lastMessage = remember { mutableStateOf("1")}
    LaunchedEffect(receivedMessage.value) {
        Log.e("REC", "${receivedMessage.value}")
        if (receivedMessage.value.isNotEmpty() && receivedMessage.value != lastMessage.value) {
            detectionType.value = receivedMessage.value[0].digitToInt()
            Log.e("REC", "${detectionType.value}")
            receivedMessage.value = receivedMessage.value.drop(1)
            lastMessage.value = receivedMessage.value
            realData.value = decodeToString(receivedMessage, detectionType)
        }
    }


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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
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
                        startServer(port, serverStatus, receivedMessage, errorState) // Server başlatma işlemi
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Başlat")
            }

            if (errorState.value) {
                Toast.makeText(context, "Hata: Sunucu başlatılamadı", Toast.LENGTH_SHORT).show()
                errorState.value = false
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

            CheckBoxes(detectionType)
//            realData.value = decodeToString(receivedMessage, detectionType)

            if(realData.value.isNotEmpty()){
                Text(
                    text = "Gerçek Veri: ${realData.value}",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

    }

}

// Sunucu başlatma fonksiyonu
fun startServer(
    port: Int,
    serverStatus: MutableState<String>,
    receivedMessage: MutableState<String>,
    errorState: MutableState<Boolean>
) {
    Thread {
        try {
            val dataReceiver = DataReceiver(port)
            serverStatus.value = "Sunucu başlatılıyor..."
            dataReceiver.startServer(receivedMessage, errorState)
            serverStatus.value = "Sunucu çalışıyor: Port $port"
        } catch (e: Exception) {
            serverStatus.value = "Sunucu başlatılamadı: ${e.message}"
        }
    }.start()
}