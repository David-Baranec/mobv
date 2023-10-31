package com.example.cvicenie2.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.cvicenie2.data.DataRepository

class MyWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Tu môžete vykonávať asynchrónnu prácu
        Log.d("MyWorker", "spustenie workera")
        DataRepository.getInstance(applicationContext).apiListGeofence()

        return Result.success()
    }
}