package com.mudasir.nexacvai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mudasir.nexacvai.data.local.entity.CVGenerationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CVGenerationDao {

    @Query("SELECT * FROM cv_generations WHERE profileId = :profileId ORDER BY createdAt DESC")
    fun getCVGenerationsForProfile(profileId: Long): Flow<List<CVGenerationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCVGeneration(cvGeneration: CVGenerationEntity): Long

    @Update
    suspend fun updateCVGeneration(cvGeneration: CVGenerationEntity)

    @Delete
    suspend fun deleteCVGeneration(cvGeneration: CVGenerationEntity)
}
