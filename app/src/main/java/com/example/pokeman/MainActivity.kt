package com.example.pokeman

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pokeman.databinding.ActivityMainBinding

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
                R.id.action_items -> BerryFragment()
//                R.id.action_tms -> TmFragment()
                else -> PokemonFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit()
            true
        }
        binding.bottomNavigation.selectedItemId = R.id.action_pokemons
    }


}