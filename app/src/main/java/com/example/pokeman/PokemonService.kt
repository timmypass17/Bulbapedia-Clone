package com.example.pokeman

import android.database.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

// Define api endpoints here.
interface PokemonService {

    @GET("pokemon")
    fun getPokemons() : Call<PokemonSearchResult>
    @GET("pokemon/{id}")
    fun getPokemonById(@Path("id") id: String) : Call<PokemonInfo>
}