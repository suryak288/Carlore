package com.example.data

import kotlinx.coroutines.flow.Flow

class SearchRepository(private val dao: RecentSearchDao) {
    val recentSearches: Flow<List<RecentSearchEntity>> = dao.getRecentSearches()

    suspend fun addSearch(search: RecentSearchEntity) {
        dao.insertSearch(search)
    }
}
