package com.example.productscanapp.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class FavoritesSyncWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            FavoritesSyncWorkerEntryPoint::class.java,
        )

        val syncResult = entryPoint.syncFavoritesUseCase().invoke()
        if (syncResult.isSuccess) {
            return Result.success()
        }

        return if (runAttemptCount < MAX_RETRY_COUNT) {
            Result.retry()
        } else {
            Result.failure()
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface FavoritesSyncWorkerEntryPoint {
        fun syncFavoritesUseCase(): SyncFavoritesUseCase
    }

    companion object {
        const val WORK_NAME = "favorites_sync_work"
        private const val MAX_RETRY_COUNT = 3
    }
}
