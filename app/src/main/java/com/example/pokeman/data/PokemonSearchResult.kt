package com.example.pokeman.data

import com.google.gson.annotations.SerializedName

/** pokemon/{id} **/
data class PokemonSearchResult(
    val name: String,
    val id: Int,
    val sprites: PokemonSprite,
    val types: List<PokemonTypes>,
    val stats: List<PokemonStats>,
    val abilities: List<PokemonAbilities>,
    val species: PokemonSpecies,
    val moves: List<PokemonMoves>
)

data class PokemonSprite(
    val front_default: String,
    val versions: PokemonVersions,
    val other: PokemonOther
)

data class PokemonVersions(
    @SerializedName("generation-vii") val generation_viii: PokemonGeneration
)

data class PokemonOther(
    @SerializedName("official-artwork") val official_art: PokemonOfficialArt
)

data class PokemonOfficialArt(
    val front_default: String
)

data class PokemonGeneration(
    val icons: PokemonIcon
)

data class PokemonIcon(
    @SerializedName("front_default") val icon: String
)

data class PokemonTypes(
    val slot: Int,
    val type: PokemonType
)

data class PokemonType(
    val name: String
)

// {hp, atk, def, spatk,spdef, spd}
data class PokemonStats(
    val base_stat: Int,
    @SerializedName("stat") val stat_name: PokemonStat
)

data class PokemonStat(
    val name: String
)

// Moves
data class PokemonMoves(
    val move: PokemonMove
)

data class PokemonMove(
    val url: String
)

















