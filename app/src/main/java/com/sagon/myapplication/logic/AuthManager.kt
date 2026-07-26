package com.sagon.myapplication.logic

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.widget.Toast
import androidx.credentials.*
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object AuthManager {
    private const val TAG = "AuthManager"
    private val auth = FirebaseAuth.getInstance()
    private const val WEB_CLIENT_ID = "971255407953-avfeu5c9ckuum1sqsv9o5u5po725j9v8.apps.googleusercontent.com"

    private fun Context.findActivity(): Activity? {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        return null
    }

    suspend fun signInWithGoogle(context: Context): Boolean = withContext(Dispatchers.Main.immediate) {
        val activity = context.findActivity() ?: return@withContext false
        
        // PAUSA CRÍTICA PARA XIAOMI: Deja que el sistema termine sus tareas antes de pedir la ventana
        delay(600)

        Log.i(TAG, "Solicitando selector de cuentas (SIWG)...")
        val credentialManager = CredentialManager.create(activity)
        
        val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(WEB_CLIENT_ID)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        try {
            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential
            
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                auth.signInWithCredential(firebaseCredential).await()
                return@withContext true
            }
            false
        } catch (e: GetCredentialException) {
            // Mostramos el error real en el móvil para debuguear en vivo
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error Google: ${e.message}", Toast.LENGTH_LONG).show()
            }
            Log.e(TAG, "Error: ${e.message}")
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error Crítico: ${e.message}")
            false
        }
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null
}
