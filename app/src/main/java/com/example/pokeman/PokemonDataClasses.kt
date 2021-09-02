package com.example.pokeman

import com.google.gson.annotations.SerializedName

data class Pokemon(
    val name: String,
    var id: Int,
    val sprites: PokemonSprite,
    val types: List<PokemonTypes>
) {
    fun isDualType(): Boolean {
        if (types.size > 1) {
            return true
        }
        return false
    }
}

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

data class PokemonTypes(
    val slot: Int,
    val type: PokemonType
)

data class PokemonType(
    val name: String
)
