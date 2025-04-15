package com.example.p2p_error.Functions

fun toBinary(text: String): String {
    return text.map {
        it.code.toString(2).padStart(8, '0')
    }.joinToString(" ")
}