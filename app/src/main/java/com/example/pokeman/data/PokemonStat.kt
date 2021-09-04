package com.example.pokeman.data

import com.google.gson.annotations.SerializedName

// {hp, atk, def, spatk,spdef, spd}
class PokemonStats(
    val base_stat: Int,
    @SerializedName("stat") val stat_name: PokemonStat
)

class PokemonStat(
    val name: String
)