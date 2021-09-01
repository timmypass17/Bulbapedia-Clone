package com.example.pokeman

import retrofit2.Call
import retrofit2.http.GET

// Define api endpoints here.
interface PokemonService {

    @GET("pokemon")
    fun getPokemons() : Call<Any>
}