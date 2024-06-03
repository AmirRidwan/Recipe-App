package com.example.recipeapp.data

import androidx.lifecycle.LiveData
import com.example.recipeapp.entities.Recipe

class RecipeRepository(private val recipeDao: RecipeDao) {

    fun getAllRecipeTypes(userId: String): LiveData<List<String>> = recipeDao.getAllRecipeTypes(userId)
    fun getFavoriteRecipes(userId: String): LiveData<List<Recipe>> {
        return recipeDao.getFavoriteRecipes(userId)
    }

    suspend fun addRecipeToFavorites(recipeId: Int, userId: String) {
        recipeDao.addRecipeToFavorites(recipeId, userId)
    }

    suspend fun removeRecipeFromFavorites(recipeId: Int, userId: String) {
        recipeDao.removeRecipeFromFavorites(recipeId, userId)
    }

    suspend fun filterRecipesByType(type: String, userId: String): List<Recipe> = recipeDao.getRecipesByType(type, userId)

    fun getAllRecipes(userId: String): LiveData<List<Recipe>> = recipeDao.getAllRecipes(userId)

    suspend fun insertRecipe(recipe: Recipe) = recipeDao.insertRecipe(recipe)
    fun getRecipeById(recipeId: Int, userId: String): LiveData<Recipe> = recipeDao.getRecipeById(recipeId, userId)

    suspend fun updateRecipe(recipe: Recipe) = recipeDao.updateRecipe(recipe)
    suspend fun deleteRecipe(recipe: Recipe) = recipeDao.deleteRecipe(recipe)
}
