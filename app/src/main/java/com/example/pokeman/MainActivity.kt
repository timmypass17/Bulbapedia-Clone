package com.example.pokeman

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokeman.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val BASE_URL = "https://pokeapi.co/api/v2/"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var retrofit: Retrofit
    private lateinit var pokemonService: PokemonService
    private lateinit var adapter: PokemonAdapter
    private var pokemons = mutableListOf<Pokemon>()
    private var pokemon_seen = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        adapter = PokemonAdapter(this, pokemons)
        binding.rvPokemons.adapter = adapter
        binding.rvPokemons.layoutManager = LinearLayoutManager(this)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        pokemonService = retrofit.create(PokemonService::class.java)

        queryPokemons()
    }

    private fun queryPokemons() {
        pokemonService.getPokemons().enqueue(object : Callback<PokemonSearchResult> {
            override fun onResponse(call: Call<PokemonSearchResult>, response: Response<PokemonSearchResult>) {
                Log.i(TAG, "onResponse $response")
                val body = response.body()
                if (body == null) {
                    Log.w(TAG, "Did not receive valid response body from Pokemon API")
                    return
                }
                pokemons.addAll(body.pokemons)
                for (pokemon: Pokemon in body.pokemons){
                    getMorePokemonInfo(pokemon)
                }
            }

            override fun onFailure(call: Call<PokemonSearchResult>, t: Throwable) {
                Log.i(TAG, "onFailure $t")
            }
        })
    }
    // If we seen ALL the pokemon, then notify data set changed
    // Reason: This is an asynchronous operation so we should notify data set has changed
    // after we finish seeing our LAST pokemon.
    private fun getMorePokemonInfo(pokemon: Pokemon) {
        val pokemonId = getId(pokemon.url)
        pokemonService.getPokemonById(pokemonId).enqueue(object : Callback<PokemonInfo> {
            override fun onResponse(call: Call<PokemonInfo>, response: Response<PokemonInfo>) {
                Log.i(TAG, "onResponse $response")
                val body = response.body()
                if (body == null) {
                    Log.w(TAG, "Did not receive valid response body from Pokemon API")
                    return
                }
                // Update pokemon with additional pokemon info
                updatePokemonInfo(pokemon, body)
                pokemon_seen += 1
                // Add all pokemons once we finish "updating" our last pokemon, notify adapter
                if (pokemon_seen == 20) {
                    Log.i(TAG, "Notifying adapter changed")
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<PokemonInfo>, t: Throwable) {
                Log.i(TAG, "onFailure $t")
            }

        })
    }

    private fun updatePokemonInfo(pokemon: Pokemon, pokemonInfo: PokemonInfo) {
        pokemon.number = pokemonInfo.position
        pokemon.sprite = pokemonInfo.sprites.versions.generation_viii.icons.sprite
        for (types: PokemonTypes in pokemonInfo.types) {
            // Pokemons only have 2 types
            if (types.slot == 1) {
                pokemon.type1 = types.type.name
            }
            else {
                pokemon.type2 = types.type.name
            }
        }
    }

    private fun getId(url: String): String {
        val urlParts = url.split("/").toTypedArray()
        return urlParts[urlParts.size - 2]
    }
}