package com.example.pokeman.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pokeman.data.Pokemon

private const val TAG = "PokemonViewModel"

class PokemonViewModel : ViewModel(){
    private val pokemonsLiveData: MutableLiveData<MutableList<Pokemon>>

    init {
        Log.i(TAG, "PokemonViewModel created!")
        pokemonsLiveData = MutableLiveData()
    }

    // Viewonly is destroyed when the associated fragment is detached, or when the activity is,
    // right before the viewmodel is destroyed, the onCleared() is call to clean up the resources
    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "PokemonViewModel destroyed!")
    }
}