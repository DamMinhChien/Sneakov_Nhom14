package com.firebase.sneakov.ui.screen

import android.content.Context
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowLeft
import io.github.sceneview.Scene
import io.github.sceneview.animation.Transition.animateRotation
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode
import io.github.sceneview.rememberOnGestureListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

@Composable
fun Model3DScreen(
    glbUrl: String = "https://damminhchien.github.io/Shoe3D/nike/af1/af1.glb",
    onBackClick: (() -> Unit)? = null
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)

    val centerNode = rememberNode(engine)

    val cameraNode = rememberCameraNode(engine) {
        position = Position(y = -0.5f, z = 2.0f)
        lookAt(centerNode)
        centerNode.addChildNode(this)
    }

    val cameraTransition = rememberInfiniteTransition(label = "CameraTransition")
    val cameraRotation by cameraTransition.animateRotation(
        initialValue = Rotation(y = 0.0f),
        targetValue = Rotation(y = 360.0f),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7.seconds.toInt(DurationUnit.MILLISECONDS))
        )
    )

    val context = LocalContext.current
    var glbFile by remember { mutableStateOf<File?>(null) }

    LaunchedEffect(glbUrl) {
        glbFile = getCachedGlbFile(context, glbUrl)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (glbFile == null) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp)
            )
        } else {
            Scene(
                modifier = Modifier.fillMaxSize(),
                engine = engine,
                modelLoader = modelLoader,
                cameraNode = cameraNode,
                cameraManipulator = rememberCameraManipulator(
                    orbitHomePosition = cameraNode.worldPosition,
                    targetPosition = centerNode.worldPosition
                ),
                childNodes = listOf(
                    centerNode,
                    rememberNode {
                        ModelNode(
                            modelInstance = modelLoader.createModelInstance(
                                file = glbFile!!
                            ),
                            scaleToUnits = 0.25f
                        )
                    }),
                environment = environmentLoader.createHDREnvironment(
                    assetFileLocation = "environments/sky_2k.hdr"
                )!!,
                onFrame = {
                    //centerNode.rotation = cameraRotation
                    cameraNode.lookAt(centerNode)
                },
                onGestureListener = rememberOnGestureListener(
                    onDoubleTap = { _, node ->
                        node?.apply { scale *= 2.0f }
                    }
                )
            )
        }

        // Nút quay lại ở góc trên trái
        IconButton(
            onClick = {
                onBackClick?.invoke()
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(
                    color = MaterialTheme.colorScheme.secondary.copy(0.2f),
                    shape = CircleShape
                )
                .padding(8.dp)
                .size(32.dp)
        ) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.ArrowLeft,
                contentDescription = "Back to DetailScreen",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


suspend fun getCachedGlbFile(context: Context, glbUrl: String): File {
    val cacheDir = File(context.filesDir, "glb_cache")
    if (!cacheDir.exists()) cacheDir.mkdirs()

    // Đặt tên file dựa theo hash của URL
    val fileName = "${glbUrl.hashCode()}.glb"
    val cacheFile = File(cacheDir, fileName)

    // Nếu đã tồn tại => dùng luôn
    if (cacheFile.exists()) {
        return cacheFile
    }

    // Nếu chưa có => tải về
    withContext(Dispatchers.IO) {
        val url = URL(glbUrl)
        url.openStream().use { input ->
            FileOutputStream(cacheFile).use { output ->
                input.copyTo(output)
            }
        }
    }

    return cacheFile
}