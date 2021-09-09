package com.example.pokeman.data

data class PokemonSpecies(
    val url: String
)

data class PokemonSpeciesResult(
    val flavor_text_entries: List<PokemonFlavorText>
)

class PokemonFlavorText (
    val flavor_text: String,
    val language: PokemonFlavorTextLanguage
)

class PokemonFlavorTextLanguage(
    val name: String
)
