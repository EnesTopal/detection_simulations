package com.example.p2p_error.Functions

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState

fun decodeToString(binaryData: MutableState<String>, detectionType: MutableIntState, divider: String = "1011"): String {
    return when (detectionType.value) {
        0 -> decodeParityCheck(binaryData.value)    // Parity check için
        1 -> decodeCRC(binaryData.value, divider)   // CRC için
        2 -> decodeChecksum(binaryData.value)       // Checksum için
        else -> "Decode işlemi yapılamadı"
    }
}




fun decodeCRC(binaryData: String, divider: String): String {
    val bits = binaryData.split(" ")
    var dividend = bits.joinToString("") // Binary verisini tek bir string haline getiriyoruz.

    // CRC işlemi için divider ile XOR yapıyoruz (bunu basit tutalım).
    var remainder = dividend
    while (remainder.length >= divider.length) {
        if (remainder[0] == '1') {
            // XOR işlemi (bölme işlemi)
            remainder = xor(remainder, divider)
        }
        remainder = remainder.substring(1) // Sol tarafı çıkarıyoruz
    }

    // Kalan değer 0 ise veri doğru, değilse hata vardır.
    return if (remainder == "0") {
        "Veri doğrulandı (CRC)"
    } else {
        "Veri hatalı (CRC)"
    }
}

fun xor(a: String, b: String): String {
    val result = StringBuilder()
    for (i in a.indices) {
        result.append(if (a[i] == b[i]) '0' else '1')
    }
    return result.toString()
}

fun decodeChecksum(binaryData: String): String {
    val bits = binaryData.split(" ")
    var checksum = 0

    // Binary verileri toplayarak checksum hesaplıyoruz.
    for (bit in bits) {
        checksum += bit.toInt()
    }

    // Eğer checksum değeri 0'a bölünebiliyorsa, veri doğrulanır.
    return if (checksum % 256 == 0) {
        "Veri doğrulandı (Checksum)"
    } else {
        "Veri hatalı (Checksum)"
    }
}

fun decodeParityCheck(binaryData: String): String {
    // Parity bit kontrolü yapıyoruz.
    val bits = binaryData.split(" ") // Binary verisi boşluklarla ayrılmış.
    var countOnes = 0

    // Tüm bitleri sayarak '1'lerin sayısını buluyoruz.
    for (bit in bits) {
        if (bit == "1") countOnes++
    }

    // Eğer '1'lerin sayısı çiftse, parity bit'i doğru demektir.
    return if (countOnes % 2 == 0) {
        "Veri doğrulandı (Çift Parity)"
    } else {
        "Veri hatalı (Tek Parity)"
    }
}
