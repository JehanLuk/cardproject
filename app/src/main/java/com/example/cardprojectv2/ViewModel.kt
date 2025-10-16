package com.example.cardprojectv2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LembreteViewModelFactory(
    private val dao: LembreteDAO
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LembreteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LembreteViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
