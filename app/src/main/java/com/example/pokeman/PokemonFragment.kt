package com.example.pokeman

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokeman.adapters.PokemonAdapter
import com.example.pokeman.api.PokemonService
import com.example.pokeman.data.*
import com.example.pokeman.databinding.FragmentPokemonBinding
import com.example.pokeman.utilities.Generation
import com.example.pokeman.utilities.getIdFromUrl
import com.example.pokeman.utilities.isDualType
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//TODO: 1. GEN7 and GEN8 not working. pokemon 808 meltan not working?
class PokemonFragment : Fragment() {

    companion object {
        private const val TAG = "PokemonFragment"
        private const val BASE_URL = "https://pokeapi.co/api/v2/"
    }

    private lateinit var binding: FragmentPokemonBinding
    private lateinit var adapter: PokemonAdapter
    private var pokemons = mutableListOf<Pokemon>()
    private var generation = Generation.GEN1 // Default generation init
    private var pokemon_seen = 0

    private lateinit var retrofit: Retrofit
    private lateinit var pokemonService: PokemonService

    private val db = Firebase.firestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentPokemonBinding.inflate(layoutInflater)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        adapter = PokemonAdapter(requireContext(), pokemons)
        binding.rvPokemons.adapter = adapter
        binding.rvPokemons.layoutManager = LinearLayoutManager(requireContext())

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        pokemonService = retrofit.create(PokemonService::class.java)

        // For adding to database, uncommment bottom
//        queryPokemonFromGeneration(Generation.GEN8)

