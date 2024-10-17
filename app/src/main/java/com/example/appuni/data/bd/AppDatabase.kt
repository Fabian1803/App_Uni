package com.example.appuni.data.bd

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.appuni.data.dao.StudentDao
import com.example.appuni.data.entities.StudentEntity
import android.content.Context
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(entities = [StudentEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                //insertDefaultData(instance)
                INSTANCE = instance
                instance
            }
        }
        private fun insertDefaultData(db: AppDatabase) {
            GlobalScope.launch {
                val studentDao = db.studentDao()

                // Inserta tus datos predeterminados
                studentDao.insertStudent(StudentEntity(
                    firstName = "Fabian",
                    lastName = "Rivera Morales",
                    email = "u20227896@usc.edu.pe",
                    code = "u20227896",
                    career = "Ingieneria de software",
                    password = "123456"
                ))

                // Puedes agregar más estudiantes si lo deseas
                studentDao.insertStudent(StudentEntity(
                    firstName = "Jose",
                    lastName = "Lopez nose",
                    email = "u12457896@ejemplo.edu.pe",
                    code = "u12457896",
                    career = "Medicina",
                    password = "asd"
                ))
            }
        }
    }
}