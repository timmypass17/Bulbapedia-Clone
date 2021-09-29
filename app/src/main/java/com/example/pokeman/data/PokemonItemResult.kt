package com.example.pokeman.data

data class PokemonItemResult(
    val name: String,
    val cost: Int,
    val effect_entries: List<PokemonItemEffect>,
    val sprites: PokemonItemSprite
)

data class PokemonItemEffect(
    val effect: String,
    val short_effect: String
)

data class PokemonItemSprite(
    val default: String
)