package com.example.learning_2.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Vacation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "hotel") var hotel: String,
    @ColumnInfo(name = "start_date") var startDate: String,
    @ColumnInfo(name = "end_date") var endDate: String
)