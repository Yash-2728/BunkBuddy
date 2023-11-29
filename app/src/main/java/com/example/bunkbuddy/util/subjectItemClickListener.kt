package com.example.bunkbuddy.util

import com.example.bunkbuddy.datamodel.Subject

interface subjectItemClickListener {

    fun onIncreaseAttendenceBtnClicked(subject: Subject)
    fun onDecreaseAttendenceBtnClicked(subject: Subject)

    fun onIncreaseMissedBtnClicked(subject: Subject)
    fun onDecreaseMissedBtnClicked(subject: Subject)
}