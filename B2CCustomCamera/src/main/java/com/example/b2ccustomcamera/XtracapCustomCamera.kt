package com.example.b2ccustomcamera

import android.content.Context
import android.content.Intent

class XtracapCustomCamera private constructor(private val context: Context) {



    private var scanType: String? = null

    fun setScanType(scanType: String): XtracapCustomCamera {
        this.scanType = scanType
        return this
    }

    fun start() {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.SCAN_TYPE_KEY, scanType)
        }
        context.startActivity(intent)
    }

    companion object {
        fun with(context: Context): XtracapCustomCamera {
            return XtracapCustomCamera(context)
        }
    }
}


