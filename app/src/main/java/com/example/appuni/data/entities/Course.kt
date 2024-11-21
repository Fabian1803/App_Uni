package com.example.appuni.data.entities

data class Course(
    val id: Long,
    val courseCode: String,
    val courseName: String,
    val credits: Int,
    val teacher: String
)