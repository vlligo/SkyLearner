package com.example.astroguessr.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Star::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun starDao(): StarDao

    companion object {
        private const val DB_NAME = "stars.db"

        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DB_NAME
            ).createFromAsset("databases/$DB_NAME")
                .fallbackToDestructiveMigration(false)
                .build()
        }
    }
}