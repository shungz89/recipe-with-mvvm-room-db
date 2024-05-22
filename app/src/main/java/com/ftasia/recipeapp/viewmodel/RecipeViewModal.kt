package com.ftasia.recipeapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ftasia.recipeapp.R
import com.ftasia.recipeapp.database.RecipeDatabase
import com.ftasia.recipeapp.entity.Recipe
import com.ftasia.recipeapp.repository.RecipeRepository
import com.ftasia.recipeapp.utils.ImageUtils.Companion.saveDrawableToInternalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeViewModal(application: Application) : AndroidViewModel(application) {
    val allRecipes: LiveData<List<Recipe>>
    val repository: RecipeRepository


    init {
        val dao = RecipeDatabase.getDatabase(application).getRecipesDao()
        repository = RecipeRepository(dao)
        allRecipes = repository.allRecipes

        viewModelScope.launch(Dispatchers.IO) {

            //Use sharedPreference to check if the app is opened for the first time
            val sharedPreferences =
                application.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val isFirstTime = sharedPreferences.getBoolean("firstTime", true)

            //We need two conditions because we don't want to populate again if we delete all the data after app installed,
            //Meanwhile we also don't want to populate the db if there is data for the first time
            if (isFirstTime && repository.getRecipeCount() == 0) {
                prepopulateDatabase(application)
                sharedPreferences.edit().putBoolean("firstTime", false).apply()
            }
        }
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

    private suspend fun prepopulateDatabase(context: Context) {

        val drawableInfoList = listOf(
            R.drawable.pancakes to "pancakes",
            R.drawable.chicken_caesar_salad to "chicken_caesar_salad",
            R.drawable.beef_stroganoff to "beef_stroganoff",
            R.drawable.granola_bars to "granola_bars",
            R.drawable.chocolate_cake to "chocolate_cake"
        )

        val drawableFilePaths: MutableList<String> = mutableListOf()

        drawableInfoList.forEach { (drawableId, drawableName) ->
            val filePath = saveDrawableToInternalStorage(context, drawableId, "$drawableName.png")
            drawableFilePaths.add(filePath ?: "")
        }


        val predefinedRecipes = listOf(
            Recipe(
                "Pancakes",
                "Flour, Eggs, Milk, Sugar, Baking powder, Butter",
                "Mix dry ingredients, Add wet ingredients, Cook on griddle",
                "1",
                drawableFilePaths[0], // Ensure there's a drawable resource named pancakes
                "16 May, 2023 - 08:00"
            ),
            Recipe(
                "Chicken Caesar Salad",
                "Romaine lettuce, Chicken breast, Croutons, Caesar dressing, Parmesan cheese",
                "Grill chicken, Chop lettuce, Mix with dressing, Add croutons and cheese",
                "2",
                drawableFilePaths[1], // Ensure there's a drawable resource named pancakes
                "17 May, 2023 - 12:30"
            ),
            Recipe(
                "Beef Stroganoff",
                "Beef, Mushrooms, Onion, Sour cream, Flour, Beef broth",
                "Cook beef, Add mushrooms and onions, Stir in flour, Add broth, Mix in sour cream",
                "3",
                drawableFilePaths[2], // Ensure there's a drawable resource named pancakes
                "18 May, 2023 - 18:45"
            ),
            Recipe(
                "Granola Bars",
                "Oats, Honey, Peanut butter, Chocolate chips, Nuts",
                "Mix all ingredients, Press into pan, Bake and cut into bars",
                "4",
                drawableFilePaths[3], // Ensure there's a drawable resource named pancakes
                "9 May, 2023 - 10:15"
            ),
            Recipe(
                "Chocolate Cake",
                "Flour, Cocoa powder, Sugar, Eggs, Butter, Baking soda, Milk",
                "Mix dry ingredients, Add wet ingredients, Bake in oven",
                "5",
                drawableFilePaths[4], // Ensure there's a drawable resource named pancakes
                "20 May, 2023 - 14:00"
            )
        )
        repository.insertAll(predefinedRecipes)
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