package com.example.pokeman

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
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
    private var generation = Generation.GEN1 // Default generation init

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

        supportActionBar?.title = generation.generationName
        getPokemonFromFirebase(generation.generationName)

        // For adding to database
        // queryPokemonFromGeneration(Generation.GEN5)
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
    private fun getPokemonFromFirebase(generationName: String) {
        // Retrieve data from firebase
        db.collection("pokemons").document(generationName).get().addOnSuccessListener { document ->
            val pokemonList = document.toObject(PokemonList::class.java)
            // If we do not have generation, do nothing
            if (pokemonList?.pokemons == null) {
                Log.e(TAG, "Invalid pokemon data from Firestore")
                return@addOnSuccessListener
            }
            // Stores data from firebase and put it into our pokemons list and display data
            displayPokemonData(pokemonList.pokemons.toMutableList())
            updateSpinnerData()
            supportActionBar?.title = generation.generationName
            Log.i(TAG, "Succesfully got ${pokemons.size} pokemons from $generationName from Firebase!")
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Exception when retrieving game", exception)
        }
    }

    private fun displayPokemonData(pokemonsList: MutableList<Pokemon>) {
        // Update pokemons data with fresh data from firebase
        pokemons = pokemonsList.toMutableList()
        // Swap adapter dataset with new pokemon data
        adapter = PokemonAdapter(this, pokemons)
        binding.rvPokemons.adapter = adapter
    }

    private fun updateSpinnerData() {
        // Update spinner data
        updateSpinnerWithNumber()
        updateSpinnerWithName()
        updateSpinnerWithType()
    }

    private fun showChooseGenerationDialog() {
        val generationView = LayoutInflater.from(this).inflate(R.layout.dialog_generation, null)
        val radioGroupGen = generationView.findViewById<RadioGroup>(R.id.radioGroup)
        // Precheck radio button
        when (generation.generationName) {
            "generation-i" -> radioGroupGen.check(R.id.rbGen1)
            "generation-ii" -> radioGroupGen.check(R.id.rbGen2)
            "generation-iii" -> radioGroupGen.check(R.id.rbGen3)
            "generation-iv" -> radioGroupGen.check(R.id.rbGen3)
            "generation-v" -> radioGroupGen.check(R.id.rbGen3)
            "generation-vi" -> radioGroupGen.check(R.id.rbGen3)
            "generation-vii" -> radioGroupGen.check(R.id.rbGen3)
            "generation-viii" -> radioGroupGen.check(R.id.rbGen3)
        }
        // Radio generation on click listener
        showAlertDialog("Choose a generation", generationView, View.OnClickListener {
            // Update generation
            generation = when (radioGroupGen.checkedRadioButtonId) {
                R.id.rbGen1 -> Generation.GEN1
                R.id.rbGen2 -> Generation.GEN2
                R.id.rbGen3 -> Generation.GEN3
                R.id.rbGen4 -> Generation.GEN4
                R.id.rbGen5 -> Generation.GEN5
                R.id.rbGen6 -> Generation.GEN6
                R.id.rbGen7 -> Generation.GEN7
                else -> Generation.GEN8
            }
            // Update pokemons from this generation by querying from firebase
            getPokemonFromFirebase(generation.generationName)
        })
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


    private fun updateSpinnerWithNumber() {
        val nationalDexList = mutableListOf<String>()
        nationalDexList.add(0, "...")

        val (start, end) = generation.getStartAndEnd()
        for (i in start..end) {
            // nationalDexList.add("#${i.toString().padStart(3, '0')}")
            nationalDexList.add(i.toString())
        }
        // Add number list as data source for the spinner
        binding.spinnerNumber.attachDataSource(nationalDexList)
        binding.spinnerNumber.setOnSpinnerItemSelectedListener { parent, _, position, _ ->
            val selectedId = parent.getItemAtPosition(position) as String
            if (selectedId == "...") {
                // Swap adapter dataset with original pokemon data
                adapter = PokemonAdapter(this, pokemons)
                binding.rvPokemons.adapter = adapter
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
                // Swap adapter dataset with original pokemon data
                adapter = PokemonAdapter(this, pokemons)
                binding.rvPokemons.adapter = adapter
            } else {
                getPokemonByName(selectedName)
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
                // Swap adapter dataset with original pokemon data
                adapter = PokemonAdapter(this, pokemons)
                binding.rvPokemons.adapter = adapter
            } else {
                getPokemonByType(selectedType)
            }
        }

    }

    private fun getPokemonByName(name: String) {
        val newPokemons = mutableListOf<Pokemon>()
        for (pokemon in pokemons) {
            if (name == pokemon.name) {
                // Get single pokemon from do
                newPokemons.add(pokemon)
            }
        }
        // Swap adapter dataset with new data
        adapter = PokemonAdapter(this, newPokemons)
        binding.rvPokemons.adapter = adapter
    }

    private fun getPokemonByType(type: String) {
        // Get list of pokemon with type x
        val pokemonsWithType = mutableListOf<Pokemon>()
        for (pokemon in pokemons) {
            // Check if first type matches
            if (type == pokemon.type1) {
                pokemonsWithType.add(pokemon)
            }
            // Check if second type matches
            if (pokemon.isDualType()) {
                if (type == pokemon.type2) {
                    pokemonsWithType.add(pokemon)
                }
            }
        }
        // Swap adapter dataset with new data
        adapter = PokemonAdapter(this, pokemonsWithType)
        binding.rvPokemons.adapter = adapter
    }

    // Takes in id or name
    private fun getPokemonById(id: String) {
        for (pokemon in pokemons) {
            if (id == pokemon.id.toString()) {
                // Swap adapter dataset with new data
                adapter = PokemonAdapter(this, listOf(pokemon))
                binding.rvPokemons.adapter = adapter
                break
            }
        }
    }


    /** Not for client to use **/
    // Use for querying data use api call. Not firebase
    private fun queryPokemonFromGeneration(generationToQuery: Generation) {
        generation = generationToQuery
        val (start, end) = generationToQuery.getStartAndEnd()
        for (i in start..end) {
            queryPokemon(i.toString())
        }
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
                val pokemon = if (pokemonData.types.size == 2) {
                    Pokemon(pokemonData.name, pokemonData.id, pokemonData.sprites.versions.generation_viii.icons.sprite, pokemonData.types[0].type.name, pokemonData.types[1].type.name)
                } else {
                    Pokemon(pokemonData.name, pokemonData.id, pokemonData.sprites.versions.generation_viii.icons.sprite, pokemonData.types[0].type.name, "")
                }
                pokemons.add(pokemon)
                pokemon_seen += 1
                pokemons.sortBy { it.id }
                adapter.notifyDataSetChanged()

                // Once we queried everything, save it into firebase
                if (pokemon_seen == generation.getTotalPokemon()) {
                    saveDataToFirebase(generation.generationName, pokemons)
                }
            }

            override fun onFailure(call: Call<PokemonSearchResult>, t: Throwable) {
                Log.i(TAG, "onFailure $t")
            }
        })
    }

    // We need a list of pokemon data before hand to use this method
    private fun saveDataToFirebase(generationName: String, pokemons: MutableList<Pokemon>) {
        // Check we already have data from that generation
        db.collection("pokemons").document(generationName).get().addOnSuccessListener { document ->
            // If we have data already, do nothing
            if (document != null && document.data != null) {
                Log.i(TAG, "We already added data in Firestore for: $generationName")
                return@addOnSuccessListener
            }
            // Add pokemon documents to firebase
            db.collection("pokemons").document(generationName)
                .set(mapOf("pokemons" to pokemons))
                .addOnCompleteListener { pokemonCreationTask ->
                    if (!pokemonCreationTask.isSuccessful) {
                        Log.e(TAG, "Exception with game creation", pokemonCreationTask.exception)
                        return@addOnCompleteListener
                    }
                    Log.i(TAG, "Successfully added pokemons ${pokemons.size}")
                }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Encountered error while getting document: $generationName", exception)
        }
    }
}