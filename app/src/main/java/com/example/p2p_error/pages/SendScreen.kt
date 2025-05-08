package com.example.p2p_error.pages

import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.p2p_error.Functions.DataSender
import com.example.p2p_error.Functions.EncodeForSend
import com.example.p2p_error.Functions.toBinary
import com.example.p2p_error.R
import com.example.p2p_error.pages.inner.CheckBoxes

@Composable
fun SendScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp //411--412
    val screenWidth = configuration.screenWidthDp //411--412
    val sendingMessage = remember { mutableStateOf("") }
    val binaryVersion = remember { mutableStateOf("") }
    val binaryVersionText = remember { mutableStateOf("") }
    val detectionType = remember { mutableIntStateOf(0) }
    val serverIp = remember { mutableStateOf("192.168.0.16") }
    val serverPort = remember { mutableStateOf<Int>(0) }
    val dataToSend = remember { mutableStateOf("") }
    val showOuterPopup = remember { mutableStateOf(false) }             // Blok listesi popup’ı açık mı
    val showInnerEditPopup = remember { mutableStateOf(false) }         // Seçilen bloğun edit popup’ı açık mı
    val selectedBlockIndex = remember { mutableStateOf(-1) }            // Hangi blok seçildi
    val blocks = remember { mutableStateListOf<String>() }              // dataToSend.split(" ") edilmiş hali
    val isDataChanged = remember { mutableStateOf(false) }

    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { navController.navigate("ReceiveScreen") },
            content = {
                Icon(
                    painter = painterResource(id = R.drawable.recieve_screen),
                    contentDescription = ""
                )
            }
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Sending Message TextField
            TextField(
                value = sendingMessage.value,
                onValueChange = { sendingMessage.value = it },
                label = {
                    Text(
                        text = "Sending Message"
                    )
                }
            )
            Text(
                "Binary karşılığı:", modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 10.dp)
            )
            Text(
                text = toBinary(sendingMessage.value, binaryVersion),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 8.dp),
                textAlign = TextAlign.Center,
                maxLines = 3
            )
            CheckBoxes(detectionType)

            // Server IP TextField
            TextField(
                value = serverIp.value,
                onValueChange = { serverIp.value = it },
                label = { Text("Server IP") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            // Server port TextField
            TextField(
                value = serverPort.value.toString(),
                onValueChange = { serverPort.value = it.toIntOrNull() ?: 0},
                label = { Text("Server Port") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )


            Button(onClick = {
                dataToSend.value = EncodeForSend(binaryVersion, detectionType, binaryVersionText)
                dataToSend.value = detectionType.value.toString() + dataToSend.value
                blocks.clear()
                blocks.addAll(dataToSend.value.trim().split(" "))
                isDataChanged.value = true
                showOuterPopup.value = true
            }) {
                Log.e("Last", "Data blocks: ${blocks}")
                Text("Veriyi Değiştir")
            }

            Button(
                modifier = Modifier.padding(5.dp),
                onClick = {
                    if (isDataChanged.value == false){
                        dataToSend.value = EncodeForSend(binaryVersion, detectionType, binaryVersionText)
                        dataToSend.value = detectionType.value.toString() + dataToSend.value

                        val dataSender = DataSender(serverIp.value, serverPort.value)
                        dataSender.sendData(dataToSend.value)
                    }
                    else{
                        val dataSender = DataSender(serverIp.value, serverPort.value)
                        dataSender.sendData(dataToSend.value)
                        isDataChanged.value = false
                    }
                }
            ) {
                Log.e("Last", "Data to send: ${dataToSend.value}")
                Text(text = "Send")
            }

            // Gönderilecek veri
            Text(
                text = "Gönderilen veri\n ${dataToSend.value}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 8.dp),
                textAlign = TextAlign.Center
            )


        }

    }

    if (showOuterPopup.value) {
        AlertDialog(
            onDismissRequest = { showOuterPopup.value = false },
            title = { Text("Blok Seç") },
            text = {
                Column {
                    blocks.forEachIndexed { index, block ->
                        Button(
                            onClick = {
                                selectedBlockIndex.value = index
                                showInnerEditPopup.value = true
                            },
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(block)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(onClick = { showOuterPopup.value = false }) {
                    Text("Kapat")
                }
            }
        )
    }
    if (showInnerEditPopup.value && selectedBlockIndex.value != -1) {
        val blockChars = remember { mutableStateListOf<Char>() }

        // Seçilen blok yükleniyor
        LaunchedEffect(selectedBlockIndex.value) {
            blockChars.clear()
            blockChars.addAll(blocks[selectedBlockIndex.value].toList())
        }

        AlertDialog(
            onDismissRequest = { showInnerEditPopup.value = false },
            title = { Text("Bit Düzenle") },
            text = {
                val scrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .horizontalScroll(scrollState)
                        .padding(4.dp)
                ) {
                    blockChars.forEachIndexed { i, c ->
                        Button(onClick = {
                            blockChars[i] = if (c == '0') '1' else '0'
                        }, modifier = Modifier.padding(2.dp)) {
                            Text(blockChars[i].toString())
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    // Değiştirilen bloğu geri yaz
                    blocks[selectedBlockIndex.value] = blockChars.joinToString("")
                    dataToSend.value = blocks.joinToString(" ")
                    showInnerEditPopup.value = false
                }) {
                    Text("Onayla")
                }
            },
            dismissButton = {
                Button(onClick = { showInnerEditPopup.value = false }) {
                    Text("İptal")
                }
            }
        )
    }

}


@Preview
@Composable
fun SendScreenPreview() {
    val navController = NavController(context = LocalContext.current)
    SendScreen(navController)
}
