package com.example.onboarding.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.PrimaryKey
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "custom_masters")
data class CustomMasterEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String, // "role", "language", "genre", "instrument", "badge"
    val itemKey: String,
    val displayLabel: String,
    val extraData: String = "", // For extra properties like emoji, colorHex, letter, description, etc.
    val sortOrder: Int = 0
)

@Dao
interface CustomMasterDao {
    @Query("SELECT * FROM custom_masters ORDER BY category ASC, sortOrder ASC, displayLabel ASC")
    fun getAllCustomMastersFlow(): Flow<List<CustomMasterEntity>>

    @Query("SELECT * FROM custom_masters ORDER BY category ASC, sortOrder ASC, displayLabel ASC")
    suspend fun getAllCustomMasters(): List<CustomMasterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomMaster(entity: CustomMasterEntity)

    @Query("DELETE FROM custom_masters WHERE id = :id")
    suspend fun deleteCustomMasterById(id: Int)

    @Query("DELETE FROM custom_masters WHERE category = :category AND displayLabel = :displayLabel")
    suspend fun deleteCustomMaster(category: String, displayLabel: String)

    @Query("DELETE FROM custom_masters WHERE category = :category")
    suspend fun deleteByCategory(category: String)

    @Query("DELETE FROM custom_masters")
    suspend fun clearAll()
}
