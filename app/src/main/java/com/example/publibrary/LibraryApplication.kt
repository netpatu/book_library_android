package com.example.publibrary

import android.app.Application
import android.content.Context
import com.example.publibrary.net.BookApiImplementation


class LibraryApplication : Application() {

    companion object {
        var context: Context? = null
        var libraryServiceClient: BookApiImplementation? = null
    }

    override fun onCreate() {
        super.onCreate()
        onIwalletCreate(this)
    }

    fun onIwalletCreate(applicationContext: Application) {
        context = applicationContext
        libraryServiceClient = BookApiImplementation()
    }
}