package com.example.pokeman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
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
        pokemonService.getPokemons().enqueue(object : Callback<PokemonSearchResult> {
            override fun onResponse(call: Call<PokemonSearchResult>, response: Response<PokemonSearchResult>) {
                Log.i(TAG, "onResponse $response")
                val body = response.body()
                if (body == null) {
                    Log.w(TAG, "Did not receive valid response body from Pokemon API")
                    return
                }
                pokemons.addAll(body.pokemons)
                getMorePokemonInfo()
                // adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<PokemonSearchResult>, t: Throwable) {
                Log.i(TAG, "onFailure $t")
            }
        })
    }

    private fun getMorePokemonInfo() {
        for (pokemon: Pokemon in pokemons) {
            val pokemonId = getId(pokemon.url)
            pokemonService.getPokemonById(pokemonId).enqueue(object : Callback<PokemonInfo> {
                override fun onResponse(call: Call<PokemonInfo>, response: Response<PokemonInfo>) {
                    val body = response.body()
                    if (body == null) {
                        Log.w(TAG, "Did not receive valid response body from Pokemon API")
                        return
                    }
                    updatePokemonInfo(body)
                    pokemon_seen += 1
                    // If we seen ALL the pokemon, then notify data set changed
                    // Reason: This is an asynchronous operation so we should notify data set has changed
                    // after we seen our LAST pokemon.
                    if (pokemon_seen == pokemons.size) {
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<PokemonInfo>, t: Throwable) {
                    Log.i(TAG, "onFailure $t")
                }

            })
        }
    }

    private fun updatePokemonInfo(pokemonInfo: PokemonInfo) {
        // Recall: Bulbasaur's id is 1
        pokemons[pokemonInfo.position - 1].base_experience = pokemonInfo.base_experience
    }

    private fun getId(url: String): String {
        val urlParts = url.split("/").toTypedArray()
        Log.i(TAG, "Pokemon Id: ${urlParts[urlParts.size - 2]}")
        return urlParts[urlParts.size - 2]
    }
}