package com.example.byayamrecords.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.byayamrecords.R
import com.example.byayamrecords.adapter.BMIAdapter
import com.example.byayamrecords.databinding.FragmentBmiCalculatorFragmentBinding
import com.example.byayamrecords.model.BMIRecord
import com.example.byayamrecords.viewmodel.BMIViewModel
import java.text.SimpleDateFormat
import java.util.*

class bmi_calculator_fragment : Fragment() {

    private var _binding: FragmentBmiCalculatorFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BMIViewModel
    private lateinit var adapter: BMIAdapter
    private var currentEditingRecord: BMIRecord? = null // Stores the record being edited

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBmiCalculatorFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[BMIViewModel::class.java]

        adapter = BMIAdapter(requireContext(), ArrayList(),
            onDeleteClick = { id -> viewModel.deleteBMI(id) },
            onEdit = { record -> populateFieldsForEdit(record) } // Handle edit
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Populate feet and inches spinners
        val feetOptions = (3..7).toList()
        val inchesOptions = (0..11).toList()

        val feetAdapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, feetOptions)
        feetAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown)
        binding.spinnerFeet.adapter = feetAdapter

        val inchesAdapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, inchesOptions)
        inchesAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown)
        binding.spinnerInches.adapter = inchesAdapter

        binding.spinnerFeet.adapter = feetAdapter
        binding.spinnerInches.adapter = inchesAdapter

        // Observe Live Data
        viewModel.bmiRecords.observe(viewLifecycleOwner) { records ->
            adapter.updateData(records)
        }

        // Calculate BMI
        binding.btnCalculate.setOnClickListener {
            val weightText = binding.etWeight.text.toString()
            if (weightText.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val weight = weightText.toFloat()
            val feet = binding.spinnerFeet.selectedItem.toString().toInt()
            val inches = binding.spinnerInches.selectedItem.toString().toInt()

            // Convert feet & inches to meters
            val heightInMeters = ((feet * 12) + inches) * 0.0254f
            val bmi = weight / (heightInMeters * heightInMeters)
            val status = getBMIStatus(bmi)
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            if (currentEditingRecord == null) {
                // Create new BMI record
                val record = BMIRecord(
                    id = UUID.randomUUID().toString(),
                    bmi = String.format("%.2f", bmi),
                    weight = weight.toDouble(),
                    height = heightInMeters.toDouble(),
                    status = status,
                    date = date
                )
                viewModel.saveBMI(record)
                Toast.makeText(requireContext(), "BMI Recorded", Toast.LENGTH_SHORT).show()
            } else {
                // Update existing record
                val updatedRecord = currentEditingRecord!!.copy(
                    bmi = String.format("%.2f", bmi),
                    weight = weight.toDouble(),
                    height = heightInMeters.toDouble(),
                    status = status,
                    date = date
                )
                viewModel.saveBMI(updatedRecord)
                Toast.makeText(requireContext(), "BMI Updated", Toast.LENGTH_SHORT).show()
                currentEditingRecord = null // Reset editing state
            }

            clearInputFields()
        }
    }

    private fun getBMIStatus(bmi: Float): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi in 18.5..24.9 -> "Normal"
            bmi in 25.0..29.9 -> "Overweight"
            else -> "Obese"
        }
    }

    private fun populateFieldsForEdit(record: BMIRecord) {
        currentEditingRecord = record
        binding.etWeight.setText(record.weight.toString())

        // Convert stored height (meters) back to feet & inches
        val totalInches = (record.height / 0.0254f).toInt()
        val feet = totalInches / 12
        val inches = totalInches % 12

        binding.spinnerFeet.setSelection((3..7).indexOf(feet))
        binding.spinnerInches.setSelection((0..11).indexOf(inches))
        binding.btnCalculate.text = "Update BMI"
    }




    // Clears input fields after saving or updating BMI record
    private fun clearInputFields() {
        binding.etWeight.text.clear()
        binding.spinnerFeet.setSelection(0)
        binding.spinnerInches.setSelection(0)
        binding.btnCalculate.text = "Calculate BMI"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
