package com.example.p2p_error.pages.inner


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

//@Composable
//fun CheckBoxes(detectionType: MutableIntState) {
//
//    Row(modifier = Modifier.padding(16.dp,8.dp)) {
//        listOf("Parity Check", "CRC", "Check Sum").forEachIndexed { index, label ->
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.padding(5.dp)
//            ) {
//                Column(modifier = Modifier,
//                    horizontalAlignment = Alignment.CenterHorizontally) {
//                    Text(text = label)
//                    Checkbox(
//                        checked = detectionType.value == index,
//                        onCheckedChange = {
//                            detectionType.value = index
//                            if (index == 0) {
//                                detectionType.value = 0
//                            }
//                            if (index == 1) {
//                                detectionType.value = 1
//                            }
//                            if (index == 2) {
//                                detectionType.value = 2
//                            }
//                        }
//                    )
//                }
//            }
//        }
//    }
//}
@Composable
fun CheckBoxes(detectionType: MutableIntState) {
    Row(modifier = Modifier.padding(16.dp, 8.dp)) {
        listOf("Parity Check", "CRC", "Check Sum").forEachIndexed { index, label ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(5.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = label)
                    Checkbox(
                        checked = detectionType.value == index,
                        onCheckedChange = {
                            detectionType.value = index
                        }
                    )
                }
            }
        }
    }
}