        getPokemonFromFirebase(Generation.GEN1)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_generation, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.i(TAG, "Overflow menu clicked!")
        when (item.itemId) {
            R.id.mi_generation -> {
                showChooseGenerationDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getPokemonFromFirebase(generation: Generation) {
        // Retrieve data from firebase
        db.collection("pokemons").document(generation.generationName).get().addOnSuccessListener { document ->
            val pokemonList = document.toObject(PokemonList::class.java)
            // If we do not have generation, do nothing
            if (pokemonList?.pokemons == null) {
                Log.e(TAG, "Invalid pokemon data from Firestore")
                return@addOnSuccessListener
            }
            // Stores data from firebase and put it into our pokemons list and display data
            // Update pokemons data with fresh data from firebase
            pokemons = pokemonList.pokemons.toMutableList()
            displayPokemonData(pokemons)
            updateSpinnerData()
//            supportActionBar?.title = generation.generationName
            Log.i(TAG, "Succesfully got ${pokemons.size} pokemons from ${generation.generationName} from Firebase!")
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Exception when retrieving game", exception)
        }
    }

    private fun displayPokemonData(pokemons: MutableList<Pokemon>) {
        // Swap adapter dataset with new pokemon data
        adapter = PokemonAdapter(requireContext(), pokemons)
        binding.rvPokemons.adapter = adapter
    }

    private fun updateSpinnerData() {
        // Update spinner data
        updateSpinnerWithNumber()
        updateSpinnerWithName()
        updateSpinnerWithType()
    }

    private fun showChooseGenerationDialog() {
        Log.i(TAG, "Showing option menus")
        val generationView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_generation, null)
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
            getPokemonFromFirebase(generation)
        })
    }

    private fun showAlertDialog(title: String, view: View?, positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(requireContext())
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
                adapter = PokemonAdapter(requireContext(), pokemons)
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
                adapter = PokemonAdapter(requireContext(), pokemons)
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
//        typeList.sort()
        typeList.add(0, "...")
        binding.SpinnerType.attachDataSource(typeList)
        binding.SpinnerType.setOnSpinnerItemSelectedListener { parent, _, position, _ ->
            val selectedType = parent.getItemAtPosition(position) as String
            if (selectedType == "...") {
                // Swap adapter dataset with original pokemon data
                adapter = PokemonAdapter(requireContext(), pokemons)
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
        adapter = PokemonAdapter(requireContext(), newPokemons)
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
            if (isDualType(pokemon)) {
                if (type == pokemon.type2) {
                    pokemonsWithType.add(pokemon)
                }
            }
        }
        // Swap adapter dataset with new data
        adapter = PokemonAdapter(requireContext(), pokemonsWithType)
        binding.rvPokemons.adapter = adapter
    }

    // Takes in id or name
    private fun getPokemonById(id: String) {
        for (pokemon in pokemons) {
            if (id == pokemon.id.toString()) {
                // Swap adapter dataset with new data
                adapter = PokemonAdapter(requireContext(), listOf(pokemon))
                binding.rvPokemons.adapter = adapter
                break
            }
        }
    }


    /** For querying api pokemon data into firebase **/
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

                // Once we get pokemon data, we make another request with the pokemon data's ability url to get the flavor text,
                // then we create our pokemon object once we have all the components
                val abilityId = getIdFromUrl(pokemonData.abilities[0].ability.url)
                queryPokemonAbility(abilityId, pokemonData)
            }

            override fun onFailure(call: Call<PokemonSearchResult>, t: Throwable) {
                Log.i(TAG, "onFailure $t")
            }
        })
    }



    private fun queryPokemonAbility(id: String, pokemonData: PokemonSearchResult) {
        pokemonService.getPokemonAbility(id).enqueue(object : Callback<PokemonAbilityResult> {
            override fun onResponse(call: Call<PokemonAbilityResult>, response: Response<PokemonAbilityResult>) {
                val pokemonAbilityData = response.body()
                if (pokemonAbilityData == null) {
                    Log.w(TAG, "Did not receive valid response body from Pokemon API")
                    return
                }
                val speciesId = getIdFromUrl(pokemonData.species.url)
                queryPokemonSpecies(speciesId, pokemonData, pokemonAbilityData)
            }

            override fun onFailure(call: Call<PokemonAbilityResult>, t: Throwable) {
                Log.e(TAG, "onFailure querying pokemon ability")
            }

        })
    }

    private fun queryPokemonSpecies(id: String, pokemonData: PokemonSearchResult, pokemonAbilityData: PokemonAbilityResult) {
        pokemonService.getPokemonSpecies(id).enqueue(object : Callback<PokemonSpeciesResult> {
            override fun onResponse(call: Call<PokemonSpeciesResult>, response: Response<PokemonSpeciesResult>) {
                val pokemonSpeciesData = response.body()
                Log.w(TAG, "Pokemon: ${pokemonData.name}")
                if (pokemonSpeciesData == null) {
                    Log.w(TAG, "Did not receive valid response body from Pokemon API")
                    pokemon_seen += 1 // TODO: maybe remove?
                    return
                }
                // Convert PokemonSearchResult into Pokemon object
                // Creating stat map {"stat", base_stat}
                val stat_map = mutableMapOf<String, Int>()
                for (stat in pokemonData.stats) {
                    stat_map[stat.stat_name.name] = stat.base_stat
                }
                // Create ability map {"name": "flavor_text}
                val ability_map = mutableMapOf<String, String>()
                // Loop through ability
                for (pokemonAbility in pokemonData.abilities) {
                    for (abilityDescription in pokemonAbilityData.ability_description) {
                        // We want english text
                        if (abilityDescription.language.language_name == "en") {
                            ability_map[pokemonAbility.ability.name] = abilityDescription.short_effect
                        }
                    }
                }
                // Get flavor text from pokemonSpeciesData
                val flavor_text_list = mutableListOf<Char>('\"')
                var flavor_text = "\""
                // Loop through flavor texts. Recall we loop backwards to get the latest text
                for (text in pokemonSpeciesData.flavor_text_entries.reversed()) {
                    // Get the latest english flavor text
                    if (text.language.name == "en") {
                        flavor_text = text.flavor_text
                        break
                    }
                }
                // Format text
                for (c in flavor_text) {
                    if (c.isLetterOrDigit()) {
                        flavor_text_list.add(c)
                    }
                    // The api gave weird line breaks, so we add space instead
                    if (c.isWhitespace()) {
                        flavor_text_list.add(' ')
                    }
                }
                flavor_text = flavor_text_list.joinToString("")
                flavor_text.replace("POKéMON", "pokemon") // make spelling nicer
                flavor_text += ".\"" // add period at end

                // Get pokemon other name from pokemonSpeciesData
                var other_name = ""
                // Loop through genera names and find english name
                for (genera in pokemonSpeciesData.genera) {
                    if (genera.language.name == "en") {
                        other_name = genera.genus;
                        break
                    }
                }


                if (pokemonData.name == "meltan") {
                    Log.i(TAG, "Name: ${pokemonData.name}")
                    Log.i(TAG, "Genera: $other_name")
                    Log.i(TAG, "Sprite: ${pokemonData.sprites.front_default}")
                    Log.i(TAG, "Icon: ${pokemonData.sprites.versions.generation_viii.icons.icon}")
                    Log.i(TAG, "Type 1: ${pokemonData.types[0].type.name}")
                    if (pokemonData.types.size == 2) {
                        Log.i(TAG, "Type 2: ${pokemonData.types[1].type.name}")
                    }
                    Log.i(TAG, "Stats: $stat_map")
                    Log.i(TAG, "Ability: $ability_map")
                    Log.i(TAG, "Flavor_Text: $flavor_text")
                }

                // TODO: for some reason, i get icon = null for meltan 808??
                // TODO: I fucked up icon somewhere
                val pokemon = if (pokemonData.types.size == 2) {
                    if (pokemonData.sprites.versions.generation_viii.icons.icon != null) {
                        Pokemon(
                            name = pokemonData.name,
                            genera = other_name,
                            id = pokemonData.id,
                            sprite = pokemonData.sprites.front_default,
                            icon = pokemonData.sprites.versions.generation_viii.icons.icon,
                            type1 = pokemonData.types[0].type.name,
                            type2 = pokemonData.types[1].type.name,
                            stats = stat_map,
                            abilities = ability_map,
                            flavor_text = flavor_text)
                    }
                    else {
                        Pokemon(
                            name = pokemonData.name,
                            genera = other_name,
                            id = pokemonData.id,
                            sprite = pokemonData.sprites.front_default,
                            type1 = pokemonData.types[0].type.name,
                            type2 = pokemonData.types[1].type.name,
                            stats = stat_map,
                            abilities = ability_map,
                            flavor_text = flavor_text)
                    }
                } else {
                    if (pokemonData.sprites.versions.generation_viii.icons.icon != null) {
                        Pokemon(
                            name = pokemonData.name,
                            genera = other_name,
                            id = pokemonData.id,
                            sprite = pokemonData.sprites.front_default,
                            icon = pokemonData.sprites.versions.generation_viii.icons.icon,
                            type1 = pokemonData.types[0].type.name,
                            stats = stat_map,
                            abilities = ability_map,
                            flavor_text = flavor_text)
                    } else {
                        Pokemon(
                            name = pokemonData.name,
                            genera = other_name,
                            id = pokemonData.id,
                            sprite = pokemonData.sprites.front_default,
                            type1 = pokemonData.types[0].type.name,
                            stats = stat_map,
                            abilities = ability_map,
                            flavor_text = flavor_text)
                    }
                }
                pokemons.add(pokemon)
                pokemon_seen += 1
                pokemons.sortBy { it.id }
                adapter.notifyDataSetChanged()

                // Once we queried everything, save it into firebase
//                if (pokemon_seen == generation.getTotalPokemon()) {
//                    saveDataToFirebase(generation.generationName, pokemons)
//                }
                if (pokemon_seen == generation.getTotalPokemon()) {
                    Log.i(TAG, "Seen 20 pokemons, saving to firebase")
                    saveDataToFirebase(generation.generationName, pokemons)
                    pokemon_seen = 0
                }
            }

            override fun onFailure(call: Call<PokemonSpeciesResult>, t: Throwable) {
                TODO("Not yet implemented")
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
            db.collection("pokemons")
                .document(generationName)
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