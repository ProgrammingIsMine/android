package com.example.myapplicationtrendprice.scanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.myapplicationtrendprice.ui.AppBackground
import com.example.myapplicationtrendprice.ui.BrandGreen
import com.example.myapplicationtrendprice.ui.BrandGreenDark
import com.example.myapplicationtrendprice.ui.BrandGreenSoft
import com.example.myapplicationtrendprice.ui.CardWhite
import com.example.myapplicationtrendprice.ui.TextPrimary
import com.example.myapplicationtrendprice.ui.TextSecondary
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

@Composable
fun ScannerScreen(
    onBackClick: () -> Unit,
    onQrScanned: (String) -> Unit
) {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        CameraScannerContent(
            onBackClick = onBackClick,
            onQrScanned = onQrScanned
        )
    } else {
        CameraPermissionContent(
            onRequestPermission = {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onBackClick = onBackClick
        )
    }
}

@Composable
private fun CameraPermissionContent(
    onRequestPermission: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.elevatedCardColors(CardWhite),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Camera permission required",
                    color = TextPrimary,
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "TrendPrice needs camera access to scan QR codes from receipts and show price analysis.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onRequestPermission,
                        shape = RoundedCornerShape(17.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                    ) { Text("Allow camera", fontWeight = FontWeight.Black) }

                    OutlinedButton(
                        onClick = onBackClick,
                        shape = RoundedCornerShape(17.dp)
                    ) { Text("Back", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Composable
private fun CameraScannerContent(
    onBackClick: () -> Unit,
    onQrScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var scannedText by remember { mutableStateOf<String?>(null) }
    var alreadyScanned by remember { mutableStateOf(false) }

    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraProviderFuture.get().unbindAll()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { viewContext ->
                val previewView = PreviewView(viewContext).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }

                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(
                            ContextCompat.getMainExecutor(viewContext)
                        ) { imageProxy ->
                            processImageProxy(
                                imageProxy = imageProxy,
                                alreadyScanned = alreadyScanned,
                                onScanned = { text ->
                                    alreadyScanned = true
                                    scannedText = text
                                    onQrScanned(text)
                                }
                            )
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )

                previewView
            }
        )

        ScannerOverlay()

        ElevatedCard(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 28.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.elevatedCardColors(CardWhite.copy(alpha = 0.96f)),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("QR Scanner", color = TextPrimary, style = MaterialTheme.typography.titleLarge)
                    Text("Place the receipt QR inside the frame", color = TextSecondary, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onBackClick,
                    shape = RoundedCornerShape(16.dp)
                ) { Text("Back") }
            }
        }

        ElevatedCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.elevatedCardColors(CardWhite.copy(alpha = 0.96f)),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = if (scannedText == null) BrandGreenSoft else Color(0xFFEAF8EA),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = if (scannedText == null) "Scanning in progress" else "QR code detected",
                        color = if (scannedText == null) BrandGreenDark else BrandGreen,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }

                Text(
                    text = scannedText ?: "Hold your phone steady. The analysis opens automatically after a QR code is detected.",
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = {
                        alreadyScanned = false
                        scannedText = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) {
                    Text("Scan again", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun ScannerOverlay() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .clip(RoundedCornerShape(32.dp))
                .border(2.dp, Color.White.copy(alpha = 0.55f), RoundedCornerShape(32.dp))
        )

        Canvas(modifier = Modifier.size(292.dp)) {
            val stroke = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
            val color = BrandGreen
            val length = 54.dp.toPx()
            val inset = 16.dp.toPx()
            val left = inset
            val top = inset
            val right = size.width - inset
            val bottom = size.height - inset

            drawLine(
                color = color,
                start = Offset(left, top),
                end = Offset(left + length, top),
                strokeWidth = stroke.width,
                cap = stroke.cap
            )

            drawLine(
                color = color,
                start = Offset(left, top),
                end = Offset(left, top + length),
                strokeWidth = stroke.width,
                cap = stroke.cap
            )

            drawLine(
                color = color,
                start = Offset(right, top),
                end = Offset(right - length, top),
                strokeWidth = stroke.width,
                cap = stroke.cap
            )

            drawLine(
                color = color,
                start = Offset(right, top),
                end = Offset(right, top + length),
                strokeWidth = stroke.width,
                cap = stroke.cap
            )

            drawLine(
                color = color,
                start = Offset(left, bottom),
                end = Offset(left + length, bottom),
                strokeWidth = stroke.width,
                cap = stroke.cap
            )

            drawLine(
                color = color,
                start = Offset(left, bottom),
                end = Offset(left, bottom - length),
                strokeWidth = stroke.width,
                cap = stroke.cap
            )

            drawLine(
                color = color,
                start = Offset(right, bottom),
                end = Offset(right - length, bottom),
                strokeWidth = stroke.width,
                cap = stroke.cap
            )

            drawLine(
                color = color,
                start = Offset(right, bottom),
                end = Offset(right, bottom - length),
                strokeWidth = stroke.width,
                cap = stroke.cap
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth(0.52f)
                .height(2.dp)
                .background(BrandGreen)
        )
    }
}

@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    imageProxy: androidx.camera.core.ImageProxy,
    alreadyScanned: Boolean,
    onScanned: (String) -> Unit
) {
    val mediaImage = imageProxy.image

    if (mediaImage == null || alreadyScanned) {
        imageProxy.close()
        return
    }

    val image = InputImage.fromMediaImage(
        mediaImage,
        imageProxy.imageInfo.rotationDegrees
    )

    val scanner = BarcodeScanning.getClient()

    scanner.process(image)
        .addOnSuccessListener { barcodes ->
            val qrText = barcodes.firstOrNull()?.rawValue

            if (!qrText.isNullOrBlank()) {
                onScanned(qrText)
            }
        }
        .addOnFailureListener {
            // Ignore failed frame and continue scanning.
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}
