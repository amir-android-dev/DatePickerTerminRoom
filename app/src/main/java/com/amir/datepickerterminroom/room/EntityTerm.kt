package com.amir.datepickerterminroom.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "term-table")
data class EntityTerm(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val term: String = "",
    val date: String = ""
)