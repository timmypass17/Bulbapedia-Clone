package com.example.pokeman

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class PokemonAdapter(val context: Context, val pokemons: List<Pokemon>) :
    RecyclerView.Adapter<PokemonAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_pokemon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pokemon = pokemons[position]
        Log.i("PokemonAdapter", "Binding ${pokemon.name} Position: $position")
        holder.bind(pokemon)
    }

    override fun getItemCount() = pokemons.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvNumber = itemView.findViewById<TextView>(R.id.tvNumber)
        private val tvName = itemView.findViewById<TextView>(R.id.tvName)
        private val ivSprite = itemView.findViewById<ImageView>(R.id.ivSprite)
        private val chipTypes = itemView.findViewById<ChipGroup>(R.id.chipTypes)
        private val chipType1 = itemView.findViewById<Chip>(R.id.chipType1)
        private var chipType2 = itemView.findViewById<Chip>(R.id.chipType2)

        fun bind(pokemon: Pokemon) {
            tvNumber.text = "#${pokemon.number.toString().padStart(3, '0')}"
            tvName.text = pokemon.name
            Glide.with(context)
                .load(pokemon.sprite)
                .into(ivSprite)
            chipType1.text = pokemon.type1
            if (pokemon.type2 != null) {
                chipType2.text = pokemon.type2
                chipType2.visibility = View.VISIBLE
            } else {
                chipType2.visibility = View.GONE
            }
            setupColors(pokemon)
        }

        private fun setupColors(pokemon: Pokemon) {
            val textColor1 = getTextColor(pokemon.type1)
            chipType1.setTextAppearanceResource(textColor1)
            chipType1.setChipStrokeColorResource(getStrokeColor(pokemon.type1))
            if (pokemon.type2 != null) {
                val textColor2 = getTextColor(pokemon.type2)
                chipType2.setTextAppearance(textColor2)
                chipType2.setChipStrokeColorResource(getStrokeColor(pokemon.type2))
            }
        }
    }

    // Color Utility
    private fun getTextColor(type: String): Int {
        return when (type) {
            "grass" -> R.style.grass
            "poison" -> R.style.poison
            "fire" -> R.style.fire
            "flying" -> R.style.flying
            "water" -> R.style.water
            "bug" -> R.style.bug
            else -> R.style.grass
        }
    }

    private fun getStrokeColor(type: String): Int {
        return when (type) {
            "grass" -> R.color.grass
            "poison" -> R.color.poison
            "fire" -> R.color.fire
            "flying" -> R.color.flying
            "water" -> R.color.water
            "bug" -> R.color.bug
            else -> R.color.grass
        }
    }

}
