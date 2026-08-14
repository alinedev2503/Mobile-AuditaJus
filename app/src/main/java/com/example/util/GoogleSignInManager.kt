package com.example.util

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleSignInManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val credentialManager = CredentialManager.create(context)
    
    // Replace this with your actual Web Client ID from Firebase/Google Cloud Console
    private val webClientId = "123456789012-dummy.apps.googleusercontent.com" 

    suspend fun signInWithGoogle(): Pair<Boolean, String?> {
        try {
            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()
                
            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
                
            val result = credentialManager.getCredential(context, request)
            return handleSignInResult(result)
        } catch (e: GetCredentialException) {
            Log.e("GoogleSignIn", "GetCredentialException", e)
            return Pair(false, "Login falhou: ${e.message}")
        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Exception", e)
            return Pair(false, "Login falhou: ${e.message}")
        }
    }
    
    private suspend fun handleSignInResult(result: GetCredentialResponse): Pair<Boolean, String?> {
        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                
                // Authenticate with Firebase
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()
                
                return Pair(true, authResult.user?.displayName)
            } catch (e: Exception) {
                Log.e("GoogleSignIn", "Firebase Auth Exception", e)
                return Pair(false, "Falha na autenticação do Firebase")
            }
        }
        return Pair(false, "Credencial inválida")
    }
}
