package com.amir.datepickerterminroom.room

import androidx.room.*

import kotlinx.coroutines.flow.Flow


@Dao
interface DaoTerm {

    @Insert
    suspend fun insert(entityTerm: EntityTerm)

    @Update
    suspend fun update(entityTerm: EntityTerm)

    @Delete
    suspend fun delete(entityTerm: EntityTerm)

    @Query("select * from `term-table`")
    fun fetchAllTerms(): Flow<MutableList<EntityTerm>>

    @Query("select * from `term-table` where id=:id")
    fun fetchTermById(id: Int): Flow<EntityTerm>
}