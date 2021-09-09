package com.example.pokeman.utilities

import com.example.pokeman.R

// Color Utility
fun getTextColor(type: String): Int {
    return when (type) {
        "normal" -> R.style.normal
        "fire" -> R.style.fire
        "water" -> R.style.water
        "grass" -> R.style.grass
        "electric" -> R.style.electric
        "ice" -> R.style.ice
        "fighting" -> R.style.fighting
        "poison" -> R.style.poison
        "ground" -> R.style.ground
        "flying" -> R.style.flying
        "psychic" -> R.style.psychic
        "bug" -> R.style.bug
        "rock" -> R.style.rock
        "ghost" -> R.style.ghost
        "dark" -> R.style.dark
        "dragon" -> R.style.dragon
        "steel" -> R.style.steel
        "fairy" -> R.style.fairy
        else -> R.style.grass
    }
}

fun getStrokeColor(type: String): Int {
    return when (type) {
        "normal" -> R.color.normal
        "fire" -> R.color.fire
        "water" -> R.color.water
        "grass" -> R.color.grass
        "electric" -> R.color.electric
        "ice" -> R.color.ice
        "fighting" -> R.color.fighting
        "poison" -> R.color.poison
        "ground" -> R.color.ground
        "flying" -> R.color.flying
        "psychic" -> R.color.psychic
        "bug" -> R.color.bug
        "rock" -> R.color.rock
        "ghost" -> R.color.ghost
        "dark" -> R.color.dark
        "dragon" -> R.color.dragon
        "steel" -> R.color.steel
        "fairy" -> R.color.fairy
        else -> R.color.black
    }
}