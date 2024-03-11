package com.tejasdev.bunkbuddy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.databinding.ActivityOnboardingBinding
import com.tejasdev.bunkbuddy.datamodel.OnboardingItem
import com.tejasdev.bunkbuddy.util.adapters.OnboardingAdapter

class OnboardingActivity : AppCompatActivity() {
    private lateinit var onboardingAdapter: OnboardingAdapter

    private var _binding: ActivityOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setOnboardingItem()
        binding.viewPager.adapter = onboardingAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager){_, _ -> }.attach()

        binding.actionBtn.setOnClickListener {
            if(binding.viewPager.currentItem + 1 < onboardingAdapter.itemCount){
                binding.viewPager.currentItem = binding.viewPager.currentItem + 1
            }
            else{
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                binding.actionBtn.text = when(position){
                    0-> "Next"
                    1->"Next"
                    else -> "Get started"
                }
            }
        })

    }

    private fun setOnboardingItem(){
        val onBoardingItems = ArrayList<OnboardingItem>()

        //create the onBoardingItems and pass into the adapter

        val item1 = OnboardingItem(
            R.drawable.bunkbuddy_hold,
            "Hold and Drag",
            "Hold and drag your subjects/lectures to change their position"
        )

        val item2 = OnboardingItem(
            R.drawable.bunkbuddy_swipe,
            "Swipe to Delete",
            "Swipe left to delete your lectures/subjects"
        )

        val item3 = OnboardingItem(
            R.drawable.bunkbuddy_alerts,
            "Alerts",
            "Turn on alerts to stay informed about upcoming lectures where you may be at risk of not meeting attendance requirements."
        )

        onBoardingItems.add(item1)
        onBoardingItems.add(item2)
        onBoardingItems.add(item3)

        onboardingAdapter = OnboardingAdapter(this, onBoardingItems)
    }
}