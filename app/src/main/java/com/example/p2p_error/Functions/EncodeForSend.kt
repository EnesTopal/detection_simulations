package com.example.p2p_error.Functions

import android.util.Log
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState

fun EncodeForSend(
    sendingMessage: MutableState<String>,
    detectionType: MutableIntState,
    binaryVersionText: MutableState<String>
): String {
    when (detectionType.value) {
        0 -> {
            Log.d("TEST", "Parity Check uygulanıyor")
            val countOnes = sendingMessage.value.count { it == '1' }
            val parityBit = if (countOnes % 2 == 0) '0' else '1'
            binaryVersionText.value = sendingMessage.value + " " + parityBit
            return binaryVersionText.value
        }

        1 -> {
            Log.d("TEST", "CRC uygulanıyor")
            val divisor = "1101"
            val data = sendingMessage.value
            val crc = applyCRC(data, divisor)
            binaryVersionText.value = sendingMessage.value + " " + crc
            return binaryVersionText.value
        }

        2 -> {
            Log.d("TEST", "Checksum uygulanıyor")
            val data = sendingMessage.value.replace(" ", "")
            val checksum = applyChecksum(data)
            binaryVersionText.value = sendingMessage.value + " " + checksum
            return binaryVersionText.value
        }

        else -> {
            println("Geçersiz detection tipi")
        }
    }
    return sendingMessage.value
}


//fun applyCRC(data: String, divisor: String): String {
//    val dataBits = data + "0".repeat(divisor.length - 1) // Ekstra 0'lar eklendi
//    val divisorLength = divisor.length
//
//    var current = dataBits.substring(0, divisorLength).toMutableList()
//    val remaining = dataBits.substring(divisorLength).toList()
//
//    fun xor(a: List<Char>, b: String): List<Char> {
//        return a.mapIndexed { i, bit ->
//            if (bit == b[i]) '0' else '1'
//        }
//    }
//
//    for (bit in remaining) {
//        if (current[0] == '1') {
//            current = xor(current, divisor).drop(1).toMutableList()
//        } else {
//            current = xor(current, "0".repeat(divisorLength)).drop(1).toMutableList()
//        }
//        current.add(bit)
//    }
//
//    // Son bir XOR daha yapmamız gerekebilir
//    current = if (current[0] == '1') {
//        xor(current, divisor).drop(1).toMutableList()
//    } else {
//        xor(current, "0".repeat(divisorLength)).drop(1).toMutableList()
//    }
//
//    return current.joinToString("")
//}

fun applyCRC(data: String, divisor: String): String {
    val paddedData = data.replace(" ", "") + "0".repeat(divisor.length - 1)
    var temp = paddedData

    Log.e("CRC", "Padded Data: $paddedData")
    while (temp.length >= divisor.length) {
        if (temp[0] == '1') {
            val xorResult = xor(temp.substring(0, divisor.length), divisor)
            temp = xorResult + temp.substring(divisor.length)
        } else {
            // sol bit 0 ise 000... ile xor
            val xorResult = xor(temp.substring(0, divisor.length), "0".repeat(divisor.length))
            temp = xorResult + temp.substring(divisor.length)
        }
        temp = temp.drop(1) // soldan bir bit at
    }
    Log.e("CRC", "Temp: $temp")

    // temp artık CRC remainder (divisor.length - 1 uzunlukta olmalı)
    return temp.padStart(divisor.length - 1, '0') // eksikse başa 0 koy
}




fun applyChecksum(binaryData: String): String {
    val cleanData = binaryData.replace(" ", "")
    val chunks = cleanData.chunked(8)

    var sum = 0
    for (chunk in chunks) {
        val byte = Integer.parseInt(chunk.padEnd(8, '0'), 2) // Eksikse sağdan 0'la tamamla
        sum += byte
    }

    // Eğer taşma olursa, sadece en düşük 8 bit alınır (8-bit overflow)
    sum = sum and 0xFF

    // 1'lerin tümleyeni (bitwise NOT)
    val checksum = sum.inv() and 0xFF

    return checksum.toString(2).padStart(8, '0')
}
