package com.example.recipeapp.adapters

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.entities.Recipe

class RecipeAdapter(private val onRecipeClick: (Recipe) -> Unit) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private var recipes: List<Recipe> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int = recipes.size

    fun submitList(recipeList: List<Recipe>) {
        recipes = recipeList
        notifyDataSetChanged()
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeNameTextView: TextView = itemView.findViewById(R.id.recipe_name)
        private val recipeTypeTextView: TextView = itemView.findViewById(R.id.recipe_type)
        private val recipeImageView: ImageView = itemView.findViewById(R.id.recipe_image)

        fun bind(recipe: Recipe) {
            recipeNameTextView.text = recipe.name
            recipeTypeTextView.text = recipe.type

            if (recipe.imageUri.isNotEmpty()) {
                val uri = Uri.parse(recipe.imageUri)
                Log.d("RecipeAdapter", "Loading image from URI: $uri")

                try {
                    Glide.with(itemView.context)
                        .load(uri)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(recipeImageView)
                } catch (e: Exception) {
                    Log.e("RecipeAdapter", "Error loading image", e)
                    recipeImageView.setImageResource(R.drawable.error_image)
                }
            } else {
                recipeImageView.setImageResource(R.drawable.placeholder_image)
            }

            itemView.setOnClickListener {
                onRecipeClick(recipe)
            }
        }
    }
}