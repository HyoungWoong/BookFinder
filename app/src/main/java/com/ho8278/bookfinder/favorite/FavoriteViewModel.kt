package com.ho8278.bookfinder.favorite

import androidx.lifecycle.ViewModel
import com.ho8278.data.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val marbleRepository: ImageRepository
) : ViewModel() {

}