package com.akribase.timelineviewapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.akribase.timelineviewapp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.pager.adapter = object: FragmentStateAdapter(this) {
            override fun getItemCount() = 3
            override fun createFragment(position: Int) = TimelineDayFragment.instantiate(position)
        }

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = "Day ${(position + 1)}"
        }.attach()

    }

}