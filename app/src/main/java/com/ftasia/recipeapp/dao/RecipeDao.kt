package com.ftasia.recipeapp.dao
import androidx.lifecycle.LiveData
import androidx.room.*
import com.ftasia.recipeapp.entity.Recipe

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(recipe : Recipe)

    @Update
    suspend fun update(recipe: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)

    @Query("Select * from recipeTable order by id ASC")
    fun getAllRecipes(): LiveData<List<Recipe>>

    @Query("SELECT COUNT(*) FROM recipeTable")
    suspend fun getRecipeCount(): Int

    @Insert
    suspend fun insertAll(recipes: List<Recipe>)

}