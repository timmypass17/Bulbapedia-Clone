package com.example.pokeman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val pokemonService = retrofit.create(PokemonService::class.java)
        pokemonService.getPokemons().enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                Log.i(TAG, "onResponse $response")
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.i(TAG, "onFailure $t")

            }

        })

    }
}