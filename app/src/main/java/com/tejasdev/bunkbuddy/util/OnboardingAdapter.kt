package com.tejasdev.bunkbuddy.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.datamodel.OnboardingItem

class OnboardingAdapter(private val context: Context, private val onBoardingItems: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        return OnboardingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.onboarding_item_view, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.setOnBoardingData(onBoardingItems[position])
    }

    override fun getItemCount(): Int {
        return onBoardingItems.size
    }

    inner class OnboardingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        private val textDescription: TextView = itemView.findViewById(R.id.textDescription)
        private val imageOnboarding: ImageView = itemView.findViewById(R.id.image_onboarding)

        fun setOnBoardingData(onBoardingItem: OnboardingItem) {
            textTitle.text = onBoardingItem.title
            textDescription.text = onBoardingItem.description

            Glide.with(context).asGif().load(onBoardingItem.image).into(imageOnboarding)
        }
    }
}