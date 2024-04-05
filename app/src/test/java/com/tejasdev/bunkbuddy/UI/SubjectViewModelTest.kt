package com.tejasdev.bunkbuddy.UI

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.tejasdev.bunkbuddy.datamodel.Lecture
import com.tejasdev.bunkbuddy.datamodel.Subject
import com.tejasdev.bunkbuddy.getOrAwaitValue
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
@SmallTest
class SubjectViewModelTest{

    /**
     *  Don't run the tests, they keep failing.
     */

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    private lateinit var viewModel: SubjectViewModel
    private lateinit var allLectures: List<Lecture>
    private lateinit var allSubjects: List<Subject>
    @Before
    @Test
    fun setUp(){
        val repo = MockSubjectRepository()
        viewModel = SubjectViewModel(repo)
        addMockLectures()
        allLectures = viewModel.getAllLecturesSync()
        allSubjects = viewModel.getAllSubjectSync()
        assertThat(allLectures.size).isNotEqualTo(0)
    }

    private fun addMockLectures(){
        val subject = Subject(name = "subject", missed = 10, attended = 10, requirement = 44, id = 0, lastUpdated = "")
        for(dayNumber in 0..6){
            viewModel.addLecture(
                Lecture(
                    pid = 0,
                    dayNumber = dayNumber,
                    subject = subject,
                    subjectId = subject.id,
                    startTime = "",
                    endTime = ""
                )
            )
        }
    }


    @Test
    fun `any missing monday lecture, returns error`(){
        val mondayLectures = viewModel.monday.getOrAwaitValue()
        val mondayLecturesSync = mutableListOf<Lecture>()
        assertThat(allLectures.size).isNotEqualTo(0)
        for(lecture in allLectures) if(lecture.dayNumber==0) mondayLecturesSync.add(lecture)
        assertThat(mondayLectures.size).isNotEqualTo(0)
        assertThat(mondayLectures).isEqualTo(mondayLecturesSync)
    }

    @Test
    fun `any missing tuesday lecture, returns error`(){
        val tuesdayLectures = viewModel.tuesday.getOrAwaitValue()
        val tuesdayLecturesSync = mutableListOf<Lecture>()
//        for(lecture in )
        assertThat(tuesdayLectures).isEqualTo(tuesdayLecturesSync)
    }
    @Test
    fun `any missing wednesday lecture, returns error`(){
        val wednesdayLectures = viewModel.wednesday.getOrAwaitValue()
        val wednesdayLecturesSync = allLectures.filter {
            it.dayNumber == 2
        }
        assertThat(wednesdayLectures).isEqualTo(wednesdayLecturesSync)
    }

    @Test
    fun `any missing thursday lecture, returns error`(){
        val thursdayLectures = viewModel.thursday.getOrAwaitValue()
        val thursdayLecturesSync = allLectures.filter {
            it.dayNumber == 3
        }
        assertThat(thursdayLectures).isEqualTo(thursdayLecturesSync)
    }

    @Test
    fun `any missing friday lecture, returns error`(){
        val fridayLectures = viewModel.friday.getOrAwaitValue()
        val fridayLecturesSync = allLectures.filter {
            it.dayNumber == 4
        }
        assertThat(fridayLectures).isEqualTo(fridayLecturesSync)
    }

    @Test
    fun `any missing saturday lecture, returns error`(){
        val saturdayLectures = viewModel.saturday.getOrAwaitValue()
        val saturdayLecturesSync = allLectures.filter {
            it.dayNumber == 5
        }
        assertThat(saturdayLectures).isEqualTo(saturdayLecturesSync)
    }

    @Test
    fun `any missing sunday lecture, returns error`(){
        val sundayLectures = viewModel.sunday.getOrAwaitValue()
        val sundayLecturesSync = allLectures.filter {
            it.dayNumber == 6
        }
        assertThat(sundayLectures).isEqualTo(sundayLecturesSync)
    }
}