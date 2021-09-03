package com.example.pokeman

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokeman.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
    private var generation = "generation-i"

    private val db = Firebase.firestore

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
        // for (i in 1..151) {
        // queryPokemon(i.toString())
        // }

        // Initalize query
        getPokemonFromFirebase(generation)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_generation -> {
                showChooseGenerationDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showChooseGenerationDialog() {
        val generationView = LayoutInflater.from(this).inflate(R.layout.dialog_generation, null)
        val radioGroupGen = generationView.findViewById<RadioGroup>(R.id.radioGroup)
        // Precheck radio button
        when (generation) {
            "generation-i" -> radioGroupGen.check(R.id.rbGen1)
            "generation-ii" -> radioGroupGen.check(R.id.rbGen2)
            "generation-iii" -> radioGroupGen.check(R.id.rbGen3)
        }
        showAlertDialog("Choose generation", generationView, View.OnClickListener {
            // Set a new value for the generation
            generation = when (radioGroupGen.checkedRadioButtonId) {
                R.id.rbGen1 -> "generation-i"
                R.id.rbGen2 -> "generation-ii"
                else -> "generation-iii"
            }
            // Saves pokemons into firebase, if exist do nothing
            saveDataToFirebase(generation, pokemons)
            // Update pokemons from this generation by querying from firebase
            getPokemonFromFirebase(generation)
        })
    }

    private fun getPokemonFromFirebase(generation: String) {
        // Retrieve data from firebase
        db.collection("pokemons").document(generation).get().addOnSuccessListener { document ->
            val pokemonList = document.toObject(PokemonList::class.java)
            if (pokemonList?.pokemons == null) {
                Log.e(TAG, "Invalid custom game data from Firestore")
                return@addOnSuccessListener
            }
            updatePokemonData(pokemonList.pokemons.toMutableList())
            // Make spinner query from firebase, not api
            updateSpinnerWithNumber()
            updateSpinnerWithName()
            updateSpinnerWithType()
            Log.i(TAG, "Succesfully got ${pokemons.size} pokemons from $generation from Firebase!")
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Exception when retrieving game", exception)
        }
    }

    private fun updatePokemonData(pokemonList: MutableList<Pokemon>) {
        // Update pokemons
        pokemons.clear()
        Log.i(TAG, "Clearing pokemon data. Pokemon Size: ${pokemons.size}")
        // Copy over pokemons
        pokemons.addAll(pokemonList.toMutableList())
        Log.i(TAG, "Updating pokemon data ${pokemons}. Pokemon Size: ${pokemons.size}")
        adapter.notifyDataSetChanged()
    }

    private fun saveDataToFirebase(generation: String, pokemons: MutableList<Pokemon>) {
        var hasDataAlready = false
        // Check we already have data from that generation
        db.collection("pokemons").document(generation).get().addOnSuccessListener { document ->
            if (document != null && document.data != null) {
                Log.i(TAG, "We already added data in Firestore for: $generation")
                hasDataAlready = true
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Encountered error while getting document: $generation", exception)
        }.addOnCompleteListener {
            // If we have data already, do nothing
            if (hasDataAlready) {
                return@addOnCompleteListener
            }
            var firebaseFriendlyPokemon: Pokemon
            val pokemonData = mutableListOf<Pokemon>()
            for (pokemon in pokemons) {
                firebaseFriendlyPokemon = if (pokemon.isDualType()) {
                    Pokemon(pokemon.name, pokemon.id, pokemon.sprite, pokemon.type1, pokemon.type2)
                } else {
                    Pokemon(pokemon.name, pokemon.id, pokemon.sprite, pokemon.type1, "")
                }
                pokemonData.add(firebaseFriendlyPokemon)
            }

            // Add pokemon documents to firebase
            db.collection("pokemons")
                .document(generation)
                .set(mapOf("pokemons" to pokemonData))
                .addOnCompleteListener { pokemonCreationTask ->
                    if (!pokemonCreationTask.isSuccessful) {
                        Log.e(TAG, "Exception with game creation", pokemonCreationTask.exception)
                        Toast.makeText(this, "failed game creation", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }
                    Log.i(TAG, "Successfully added pokemons ${pokemonData.size}")
                }
        }
    }


    private fun showAlertDialog(title: String, view: View?, positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK") { _, _ ->
                positiveClickListener.onClick(null)
            }.show()
    }

    private fun queryPokemon(id: String) {
        pokemonService.getPokemonById(id).enqueue(object : Callback<PokemonSearchResult> {
            override fun onResponse(call: Call<PokemonSearchResult>, response: Response<PokemonSearchResult>) {
                Log.i(TAG, "onResponse $response")
                val pokemonData = response.body()
                if (pokemonData == null) {
                    Log.w(TAG, "Did not receive valid response body from Pokemon API")
                    return
                }
                // Convert PokemonSearchResult into Pokemon object
                val pokemon: Pokemon
                if (pokemonData.types.size == 2) {
                    pokemon = Pokemon(pokemonData.name, pokemonData.id, pokemonData.sprites.versions.generation_viii.icons.sprite, pokemonData.types[0].type.name, pokemonData.types[1].type.name)
                } else {
                    pokemon = Pokemon(pokemonData.name, pokemonData.id, pokemonData.sprites.versions.generation_viii.icons.sprite, pokemonData.types[0].type.name, "")
                }

                pokemons.add(pokemon)
                pokemon_seen+=1
                pokemons.sortBy { it.id }
                adapter.notifyDataSetChanged()
                if (pokemon_seen == 151) {
//                    saveDataToFirebase()
                    updateSpinnerWithNumber()
                    updateSpinnerWithName()
                    updateSpinnerWithType()
                }
            }

            override fun onFailure(call: Call<PokemonSearchResult>, t: Throwable) {
                Log.i(TAG, "onFailure $t")
            }
        })
    }

    private fun updateSpinnerWithNumber() {
        val nationalDexList = mutableListOf<String>()
        nationalDexList.add(0, "...")
        for (i in 1..151) {
            // nationalDexList.add("#${i.toString().padStart(3, '0')}")
            nationalDexList.add(i.toString())
        }
        // Add number list as data source for the spinner
        binding.spinnerNumber.attachDataSource(nationalDexList)
        binding.spinnerNumber.setOnSpinnerItemSelectedListener { parent, _, position, _ ->
            val selectedId = parent.getItemAtPosition(position) as String
            if (selectedId == "...") {
                getPokemonFromFirebase(generation)
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
                getPokemonFromFirebase(generation)
            } else {
                getPokemonByName(selectedName)
            }

        }
    }

    private fun getPokemonByName(name: String) {
        val pokemonWithName: Pokemon
        for (pokemon in pokemons) {
            if (name == pokemon.name) {
                pokemonWithName = pokemon
                // Clear pokemons list
                pokemons.clear()
                // Get single pokemon from do
                pokemons.add(pokemonWithName)
                adapter.notifyDataSetChanged()
                break
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
                getPokemonFromFirebase(generation)
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
            if (type == pokemon.type1) {
                pokemonTypeList.add(pokemon)
            }
            // Check if second type matches
            if (pokemon.isDualType()) {
                if (type == pokemon.type2) {
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
        val pokemonWithId: Pokemon
        for (pokemon in pokemons) {
            if (id == pokemon.id.toString()) {
                pokemonWithId = pokemon
                // Clear pokemons list
                pokemons.clear()
                // Get single pokemon from do
                pokemons.add(pokemonWithId)
                adapter.notifyDataSetChanged()
                break
            }
        }
    }
}