package com.tejasdev.bunkbuddy.util.listeners

import com.tejasdev.bunkbuddy.datamodel.Subject

interface subjectItemClickListener {

    fun onIncreaseAttendenceBtnClicked(subject: Subject)
    fun onDecreaseAttendenceBtnClicked(subject: Subject)

    fun onIncreaseMissedBtnClicked(subject: Subject)
    fun onDecreaseMissedBtnClicked(subject: Subject)

    fun onDeleteBtnClicked(subject: Subject)
}