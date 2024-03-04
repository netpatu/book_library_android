package com.example.publibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    lateinit var bookFrgmnt: BooksFragment

    val handler: Handler = Handler(Looper.getMainLooper()) {
        bookFrgmnt.getBooksData()
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main)

        bookFrgmnt = BooksFragment.instance
        supportFragmentManager.beginTransaction()
            .add(R.id.container, bookFrgmnt)
            .commitNow()
    }
}