package com.tejasdev.bunkbuddy.room


import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.tejasdev.bunkbuddy.datamodel.Lecture
import com.tejasdev.bunkbuddy.datamodel.Subject
import com.tejasdev.bunkbuddy.getOrAwaitValue
import com.tejasdev.bunkbuddy.room.dao.SubjectDao
import com.tejasdev.bunkbuddy.room.db.SubjectDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class SubjectDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: SubjectDatabase
    private lateinit var dao: SubjectDao

    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                SubjectDatabase::class.java
            )
            .allowMainThreadQueries()
            .build()
        dao = database.getDao()
    }
    @After
    fun destroyEverything(){
        database.close()
    }


    @Test
    fun insertShoppingItem() = runBlocking {
        val subject = Subject(
            "test_subject",
            1,
             0,
            1,
            "now",
            75
        )
        dao.addSubject(subject)
        val allShoppingItems = dao.getAllSubjects().getOrAwaitValue()
        Log.w("testing-dao", allShoppingItems.toString())
        assertThat(allShoppingItems).contains(subject)
    }

    @Test
    fun deleteShoppingItem() = runBlocking {
        val subject = Subject(
            "test_subject",
            1,
            0,
            1,
            "now",
            75
        )
        dao.addSubject(subject)
        dao.deleteSubject(subject)
        val allItems = dao.getAllSubjects().getOrAwaitValue()
        assertThat(allItems).doesNotContain(subject)
    }

    @Test
    fun addLecture() = runBlocking{
        val subject = Subject("name", 1, 1, 1, "now", 1)
        dao.addSubject(subject)
        val lecture = Lecture(0, subject, "", "", 1, subject.id)
        dao.addLecture(lecture)
        val allLectures = dao.getAllLectures()
        assertThat(allLectures).contains(lecture)
    }

    @Test
    fun deleteLecture() = runBlocking {
        val subject = Subject("name", 1, 1, 1, "now", 1)
        dao.addSubject(subject)
        val lecture = Lecture(0, subject, "", "", 1, subject.id)
        dao.addLecture(lecture)
        dao.deleteLecture(lecture)
        val allLectures = dao.getAllLectures()
        assertThat(allLectures).doesNotContain(lecture)
    }

    @Test
    fun updateSubjectAttendance() = runBlocking {
        val subject = Subject("name", 1, 1, 1, "now", 1)
        dao.addSubject(subject)
        subject.attended++
        dao.updateSubject(subject)
        val updatedSubject = dao.getSubject(1)
        assertThat(updatedSubject).isEqualTo(subject)
    }

    @Test
    fun updateLectureOnUpdateSubject() = runBlocking {
        val subject = Subject("name", 1, 1, 1, "now", 1)
        dao.addSubject(subject)
        val lecture = Lecture(0, subject, "", "", 1, subject.id)
        dao.addLecture(lecture)
        subject.attended++
        dao.updateSubjectAndRelatedLectures(subject)
        val allLectures = dao.getLecturesForSubject(subjectId =  1)

        Log.w("testing-room", "o: $subject")
        for(updatedLecture in allLectures){
            Log.w("testing-room", "u: $updatedLecture")
            assertThat(updatedLecture.subject).isEqualTo(subject)
        }
    }

}