package com.example.recipeapp.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.example.recipeapp.ui.recipe.RecipeDetailActivity
import com.example.recipeapp.viewmodels.RecipeViewModel
import com.example.recipeapp.viewmodels.RecipeViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class FavoritesFragment : Fragment() {

    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var noFavoritesTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

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

        recipeRecyclerView = view.findViewById(R.id.favorites_recycler_view)
        recipeAdapter = RecipeAdapter { recipe ->
            val intent = Intent(requireContext(), RecipeDetailActivity::class.java)
            intent.putExtra("RECIPE_ID", recipe.id)
            startActivity(intent)
        }

        recipeRecyclerView.adapter = recipeAdapter
        recipeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        noFavoritesTextView = view.findViewById(R.id.no_favorites_text_view)

        val userId = auth.currentUser?.uid
        if (userId != null) {
            recipeViewModel.getFavoriteRecipes(userId).observe(viewLifecycleOwner, Observer { recipes ->
                if (recipes.isEmpty()) {
                    noFavoritesTextView.visibility = View.VISIBLE
                    recipeRecyclerView.visibility = View.GONE
                } else {
                    noFavoritesTextView.visibility = View.GONE
                    recipeRecyclerView.visibility = View.VISIBLE
                    recipeAdapter.submitList(recipes)
                }
            })
        }

        return view
    }
}