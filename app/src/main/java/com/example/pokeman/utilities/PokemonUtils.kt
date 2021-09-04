package com.example.pokeman.utilities

import com.example.pokeman.data.Pokemon

fun isDualType(pokemon: Pokemon): Boolean {
    if (pokemon.type2 != "") {
        return true
    }
    return false
}

fun getIdFromUrl(url: String): String {
    val url_parts = url.split("/").toTypedArray()
    return url_parts[url_parts.size - 2]
}