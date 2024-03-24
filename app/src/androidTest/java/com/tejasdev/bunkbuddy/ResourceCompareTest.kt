package com.tejasdev.bunkbuddy

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before

class ResourceCompareTest {

    private lateinit var comparer: ResourceComparer

    @Before
    fun setup(){
        comparer = ResourceComparer()
    }

    @After
    fun teardown(){
        
    }

    @Test
    fun stringResourceSameAsGivenString_returnsTrue(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = comparer.isEqual(context, R.string.app_name, "BunkBuddy")
        assertThat(result).isTrue()
    }
    @Test
    fun stringResourceDifferentAsGivenString_returnsTrue(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = comparer.isEqual(context, R.string.app_name, "BunkBuddy")
        assertThat(result).isFalse()
    }
}