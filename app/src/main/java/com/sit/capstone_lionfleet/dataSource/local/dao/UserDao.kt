package com.sit.capstone_lionfleet.dataSource.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sit.capstone_lionfleet.dataSource.local.model.CURRENT_USER_ID
import com.sit.capstone_lionfleet.dataSource.local.model.UserCacheEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userCacheEntity: UserCacheEntity): Long

    @Query("SELECT * FROM users WHERE uid = $CURRENT_USER_ID")
    suspend fun getUser(): UserCacheEntity
}