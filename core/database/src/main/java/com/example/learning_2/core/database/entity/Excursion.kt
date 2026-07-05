package com.example.learning_2.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Vacation::class,
        parentColumns = ["id"], // Reference the primary key in the Vacation entity
        childColumns = ["vacation_id"], // Reference the vacationId in the Excursion entity
        onDelete = ForeignKey.CASCADE // Automatically delete excursions when a vacation is deleted
    )],
    indices = [Index(value = ["vacation_id"])] // Index to improve query performance
)
data class Excursion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "vacation_id") var vacationId: Long // Foreign key linking to Vacation
)
