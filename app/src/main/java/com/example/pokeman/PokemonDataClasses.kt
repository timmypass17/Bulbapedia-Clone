package com.example.pokeman

import com.google.gson.annotations.SerializedName

data class PokemonSearchResult(
    @SerializedName("results") val pokemons: List<Pokemon>,
)

data class PokemonInfo(
    val base_experience: Int,
    @SerializedName("id") val position: Int
)

data class Pokemon(
    val name: String,
    val url: String,
    var base_experience: Int
)
