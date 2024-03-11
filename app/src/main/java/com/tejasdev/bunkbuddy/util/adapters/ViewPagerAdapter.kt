package com.tejasdev.bunkbuddy.util.adapters

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tejasdev.bunkbuddy.UI.SubjectViewModel
import com.tejasdev.bunkbuddy.fragments.TimetableContentFragment

class ViewPagerAdapter(fa: FragmentActivity, private val viewModel: SubjectViewModel): FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return 7
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = TimetableContentFragment()
        val args = Bundle()
        args.putInt("dayNumber", position)
        fragment.arguments = args
        return fragment
    }
}