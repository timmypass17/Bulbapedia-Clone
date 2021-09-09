package com.example.pokeman.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.example.pokeman.R
import com.example.pokeman.data.Pokemon
import com.example.pokeman.utilities.getStrokeColor
import com.example.pokeman.utilities.getTextColor
import com.example.pokeman.utilities.isDualType
import com.google.android.material.chip.Chip


class PokemonAdapter(
    val context: Context,
    val pokemons: List<Pokemon>
    ) : RecyclerView.Adapter<PokemonAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "PokemonAdapter"
        private lateinit var fragmentManager: FragmentManager

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_pokemon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pokemon = pokemons[position]
        holder.bind(pokemon)
    }

    override fun getItemCount() = pokemons.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cardView = itemView.findViewById<CardView>(R.id.cardView)
        private val tvNumber = itemView.findViewById<TextView>(R.id.tvNumber)
        private val tvName = itemView.findViewById<TextView>(R.id.tvName)
        private val ivSprite = itemView.findViewById<ImageView>(R.id.ivSprite)
        private val chipType1 = itemView.findViewById<Chip>(R.id.chipType1)
        private val chipType2 = itemView.findViewById<Chip>(R.id.chipType2)

        fun bind(pokemon: Pokemon) {
            tvNumber.text = "#${pokemon.id.toString().padStart(3, '0')}"
            tvName.text = pokemon.name
            // Might remove transition
            Glide.with(context).load(pokemon.icon).transition(withCrossFade()).into(ivSprite)
            chipType1.text = pokemon.type1
            if (isDualType(pokemon)) {
                chipType2.text = pokemon.type2
                chipType2.visibility = View.VISIBLE
            } else {
                chipType2.visibility = View.GONE
            }
            // Navigate to details page
            cardView.setOnClickListener {
                setupPokemonDetailDialog(pokemon)
                Log.i("PokemonAdapter", "Showing detail dialog")
            }
            setupColors(pokemon)
        }

        private fun setupPokemonDetailDialog(pokemon: Pokemon){
            val pokemonDetailView = LayoutInflater.from(context).inflate(R.layout.dialog_pokemon_details, null)

            val ivPokemon = pokemonDetailView.findViewById<ImageView>(R.id.ivPokemon)
            val tvName = pokemonDetailView.findViewById<TextView>(R.id.tvName)
            val chipDetailType1 = pokemonDetailView.findViewById<Chip>(R.id.chipDetailType1)
            val chipDetailType2 = pokemonDetailView.findViewById<Chip>(R.id.chipDetailType2)
            val tvDescription = pokemonDetailView.findViewById<TextView>(R.id.tvDescription)

            // TODO: Click on image, change sprite to animated one
            Glide.with(context).load(pokemon.sprite).into(ivPokemon)
            tvName.text = pokemon.name
            tvDescription.text = pokemon.flavor_text
            chipDetailType1.text = pokemon.type1
            if (isDualType(pokemon)) {
                chipDetailType2.text = pokemon.type2
                chipDetailType2.visibility = View.VISIBLE
            } else {
                chipDetailType2.visibility = View.GONE
            }
            val textColor1 = getTextColor(pokemon.type1)
            chipDetailType1.setTextAppearanceResource(textColor1)
            chipDetailType1.setChipStrokeColorResource(getStrokeColor(pokemon.type1))
            if (isDualType(pokemon)) {
                val textColor2 = getTextColor(pokemon.type2)
                chipDetailType2.setTextAppearance(textColor2)
                chipDetailType2.setChipStrokeColorResource(getStrokeColor(pokemon.type2))
            }

            val tvHealth = pokemonDetailView.findViewById<TextView>(R.id.tvHealth)
            val tvAttack = pokemonDetailView.findViewById<TextView>(R.id.tvAttack)
            val tvDefense = pokemonDetailView.findViewById<TextView>(R.id.tvDefense)
            val tvSAttack = pokemonDetailView.findViewById<TextView>(R.id.tvSAttack)
            val tvSDefense = pokemonDetailView.findViewById<TextView>(R.id.tvSDefense)
            val tvSpeed = pokemonDetailView.findViewById<TextView>(R.id.tvSpeed)

            tvHealth.text = pokemon.stats["hp"].toString()
            tvAttack.text = pokemon.stats["attack"].toString()
            tvDefense.text = pokemon.stats["defense"].toString()
            tvSAttack.text = pokemon.stats["special-attack"].toString()
            tvSDefense.text = pokemon.stats["special-defense"].toString()
            tvSpeed.text = pokemon.stats["speed"].toString()

            val pgbHealth = pokemonDetailView.findViewById<ProgressBar>(R.id.pgbHealth)
            val pgbAttack = pokemonDetailView.findViewById<ProgressBar>(R.id.pgbAttack)
            val pgbDefense = pokemonDetailView.findViewById<ProgressBar>(R.id.pgbDefense)
            val pgbSpecialAttack = pokemonDetailView.findViewById<ProgressBar>(R.id.pgbSpecialAttack)
            val pgbSpecialDefense = pokemonDetailView.findViewById<ProgressBar>(R.id.pgbSpecialDefense)
            val pgbSpeed = pokemonDetailView.findViewById<ProgressBar>(R.id.pgbSpeed)

            pokemon.stats["hp"]?.let { pgbHealth.progress = it }
            pokemon.stats["attack"]?.let { pgbAttack.progress = it }
            pokemon.stats["defense"]?.let { pgbDefense.progress = it }
            pokemon.stats["special-attack"]?.let { pgbSpecialAttack.progress = it }
            pokemon.stats["special-defense"]?.let { pgbSpecialDefense.progress = it }
            pokemon.stats["speed"]?.let { pgbSpeed.progress = it }

            val colorInt = context.resources.getColor(getStrokeColor(pokemon.type1))
            pgbHealth.progressTintList = ColorStateList.valueOf(colorInt)
            pgbAttack.progressTintList = ColorStateList.valueOf(colorInt)
            pgbDefense.progressTintList = ColorStateList.valueOf(colorInt)
            pgbSpecialAttack.progressTintList = ColorStateList.valueOf(colorInt)
            pgbSpecialDefense.progressTintList = ColorStateList.valueOf(colorInt)
            pgbSpeed.progressTintList = ColorStateList.valueOf(colorInt)

            val btnStats = pokemonDetailView.findViewById<ImageView>(R.id.btnStats)
            // Fragments here
            // TODO: Use viewmodel to share data
            btnStats.setOnClickListener {
            }

            showAlertDialog(pokemonDetailView)
        }

        private fun showAlertDialog(view: View?) {
            AlertDialog.Builder(context)
                .setView(view)
                .setNegativeButton("Back", null)
                .show()
        }

        private fun setupColors(pokemon: Pokemon) {
            val textColor1 = getTextColor(pokemon.type1)
            chipType1.setTextAppearanceResource(textColor1)
            chipType1.setChipStrokeColorResource(getStrokeColor(pokemon.type1))
            if (isDualType(pokemon)) {
                val textColor2 = getTextColor(pokemon.type2)
                chipType2.setTextAppearance(textColor2)
                chipType2.setChipStrokeColorResource(getStrokeColor(pokemon.type2))
            }
        }
    }

}
