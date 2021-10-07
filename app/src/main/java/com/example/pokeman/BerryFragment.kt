package com.example.pokeman

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pokeman.adapters.BerryAdapter
import com.example.pokeman.api.PokemonService
import com.example.pokeman.data.BerryList
import com.example.pokeman.data.PokemonItem
import com.example.pokeman.data.PokemonItemResult
import com.example.pokeman.databinding.FragmentBerryBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BerryFragment : Fragment() {

    companion object {
        private const val TAG = "BerryFragment"
        private const val BASE_URL = "https://pokeapi.co/api/v2/"
    }

    private lateinit var binding: FragmentBerryBinding
    private lateinit var adapter: BerryAdapter
    private var berries = mutableListOf<PokemonItem>()
    private var originalBerries = mutableListOf<PokemonItem>()
    private var berryCount = 0

    private lateinit var retrofit: Retrofit
    private lateinit var pokemonService: PokemonService
    private val db = Firebase.firestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentBerryBinding.inflate(layoutInflater)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up adapter
        adapter = BerryAdapter(requireContext(), berries)
        binding.rvBerries.adapter = adapter
        binding.rvBerries.layoutManager = GridLayoutManager(requireContext(),5)

        // Set up retrofit
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        pokemonService = retrofit.create(PokemonService::class.java)

        // Get pokemon items from firebase
        getBerriesFromFirebase()

//        for (i in 1..954) {
//            queryPokemonItem(i.toString());
//        }

        binding.svBerry.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // clears focus
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.i(TAG, "Text changed: $newText")

                // if query is empty, show all berries
                if (newText.toString().isEmpty()) {
                    berries.clear()
                    berries.addAll(originalBerries)
                    adapter.notifyDataSetChanged()
                    // Update total text
                    val totalText = getString(R.string.total_items)
                    binding.tvBerryTotal.text = String.format(totalText, originalBerries.size)
                    return true
                }

                // else, create temp berry list to update adapter dataset
                val berriesSearch = mutableListOf<PokemonItem>()
                for (berry in originalBerries){
                    // get berries that match search query
                    if (newText.toString() in berry.name || newText.toString() in berry.category) {
                        berriesSearch.add(berry)
                    }
                }
                // TODO: Add covid tracker spinning number effect
                // Update total text
                val totalText = getString(R.string.total_items)
                binding.tvBerryTotal.text = String.format(totalText, berriesSearch.size)

                // update adapter dataset
                berries.clear()
                berries.addAll(berriesSearch)
                adapter.notifyDataSetChanged()
                return true
            }

        })
    }

    private fun getBerriesFromFirebase() {
        db.collection("pokemon_items").document("berries_doc").get().addOnSuccessListener { document ->
            val berryData = document.toObject(BerryList::class.java)
            // If we did not get valid data back, do nothing
            if (berryData?.berries == null) {
                Log.e(TAG, "Invalid berry data")
                return@addOnSuccessListener
            }
            Log.i(TAG, "Getting berries from fb")

            // intialize berry count
            val totalText = getString(R.string.total_items)
            binding.tvBerryTotal.text = String.format(totalText, berryData.berries.size)

            // Add data to adapter
            berries.addAll(berryData.berries.toMutableList())
            originalBerries = berryData.berries.toMutableList()
            adapter.notifyDataSetChanged()
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Exception when retrieving berries")
        }
    }

    private fun queryPokemonItem(id: String) {
        pokemonService.getPokemonItem(id).enqueue(object : Callback<PokemonItemResult> {
            override fun onResponse(call: Call<PokemonItemResult>, response: Response<PokemonItemResult>) {
                Log.i(TAG, "onResponse $response")
                val berryData = response.body()
                if (berryData == null) {
                    Log.w(TAG, "Did not receive valid response body from Pokemon API")
                    berryCount += 1
                    return
                }
                //TODO: 1. fix awkward extra space (should add newline)
                //      2. some needs a space

                // Reformat text (remove awkward newline)
                val effectList = mutableListOf<Char>()
                for (c in berryData.effect_entries[0].effect) {
                    if (c != '\n'){
                        effectList.add(c)
                    }
                }
                val effectText = effectList.joinToString("")

                var flavorText = ""
                // Get recent english text
                for (text in berryData.flavor_text_entries.reversed()) {
                    if (text.language.name == "en") {
                        flavorText = text.text
                        break
                    }
                }

                val flavorList = mutableListOf<Char>()
                for (c in flavorText) {
                    if (c != '\n'){
                        flavorList.add(c)
                    }
                }
                flavorText = flavorList.joinToString("")

                val berry = PokemonItem(
                    name = berryData.name,
                    cost = berryData.cost,
                    effect = effectText,
                    sprite = berryData.sprites.default,
                    flavor_text = flavorText,
                    category = berryData.category.name
                )
                berries.add(berry)
                berryCount += 1
                // Once we seen all the queries, save it to firebase
                if (berryCount == 954) {
                    Log.i(TAG, "Seen 954 berries, saving to firebase")
                    saveBerriesToFirebase(berries)
                }
            }

            override fun onFailure(call: Call<PokemonItemResult>, t: Throwable) {
                Log.e(TAG, "onFailure $t")
            }
        })
    }

    private fun saveBerriesToFirebase(berries: MutableList<PokemonItem>) {
        // Get berries from firebase
        db.collection("pokemon_items").document("berries_doc").get().addOnSuccessListener { document ->
            // If we already have berries in database
            if (document != null && document.data != null) {
                return@addOnSuccessListener
            }
            // Else, add to firebase
            db.collection("pokemon_items")
                .document("berries_doc")
                .set(mapOf("berries" to berries))
                .addOnCompleteListener { berryTask ->
                    if (!berryTask.isSuccessful) {
                        return@addOnCompleteListener
                    }
                    Log.i(TAG, "Successfully added pokemons ${berries.size}")
                }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Encountered error while getting document: berries", exception)
        }
    }
}