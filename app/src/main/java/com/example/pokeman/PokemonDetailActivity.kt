package com.example.pokeman

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.pokeman.adapters.AbilityAdapter
import com.example.pokeman.data.Pokemon
import com.example.pokeman.databinding.ActivityPokemonDetailBinding
import com.example.pokeman.utilities.EXTRA_POKEMON
import com.example.pokeman.utilities.getStrokeColor
import com.example.pokeman.utilities.getTextStyle
import com.example.pokeman.utilities.isDualType

class PokemonDetailActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "PokemonDetailActivity"
    }

    private lateinit var binding: ActivityPokemonDetailBinding
    private lateinit var adapter: AbilityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = ActivityPokemonDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val pokemon = intent.getParcelableExtra<Pokemon>(EXTRA_POKEMON)
        if (pokemon != null) {
            bind(pokemon)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bind(pokemon: Pokemon) {
        // Set up abilities rv
        adapter = AbilityAdapter(this, pokemon.abilities.toList())
        binding.rvAbilities.adapter = adapter
        binding.rvAbilities.layoutManager = LinearLayoutManager(this)

        supportActionBar?.title = pokemon.name

        // TODO: Click on image, change sprite to animated one
        // Set up sprite and palette
        Glide.with(this)
            .asBitmap()
            .load(pokemon.sprite)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    return false;
                }

                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    if (resource != null) {
                        // Get color palette from pokemon sprite
                        Palette.from(resource).generate { palette ->
                            if (palette != null) {
                                setPalette(palette)
                            }
                        }
                    }
                    return false;
                }
            })
            .into(binding.ivPokemon)

        // Set up text views and pokemon types
        binding.tvName.text = pokemon.name
        binding.tvGenera.text = pokemon.genera
        binding.tvFlavorText.text = pokemon.flavor_text
        binding.chipType1.text = pokemon.type1
        if (isDualType(pokemon)) {
            binding.chipType2.text = pokemon.type2
            binding.chipType2.visibility = View.VISIBLE
        } else {
            binding.chipType2.visibility = View.GONE
        }
        val textColor1 = getTextStyle(pokemon.type1)
        binding.chipType1.setTextAppearanceResource(textColor1)
        binding.chipType1.setChipStrokeColorResource(getStrokeColor(pokemon.type1))
        if (isDualType(pokemon) || pokemon.type2 != null) {
            val textColor2 = getTextStyle(pokemon.type2)
            binding.chipType2.setTextAppearance(textColor2)
            binding.chipType2.setChipStrokeColorResource(getStrokeColor(pokemon.type2))
        }

        // Set up stats
        binding.tvHealth.text = pokemon.stats["hp"].toString()
        binding.tvAttack.text = pokemon.stats["attack"].toString()
        binding.tvDefense.text = pokemon.stats["defense"].toString()
        binding.tvSAttack.text = pokemon.stats["special-attack"].toString()
        binding.tvSDefense.text = pokemon.stats["special-defense"].toString()
        binding.tvSpeed.text = pokemon.stats["speed"].toString()

        pokemon.stats["hp"]?.let { binding.pgbHealth.progress = it }
        pokemon.stats["attack"]?.let { binding.pgbAttack.progress = it }
        pokemon.stats["defense"]?.let { binding.pgbDefense.progress = it }
        pokemon.stats["special-attack"]?.let { binding.pgbSpecialAttack.progress = it }
        pokemon.stats["special-defense"]?.let { binding.pgbSpecialDefense.progress = it }
        pokemon.stats["speed"]?.let { binding.pgbSpeed.progress = it }

        val colorInt = resources.getColor(getStrokeColor(pokemon.type1))
        binding.pgbHealth.progressTintList = ColorStateList.valueOf(colorInt)
        binding.pgbAttack.progressTintList = ColorStateList.valueOf(colorInt)
        binding.pgbDefense.progressTintList = ColorStateList.valueOf(colorInt)
        binding.pgbSpecialAttack.progressTintList = ColorStateList.valueOf(colorInt)
        binding.pgbSpecialDefense.progressTintList = ColorStateList.valueOf(colorInt)
        binding.pgbSpeed.progressTintList = ColorStateList.valueOf(colorInt)

    }

    private fun setPalette(palette: Palette) {
        // Get the "vibrant" color swatch based on the bitmap
        val vibrant = palette.vibrantSwatch
        if (vibrant != null) {
            // Change action bar color depending on pokemon's color palette
            window.statusBarColor = vibrant.rgb
            supportActionBar?.setBackgroundDrawable(ColorDrawable(ColorUtils.blendARGB(vibrant.rgb, Color.WHITE, 0.2f)))
        }
    }
}