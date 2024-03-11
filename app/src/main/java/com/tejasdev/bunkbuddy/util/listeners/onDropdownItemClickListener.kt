package com.tejasdev.bunkbuddy.util.listeners

import com.tejasdev.bunkbuddy.datamodel.Subject

interface onDropdownItemClickListener {
    fun onSubjectNameClicked(subject: Subject)
}