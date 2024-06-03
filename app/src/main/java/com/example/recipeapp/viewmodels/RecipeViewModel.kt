package com.example.recipeapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.data.RecipeRepository
import com.example.recipeapp.entities.Recipe
import kotlinx.coroutines.launch

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _filteredRecipes = MutableLiveData<List<Recipe>>()
    val filteredRecipes: LiveData<List<Recipe>> get() = _filteredRecipes

    fun getAllRecipeTypes(userId: String): LiveData<List<String>> = repository.getAllRecipeTypes(userId)
    fun getFavoriteRecipes(userId: String): LiveData<List<Recipe>> = repository.getFavoriteRecipes(userId)

    fun filterRecipesByType(type: String, userId: String): LiveData<List<Recipe>> {
        viewModelScope.launch {
            _filteredRecipes.value = repository.filterRecipesByType(type, userId)
        }
        return _filteredRecipes
    }

    fun getAllRecipes(userId: String): LiveData<List<Recipe>> {
        return repository.getAllRecipes(userId)
    }

    fun insertRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.insertRecipe(recipe)
        }
    }

    fun addRecipeToFavorites(recipeId: Int, userId: String) {
        viewModelScope.launch {
            repository.addRecipeToFavorites(recipeId, userId)
        }
    }

    fun removeRecipeFromFavorites(recipeId: Int, userId: String) {
        viewModelScope.launch {
            repository.removeRecipeFromFavorites(recipeId, userId)
        }
    }

    fun getRecipeById(recipeId: Int, userId: String): LiveData<Recipe> {
        return repository.getRecipeById(recipeId, userId)
    }

    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.updateRecipe(recipe)
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.deleteRecipe(recipe)
        }
    }
}
