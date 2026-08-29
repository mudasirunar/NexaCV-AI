package com.mudasir.nexacvai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mudasir.nexacvai.data.local.entity.FavoriteTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTemplateDao {

    @Query("SELECT templateId FROM favorite_templates ORDER BY createdAt DESC")
    fun getFavoriteTemplateIds(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_templates WHERE templateId = :templateId)")
    suspend fun isFavorite(templateId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(entity: FavoriteTemplateEntity)

    @Query("DELETE FROM favorite_templates WHERE templateId = :templateId")
    suspend fun removeFavorite(templateId: String)

    @Query("DELETE FROM favorite_templates")
    suspend fun clearAllFavorites()
}
