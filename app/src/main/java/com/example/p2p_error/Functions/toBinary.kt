package com.example.p2p_error.Functions

import androidx.compose.runtime.MutableState

fun toBinary(text: String, binaryVersion : MutableState<String>): String {
    binaryVersion.value = text.map {
        it.code.toString(2).padStart(8, '0')
    }.joinToString(" ")
    return binaryVersion.value
}