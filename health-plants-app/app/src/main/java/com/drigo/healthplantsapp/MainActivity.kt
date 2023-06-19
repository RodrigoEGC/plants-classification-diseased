package com.drigo.healthplantsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.drigo.healthplantsapp.databinding.ActivityMainBinding
import com.drigo.healthplantsapp.view.ui.AnalyzeFragment
import com.drigo.healthplantsapp.view.ui.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.executePendingBindings()

        supportFragmentManager.beginTransaction().replace(R.id.container, HomeFragment()).commit()

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.dashboard_page -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.analyze_page -> {
                    loadFragment(AnalyzeFragment())
                    true
                }
                else -> {
                    true
                }
            }
        }
    }

    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }
}