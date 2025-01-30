package com.example.byayamrecords.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.byayamrecords.R
import com.example.byayamrecords.databinding.ActivityNavigationBinding
import com.example.byayamrecords.ui.fragment.bmi_calculator_fragment
import com.example.byayamrecords.ui.fragment.profile_fragment
import com.example.byayamrecords.ui.fragment.workout_log_fragment

class NavigationActivity : AppCompatActivity() {
    lateinit var navigationBinding: ActivityNavigationBinding

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        navigationBinding = ActivityNavigationBinding.inflate(layoutInflater)

        setContentView(navigationBinding.root)

        replaceFragment(bmi_calculator_fragment())
        navigationBinding.buttomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_bmi_calculator -> replaceFragment(bmi_calculator_fragment())
                R.id.nav_workout_log -> replaceFragment(workout_log_fragment())
                R.id.nav_profile -> replaceFragment(profile_fragment())
                else -> {}
            }
            true
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }
}