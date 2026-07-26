package com.sagon.myapplication.logic

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.credentials.*
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID

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

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun signInWithGoogle(context: Context): Boolean = withContext(Dispatchers.Main) {
        if (!isInternetAvailable(context)) {
            Log.e(TAG, "No hay conexión a internet")
            return@withContext false
        }

        val activity = context.findActivity() ?: return@withContext false
        val credentialManager = CredentialManager.create(activity)
        
        // Generar un nonce para mayor seguridad (requerido por algunas versiones de Firebase/Google)
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(WEB_CLIENT_ID)
            .setAutoSelectEnabled(false)
            .setNonce(hashedNonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            Log.i(TAG, "Lanzando selector de Google con nonce...")
            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential
            
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                auth.signInWithCredential(firebaseCredential).await()
                Log.i(TAG, "¡Login Firebase Exitoso!")
                return@withContext true
            }
            false
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Error de Google (${e.type}): ${e.message}")
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error crítico: ${e.message}")
            false
        }
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null
}
