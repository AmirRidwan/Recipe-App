package com.example.recipeapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.recipeapp.entities.Recipe

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipe WHERE type = :type AND userId = :userId")
    suspend fun getRecipesByType(type: String, userId: String): List<Recipe>

    @Query("SELECT DISTINCT type FROM recipe WHERE userId = :userId")
    fun getAllRecipeTypes(userId: String): LiveData<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe)

    @Query("SELECT * FROM recipe WHERE isFavorite = 1 AND userId = :userId")
    fun getFavoriteRecipes(userId: String): LiveData<List<Recipe>>

    @Query("UPDATE recipe SET isFavorite = 1 WHERE id = :id AND userId = :userId")
    suspend fun addRecipeToFavorites(id: Int, userId: String)

    @Query("UPDATE recipe SET isFavorite = 0 WHERE id = :id AND userId = :userId")
    suspend fun removeRecipeFromFavorites(id: Int, userId: String)

    @Query("SELECT * FROM recipe WHERE id = :id AND userId = :userId")
    fun getRecipeById(id: Int, userId: String): LiveData<Recipe>

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Query("SELECT * FROM recipe WHERE userId = :userId")
    fun getAllRecipes(userId: String): LiveData<List<Recipe>>
}
