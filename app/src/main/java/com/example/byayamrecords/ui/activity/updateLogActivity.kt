package com.example.byayamrecords.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.byayamrecords.databinding.ActivityUpdateLogBinding
import com.example.byayamrecords.repository.WorkoutLofRepoImpl
import com.example.byayamrecords.viewmodel.WorkoutLogViewModel

class updateLogActivity : AppCompatActivity() {

    lateinit var binding: ActivityUpdateLogBinding
    lateinit var viewModel: WorkoutLogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpdateLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repo = WorkoutLofRepoImpl()
        viewModel = WorkoutLogViewModel(repo)

        var logId: String? = intent.getStringExtra("logId")

        viewModel.getLogById(logId.toString())

        viewModel.products.observe(this) {
            binding.etWorkoutName.setText(it?.WorkoutName.toString())
            binding.etWorkoutSets.setText(it?.WorkoutSets.toString())
            binding.etWorkoutWeight.setText(it?.WorkoutWeight.toString())
            binding.etWorkoutDesc.setText(it?.WorkoutDesc.toString())
        }

        binding.btnUpdate.setOnClickListener {
            val name = binding.etWorkoutName.text.toString()
            val sets = binding.etWorkoutSets.text.toString().toIntOrNull() ?: 0
            val weight = binding.etWorkoutWeight.text.toString().trim()
            val desc = binding.etWorkoutDesc.text.toString().trim()

            var updatedMap = mutableMapOf<String, Any>()
            updatedMap["WorkoutName"] = name
            updatedMap["WorkoutSets"] = sets
            updatedMap["WorkoutWeight"] = weight
            updatedMap["WorkoutDesc"] = desc

            viewModel.updateLog(
                logId.toString(),
                updatedMap
            ) { success, message ->
                Toast.makeText(this@updateLogActivity, message, Toast.LENGTH_LONG).show()

                if(success) {
                    viewModel.getLogById(logId.toString())
                    finish()
                }

            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
