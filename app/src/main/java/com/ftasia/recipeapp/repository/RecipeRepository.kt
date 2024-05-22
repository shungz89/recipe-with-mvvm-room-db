package com.ftasia.recipeapp.repository
import androidx.lifecycle.LiveData
import com.ftasia.recipeapp.dao.RecipeDao
import com.ftasia.recipeapp.entity.Recipe

class RecipeRepository(private val recipeDao: RecipeDao) {

    val allRecipes: LiveData<List<Recipe>> = recipeDao.getAllRecipes()

    suspend fun insert(recipe: Recipe) {
        recipeDao.insert(recipe)
    }
    suspend fun delete(recipe: Recipe){
        recipeDao.delete(recipe)
    }

    suspend fun update(recipe: Recipe){
        recipeDao.update(recipe)
    }

    suspend fun getRecipeCount(): Int {
        return recipeDao.getRecipeCount()
    }

    suspend fun insertAll(recipes: List<Recipe>) {
        recipeDao.insertAll(recipes)
    }
}