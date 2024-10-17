package com.technopradyumn.easybid.authentication.presentation.auth

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.ContextWrapper
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.technopradyumn.easybid.R
import com.technopradyumn.easybid.authentication.data.network.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

@Composable
fun AuthScreen(
    navController: NavHostController,
    onLoginSignUpGoogleClick: () -> Unit,
    ) {

    val authViewModel: AuthViewModel = hiltViewModel()

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    var authState by remember { mutableStateOf<String?>(null) }
    var errorState by remember { mutableStateOf<String?>(null) }

    val gradientColors = listOf(
        Color(0xFFFF4081), // Pink Accent
        Color(0xFFFFEB3B), // Yellow
        Color(0xFFFFC107), // Amber
        Color(0xFFFF9800), // Orange
        Color(0xFFFFAB40), // Orange Accent
        Color(0xFF8BC34A), // Green
        Color(0xFFB2FF59), // Light Green
        Color(0xFF4CAF50), // Dark Green
        Color(0xFF69F0AE), // Green Accent
        Color(0xFF00BCD4), // Cyan
        Color(0xFF18FFFF), // Cyan Accent
        Color(0xFF03A9F4), // Light Blue
        Color(0xFF40C4FF), // Light Blue Accent
        Color(0xFF2196F3), // Blue
        Color(0xFF607D8B), // Blue Grey
        Color(0xFF9C27B0), // Purple
        Color(0xFF7C4DFF), // Deep Purple Accent
        Color(0xFF673AB7), // Deep Purple
        Color(0xFFE91E63), // Pink
        Color(0xFF795548)  // Brown
    )

    val colorAnimationDuration = 2000

    val animatedColor1 = remember { Animatable(gradientColors.first()) }
    val animatedColor2 = remember { Animatable(gradientColors.last()) }

    LaunchedEffect(Unit) {
        while (true) {
            for (i in gradientColors.indices) {
                val nextIndex = (i + 1) % gradientColors.size
                animatedColor1.animateTo(
                    targetValue = gradientColors[i],
                    animationSpec = TweenSpec(
                        durationMillis = colorAnimationDuration,
                        easing = LinearEasing
                    )
                )
                animatedColor2.animateTo(
                    targetValue = gradientColors[nextIndex],
                    animationSpec = TweenSpec(
                        durationMillis = colorAnimationDuration,
                        easing = LinearEasing
                    )
                )
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(animatedColor1.value, animatedColor2.value)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = "One App.\nAll You Need.\nReady-to-Go.",
                        lineHeight = 64.sp,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 48.sp,
                            color = Color.Black
                        ),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Your world of auctions awaits:\n Explore, discover, bid, and own!",
                        textAlign = TextAlign.Start,
                        lineHeight = 24.sp,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 16.sp,
                            color = Color.Black
                        ),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.RawRes(
                                R.raw.google_icon_anim
                            )
                        )

                        val progress by animateLottieCompositionAsState(
                            composition = composition,
                            iterations = LottieConstants.IterateForever
                        )

                        ElevatedCard(
                            onClick = {
                                authViewModel.signInWithGoogle()
                            },
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp)
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(50.dp),
                        ) {
                            LottieAnimation(
                                composition = composition,
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(Alignment.CenterHorizontally)
                            )
                        }


                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    AuthScreen(
        navController = rememberNavController(),
        onLoginSignUpGoogleClick = {
        }
    )
}

@Composable
fun NameOrUserNameScreen(
    navController: NavHostController,
    token: String,
) {
    // Display the token or perform other actions
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Token: $token",
            style = MaterialTheme.typography.bodyLarge
        )
        // Add more UI components as needed
    }
}

@Preview(showBackground = true)
@Composable
fun NameOrUserNameScreenPreview() {
    NameOrUserNameScreen(
        navController = rememberNavController(),
        token = "sample_token"
    )
}


@Composable
fun addData() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {
            coroutineScope.launch {
                try {
                    SupabaseClient.supabaseClient.from("Users")
                        .insert(mapOf("content" to "Hello from Android"))
                    // Check if context is still valid before showing Toast
                    if (context is ContextWrapper && context.baseContext != null) {
                        Toast.makeText(context, "Welcome: New Row Inserted", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: Exception) {
                    // Check if context is still valid before showing Toast
                    if (context is ContextWrapper && context.baseContext != null) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    ) {
        Text("Add Data")
    }
}