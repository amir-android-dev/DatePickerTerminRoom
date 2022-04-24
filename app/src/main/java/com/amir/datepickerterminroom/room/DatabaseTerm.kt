package com.amir.datepickerterminroom.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EntityTerm::class], version = 1)
abstract class DatabaseTerm : RoomDatabase() {

    abstract fun daoTerm(): DaoTerm

    companion object {

        @Volatile
        private var INSTANCE: DatabaseTerm? = null
        
        fun getInstance(context: Context):DatabaseTerm{

            synchronized(this){
                var instance = INSTANCE
                if(instance==null){
                    instance= Room.databaseBuilder(
                        context.applicationContext,
                        DatabaseTerm::class.java,
                        "term_database"
                    ).fallbackToDestructiveMigration().build()

                    INSTANCE=instance
                }
                return instance
            }
        }
    }
}