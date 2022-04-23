package com.example.contextualtriggers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.contextualtriggers.Authentication.AuthResultContract
import com.example.contextualtriggers.DataSources.accessGoogleFit
import com.example.contextualtriggers.DataSources.getGoal
import com.example.contextualtriggers.Services.GoalService
import com.example.contextualtriggers.ui.theme.ContextualTriggersTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset

class AuthenticationScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContextualTriggersTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}


fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_STEP_COUNT_CADENCE, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
        .build();
    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(Fitness.SCOPE_ACTIVITY_READ_WRITE)
        .requestScopes(Fitness.SCOPE_BODY_READ_WRITE)
        .requestScopes(Fitness.SCOPE_NUTRITION_READ_WRITE)
        .requestScopes(Fitness.SCOPE_LOCATION_READ_WRITE)
        .addExtension(fitnessOptions)
        .build()
    return GoogleSignIn.getClient(context, signInOptions)
}

@Composable
fun Greeting(name: String) {
    var text by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val authResultLauncher =
        rememberLauncherForActivityResult(contract = AuthResultContract()) {
            try {
                val account = it?.getResult(ApiException::class.java)
                if (account == null) {
                    text = "Google sign in failed"
                } else {
                    Log.d("TAG", "Greeting: ${account.requestedScopes}")
                    coroutineScope.launch {

                    }
                }
            } catch (e: ApiException) {
                text = "Google sign in failed"
            }
        }
    val context = LocalContext.current;
    val todayStart = LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
    val now = System.currentTimeMillis()
    val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_STEP_COUNT_CADENCE, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
        .build();
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                authResultLauncher.launch(1)
            }) {
                Text(text = "Sign in")
            }
            /*Spacer(modifier = Modifier.padding(top = 10.dp))
            Button(onClick = {
                *//*TODO*//*
                if(!ServicesManager.isServiceRunning(context,GoalService::class.java.name)) {
                    context.startForegroundService(Intent(context,GoalService::class.java))
                }
            }) {
                Text(text = "Start Service")
            }*/
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ContextualTriggersTheme {
        Greeting("Android")
    }
}