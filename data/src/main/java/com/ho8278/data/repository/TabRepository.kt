package com.ho8278.data.repository

import kotlinx.coroutines.flow.Flow

interface TabRepository {
    suspend fun setTabPosition(position: Int)
    suspend fun getTabPosition(): Int
    fun tabChanges(): Flow<Int>
}