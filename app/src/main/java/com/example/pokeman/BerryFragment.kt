package com.example.pokeman

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pokeman.api.PokemonService
import com.example.pokeman.data.PokemonItemResult
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

    private lateinit var retrofit: Retrofit
    private lateinit var pokemonService: PokemonService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_berry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        pokemonService = retrofit.create(PokemonService::class.java)
        queryPokemonItem("1");
    }

    private fun queryPokemonItem(id: String) {
        pokemonService.getPokemonItem(id).enqueue(object : Callback<PokemonItemResult> {
            override fun onResponse(call: Call<PokemonItemResult>, response: Response<PokemonItemResult>) {
                Log.i(TAG, "onResponse $response")
                val itemData = response.body()
                if (itemData == null) {
                    Log.w(TAG, "Did not receive valid response body from Pokemon API")
                    return
                }
            }

            override fun onFailure(call: Call<PokemonItemResult>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}