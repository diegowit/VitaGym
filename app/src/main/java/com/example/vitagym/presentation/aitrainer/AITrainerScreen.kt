package com.example.vitagym.presentation.aitrainer

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.vitagym.util.CameraPermissionHelper
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AITrainerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(CameraPermissionHelper.hasCameraPermission(context))
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Form Trainer", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        if (hasCameraPermission) {
            PoseDetectionCameraView(modifier = Modifier.padding(padding))
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Camera permission is required to use this feature.")
            }
        }
    }
}

@Composable
fun PoseDetectionCameraView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor = remember { Executors.newSingleThreadExecutor() }
    
    var detectedPose by remember { mutableStateOf<Pose?>(null) }

    val poseDetector = remember {
        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
            .build()
        PoseDetection.getClient(options)
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(executor) { imageProxy ->
                                processImageProxy(poseDetector, imageProxy) { pose ->
                                    detectedPose = pose
                                }
                            }
                        }

                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay for landmarks
        detectedPose?.let { pose ->
            PoseOverlay(pose = pose)
        }
        
        // Simple Feedback Box
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .background(Color.Black.copy(alpha = 0.6f), shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
        ) {
            val feedback = getPoseFeedback(detectedPose)
            Text(text = feedback, color = Color.White, fontSize = 18.sp)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
            poseDetector.close()
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    poseDetector: PoseDetector,
    imageProxy: ImageProxy,
    onPoseDetected: (Pose) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        poseDetector.process(image)
            .addOnSuccessListener { pose ->
                onPoseDetected(pose)
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}

@Composable
fun PoseOverlay(pose: Pose) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val landmarks = pose.allPoseLandmarks
        
        // Draw connections
        drawPoseConnections(this, pose)

        // Draw points
        for (landmark in landmarks) {
            drawCircle(
                color = Color.Cyan,
                radius = 8f,
                center = androidx.compose.ui.geometry.Offset(landmark.position.x, landmark.position.y)
            )
        }
    }
}

private fun drawPoseConnections(drawScope: androidx.compose.ui.graphics.drawscope.DrawScope, pose: Pose) {
    val color = Color.Green
    val strokeWidth = 5f

    fun drawLine(from: Int, to: Int) {
        val start = pose.getPoseLandmark(from)
        val end = pose.getPoseLandmark(to)
        if (start != null && end != null) {
            drawScope.drawLine(
                color = color,
                start = androidx.compose.ui.geometry.Offset(start.position.x, start.position.y),
                end = androidx.compose.ui.geometry.Offset(end.position.x, end.position.y),
                strokeWidth = strokeWidth
            )
        }
    }

    // Arms
    drawLine(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_ELBOW)
    drawLine(PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_WRIST)
    drawLine(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_ELBOW)
    drawLine(PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_WRIST)

    // Torso
    drawLine(PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER)
    drawLine(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP)
    drawLine(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP)
    drawLine(PoseLandmark.LEFT_HIP, PoseLandmark.RIGHT_HIP)

    // Legs
    drawLine(PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE)
    drawLine(PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE)
    drawLine(PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE)
    drawLine(PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE)
}

fun getPoseFeedback(pose: Pose?): String {
    if (pose == null) return "Scanning for pose..."
    
    val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
    val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
    val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)

    if (leftHip != null && leftKnee != null && leftAnkle != null) {
        val angle = calculateAngle(leftHip, leftKnee, leftAnkle)
        return when {
            angle > 160 -> "Stand straight"
            angle in 90.0..120.0 -> "Good depth!"
            angle < 90 -> "Too low!"
            else -> "Performing Squat"
        }
    }
    
    return "Ensure full body is visible"
}

fun calculateAngle(first: PoseLandmark, second: PoseLandmark, third: PoseLandmark): Double {
    var result = Math.toDegrees(
        Math.atan2((third.position.y - second.position.y).toDouble(), (third.position.x - second.position.x).toDouble()) -
                Math.atan2((first.position.y - second.position.y).toDouble(), (first.position.x - second.position.x).toDouble())
    )
    result = Math.abs(result)
    if (result > 180) {
        result = 360.0 - result
    }
    return result
}
