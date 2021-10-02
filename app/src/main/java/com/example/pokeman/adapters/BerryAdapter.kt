package com.example.pokeman.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokeman.BerryDetailActivity
import com.example.pokeman.R
import com.example.pokeman.data.PokemonItem
import com.example.pokeman.utilities.EXTRA_BERRY

class BerryAdapter(
    val context: Context,
    val berries: List<PokemonItem>
    ) : RecyclerView.Adapter<BerryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_berry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val berry = berries[position]
        holder.bind(berry)
    }

    override fun getItemCount() = berries.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivBerrySprite = itemView.findViewById<ImageView>(R.id.ivBerrySprite)

        fun bind(berry: PokemonItem) {
            Glide.with(context).load(berry.sprite).into(ivBerrySprite)

            // OnClick navigate to details page
            ivBerrySprite.setOnClickListener {
                val intent = Intent(context, BerryDetailActivity::class.java).apply {
                    putExtra(EXTRA_BERRY, berry)
                }
                context.startActivity(intent)
            }
        }
    }
}