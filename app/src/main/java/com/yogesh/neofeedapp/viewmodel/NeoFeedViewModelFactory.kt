package com.yogesh.neofeedapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yogesh.neofeedapp.model.models.MainRepository


@Suppress("UNCHECKED_CAST")
class NeoFeedViewModelFactory(private val mainRepository: MainRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NeoFeedViewModel(mainRepository) as T
    }
}