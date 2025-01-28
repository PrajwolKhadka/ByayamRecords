package com.example.byayamrecords.ui.activity

import UserViewModel
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.byayamrecords.R
import com.example.byayamrecords.databinding.ActivityRegisterBinding
import com.example.byayamrecords.model.UserModel
import com.example.byayamrecords.repository.UserRepositoryImpl
import com.example.byayamrecords.utils.LoadingUtils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding

    lateinit var userViewModel: UserViewModel

    lateinit var loadingUtils: LoadingUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingUtils=LoadingUtils(this)

        var repo= UserRepositoryImpl()
        userViewModel= UserViewModel(repo)
        binding.signUp.setOnClickListener{
            loadingUtils.show()
            var email= binding.registerEmail.text.toString()
            var password= binding.registerPassword.text.toString()
            var firstName= binding.registerFname.text.toString()
            var contact= binding.registerContact.text.toString()

            userViewModel.signup(email, password){
                    success,message,userId->
                if(success){
                    var userModel = UserModel(
                        userId.toString(),
                        firstName,
                        contact,
                        email
                    )

                    userViewModel.addUserToDatabase(userId,userModel){
                            success,message->
                        if(success){
                            loadingUtils.dismiss()
                            Toast.makeText(
                                this@RegisterActivity,
                                message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }else{
                    loadingUtils.dismiss()
                    Toast.makeText(this@RegisterActivity,message,Toast.LENGTH_LONG).show()
                }
            }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
    }
}}