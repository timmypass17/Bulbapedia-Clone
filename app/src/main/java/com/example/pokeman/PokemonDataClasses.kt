package com.example.pokeman

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName

// not collection name
data class PokemonList(
    @PropertyName("pokemons") val pokemons: List<Pokemon>? = null
)

data class PokemonSearchResult(
    val name: String,
    var id: Int,
    val sprites: PokemonSprite,
    val types: List<PokemonTypes>
)

data class Pokemon(
    val name: String = "",
    val id: Int = 0,
    val sprite: String = "",
    val type1: String = "",
    val type2: String = ""
){
    fun isDualType(): Boolean {
        if (type2 != "") {
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
