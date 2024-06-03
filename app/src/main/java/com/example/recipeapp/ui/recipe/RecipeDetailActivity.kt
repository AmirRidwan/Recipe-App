package com.example.recipeapp.ui.recipe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.data.RecipeDatabase
import com.example.recipeapp.data.RecipeRepository
import com.example.recipeapp.entities.Recipe
import com.example.recipeapp.viewmodels.RecipeViewModel
import com.example.recipeapp.viewmodels.RecipeViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var recipeImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var typeTextView: TextView
    private lateinit var ingredientsTextView: TextView
    private lateinit var stepsTextView: TextView
    private lateinit var favoriteButton: Button
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var saveButton: Button
    private lateinit var recipeViewModel: RecipeViewModel
    private var recipeId: Int = 0
    private var isFavorite: Boolean = false
    private lateinit var auth: FirebaseAuth
    private var selectedImageUri: Uri? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        recipeImageView = findViewById(R.id.recipeImageView)
        nameTextView = findViewById(R.id.nameTextView)
        typeTextView = findViewById(R.id.typeTextView)
        ingredientsTextView = findViewById(R.id.ingredientsTextView)
        stepsTextView = findViewById(R.id.stepsTextView)
        favoriteButton = findViewById(R.id.favoriteButton)
        editButton = findViewById(R.id.editButton)
        deleteButton = findViewById(R.id.deleteButton)
        saveButton = findViewById(R.id.saveButton)

        recipeId = intent.getIntExtra("RECIPE_ID", 0)

        auth = FirebaseAuth.getInstance()

        val database = Room.databaseBuilder(
            applicationContext,
            RecipeDatabase::class.java,
            "recipe_database"
        ).addMigrations(RecipeDatabase.MIGRATION_2_3)
            .fallbackToDestructiveMigration()
            .build()
        val recipeRepository = RecipeRepository(database.recipeDao())

        recipeViewModel = ViewModelProvider(
            this,
            RecipeViewModelFactory(recipeRepository)
        ).get(RecipeViewModel::class.java)

        val userId = auth.currentUser?.uid

        if (userId != null) {
            recipeViewModel.getRecipeById(recipeId, userId).observe(this, { recipe ->
                if (recipe != null) {
                    displayRecipeDetails(recipe)
                    isFavorite = recipe.isFavorite
                    updateFavoriteButton()
                }
            })

            favoriteButton.setOnClickListener {
                if (isFavorite) {
                    recipeViewModel.removeRecipeFromFavorites(recipeId, userId)
                    Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
                } else {
                    recipeViewModel.addRecipeToFavorites(recipeId, userId)
                    Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
                }
                isFavorite = !isFavorite
                updateFavoriteButton()
            }

            editButton.setOnClickListener {
                enableEditing(true)
            }

            deleteButton.setOnClickListener {
                val recipe = Recipe(
                    id = recipeId,
                    userId = userId,
                    name = nameTextView.text.toString(),
                    type = typeTextView.text.toString(),
                    ingredients = ingredientsTextView.text.toString(),
                    steps = stepsTextView.text.toString(),
                    imageUri = selectedImageUri?.toString() ?: "android.resource://${packageName}/${R.drawable.placeholder_image}",
                    isFavorite = isFavorite
                )
                recipeViewModel.deleteRecipe(recipe)
                Toast.makeText(this, "Recipe deleted", Toast.LENGTH_SHORT).show()
                finish()
            }

            saveButton.setOnClickListener {
                saveChanges()
                enableEditing(false)
            }

            // Register the launcher for image picking
            imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    selectedImageUri = result.data?.data
                    recipeImageView.setImageURI(selectedImageUri)
                }
            }

            recipeImageView.setOnClickListener {
                if (saveButton.visibility == View.VISIBLE) {
                    selectImage()
                }
            }
        }
    }

    private fun displayRecipeDetails(recipe: Recipe) {
        nameTextView.text = recipe.name
        typeTextView.text = recipe.type
        ingredientsTextView.text = recipe.ingredients
        stepsTextView.text = recipe.steps
        Glide.with(this)
            .load(recipe.imageUri)
            .placeholder(R.drawable.placeholder_image)
            .into(recipeImageView)
    }

    private fun updateFavoriteButton() {
        favoriteButton.text = if (isFavorite) "Remove from Favorites" else "Add to Favorites"
    }

    private fun enableEditing(enable: Boolean) {
        nameTextView.isEnabled = enable
        typeTextView.isEnabled = enable
        ingredientsTextView.isEnabled = enable
        stepsTextView.isEnabled = enable
        recipeImageView.isClickable = enable
        favoriteButton.visibility = if (enable) View.GONE else View.VISIBLE
        editButton.visibility = if (enable) View.GONE else View.VISIBLE
        deleteButton.visibility = if (enable) View.GONE else View.VISIBLE
        saveButton.visibility = if (enable) View.VISIBLE else View.GONE
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun saveChanges() {
        val name = nameTextView.text.toString()
        val type = typeTextView.text.toString()
        val ingredients = ingredientsTextView.text.toString()
        val steps = stepsTextView.text.toString()
        val imageUri = selectedImageUri?.toString() ?: "android.resource://${packageName}/${R.drawable.placeholder_image}"

        val userId = auth.currentUser?.uid ?: return
        val recipe = Recipe(
            id = recipeId,
            userId = userId,
            name = name,
            type = type,
            ingredients = ingredients,
            steps = steps,
            imageUri = imageUri,
            isFavorite = isFavorite
        )
        recipeViewModel.updateRecipe(recipe)
        Toast.makeText(this, "Recipe updated", Toast.LENGTH_SHORT).show()
        finish()
    }
}