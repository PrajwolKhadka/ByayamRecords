package com.example.byayamrecords.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.byayamrecords.databinding.ActivityUpdateLogBinding
import com.example.byayamrecords.repository.WorkoutLofRepoImpl
import com.example.byayamrecords.viewmodel.WorkoutLogViewModel

class updateLogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateLogBinding
    private lateinit var viewModel: WorkoutLogViewModel
    private var logId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // View Binding
        binding = ActivityUpdateLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize ViewModel
        val repo = WorkoutLofRepoImpl()
        viewModel = WorkoutLogViewModel(repo)

        // Get Log ID from Intent
        logId = intent.getStringExtra("productId")

        if (logId != null) {
            fetchLogDetails(logId!!)
        } else {
            Toast.makeText(this, "Error: No Log ID found", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Update Button Click
        binding.btnUpdate.setOnClickListener {
            updateLog()
        }
    }

    private fun fetchLogDetails(logId: String) {
        binding.progressBar.visibility = View.VISIBLE

        viewModel.getLogById(logId)
        viewModel.products.observe(this) { log ->
            if (log != null) {
                binding.etWorkoutName.setText(log.WorkoutName)
                binding.etWorkoutSets.setText(log.WorkoutSets.toString())
                binding.etWorkoutWeight.setText(log.WorkoutWeight)
                binding.etWorkoutDesc.setText(log.WorkoutDesc)
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun updateLog() {
        val name = binding.etWorkoutName.text.toString().trim()
        val sets = binding.etWorkoutSets.text.toString().trim()
        val weight = binding.etWorkoutWeight.text.toString().trim()
        val desc = binding.etWorkoutDesc.text.toString().trim()

        if (name.isEmpty() || sets.isEmpty() || weight.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedData = mutableMapOf<String, Any>(
            "WorkoutName" to name,
            "WorkoutSets" to sets.toInt(),
            "WorkoutWeight" to weight,
            "WorkoutDesc" to desc
        )

        binding.progressBar.visibility = View.VISIBLE

        logId?.let {
            viewModel.updateLog(it, updatedData) { success, message ->
                binding.progressBar.visibility = View.GONE
                if (success) {
                    Toast.makeText(this, "Workout Log Updated!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Update Failed: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
