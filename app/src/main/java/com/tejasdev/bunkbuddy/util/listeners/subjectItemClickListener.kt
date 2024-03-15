package com.tejasdev.bunkbuddy.util.listeners

import com.tejasdev.bunkbuddy.datamodel.Subject

interface SubjectItemClickListener {
    fun onIncreaseAttendanceBtnClicked(subject: Subject)
    fun onDecreaseAttendanceBtnClicked(subject: Subject)

    fun onIncreaseMissedBtnClicked(subject: Subject)
    fun onDecreaseMissedBtnClicked(subject: Subject)

    fun onDeleteBtnClicked(pos: Int, subject: Subject)
    fun onEditOptionSelected(subject: Subject)
}