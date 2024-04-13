package com.ho8278.bookfinder.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ho8278.data.model.Image
import com.ho8278.data.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val isInEditMode = MutableStateFlow(false)
    private val selectedImage = MutableStateFlow(emptyList<Image>())

    val uiState = combine(
        imageRepository.favoriteChanges()
            .flowOn(Dispatchers.IO),
        selectedImage,
        isInEditMode,
    ) { favorites, selectedImage, isEditMode ->
        val favoriteItemHolder = favorites.map {
            val selected = if (isEditMode) {
                selectedImage.contains(it)
            } else {
                false
            }
            FavoriteItemHolder(it, selected)
        }

        FavoriteUiState(favoriteItemHolder, isEditMode)
    }

    fun setEditMode(editMode: Boolean) {
        viewModelScope.launch {
            isInEditMode.emit(editMode)
            if (editMode) {
                selectedImage.emit(emptyList())
            }
        }
    }

    fun selectImage(image: Image) {
        viewModelScope.launch {
            val newList = selectedImage.value + image
            selectedImage.emit(newList)
        }
    }

    fun unSelectImage(image: Image) {
        viewModelScope.launch {
            val newList = selectedImage.value - image
            selectedImage.emit(newList)
        }
    }

    fun confirm() {
        viewModelScope.launch {
            selectedImage.value.forEach {
                imageRepository.removeFavorite(it)
            }
            setEditMode(false)
        }
    }
}