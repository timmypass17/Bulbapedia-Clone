package com.example.pokeman.data

data class PokemonSpecies(
    val url: String
)

data class PokemonSpeciesResult(
    val flavor_text_entries: List<PokemonFlavorText>,
    val genera: List<PokemonGenera>
)

data class PokemonFlavorText (
    val flavor_text: String,
    val language: PokemonFlavorTextLanguage
)

data class PokemonFlavorTextLanguage(
    val name: String
)

data class PokemonGenera(
    val genus: String,
    val language: PokemonGeneraLanguage
)

data class PokemonGeneraLanguage(
    val name: String
)
