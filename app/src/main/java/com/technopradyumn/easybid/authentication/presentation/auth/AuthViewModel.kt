package com.technopradyumn.easybid.authentication.presentation.auth

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.technopradyumn.easybid.authentication.data.network.SupabaseClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val credentialManager: CredentialManager
) : ViewModel() {

    fun signInWithGoogle() {
        val credentialManager = CredentialManager.create(context)

        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("")
            .setNonce(hashedNonce)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewModelScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )

                val googleIdTokenCredential = GoogleIdTokenCredential
                    .createFrom(result.credential.data)

                val googleIdToken = googleIdTokenCredential.idToken
                Log.d("GoogleSignIn", googleIdToken)

                // Decode the ID token to extract email
                val email = decodeEmailFromIdToken(googleIdToken)
                Log.d("GoogleSignIn", "User's email: $email")

                // Check if the email exists in the database

                // Write code here

                SupabaseClient.supabaseClient.auth.signInWith(IDToken) {
                    idToken = googleIdToken
                    provider = Google
                    nonce = rawNonce
                }

            } catch (e: GetCredentialException) {
                Log.e("GoogleSignIn", "GetCredentialException: ${e.message}")
            } catch (e: GoogleIdTokenParsingException) {
                Log.e("GoogleSignIn", "GoogleIdTokenParsingException: ${e.message}")
            } catch (e: RestException) {
                Log.e("GoogleSignIn", "RestException: ${e.message}")
            } catch (e: Exception) {
                Log.e("GoogleSignIn", "Exception: ${e.message}")
            }
        }
    }

    // Helper function to decode the ID token and extract the email
    fun decodeEmailFromIdToken(idToken: String): String? {
        try {
            val parts = idToken.split(".")
            if (parts.size == 3) {
                // The second part of the JWT is the payload
                val payload = parts[1]
                // Decode the payload from base64
                val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
                val decodedPayload = String(decodedBytes, StandardCharsets.UTF_8)
                // Parse the payload JSON to get the email
                val jsonObject = JSONObject(decodedPayload)
                return jsonObject.getString("email")
            }
        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Failed to decode ID token: ${e.message}")
        }
        return null
    }


    fun doSomethingForExistingEmail(email: String) {
        // Implement your logic here for existing email
        println("Email '$email' exists in the database.")
    }

    fun doSomethingForNonExistingEmail(email: String) {
        // Implement your logic here for non-existing email
        println("Email '$email' does not exist in the database.")
    }


}