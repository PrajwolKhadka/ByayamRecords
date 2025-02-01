package com.example.byayamrecords.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.byayamrecords.R
import com.example.byayamrecords.adapter.WorkoutLogAdapter
import com.example.byayamrecords.databinding.FragmentWorkoutLogFragmentBinding
import com.example.byayamrecords.repository.WorkoutLofRepoImpl
import com.example.byayamrecords.ui.activity.addLogActivity
import com.example.byayamrecords.viewmodel.WorkoutLogViewModel

class workout_log_fragment : Fragment() {

    private var _binding: FragmentWorkoutLogFragmentBinding? = null
    private val binding get() = _binding!!  // Use this to prevent memory leaks

    private lateinit var productViewModel: WorkoutLogViewModel
    private lateinit var adapter: WorkoutLogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutLogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WorkoutLogAdapter(requireContext(), ArrayList())
        val repo = WorkoutLofRepoImpl()
        productViewModel = WorkoutLogViewModel(repo)

        // Set up RecyclerView
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())

        // Observe Workout Logs
        productViewModel.getAllLog()
        productViewModel.allProducts.observe(viewLifecycleOwner) { logs ->
            logs?.let { adapter.updateData(it) }
        }

        // Observe Loading State
        productViewModel.loadingState.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        // Swipe to delete functionality
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val logId = adapter.getProductId(viewHolder.adapterPosition)

                productViewModel.deleteLog(logId) { success, message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }).attachToRecyclerView(binding.recycler)

        // Floating Action Button to Add New Log
        binding.floatingActionButton2.setOnClickListener {
            val intent = Intent(requireContext(), addLogActivity::class.java)
            startActivity(intent)
        }

        // Adjust UI for insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Prevent memory leaks
    }
}
