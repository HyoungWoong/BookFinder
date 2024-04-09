package com.ho8278.data.repository

import com.ho8278.data.local.FavoritePref
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class TabRepositoryImpl @Inject constructor(
    private val favoritePref: FavoritePref
) : TabRepository {

    private val tabChangeEvent = MutableSharedFlow<Int>()

    override suspend fun setTabPosition(position: Int) {
        favoritePref.putValue(KEY_POSITION, position, Int::class.java)
        tabChangeEvent.emit(position)
    }

    override suspend fun getTabPosition(): Int {
        return favoritePref.getValue(KEY_POSITION, Int::class.java) ?: 0
    }

    override fun tabChanges(): Flow<Int> {
        return tabChangeEvent.onStart { emit(getTabPosition()) }
    }

    companion object {
        private const val KEY_POSITION = "key_position"
    }
}