package com.example.vitagym.presentation.aitrainer

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitagym.data.ml.ExerciseFormAnalyzer
import com.example.vitagym.data.ml.ExerciseType
import com.example.vitagym.data.ml.PoseDetectorManager
import com.example.vitagym.data.ml.toPoseAnalysis
import com.example.vitagym.presentation.aitrainer.camera.CameraPreview
import com.example.vitagym.util.CameraPermissionHelper
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

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

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasCameraPermission) {
            PoseDetectionCameraView(onBack = onBack)
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Camera permission required", color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun PoseDetectionCameraView(onBack: () -> Unit) {
    val executor = remember { Executors.newSingleThreadExecutor() }
    val scope = rememberCoroutineScope()
    
    var detectedPose by remember { mutableStateOf<Pose?>(null) }
    var imageWidth by remember { mutableIntStateOf(480) }
    var imageHeight by remember { mutableIntStateOf(640) }
    
    var exerciseType by remember { mutableStateOf<ExerciseType>(ExerciseType.Squat) }
    
    val poseDetectorManager = remember { PoseDetectorManager() }
    val formAnalyzer = remember { ExerciseFormAnalyzer() }

    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(executor) { imageProxy ->
                    val rotation = imageProxy.imageInfo.rotationDegrees
                    val isRotated = rotation == 90 || rotation == 270
                    
                    val currentWidth = if (isRotated) imageProxy.height else imageProxy.width
                    val currentHeight = if (isRotated) imageProxy.width else imageProxy.height
                    
                    if (imageWidth != currentWidth || imageHeight != currentHeight) {
                        imageWidth = currentWidth
                        imageHeight = currentHeight
                    }

                    scope.launch {
                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val image = InputImage.fromMediaImage(mediaImage, rotation)
                            val pose = poseDetectorManager.detectPose(image)
                            if (pose != null) {
                                detectedPose = pose
                            }
                        }
                        imageProxy.close()
                    }
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Full screen Camera Preview
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            useCases = listOf(imageAnalysis)
        )

        // Pose skeleton overlay
        detectedPose?.let { pose ->
            PoseOverlay(
                pose = pose,
                imageWidth = imageWidth,
                imageHeight = imageHeight
            )
        }

        // Floating Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp))
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        // Exercise Selection (Top center, below back button)
        Card(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 70.dp)
                .align(Alignment.TopCenter),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                Modifier
                    .selectableGroup()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExerciseChip("Squat", exerciseType == ExerciseType.Squat) { 
                    exerciseType = ExerciseType.Squat 
                    formAnalyzer.resetCounter()
                }
                ExerciseChip("Push-up", exerciseType == ExerciseType.PushUp) { 
                    exerciseType = ExerciseType.PushUp 
                    formAnalyzer.resetCounter()
                }
                ExerciseChip("Plank", exerciseType == ExerciseType.Plank) { 
                    exerciseType = ExerciseType.Plank 
                    formAnalyzer.resetCounter()
                }
            }
        }

        // Feedback Card (Bottom Center)
        val feedback = remember(detectedPose, exerciseType) {
            detectedPose?.let { pose ->
                val analysis = pose.toPoseAnalysis()
                when (exerciseType) {
                    ExerciseType.Squat -> formAnalyzer.analyzeSquat(analysis)
                    ExerciseType.PushUp -> formAnalyzer.analyzePushUp(analysis)
                    ExerciseType.Plank -> formAnalyzer.analyzePlank(analysis)
                    else -> null
                }
            }
        }

        if (feedback != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(0.85f),
                colors = CardDefaults.cardColors(
                    containerColor = if (feedback.isCorrect) Color(0xFF00E676) else Color(0xFFFF5252)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = feedback.feedback,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (exerciseType != ExerciseType.Plank) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Reps: ${feedback.repCount}",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
            poseDetectorManager.close()
        }
    }
}

@Composable
fun ExerciseChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        selected = selected,
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        contentColor = if (selected) Color.White else Color.White.copy(alpha = 0.7f),
        modifier = Modifier.height(32.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun PoseOverlay(pose: Pose, imageWidth: Int, imageHeight: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        
        val scaleX = canvasWidth / imageWidth
        val scaleY = canvasHeight / imageHeight
        
        // Front camera is mirrored, so we need to flip the X coordinate
        fun transform(x: Float, y: Float): Offset {
            return Offset(canvasWidth - (x * scaleX), y * scaleY)
        }

        drawPoseConnections(this, pose, ::transform)
        
        for (landmark in pose.allPoseLandmarks) {
            val offset = transform(landmark.position.x, landmark.position.y)
            drawCircle(
                color = Color.Cyan,
                radius = 10f, // Increased radius
                center = offset
            )
        }
    }
}

private fun drawPoseConnections(
    drawScope: androidx.compose.ui.graphics.drawscope.DrawScope, 
    pose: Pose,
    transform: (Float, Float) -> Offset
) {
    val color = Color.Green
    val strokeWidth = 8f // Increased stroke width

    fun drawLine(from: Int, to: Int) {
        val start = pose.getPoseLandmark(from)
        val end = pose.getPoseLandmark(to)
        if (start != null && end != null) {
            drawScope.drawLine(
                color = color,
                start = transform(start.position.x, start.position.y),
                end = transform(end.position.x, end.position.y),
                strokeWidth = strokeWidth
            )
        }
    }

    // Connect joints
    drawLine(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_ELBOW)
    drawLine(PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_WRIST)
    drawLine(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_ELBOW)
    drawLine(PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_WRIST)
    drawLine(PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER)
    drawLine(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP)
    drawLine(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP)
    drawLine(PoseLandmark.LEFT_HIP, PoseLandmark.RIGHT_HIP)
    drawLine(PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE)
    drawLine(PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE)
    drawLine(PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE)
    drawLine(PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE)
}
