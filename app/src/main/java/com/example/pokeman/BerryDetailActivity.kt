package com.example.pokeman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.example.pokeman.data.PokemonItem
import com.example.pokeman.databinding.ActivityBerryDetailBinding
import com.example.pokeman.utilities.EXTRA_BERRY

class BerryDetailActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "BerryDetailActivity"
    }

    private lateinit var binding: ActivityBerryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = ActivityBerryDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val berry = intent.getParcelableExtra<PokemonItem>(EXTRA_BERRY)
        if (berry != null) {
            bind(berry)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bind(berry: PokemonItem) {
        Glide.with(this).load(berry.sprite).into(binding.ivSprite)
        binding.tvName.text = berry.name
        // some items cant be bought, only found
        if (berry.cost == 0) {
            binding.tvCost.text = getString(R.string.cannot_be_purchased)
        } else {
            binding.tvCost.text = "₽${berry.cost}"
        }
        binding.tvFlavorText.text = "\"${berry.flavor_text}\""
        binding.tvCategory.text = berry.category
        binding.tvEffect.text = berry.effect
    }
}