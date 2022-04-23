package com.example.contextualtriggers.DataSources

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.GoalsReadRequest

const val TAG = "TAG"
fun accessGoogleFit(
    context: Context,
    fitnessOptions: FitnessOptions,
    startSeconds: Long,
    endSeconds: Long
) {
    val heightAndWeightRequest = DataReadRequest.Builder()
        .read(DataType.TYPE_HEIGHT)
        .read(DataType.TYPE_WEIGHT)
        .setTimeRange(1, endSeconds, java.util.concurrent.TimeUnit.MILLISECONDS)
        .build()
    val calorieRequest = DataReadRequest.Builder()
        .read(DataType.TYPE_CALORIES_EXPENDED)
        .setTimeRange(startSeconds, endSeconds, java.util.concurrent.TimeUnit.MILLISECONDS)
        .build()
    val account = GoogleSignIn.getAccountForExtension(context, fitnessOptions)
    Fitness.getHistoryClient(context, account)
        .readData(calorieRequest)
        .addOnSuccessListener {
            val caloriesDataSet = it.getDataSet(DataType.TYPE_CALORIES_EXPENDED)
            var calories: Float
            for (dataPoint in caloriesDataSet.dataPoints) {
                calories = dataPoint.getValue(Field.FIELD_CALORIES).asFloat()
                Log.d(TAG, "accessGoogleFit: $calories")
            }
        }
        .addOnFailureListener {
            //handle exception
            Log.d(TAG, "accessGoogleFit: Failed")
        }

    Fitness.getHistoryClient(context, account)
        .readData(heightAndWeightRequest)
        .addOnSuccessListener {
            // Use response data
            val weightDataSet = it.getDataSet(DataType.TYPE_WEIGHT)
            val heightWeightSet = it.getDataSet(DataType.TYPE_HEIGHT)

            var height: Float = 0f
            var weight: Float = 0f
            for (dataPoint in weightDataSet.dataPoints) {
                weight = dataPoint.getValue(Field.FIELD_WEIGHT).asFloat()
            }
            for (dataPoint in heightWeightSet.dataPoints) {
                height = dataPoint.getValue(Field.FIELD_HEIGHT).asFloat()

            }
            Log.d(TAG, "height and weight: $height $weight")

        }
        .addOnFailureListener {
            //handle exception
            Log.d(TAG, "accessGoogleFit: Failed")
        }
//    Fitness.getHistoryClient(context, account)
//        .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
//        .addOnSuccessListener {
//            // Use response data
//            val totalSteps =
//                it.dataPoints.firstOrNull()?.getValue(Field.FIELD_STEPS)?.asInt() ?: 0
//            Log.d(TAG, "Total Steps: $totalSteps ")
//
//        }
//        .addOnFailureListener {
//            //handle exception
//            Log.d(TAG, "accessGoogleFit: Failed")
//        }
}

fun getCalories(
    startSeconds: Long,
    endSeconds: Long,
    context: Context,
    account: GoogleSignInAccount,
    onCaloriesFetched: (weight: Float) -> Unit = {}
) {
    val calorieRequest = DataReadRequest.Builder()
        .read(DataType.TYPE_CALORIES_EXPENDED)
        .setTimeRange(startSeconds, endSeconds, java.util.concurrent.TimeUnit.MILLISECONDS)
        .build()
    Fitness.getHistoryClient(context, account)
        .readData(calorieRequest)
        .addOnSuccessListener {
            val caloriesDataSet = it.getDataSet(DataType.TYPE_CALORIES_EXPENDED)
            var calories: Float = 0f
            for (dataPoint in caloriesDataSet.dataPoints) {
                calories = dataPoint.getValue(Field.FIELD_CALORIES).asFloat()
//                Log.d(TAG, "accessGoogleFit: $calories")
            }
            onCaloriesFetched(calories)

        }
        .addOnFailureListener {
            //handle exception
            Log.d(TAG, "accessGoogleFit: Failed")
        }
}

fun getWeight(
    endSeconds: Long = System.currentTimeMillis(),
    context: Context,
    account: GoogleSignInAccount,
    onWeightFetched: (weight: Float) -> Unit = {}
) {
    val weightRequest = DataReadRequest.Builder()
        .read(DataType.TYPE_WEIGHT)
        .setTimeRange(1, endSeconds, java.util.concurrent.TimeUnit.MILLISECONDS)
        .build()
    Fitness.getHistoryClient(context, account)
        .readData(weightRequest)
        .addOnSuccessListener {
            val weightDataSet = it.getDataSet(DataType.TYPE_WEIGHT)

            var height: Float = 0f
            var weight: Float = 0f
            for (dataPoint in weightDataSet.dataPoints) {
                weight = dataPoint.getValue(Field.FIELD_WEIGHT).asFloat()
            }
            onWeightFetched(weight)
        }
        .addOnFailureListener {}
}

fun getHeight(
    endSeconds: Long = System.currentTimeMillis(),
    context: Context,
    account: GoogleSignInAccount,
    onHeightFetched: (weight: Float) -> Unit = {}
) {
    val heightRequest = DataReadRequest.Builder()
        .read(DataType.TYPE_HEIGHT)
        .setTimeRange(1, endSeconds, java.util.concurrent.TimeUnit.MILLISECONDS)
        .build()
    Fitness.getHistoryClient(context, account)
        .readData(heightRequest)
        .addOnSuccessListener {
            val heightWeightSet = it.getDataSet(DataType.TYPE_HEIGHT)

            var height: Float = 0f
            for (dataPoint in heightWeightSet.dataPoints) {
                height = dataPoint.getValue(Field.FIELD_HEIGHT).asFloat()
            }
            onHeightFetched(height)
        }
        .addOnFailureListener {}
}

fun getTodayTotalSteps(
    context: Context,
    account: GoogleSignInAccount,
    onStepsFetched: (steps: Int) -> Unit = {}
) {
    Fitness.getHistoryClient(context, account)
        .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
        .addOnSuccessListener {
            val totalSteps =
                it.dataPoints.firstOrNull()?.getValue(Field.FIELD_STEPS)?.asInt() ?: 0
            onStepsFetched(totalSteps)
            Log.d(TAG, "Total Steps: $totalSteps ")

        }
        .addOnFailureListener {
            Log.d(TAG, "accessGoogleFit: Failed")
        }
}

fun getGoal(
    context: Context,
    account: GoogleSignInAccount,
    onGoalStepsFetched: (steps: Int) -> Unit = {}
) {
    val goalsReadRequest = GoalsReadRequest.Builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
        .build()
    Fitness.getGoalsClient(context, account)
        .readCurrentGoals(goalsReadRequest)
        .addOnSuccessListener { goals ->
            // There should be at most one heart points goal currently.
            goals.firstOrNull()?.apply {
                onGoalStepsFetched(metricObjective.value.toInt())
            }
        }
}
