package com.tejasdev.bunkbuddy

import android.content.Context
import org.junit.Test

class ResourceComparer {

    fun isEqual(
        context: Context,
        resId: Int,
        string: String
    ): Boolean{
        return context.getString(resId) == string
    }

    @Test
    fun ResourceCompareTest() {

    }
}