package com.example.byayamrecords.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.byayamrecords.R
import com.example.byayamrecords.databinding.FragmentWorkoutLogFragmentBinding

class workout_log_fragment : Fragment() {

    lateinit var binding: FragmentWorkoutLogFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workout_log_fragment, container, false)
    }


}