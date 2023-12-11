package com.tejasdev.bunkbuddy.util

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
        return when(position){
            0 -> TimetableContentFragment(viewModel.monday, viewModel)
            1 -> TimetableContentFragment(viewModel.tuesday, viewModel)
            2 -> TimetableContentFragment(viewModel.wednesday, viewModel)
            3 -> TimetableContentFragment(viewModel.thursday, viewModel)
            4 -> TimetableContentFragment(viewModel.friday, viewModel)
            5 -> TimetableContentFragment(viewModel.saturday, viewModel)
            else -> TimetableContentFragment(viewModel.sunday, viewModel)
        }
    }
}