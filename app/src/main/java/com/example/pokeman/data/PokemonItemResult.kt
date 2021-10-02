package com.example.pokeman.data

// TODO: Add category

data class PokemonItemResult(
    val name: String,
    val cost: Int,
    val effect_entries: List<PokemonItemEffect>,
    val sprites: PokemonItemSprite,
    val flavor_text_entries: List<PokemonItemFlavorText>,
    val category: PokemonItemCategory
)

data class PokemonItemEffect(
    val effect: String
)

data class PokemonItemSprite(
    val default: String
)

data class PokemonItemFlavorText(
    val language: PokemonItemLanguage,
    val text: String
)

data class PokemonItemLanguage(
    val name: String
)

data class PokemonItemCategory(
    val name: String
)