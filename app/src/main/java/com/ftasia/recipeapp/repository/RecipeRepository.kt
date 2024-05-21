package com.ftasia.recipeapp.repository
import androidx.lifecycle.LiveData
import com.ftasia.recipeapp.dao.RecipeDao
import com.ftasia.recipeapp.entity.Recipe

class RecipeRepository(private val recipeDao: RecipeDao) {

    val allRecipes: LiveData<List<Recipe>> = recipeDao.getAllRecipes()

    suspend fun insert(note: Recipe) {
        recipeDao.insert(note)
    }
    suspend fun delete(note: Recipe){
        recipeDao.delete(note)
    }

    suspend fun update(note: Recipe){
        recipeDao.update(note)
    }
}