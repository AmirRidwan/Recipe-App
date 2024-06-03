package com.example.recipeapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey val userId: String,
    val name: String,
    val email: String,
    val profileImageUri: String
)
