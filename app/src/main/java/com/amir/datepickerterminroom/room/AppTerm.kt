package com.amir.datepickerterminroom.room

import android.app.Application

class AppTerm : Application() {
    val db by lazy {
        DatabaseTerm.getInstance(this)
    }
}