package com.example.byayamrecords.ui.activity

import UserViewModel
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.byayamrecords.R
import com.example.byayamrecords.databinding.ActivityLoginBinding
import com.example.byayamrecords.repository.UserRepositoryImpl
import com.example.byayamrecords.utils.LoadingUtils

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var userViewModel: UserViewModel
    lateinit var loadingUtils: LoadingUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repo = UserRepositoryImpl()
        userViewModel = UserViewModel(repo)
        userViewModel.init(this) // Initialize with context
        loadingUtils = LoadingUtils(this)

        // Check if user is already logged in
        if (userViewModel.isLoggedIn()) {
            navigateToNavigationActivity()
            return // Exit the onCreate method
        }

        binding.btnLogin.setOnClickListener {
            loadingUtils.show()
            val email: String = binding.editEmail.text.toString()
            val password: String = binding.editPassword.text.toString()

            userViewModel.login(email, password) { success, message ->
                if (success) {
                    loadingUtils.dismiss()
                    Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()
                    navigateToNavigationActivity()
                } else {
                    loadingUtils.dismiss()
                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnSignupnavigate.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnForget.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun navigateToNavigationActivity() {
        val intent = Intent(this@LoginActivity, NavigationActivity::class.java)
        startActivity(intent)
        finish() // Close LoginActivity
    }
}