package com.example.recipeapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.entities.Recipe

class RecipeAdapter(private val onRecipeClick: (Recipe) -> Unit) : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = getItem(position)
        holder.bind(recipe)
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeImageView: ImageView = itemView.findViewById(R.id.recipe_image)
        private val recipeNameTextView: TextView = itemView.findViewById(R.id.recipe_name)
        private val recipeTypeTextView: TextView = itemView.findViewById(R.id.recipe_type)

        fun bind(recipe: Recipe) {
            recipeNameTextView.text = recipe.name
            recipeTypeTextView.text = recipe.type

            if (recipe.imageUri.isNotEmpty()) {
                Log.d("RecipeAdapter", "Loading image from URI: ${recipe.imageUri}")

                Glide.with(itemView.context)
                    .load(recipe.imageUri)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image) // Add an error placeholder
                    .into(recipeImageView)
            } else {
                recipeImageView.setImageResource(R.drawable.placeholder_image)
            }

            itemView.setOnClickListener {
                onRecipeClick(recipe)
            }
        }
    }
}