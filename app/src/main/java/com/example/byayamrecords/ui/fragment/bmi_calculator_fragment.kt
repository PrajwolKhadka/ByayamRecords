package com.example.byayamrecords.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.byayamrecords.R
import com.example.byayamrecords.databinding.FragmentBmiCalculatorFragmentBinding

class bmi_calculator_fragment : Fragment() {

lateinit var binding: FragmentBmiCalculatorFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bmi_calculator_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.test.setOnClickListener {
////            val intent = Intent(requireContext())
////            Toast.makeText(requireContext())
//        }
    }
}