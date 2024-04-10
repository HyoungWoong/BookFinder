package com.ho8278.bookfinder.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ho8278.data.repository.TabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tabRepository: TabRepository
) : ViewModel() {

    val tabPosition = tabRepository.tabChanges()

    fun onTabSelected(position: Int) {
        viewModelScope.launch {
            tabRepository.setTabPosition(position)
        }
    }
}