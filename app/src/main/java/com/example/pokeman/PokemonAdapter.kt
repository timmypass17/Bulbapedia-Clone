package com.example.pokeman

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeman.databinding.ItemPokemonBinding
import org.w3c.dom.Text

class PokemonAdapter(val context: Context, val pokemons: List<Pokemon>) :
    RecyclerView.Adapter<PokemonAdapter.ViewHolder>() {

    private lateinit var binding: ItemPokemonBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemPokemonBinding.inflate(LayoutInflater.from(context))
        val view = binding.root
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pokemon = pokemons[position]
        holder.bind(pokemon)
    }

    override fun getItemCount() = pokemons.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

//        private val tvName = itemView.findViewById<TextView>(R.id.tvName)

        fun bind(pokemon: Pokemon) {
            binding.tvName.text = pokemon.name
        }
    }

}
