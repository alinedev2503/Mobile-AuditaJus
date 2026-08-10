package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {
    @Query("SELECT * FROM cases ORDER BY createdAt DESC")
    fun getAllCases(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM cases WHERE id = :id")
    fun getCaseById(id: Long): Flow<CaseEntity?>

    @Query("SELECT * FROM cases WHERE id = :id")
    suspend fun getCaseByIdDirect(id: Long): CaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(caseEntity: CaseEntity): Long

    @Update
    suspend fun updateCase(caseEntity: CaseEntity)

    @Delete
    suspend fun deleteCase(caseEntity: CaseEntity)
}

@Dao
interface EvidencePhotoDao {
    @Query("SELECT * FROM evidence_photos WHERE caseId = :caseId ORDER BY timestamp ASC")
    fun getPhotosForCase(caseId: Long): Flow<List<EvidencePhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: EvidencePhotoEntity): Long

    @Delete
    suspend fun deletePhoto(photo: EvidencePhotoEntity)
}

@Dao
interface HearingDeadlineDao {
    @Query("SELECT * FROM hearings_deadlines ORDER BY timestamp ASC")
    fun getAllHearingsDeadlines(): Flow<List<HearingDeadlineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HearingDeadlineEntity): Long

    @Update
    suspend fun update(item: HearingDeadlineEntity)

    @Delete
    suspend fun delete(item: HearingDeadlineEntity)
}

@Dao
interface PetitionTemplateDao {
    @Query("SELECT * FROM petition_templates")
    fun getAllTemplates(): Flow<List<PetitionTemplateEntity>>

    @Query("SELECT * FROM petition_templates WHERE category = :category")
    fun getTemplatesByCategory(category: String): Flow<List<PetitionTemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(templates: List<PetitionTemplateEntity>)
}
