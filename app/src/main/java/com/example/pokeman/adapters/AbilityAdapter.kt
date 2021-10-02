package com.example.pokeman.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeman.R

class AbilityAdapter(
    val context: Context,
    val abilites: List<Pair<String, String>>
    ) : RecyclerView.Adapter<AbilityAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_ability, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ability = abilites[position]
        holder.bind(ability)
    }

    override fun getItemCount() = abilites.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvAbility = itemView.findViewById<TextView>(R.id.tvAbility)
        private val tvDescription = itemView.findViewById<TextView>(R.id.tvDescription)

        fun bind(ability: Pair<String, String>) {
            tvAbility.text = ability.first
            tvDescription.text = ability.second
        }

    }













}