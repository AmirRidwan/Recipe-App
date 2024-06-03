package com.example.recipeapp.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.data.RecipeDatabase
import com.example.recipeapp.data.UserRepository
import com.example.recipeapp.entities.User
import com.example.recipeapp.viewmodels.UserViewModel
import com.example.recipeapp.viewmodels.UserViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var emailTextView: TextView
    private lateinit var saveButton: Button
    private lateinit var editButton: Button
    private lateinit var userViewModel: UserViewModel
    private lateinit var auth: FirebaseAuth
    private var selectedImageUri: Uri? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        val context = requireContext().applicationContext
        val database = Room.databaseBuilder(
            context,
            RecipeDatabase::class.java,
            "recipe_database"
        ).addMigrations(RecipeDatabase.MIGRATION_2_3).fallbackToDestructiveMigration().build()
        val userRepository = UserRepository(database.userDao())

        userViewModel = ViewModelProvider(
            this,
            UserViewModelFactory(userRepository)
        ).get(UserViewModel::class.java)

        profileImageView = view.findViewById(R.id.profileImageView)
        nameEditText = view.findViewById(R.id.nameEditText)
        emailTextView = view.findViewById(R.id.emailTextView)
        saveButton = view.findViewById(R.id.saveButton)
        editButton = view.findViewById(R.id.editButton)

        val userId = auth.currentUser?.uid
        if (userId != null) {
            userViewModel.getUserById(userId).observe(viewLifecycleOwner, Observer { user ->
                if (user != null) {
                    nameEditText.setText(user.name)
                    emailTextView.text = user.email
                    Glide.with(this)
                        .load(user.profileImageUri)
                        .circleCrop()
                        .placeholder(R.drawable.default_profile)
                        .into(profileImageView)
                }
            })
        }

        profileImageView.setOnClickListener {
            if (saveButton.visibility == View.VISIBLE) {
                selectImage()
            }
        }

        editButton.setOnClickListener {
            enableEditing(true)
        }

        saveButton.setOnClickListener {
            saveChanges()
            enableEditing(false)
        }

        // Register the launcher for image picking
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedImageUri = result.data?.data
                profileImageView.setImageURI(selectedImageUri)
            }
        }

        enableEditing(false)

        return view
    }

    private fun enableEditing(enable: Boolean) {
        nameEditText.isEnabled = enable
        profileImageView.isClickable = enable
        saveButton.visibility = if (enable) View.VISIBLE else View.GONE
        editButton.visibility = if (enable) View.GONE else View.VISIBLE
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun saveChanges() {
        val name = nameEditText.text.toString()
        val email = emailTextView.text.toString()
        val profileImageUri = selectedImageUri?.toString() ?: "android.resource://${requireContext().packageName}/${R.drawable.default_profile}"

        val userId = auth.currentUser?.uid ?: return
        val user = User(userId = userId, name = name, email = email, profileImageUri = profileImageUri)
        userViewModel.insertUser(user)
    }
}
