package com.example.recipeapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val name: String,
    val type: String,
    val ingredients: String,
    val steps: String,
    val imageUri: String,
    val isFavorite: Boolean = false
)
