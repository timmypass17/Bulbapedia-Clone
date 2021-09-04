package com.example.pokeman.data

import com.google.gson.annotations.SerializedName

data class PokemonSearchResult(
    val name: String,
    val id: Int,
    val sprites: PokemonSprite,
    val types: List<PokemonTypes>,
    val stats: List<PokemonStats>,
    val abilities: List<PokemonAbilities>
)

data class PokemonSprite(
    val versions: PokemonVersions,
    val other: PokemonOther
)

data class PokemonVersions(
    @SerializedName("generation-vii") val generation_viii: PokemonGeneration
)

class PokemonOther(
    @SerializedName("official-artwork") val official_art: PokemonOfficialArt
)

class PokemonOfficialArt(
    val front_default: String
)

data class PokemonGeneration(
    val icons: PokemonIcon
)

data class PokemonIcon(
    @SerializedName("front_default") val sprite: String
)

data class PokemonTypes(
    val slot: Int,
    val type: PokemonType
)

data class PokemonType(
    val name: String
)