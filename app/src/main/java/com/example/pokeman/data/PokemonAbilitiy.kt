package com.example.pokeman.data

import com.google.gson.annotations.SerializedName

class PokemonAbilities(
    val ability: PokemonAbility
)

class PokemonAbility(
    val name: String,
    val url: String
)

// Separate api call
class PokemonAbilityResult(
    @SerializedName("effect_entries") val ability_description: List<PokemonAbilityDescription>
)

class PokemonAbilityDescription(
    val short_effect: String
)
