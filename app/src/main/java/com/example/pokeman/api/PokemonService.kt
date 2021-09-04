package com.example.pokeman.api

import com.example.pokeman.data.PokemonAbilityResult
import com.example.pokeman.data.PokemonSearchResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

// Define api endpoints here.
interface PokemonService {

    // Takes in either id or name
    @GET("pokemon/{id}")
    fun getPokemonById(@Path("id") id: String) : Call<PokemonSearchResult>

    @GET("ability/{id}")
    fun getPokemonAbility(@Path("id") id: String) : Call<PokemonAbilityResult>
}