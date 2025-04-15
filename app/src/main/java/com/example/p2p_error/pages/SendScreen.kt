package com.example.p2p_error.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.p2p_error.Functions.EncodeForSend
import com.example.p2p_error.R
import com.example.p2p_error.Functions.toBinary
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
    val sendingAddress = remember { mutableStateOf("") }
    var dataToSend = remember { mutableStateOf("") }

    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { navController.navigate("RecieveScreen") },
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
            TextField(
                value = sendingAddress.value,
                onValueChange = { sendingAddress.value = it },
                label = {
                    Text(
                        text = "Sending Address"
                    )
                }
            )
            Button(
                modifier = Modifier.padding(5.dp),
                onClick = { dataToSend.value= EncodeForSend(binaryVersion, detectionType, binaryVersionText) }) {
                Text(text = "Send")
            }
            Text(
                text = "Gönderilecek veri\n ${binaryVersionText.value}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 8.dp),
                textAlign = TextAlign.Center
            )
        }

    }
}

@Preview
@Composable
fun SendScreen() {
    val navController = NavController(context = LocalContext.current)
    SendScreen(navController)
}