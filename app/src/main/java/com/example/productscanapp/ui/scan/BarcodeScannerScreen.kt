package com.example.productscanapp.ui.scan

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

@Composable
fun BarcodeScannerScreen(
    onBarcodeDetected: (String) -> Unit
) {
    var hasCameraPermission by remember {
        mutableStateOf(false)
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasCameraPermission = granted
        }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            Manifest.permission.CAMERA
        )
    }

    if (hasCameraPermission) {
        CameraPreview(onBarcodeDetected)
    } else {
        Column {
            Text("Permission caméra nécessaire")

            Button(
                onClick = {
                    permissionLauncher.launch(
                        Manifest.permission.CAMERA
                    )
                }
            ) {
                Text("Autoriser")
            }
        }
    }
}

@Composable
fun CameraPreview(
    onBarcodeDetected: (String) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val cameraProviderFuture =
        remember {
            ProcessCameraProvider.getInstance(context)
        }

    DisposableEffect(Unit) {
        onDispose {
            try {
                cameraProviderFuture.get().unbindAll()
            } catch (_: Exception) {
            }
        }
    }

    var hasScanned by remember {
        mutableStateOf(false)
    }

    var lastBarcode by remember { mutableStateOf<String?>(null) }
    var sameBarcodeCount by remember { mutableIntStateOf(0) }
    @Composable
    fun ScannerFrame() {
        Canvas(
            modifier = Modifier
                .width(300.dp)
                .height(180.dp)
        ) {
            val cornerLength = 45.dp.toPx()
            val stroke = 6.dp.toPx()

            // haut gauche
            drawLine(
                color = Color.White,
                start = Offset(0f, 0f),
                end = Offset(cornerLength, 0f),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White,
                start = Offset(0f, 0f),
                end = Offset(0f, cornerLength),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )

            // haut droit
            drawLine(
                color = Color.White,
                start = Offset(size.width, 0f),
                end = Offset(size.width - cornerLength, 0f),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White,
                start = Offset(size.width, 0f),
                end = Offset(size.width, cornerLength),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )

            // bas gauche
            drawLine(
                color = Color.White,
                start = Offset(0f, size.height),
                end = Offset(cornerLength, size.height),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White,
                start = Offset(0f, size.height),
                end = Offset(0f, size.height - cornerLength),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )

            // bas droit
            drawLine(
                color = Color.White,
                start = Offset(size.width, size.height),
                end = Offset(size.width - cornerLength, size.height),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White,
                start = Offset(size.width, size.height),
                end = Offset(size.width, size.height - cornerLength),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->

            val previewView = PreviewView(context)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({

                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val scanner = BarcodeScanning.getClient()

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context)
                ) { imageProxy ->

                    val mediaImage = imageProxy.image

                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )

                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                val barcode = barcodes.firstOrNull()

                                val value = barcode?.rawValue
                                val box = barcode?.boundingBox

                                if (value != null && box != null && !hasScanned) {

                                    val imageWidth = imageProxy.width
                                    val imageHeight = imageProxy.height

                                    val centerX = box.centerX()
                                    val centerY = box.centerY()

                                    val frameLeft = imageWidth * 0.20
                                    val frameRight = imageWidth * 0.80
                                    val frameTop = imageHeight * 0.30
                                    val frameBottom = imageHeight * 0.70

                                    val isInsideFrame =
                                        centerX in frameLeft.toInt()..frameRight.toInt() &&
                                                centerY in frameTop.toInt()..frameBottom.toInt()
                                    if (isInsideFrame) {

                                        if (value == lastBarcode) {
                                            sameBarcodeCount++
                                        } else {
                                            lastBarcode = value
                                            sameBarcodeCount = 1
                                        }

                                        Log.d(
                                            "BARCODE_SCAN",
                                            "Code dans le cadre : $value / count = $sameBarcodeCount"
                                        )

                                        if (sameBarcodeCount >= 3) {
                                            hasScanned = true

                                            Log.d(
                                                "BARCODE_SCAN",
                                                "Code validé : $value"
                                            )

                                            onBarcodeDetected(value)
                                        }

                                    } else {
                                        Log.d(
                                            "BARCODE_SCAN",
                                            "Code détecté mais hors cadre : $value"
                                        )
                                    }

                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                try {
                    cameraProvider.unbindAll()

                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }, ContextCompat.getMainExecutor(context))

            previewView
        }
    )
        ScannerFrame()
    }
}