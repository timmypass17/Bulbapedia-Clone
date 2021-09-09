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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokeman.adapters.PokemonAdapter
import com.example.pokeman.api.PokemonService
import com.example.pokeman.data.*
import com.example.pokeman.databinding.ActivityMainBinding
import com.example.pokeman.utilities.Generation
import com.example.pokeman.utilities.getIdFromUrl
import com.example.pokeman.utilities.isDualType
import com.example.pokeman.viewmodels.PokemonViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            val fragment = when (menuItem.itemId) {
                R.id.action_pokemons -> PokemonFragment()
                R.id.action_berries -> BerryFragment()
//                R.id.action_tms -> TmFragment()
                else -> PokemonFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit()
            true
        }
        binding.bottomNavigation.selectedItemId = R.id.action_pokemons
    }


}