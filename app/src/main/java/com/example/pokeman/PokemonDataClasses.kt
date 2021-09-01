package com.example.pokeman

import com.google.gson.annotations.SerializedName

data class PokemonSearchResult(
    @SerializedName("results") val pokemons: List<Pokemon>
)

data class Pokemon(
    val name: String,
    val url: String
)
