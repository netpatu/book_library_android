package com.example.publibrary.utils

import com.example.publibrary.LibraryApplication
import java.io.BufferedReader
import java.io.InputStreamReader


object FileUtil {
    fun getFromAssets(fileName: String?): String? {
        try {
            val inputReader = InputStreamReader(
                LibraryApplication.context!!.getResources().getAssets().open(fileName!!)
            )
            val bufReader = BufferedReader(inputReader)
            var line: String?
            var Result: String? = ""
            while (bufReader.readLine().also { line = it } != null) Result += line
            return Result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}