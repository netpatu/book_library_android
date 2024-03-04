package com.example.publibrary

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.publibrary.data.DataBook
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.publibrary", appContext.packageName)
    }

    @Test
    fun testGetBooks() {
        val appContext =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        var bookVm: BookViewModel? = null

        appContext.mainExecutor.execute {
            bookVm = BookViewModel(appContext as Application)
            assertNotEquals(null, bookVm)
            assertNotEquals(null, bookVm?.uiState)
            assertNotEquals(null, bookVm?.uiState?.value)
            assertEquals(0, bookVm?.uiState?.value?.deleteBookStatus)
        }

        Thread.sleep(1000L)

        bookVm?.initData()

        Thread.sleep(1000L * 60 * 3)

        assertNotEquals(0, bookVm?.uiState?.value?.dataItems?.size)
    }

    fun testGetBook() {
        val appContext =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        var bookVm: BookViewModel? = null

        appContext.mainExecutor.execute {
            bookVm = BookViewModel(appContext as Application)
            assertNotEquals(null, bookVm)
            assertNotEquals(null, bookVm?.uiState)
            assertNotEquals(null, bookVm?.uiState?.value)
            assertEquals(0, bookVm?.uiState?.value?.deleteBookStatus)
        }

        Thread.sleep(1000L)

        bookVm?.getBookData(-1685574454)

        Thread.sleep(1000L * 60 * 3)

        assertNotEquals(0, bookVm?.uiState?.value?.dataItem?.bookId)
    }

    fun testAddBook() {
        val appContext =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        var bookVm: BookViewModel? = null

        appContext.mainExecutor.execute {
            bookVm = BookViewModel(appContext as Application)
            assertNotEquals(null, bookVm)
            assertNotEquals(null, bookVm?.uiState)
            assertNotEquals(null, bookVm?.uiState?.value)
            assertEquals(0, bookVm?.uiState?.value?.deleteBookStatus)
        }

        Thread.sleep(1000L)

        val book = DataBook(bookTitle = "", author = "", publicationDate = "", isbn = "")
        bookVm?.addBookData(book)

        Thread.sleep(1000L * 60 * 3)

        assertNotEquals(0, bookVm?.uiState?.value?.dataItems?.size)
    }

    fun testUpdate() {
        val appContext =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        var bookVm: BookViewModel? = null

        appContext.mainExecutor.execute {
            bookVm = BookViewModel(appContext as Application)
            assertNotEquals(null, bookVm)
            assertNotEquals(null, bookVm?.uiState)
            assertNotEquals(null, bookVm?.uiState?.value)
            assertEquals(0, bookVm?.uiState?.value?.deleteBookStatus)
        }

        Thread.sleep(1000L)

        val book = DataBook(
            bookId = -1685574454,
            bookTitle = "Kotlin Program By Example",
            author = "lyanu Adelekan",
            publicationDate = "2017",
            isbn = "ISBN 978-1-79947-454-2"
        )
        bookVm?.modifyBookData(book)

        Thread.sleep(1000L * 60 * 3)

        assertNotEquals(0, bookVm?.uiState?.value?.dataItems?.size)
    }
}