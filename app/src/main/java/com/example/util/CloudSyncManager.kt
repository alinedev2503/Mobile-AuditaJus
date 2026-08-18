package com.example.util

import android.content.Context
import android.util.Log
import com.example.data.db.CaseEntity
import com.example.data.db.EvidencePhotoEntity
import com.example.data.db.HearingDeadlineEntity
import com.example.data.db.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CloudSyncManager(private val context: Context, private val database: AppDatabase) {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    // Check if the user is authenticated via Google (or any other provider)
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    
    fun getUserId(): String? {
        return auth.currentUser?.uid
    }
    
    /**
     * Backs up a specific case to Firestore.
     */
    suspend fun syncCaseToCloud(caseEntity: CaseEntity) {
        val uid = getUserId() ?: return
        
        try {
            val caseMap = hashMapOf(
                "id" to caseEntity.id,
                "title" to caseEntity.title,
                "category" to caseEntity.category,
                "date" to caseEntity.date,
                "description" to caseEntity.description,
                "status" to caseEntity.status,
                "subtotalUpdated" to caseEntity.subtotalUpdated,
                "suggestedMoralDamages" to caseEntity.suggestedMoralDamages,
                "processNumber" to caseEntity.processNumber,
                "fatosText" to caseEntity.fatosText,
                "fundamentosText" to caseEntity.fundamentosText,
                "pedidosText" to caseEntity.pedidosText,
                "authorName" to caseEntity.authorName
            )
            
            firestore.collection("users").document(uid)
                .collection("cases").document(caseEntity.id.toString())
                .set(caseMap).await()
                
            Log.d("CloudSyncManager", "Case synced successfully to Firestore: ${caseEntity.id}")
        } catch (e: Exception) {
            Log.e("CloudSyncManager", "Error syncing case to Firestore", e)
        }
    }
    
    /**
     * Pulls all cases from Firestore and saves them locally.
     */
    suspend fun restoreCasesFromCloud() = withContext(Dispatchers.IO) {
        val uid = getUserId() ?: return@withContext
        
        try {
            val snapshot = firestore.collection("users").document(uid)
                .collection("cases").get().await()
                
            val cases = snapshot.documents.mapNotNull { doc ->
                try {
                    CaseEntity(
                        id = doc.getLong("id") ?: 0L,
                        title = doc.getString("title") ?: "",
                        category = doc.getString("category") ?: "",
                        date = doc.getString("date") ?: "",
                        description = doc.getString("description") ?: "",
                        status = doc.getString("status") ?: "PENDING",
                        subtotalUpdated = doc.getDouble("subtotalUpdated") ?: 0.0,
                        suggestedMoralDamages = doc.getDouble("suggestedMoralDamages") ?: 0.0,
                        processNumber = doc.getString("processNumber"),
                        fatosText = doc.getString("fatosText") ?: "",
                        fundamentosText = doc.getString("fundamentosText") ?: "",
                        pedidosText = doc.getString("pedidosText") ?: "",
                        authorName = doc.getString("authorName") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            // For simplicity, we just insert them all. In a production app, you'd handle conflicts.
            cases.forEach { caseEntity ->
                // Check if exists
                val existing = database.caseDao().getCaseById(caseEntity.id)
                if (existing == null) {
                    database.caseDao().insertCase(caseEntity)
                } else {
                    database.caseDao().updateCase(caseEntity)
                }
            }
            
            Log.d("CloudSyncManager", "Successfully restored ${cases.size} cases from Firestore")
        } catch (e: Exception) {
            Log.e("CloudSyncManager", "Error restoring cases from Firestore", e)
        }
    }

    suspend fun syncEvidenceToCloud(evidence: EvidencePhotoEntity) {
        val uid = getUserId() ?: return
        try {
            val evidenceMap = hashMapOf(
                "id" to evidence.id,
                "caseId" to evidence.caseId,
                "label" to evidence.label,
                "extractedText" to evidence.extractedText,
                "analyzedAmount" to evidence.analyzedAmount,
                "photoUri" to evidence.photoUri,
                "timestamp" to evidence.timestamp
            )
            
            firestore.collection("users").document(uid)
                .collection("cases").document(evidence.caseId.toString())
                .collection("evidence").document(evidence.id.toString())
                .set(evidenceMap).await()
        } catch (e: Exception) {
            Log.e("CloudSyncManager", "Error syncing evidence to Firestore", e)
        }
    }
    
    suspend fun restoreEvidenceFromCloud(caseId: Long) = withContext(Dispatchers.IO) {
        val uid = getUserId() ?: return@withContext
        try {
            val snapshot = firestore.collection("users").document(uid)
                .collection("cases").document(caseId.toString())
                .collection("evidence").get().await()
                
            val evidenceList = snapshot.documents.mapNotNull { doc ->
                try {
                    EvidencePhotoEntity(
                        id = doc.getLong("id") ?: 0L,
                        caseId = doc.getLong("caseId") ?: 0L,
                        label = doc.getString("label") ?: "",
                        extractedText = doc.getString("extractedText") ?: "",
                        analyzedAmount = doc.getDouble("analyzedAmount") ?: 0.0,
                        photoUri = doc.getString("photoUri") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            evidenceList.forEach { ev ->
                // Basic insert without conflict check (for simplicity in dev)
                database.evidencePhotoDao().insertPhoto(ev)
            }
        } catch (e: Exception) {
            Log.e("CloudSyncManager", "Error restoring evidence from Firestore", e)
        }
    }
}
