package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CaseEntity::class,
        EvidencePhotoEntity::class,
        HearingDeadlineEntity::class,
        PetitionTemplateEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun caseDao(): CaseDao
    abstract fun evidencePhotoDao(): EvidencePhotoDao
    abstract fun hearingDeadlineDao(): HearingDeadlineDao
    abstract fun petitionTemplateDao(): PetitionTemplateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "contador_juridico_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
