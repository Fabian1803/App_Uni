package com.example.appuni.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val idStudent: Long = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
    val code: String,
    val career: String,
    val password: String
)