package com.example.recipeapp.ui.recipe

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.recipeapp.R
import com.example.recipeapp.adapters.RecipeAdapter
import com.example.recipeapp.data.RecipeDatabase
import com.example.recipeapp.data.RecipeRepository
import com.example.recipeapp.entities.Recipe
import com.example.recipeapp.viewmodels.RecipeViewModel
import com.example.recipeapp.viewmodels.RecipeViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private lateinit var recipeTypeSpinner: Spinner
    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var addRecipeButton: FloatingActionButton
    private lateinit var noRecipeTextView: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        auth = FirebaseAuth.getInstance()

        val context = requireContext().applicationContext
        val database = Room.databaseBuilder(
            context,
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

        recipeTypeSpinner = view.findViewById(R.id.recipe_type_spinner)
        recipeRecyclerView = view.findViewById(R.id.recipe_recycler_view)
        addRecipeButton = view.findViewById(R.id.add_recipe_button)
        noRecipeTextView = view.findViewById(R.id.no_recipe_text_view)
        recipeAdapter = RecipeAdapter { recipe ->
            val intent = Intent(requireContext(), RecipeDetailActivity::class.java)
            intent.putExtra("RECIPE_ID", recipe.id)
            startActivity(intent)
        }

        recipeRecyclerView.adapter = recipeAdapter
        recipeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Load recipe types from XML resource
        val recipeTypes = resources.getStringArray(R.array.recipe_types)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, recipeTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        recipeTypeSpinner.adapter = adapter

        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Load all recipes initially
            recipeViewModel.getAllRecipes(userId).observe(viewLifecycleOwner, Observer { recipes ->
                updateUI(recipes)
            })

            recipeTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedType = parent.getItemAtPosition(position) as String
                    if (selectedType != "All Types") {
                        recipeViewModel.filterRecipesByType(selectedType, userId).observe(viewLifecycleOwner, Observer { recipes ->
                            updateUI(recipes)
                        })
                    } else {
                        recipeViewModel.getAllRecipes(userId).observe(viewLifecycleOwner, Observer { recipes ->
                            updateUI(recipes)
                        })
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            recipeViewModel.filteredRecipes.observe(viewLifecycleOwner, Observer { recipes ->
                updateUI(recipes)
            })
        }

        addRecipeButton.setOnClickListener {
            val intent = Intent(requireContext(), AddRecipeActivity::class.java)
            startActivity(intent)
        }

        // Override the back button to prevent logging out
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Do nothing or navigate to another fragment
        }

        return view
    }

    private fun updateUI(recipes: List<Recipe>) {
        if (recipes.isEmpty()) {
            noRecipeTextView.visibility = View.VISIBLE
            recipeRecyclerView.visibility = View.GONE
        } else {
            noRecipeTextView.visibility = View.GONE
            recipeRecyclerView.visibility = View.VISIBLE
            recipeAdapter.submitList(recipes)
        }
    }
}