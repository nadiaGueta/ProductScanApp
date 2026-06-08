package com.example.productscanapp.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.RemoteViews
import com.example.productscanapp.MainActivity
import com.example.productscanapp.R
import com.example.productscanapp.data.local.dao.ScanHistoryDao
import dagger.hilt.android.AndroidEntryPoint
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LastScanWidget : AppWidgetProvider() {

    @Inject
    lateinit var scanHistoryDao: ScanHistoryDao

    override fun onUpdate(
        context: Context,
        manager: AppWidgetManager,
        ids: IntArray
    ) {
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val latestScan = scanHistoryDao.getLatest()
                val productImage = loadProductImage(latestScan?.imageUrl)

                ids.forEach { id ->
                    val views = RemoteViews(
                        context.packageName,
                        R.layout.widget_last_scan
                    )

                    if (latestScan == null) {
                        views.setTextViewText(
                            R.id.widget_product_name,
                            "Aucun scan récent"
                        )

                        views.setTextViewText(
                            R.id.widget_nutriscore,
                            ""
                        )

                        views.setViewVisibility(
                            R.id.widget_product_image,
                            View.GONE
                        )
                    } else {
                        views.setTextViewText(
                            R.id.widget_product_name,
                            latestScan.name
                        )

                        views.setTextViewText(
                            R.id.widget_nutriscore,
                            "NutriScore : ${latestScan.nutriScore ?: "?"}"
                        )

                        if (productImage != null) {
                            views.setImageViewBitmap(
                                R.id.widget_product_image,
                                productImage
                            )

                            views.setViewVisibility(
                                R.id.widget_product_image,
                                View.VISIBLE
                            )
                        } else {
                            views.setViewVisibility(
                                R.id.widget_product_image,
                                View.GONE
                            )
                        }
                    }

                    views.setOnClickPendingIntent(
                        R.id.widget_root,
                        createScannerIntent(context)
                    )

                    manager.updateAppWidget(id, views)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun loadProductImage(imageUrl: String?): Bitmap? {
        if (imageUrl.isNullOrBlank()) {
            return null
        }

        var connection: HttpURLConnection? = null

        return try {
            connection = URL(imageUrl)
                .openConnection() as HttpURLConnection

            connection.connectTimeout = 5_000
            connection.readTimeout = 5_000
            connection.doInput = true
            connection.connect()

            connection.inputStream.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                    ?: return null

                Bitmap.createScaledBitmap(
                    bitmap,
                    160,
                    160,
                    true
                )
            }
        } catch (_: Exception) {
            null
        } finally {
            connection?.disconnect()
        }
    }

    private fun createScannerIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = MainActivity.ACTION_OPEN_SCANNER
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)

            val component = ComponentName(
                context,
                LastScanWidget::class.java
            )

            val ids = manager.getAppWidgetIds(component)

            if (ids.isEmpty()) {
                return
            }

            val intent = Intent(
                context,
                LastScanWidget::class.java
            ).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    ids
                )
            }

            context.sendBroadcast(intent)
        }
    }
}