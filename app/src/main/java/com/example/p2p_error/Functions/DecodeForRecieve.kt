package com.example.p2p_error.Functions

import android.util.Log
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState

fun decodeToString(binaryData: MutableState<String>, detectionType: MutableIntState, divider: String = "1101"): String {
    return when (detectionType.value) {
        0 -> decodeParityCheck(binaryData.value)    // Parity check için
        1 -> decodeCRC(binaryData.value, divider)   // CRC için
        2 -> decodeChecksum(binaryData.value)       // Checksum için
        else -> "Decode işlemi yapılamadı"
    }
}

fun convertToText(binaryData: String, detectionType: Int): String {
    if (binaryData.isEmpty()) {
        return ""
    }
    var data = binaryData

    // Her error detection type'ına göre bitleri sil
    data = when (detectionType) {
        0 -> data.dropLast(2)  // Parity check'te son 1 bit + boşluk
        1 -> data.dropLast(4)  // CRC'de kalan 3 bit + boşluk
        2 -> data.dropLast(9)  // Checksum'da 8 bit + boşluk
        else -> data
    }

    // Veriyi 8 bitlik parçalara ayır ve her birini karaktere dönüştür
    val binaryValues = data.split(" ")
    val decodedString = StringBuilder()

    for (binaryValue in binaryValues) {
        val charCode = Integer.parseInt(binaryValue, 2) // Binary'yi integer'a çevir
        decodedString.append(charCode.toChar()) // ASCII karakterine çevir
    }

    return decodedString.toString() // Son olarak decode edilen string'i döndür
}



fun decodeCRC(binaryData: String, divider: String): String {
    Log.e("CRC", "CRC ${binaryData}")
    val dividend = binaryData.replace(" ", "") // boşlukları kaldır
    var remainder = dividend

    while (remainder.length >= divider.length) {
        if (remainder[0] == '1') {
            val firstPart = remainder.substring(0, divider.length)
            val xorResult = xor(firstPart, divider)
            remainder = xorResult + remainder.substring(divider.length)
        }
        remainder = remainder.substring(1)
    }

    Log.e("CRC", "Remainder: $remainder")

    return if (remainder.all { it == '0' }) {
        val realMessage = convertToText(binaryData, 1)
        "Veri doğrulandı (CRC)\nGerçek veri: $realMessage"
    } else {
        "Veri hatalı (CRC)"
    }
}



fun xor(a: String, b: String): String {
    val result = StringBuilder()
    for (i in a.indices) { // a ve b aynı uzunlukta olmalı
        result.append(if (a[i] == b[i]) '0' else '1')
    }
    return result.toString()
}



fun decodeChecksum(binaryData: String): String {
    Log.e("Checksum", "Checksum kontrolü yapılıyor")
    var sum = 0
    var realMessage: String

    // Satır satır çalışacağız
    val lines = binaryData.trim().split(" ")

    for (line in lines) {
        if (line.isNotEmpty()) {
            val value = Integer.parseInt(line, 2) // İkilik tabanda integer yap
            sum += value
            sum = sum and 0xFF // 8 bit taşmayı önlemek için sadece son 8 bit alınır
        }
    }

    // 1'e tümleyen alınır
    val checksum = sum.inv() and 0xFF

    return if (checksum == 0) {
        realMessage = convertToText(binaryData, 2)
        "Veri doğrulandı (Checksum)\nGerçek veri: $realMessage"
    } else {
        "Veri hatalı (Checksum)"
    }
}


fun decodeParityCheck(binaryData: String): String {
    val bits = binaryData.trim().split(" ")
    val dataBits = bits.dropLast(1) // Parity bit hariç
    val parityBit = bits.last()
    var countOnes = 0

    for (bit in dataBits) {
        countOnes += bit.count { it == '1' } // her 8 bitlik parçadaki 1 sayısını say
    }

    return if ((countOnes % 2).toString() == parityBit) {
        val realMessage = convertToText(binaryData, 0)
        "Veri doğrulandı (Çift Parity) \nGerçek veri: $realMessage"
    } else {
        "Veri hatalı (Parity eşleşmedi)"
    }
}

