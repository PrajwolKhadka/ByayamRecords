package com.example.byayamrecords.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.byayamrecords.R
import com.example.byayamrecords.databinding.ActivityAddLogBinding
import com.example.byayamrecords.model.WorkoutLog
import com.example.byayamrecords.repository.WorkoutLofRepoImpl
import com.example.byayamrecords.viewmodel.WorkoutLogViewModel

class addLogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddLogBinding
    private lateinit var viewModel: WorkoutLogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize View Binding
        binding = ActivityAddLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the repository and ViewModel
        val repository = WorkoutLofRepoImpl()
        viewModel = WorkoutLogViewModel(repository)

        // Set up the Floating Action Button
        binding.floatingActionButton2.setOnClickListener {
            addLog()
        }
    }

    private fun addLog() {
        val workoutName = binding.workoutNameEditText.text.toString()
        val workoutSets = binding.workoutSetsEditText.text.toString().toIntOrNull() ?: 0
        val workoutWeight = binding.workoutWeightEditText.text.toString()
        val workoutDesc = binding.workoutDescEditText.text.toString()

        val workoutLog = WorkoutLog(
            WorkoutName = workoutName,
            WorkoutSets = workoutSets,
            WorkoutWeight = workoutWeight,
            WorkoutDesc = workoutDesc
        )

        viewModel.addLog(workoutLog) { success, message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            if (success) {
                finish() // Close the activity after adding the log
            }
        }
    }
}