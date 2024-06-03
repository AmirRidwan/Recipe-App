package com.example.recipeapp.ui.recipe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.recipeapp.R
import com.example.recipeapp.data.RecipeDatabase
import com.example.recipeapp.data.RecipeRepository
import com.example.recipeapp.entities.Recipe
import com.example.recipeapp.viewmodels.RecipeViewModel
import com.example.recipeapp.viewmodels.RecipeViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class AddRecipeActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var typeSpinner: Spinner
    private lateinit var ingredientsEditText: EditText
    private lateinit var stepsEditText: EditText
    private lateinit var addRecipeButton: Button
    private lateinit var recipeImageView: ImageView
    private lateinit var recipeViewModel: RecipeViewModel
    private var selectedImageUri: Uri? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        auth = FirebaseAuth.getInstance()

        val database = Room.databaseBuilder(
            applicationContext,
            RecipeDatabase::class.java,
            "recipe_database"
        ).addMigrations(RecipeDatabase.MIGRATION_2_3).fallbackToDestructiveMigration().build()
        val recipeRepository = RecipeRepository(database.recipeDao())

        recipeViewModel = ViewModelProvider(
            this,
            RecipeViewModelFactory(recipeRepository)
        ).get(RecipeViewModel::class.java)

        nameEditText = findViewById(R.id.recipe_name_edit_text)
        typeSpinner = findViewById(R.id.recipe_type_spinner)
        ingredientsEditText = findViewById(R.id.ingredients_edit_text)
        stepsEditText = findViewById(R.id.steps_edit_text)
        addRecipeButton = findViewById(R.id.add_recipe_button)
        recipeImageView = findViewById(R.id.recipe_image_view)

        // Load recipe types from XML resource and filter out "All Types"
        val recipeTypes = resources.getStringArray(R.array.recipe_types)
        val filteredRecipeTypes = recipeTypes.filter { it != "All Types" }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filteredRecipeTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = adapter

        // Register the launcher for image picking
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                selectedImageUri = result.data?.data
                recipeImageView.setImageURI(selectedImageUri)
            }
        }

        recipeImageView.setOnClickListener {
            selectImage()
        }

        addRecipeButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val type = typeSpinner.selectedItem.toString()
            val ingredients = ingredientsEditText.text.toString()
            val steps = stepsEditText.text.toString()
            val imageUri = selectedImageUri?.toString() ?: ""

            val userId = auth.currentUser?.uid ?: return@setOnClickListener

            val recipe = Recipe(
                userId = userId, // Associate the recipe with the current user
                name = name,
                type = type,
                ingredients = ingredients,
                steps = steps,
                imageUri = imageUri
            )
            recipeViewModel.insertRecipe(recipe)
            finish()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }
}