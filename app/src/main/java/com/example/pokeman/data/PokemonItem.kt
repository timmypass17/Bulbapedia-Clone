package com.example.pokeman.data

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PokemonItem(
    val name: String = "",
    val cost: Int = 0,
    val effect: String = "",
    val sprite: String = "",
    val flavor_text: String = "",
    val category: String = ""
) : Parcelable

// property name must be same on firebase (3rd col)
data class BerryList(
    @PropertyName("berries") val berries: List<PokemonItem>? = null
)