package com.example.pokeman

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

// Define api endpoints here.
interface PokemonService {

    // Takes in either id or name
    @GET("pokemon/{id}")
    fun getPokemonById(@Path("id") id: String) : Call<PokemonSearchResult>
}