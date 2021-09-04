package com.example.pokeman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem

class PokemonDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get pokemon from parcel
        val pokemon = intent.getParcelableExtra<Pokemon>(EXTRA_POKEMON)
        supportActionBar?.title = pokemon?.name
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}