package com.ftasia.recipeapp.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ftasia.recipeapp.database.RecipeDatabase
import com.ftasia.recipeapp.repository.RecipeRepository
import com.ftasia.recipeapp.entity.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeViewModal(application: Application) : AndroidViewModel(application) {
    val allRecipes: LiveData<List<Recipe>>
    val repository: RecipeRepository


    init {
        val dao = RecipeDatabase.getDatabase(application).getNotesDao()
        repository = RecipeRepository(dao)
        allRecipes = repository.allRecipes
    }

    fun deleteRecipe(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(recipe)
    }

    fun updateRecipe(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(recipe)
    }

    fun addRecipe(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(recipe)
    }

    ///TODO:Possible Ways to explore if this function should be placed here
//    val filteredRecipes = MutableLiveData<List<Recipe>>()
//    fun filterRecipesByType(recipeTypeId: Int) {
//        val recipeTypeSelectedIndex = recipeTypeId.toString()
//        if (recipeTypeId == -1) {
//            filteredRecipes.value = allRecipes.value
//        } else {
//            filteredRecipes.value = allRecipes.value?.filter { it.recipeTypes == recipeTypeSelectedIndex }
//        }
//    }

}