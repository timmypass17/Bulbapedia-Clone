package com.example.pokeman

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

        // Query first 151 pokemons
        for (i in 1..151) {
            queryPokemon(i.toString())
        }
    }

    private fun queryPokemon(id: String) {
        pokemonService.getPokemonById(id).enqueue(object : Callback<Pokemon> {
            override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                Log.i(TAG, "onResponse $response")
                val pokemon = response.body()
                if (pokemon == null) {
                    Log.w(TAG, "Did not receive valid response body from Pokemon API")
                    return
                }
                pokemons.add(pokemon)
                pokemon_seen+=1
                pokemons.sortBy { it.id }
                adapter.notifyDataSetChanged()
                if (pokemon_seen == 151) {
                    updateSpinnerWithNumber()
                    updateSpinnerWithName()
                    updateSpinnerWithType()
                }
            }

            override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                Log.i(TAG, "onFailure $t")
            }
        })
    }

    private fun updateSpinnerWithNumber() {
        val nationalDexList = mutableListOf<String>()
        nationalDexList.add(0, "Show All")
        for (i in 1..151) {
            // nationalDexList.add("#${i.toString().padStart(3, '0')}")
            nationalDexList.add(i.toString())
        }
        // Add number list as data source for the spinner
        binding.spinnerNumber.attachDataSource(nationalDexList)
        binding.spinnerNumber.setOnSpinnerItemSelectedListener { parent, _, position, _ ->
            val selectedId = parent.getItemAtPosition(position) as String
            if (selectedId == "Show All") {
                pokemons.clear()
                for (i in 1..151) {
                    queryPokemon(i.toString())
                }
            } else {
                getPokemonById(selectedId)
            }
        }
    }

    private fun updateSpinnerWithName() {
        val pokemonNameList = mutableListOf<String>()
        pokemonNameList.add(0, "...")
        for (pokemon in pokemons) {
            pokemonNameList.add(pokemon.name)
        }
        pokemonNameList.sort()

        binding.spinnerName.attachDataSource(pokemonNameList)
        binding.spinnerName.setOnSpinnerItemSelectedListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position) as String
            if (selectedName == "...") {
                pokemons.clear()
                for (i in 1..151) {
                    queryPokemon(i.toString())
                }
            } else {
                getPokemonById(selectedName)
            }

        }
    }

    private fun updateSpinnerWithType() {
        val typeList = mutableListOf("normal","fire", "water","grass", "electric", "ice",
            "fighting", "poison", "ground", "flying", "psychic", "bug", "rock", "ghost",
            "dark", "dragon", "steel", "fairy")
        typeList.sort()
        typeList.add(0, "...")
        binding.SpinnerType.attachDataSource(typeList)
        binding.SpinnerType.setOnSpinnerItemSelectedListener { parent, _, position, _ ->
            val selectedType = parent.getItemAtPosition(position) as String
            if (selectedType == "...") {
                pokemons.clear()
                for (i in 1..151) {
                    queryPokemon(i.toString())
                }
            } else {
                getPokemonByType(selectedType)
            }
        }

    }

    // Recall: If you want to filter 2 types, just select another type
    private fun getPokemonByType(type: String) {
        // Get list of pokemon with type x
        val pokemonTypeList = mutableListOf<Pokemon>()
        for (pokemon in pokemons) {
            // Check if first type matches
            if (type == pokemon.types[0].type.name) {
                pokemonTypeList.add(pokemon)
            }
            // Check if second type matches
            if (pokemon.isDualType()) {
                if (type == pokemon.types[1].type.name) {
                    pokemonTypeList.add(pokemon)
                }
            }
        }
        // Clear pokemons list
        pokemons.clear()
        // Add list of pokemon with type x to pokemons list
        pokemons.addAll(pokemonTypeList)
        // notify adapter changed
        adapter.notifyDataSetChanged()
    }

    // Takes in id or name
    private fun getPokemonById(id: String) {
        // Clear pokemons list
        pokemons.clear()
        // Get api call for pokemon by id
        queryPokemon(id)
    }
}