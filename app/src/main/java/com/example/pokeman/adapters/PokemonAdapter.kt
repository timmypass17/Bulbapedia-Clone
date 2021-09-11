package com.example.pokeman.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.example.pokeman.PokemonDetailActivity
import com.example.pokeman.R
import com.example.pokeman.data.Pokemon
import com.example.pokeman.utilities.EXTRA_POKEMON
import com.example.pokeman.utilities.isDualType
import com.google.android.material.chip.Chip

class PokemonAdapter(
    val context: Context,
    val pokemons: List<Pokemon>
    ) : RecyclerView.Adapter<PokemonAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "PokemonAdapter"
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
                val intent = Intent(context, PokemonDetailActivity::class.java).apply {
                    putExtra(EXTRA_POKEMON, pokemon)
                }
                context.startActivity(intent)
            }
        }

    }

}
