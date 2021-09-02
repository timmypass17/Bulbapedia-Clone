package com.example.pokeman

import com.google.gson.annotations.SerializedName

data class PokemonSearchResult(
    @SerializedName("results") val pokemons: List<Pokemon>,
)

// Some are var because we need to reassign it after doing the second api call
data class Pokemon(
    var number: Int,
    val name: String,
    val url: String,
    var sprite: String,
    var type1: String,
    var type2: String
)

data class PokemonInfo(
    @SerializedName("id") val position: Int,
    val sprites: PokemonSprite,
    val types: List<PokemonTypes>
)

data class PokemonTypes(
    val slot: Int,
    val type: PokemonType
)

data class PokemonType(
    val name: String
)


data class PokemonSprite(
    val versions: PokemonVersions
)

data class PokemonVersions(
    @SerializedName("generation-vii") val generation_viii: PokemonGeneration
)

data class PokemonGeneration(
    val icons: PokemonIcon
)

data class PokemonIcon(
    @SerializedName("front_default") val sprite: String
)
