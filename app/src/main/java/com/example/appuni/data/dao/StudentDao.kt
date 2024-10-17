package com.example.appuni.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.appuni.data.entities.StudentEntity
@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Query("SELECT * FROM student WHERE code = :code AND password = :password LIMIT 1")
    suspend fun getStudentByCredentials(code: String, password: String): StudentEntity?

    @Query("SELECT * FROM student")
    suspend fun getAllStudents(): List<StudentEntity>
}