package com.pesamjanja.app.data.repository

import com.pesamjanja.app.data.dao.CategoryDao
import com.pesamjanja.app.data.entity.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val dao: CategoryDao) {
    val all: Flow<List<Category>> = dao.getAll()
    suspend fun add(category: Category): Long = dao.insert(category)
}
