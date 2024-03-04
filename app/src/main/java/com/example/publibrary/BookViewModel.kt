package com.example.publibrary

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.publibrary.data.DataBook
import com.example.publibrary.data.DataMsg
import com.ipocket.wallet.api.Respbody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response

data class BookState(
    val dataItems: ArrayList<DataBook> = ArrayList(),
    var dataItem: DataBook = DataBook(),
    var addBookStatus: Int = 0,
    var modifyBookStatus: Int = 0,
    var deleteBookStatus: Int = 0
)

class BookViewModel(application: Application) : AndroidViewModel(application = application) {
    var uiState: MutableLiveData<BookState>? = null

    init {
        uiState = MutableLiveData<BookState>()
        uiState!!.value = BookState()
//        uiState?.postValue(BookState())
    }

    fun initData() {
        Log.d("BookViewModel", "initData")
        CoroutineScope(Dispatchers.Main).launch {
            val value = async(Dispatchers.IO) {
                fun1RetrieveBooks()
            }

            val booksData: List<DataBook> = value.await()

            if (booksData.isNotEmpty()) {
                val state = uiState!!.value
                state!!.dataItems.clear()
                for (item in booksData) {
                    state.dataItems.add(
                        item
                    )
                }
                uiState!!.value = state
            } else {
                val state = uiState!!.value
                state!!.dataItems.clear()
                uiState!!.value = state
            }

        }
    }

    fun getBookData(bookId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val value = async(Dispatchers.IO) {
                fun2RetrieveBookByID(bookId)
            }

            val booksData: DataBook = value.await()

            if (booksData.bookTitle != null && booksData.bookTitle.isNotEmpty()) {
                val state = uiState!!.value
                state!!.dataItem = booksData
                state!!.dataItems.add(booksData)
                uiState!!.value = state
            } else {
                val state = uiState!!.value
                state!!.dataItem = DataBook()
                state!!.dataItems.add(DataBook())
                uiState!!.value = state
            }

        }
    }

    fun addBookData(book: DataBook) {
        CoroutineScope(Dispatchers.Main).launch {
            val value = async(Dispatchers.IO) {
                fun2AddBook(book)
            }

            val status: Int = value.await()

            if (status > 0) {
                val state = uiState!!.value
                state!!.addBookStatus = status
                uiState!!.value = state
            } else {
                val state = uiState!!.value
                state!!.addBookStatus = 0
                uiState!!.value = state
            }

        }
    }

    fun modifyBookData(book: DataBook) {
        CoroutineScope(Dispatchers.Main).launch {
            val value = async(Dispatchers.IO) {
                fun3UpdateBook(book)
            }

            val status: Int = value.await()

            if (status > 0) {
                val state = uiState!!.value
                state!!.modifyBookStatus = status
                uiState!!.value = state
            } else {
                val state = uiState!!.value
                state!!.modifyBookStatus = 0
                uiState!!.value = state
            }

        }
    }

    fun deleteBookData(book: DataBook) {
        CoroutineScope(Dispatchers.Main).launch {
            val value = async(Dispatchers.IO) {
                fun4DeleteBook(book)
            }

            val status: Int = value.await()

            if (status > 0) {
                val state = uiState!!.value
                state!!.deleteBookStatus = status
                uiState!!.value = state
            } else {
                val state = uiState!!.value
                state!!.deleteBookStatus = 0
                uiState!!.value = state
            }

        }
    }

    private suspend fun fun1RetrieveBooks(): List<DataBook> {
        Log.d("BookViewModel", "fun1RetrieveBooks")
        delay(100 * 20)

        try {
            val data: Response<Respbody<List<DataBook>>> =
                LibraryApplication.libraryServiceClient!!.getBookService()!!
                    .books()
                    .execute()

            val body: Respbody<List<DataBook>> = data.body()!!
            Log.d("BookViewModel", "${body.toString()}")

            return body.data!!
        } catch (ex: Exception) {
            Log.d("BookViewModel", "exception:${ex.message}")
        }

        return ArrayList()
    }

    private suspend fun fun2RetrieveBookByID(Id: Int): DataBook {
        delay(100 * 20)

        try {
            val data: Response<Respbody<DataBook>> =
                LibraryApplication.libraryServiceClient!!.getBookService()!!
                    .books(bookID = Id)
                    .execute()

            val body: Respbody<DataBook> = data.body()!!
            Log.d("BookViewModel", "${body.toString()}")

            return body.data!!
        } catch (ex: Exception) {
            Log.d("BookViewModel", "exception:${ex.message}")
        }

        return DataBook(bookId = 0, bookTitle = "", author = "", publicationDate = "", isbn = "")
    }

    private suspend fun fun2AddBook(book: DataBook): Int {
        delay(100 * 20)

        try {
            val data: Response<Respbody<DataMsg>> =
                LibraryApplication.libraryServiceClient!!.getBookService()!!
                    .addbook(book)
                    .execute()

            val body: Respbody<DataMsg> = data.body()!!
            Log.d("BookViewModel", "$body")

            return body.status!!
        } catch (ex: Exception) {
            Log.d("BookViewModel", "exception:${ex.message}")
        }

        return 0
    }

    private suspend fun fun3UpdateBook(book: DataBook): Int {
        delay(100 * 20)

        try {
            val data: Response<Respbody<DataMsg>> =
                LibraryApplication.libraryServiceClient!!.getBookService()!!
                    .updateBook(book)
                    .execute()

            val body: Respbody<DataMsg> = data.body()!!
            Log.d("BookViewModel", "${body.toString()}")

            return body.status!!
        } catch (ex: Exception) {
            Log.d("BookViewModel", "exception:${ex.message}")
        }

        return 0
    }

    private suspend fun fun4DeleteBook(book: DataBook): Int {
        delay(100 * 20)

        try {
            val data: Response<Respbody<DataMsg>> =
                LibraryApplication.libraryServiceClient!!.getBookService()!!
                    .deleteBook(book)
                    .execute()

            val body: Respbody<DataMsg> = data.body()!!
            Log.d("BookViewModel", "${body.toString()}")

            return body.status!!
        } catch (ex: Exception) {
            Log.d("BookViewModel", "exception:${ex.message}")
        }

        return 0
    }
}