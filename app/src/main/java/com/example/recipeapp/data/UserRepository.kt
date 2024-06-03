package com.example.recipeapp.data

import androidx.lifecycle.LiveData
import com.example.recipeapp.entities.User

class UserRepository(private val userDao: UserDao) {

    fun getUserById(userId: String): LiveData<User?> {
        return userDao.getUserById(userId)
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }
}
