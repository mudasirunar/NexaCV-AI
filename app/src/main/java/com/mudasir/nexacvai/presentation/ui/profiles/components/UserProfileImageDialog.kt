package com.mudasir.nexacvai.presentation.ui.profiles.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.presentation.ui.profiles.createImageFileUri
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ImageCompressionHelper
import com.mudasir.nexacvai.presentation.ui.profiles.utils.NameUtils
import com.mudasir.nexacvai.ui.theme.*
import kotlinx.coroutines.launch

/**
 * A fullscreen interactive profile picture viewer dialog.
 * 
 * Supports:
 * - Direct image compression and updates without closing the viewer.
 * - Flat, visually centered 320.dp profile image.
 * - Both Edit and Remove buttons aligned side-by-side at the bottom center.
 * - Bottom 180.dp vertical gradient scrim overlay ensuring 100% text/icon visibility on all themes.
 * - Circular glass-white button containers with high-contrast white text labels underneath.
 * - Live central loading spinner while compressing the selected image.
 */
@Composable
fun UserProfileImageDialog(
    showFullScreenImage: Boolean,
    onDismissFullScreen: () -> Unit,
    profile: UserProfile,
    colorPair: AvatarColorPair,
    onPhotoSelected: (String) -> Unit = {},
    onRemovePhotoClick: () -> Unit = {},
    isReadOnly: Boolean = false
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var isProcessingImage by remember { mutableStateOf(false) }
    var showImageDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showConfirmRemovePhoto by remember { mutableStateOf(false) }
    var isSquareShape by remember(showFullScreenImage) { mutableStateOf(false) }
    var imageAspectRatio by remember(profile.profilePictureUri) { mutableStateOf(1f) }
    
    var animatedAlpha by remember { mutableStateOf(0f) }
    LaunchedEffect(showFullScreenImage) {
        if (showFullScreenImage) {
            animatedAlpha = 1f
        } else {
            animatedAlpha = 0f
        }
    }
    val dialogAlpha by animateFloatAsState(
        targetValue = animatedAlpha,
        animationSpec = tween(durationMillis = 250),
        label = "dialogAlpha"
    )

    // Pick visual media launcher for gallery selection
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                coroutineScope.launch {
                    isProcessingImage = true
                    val compressedUri = ImageCompressionHelper.compressAndSaveProfilePicture(context, uri, profile.id)
                    if (compressedUri != null) {
                        onPhotoSelected(compressedUri)
                    }
                    isProcessingImage = false
                }
            }
        }
    )

    // Take picture launcher for camera selection
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempCameraUri?.let { uri ->
                    coroutineScope.launch {
                        isProcessingImage = true
                        val compressedUri = ImageCompressionHelper.compressAndSaveProfilePicture(context, uri, profile.id)
                        if (compressedUri != null) {
                            onPhotoSelected(compressedUri)
                        }
                        isProcessingImage = false
                    }
                }
            }
        }
    )

    if (showFullScreenImage) {
        val hasPhoto = profile.profilePictureUri != null
        val initials = remember(profile.fullName) { NameUtils.getInitials(profile.fullName.trim()) }

        Dialog(
            onDismissRequest = {
                if (!isProcessingImage) {
                    onDismissFullScreen()
                }
            },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = !isProcessingImage,
                dismissOnClickOutside = !isProcessingImage,
                decorFitsSystemWindows = false
            )
        ) {
            val view = LocalView.current
            
            // Synchronously configure the window properties immediately during composition
            // of the first frame, preventing any 1-2 frame layout shift or gray background flicker.
            val dialogWindow = (view.parent as? DialogWindowProvider)?.window
            if (dialogWindow != null) {
                dialogWindow.setLayout(
                    android.view.WindowManager.LayoutParams.MATCH_PARENT,
                    android.view.WindowManager.LayoutParams.MATCH_PARENT
                )
                dialogWindow.setBackgroundDrawableResource(android.R.color.transparent)
                dialogWindow.decorView.setPadding(0, 0, 0, 0)
                dialogWindow.clearFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                
                // Clear translucent flags and enable draws system bar backgrounds to set transparent colors
                dialogWindow.clearFlags(
                    android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
                    android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                )
                dialogWindow.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                dialogWindow.statusBarColor = android.graphics.Color.TRANSPARENT
                dialogWindow.navigationBarColor = android.graphics.Color.TRANSPARENT
                
                dialogWindow.setWindowAnimations(0) // Disable window manager zoom/fade animations
                WindowCompat.setDecorFitsSystemWindows(dialogWindow, false)
            }
            val configuration = LocalConfiguration.current
            val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
            val isShortHeight = configuration.screenHeightDp < 500
            val showSideBar = isLandscape && isShortHeight

            val imageSize = if (showSideBar) 280.dp else 320.dp
            val targetRadius = if (isSquareShape) 16.dp else (imageSize / 2)
            val cornerRadius by animateDpAsState(
                targetValue = targetRadius,
                animationSpec = tween(durationMillis = 300),
                label = "cornerRadius"
            )
            val imageShape = RoundedCornerShape(cornerRadius)
            
            val targetAspectRatio = if (isSquareShape) imageAspectRatio else 1f
            val animatedAspectRatio by animateFloatAsState(
                targetValue = targetAspectRatio,
                animationSpec = tween(durationMillis = 300),
                label = "aspectRatio"
            )
            
            val containerBgColor by animateColorAsState(
                targetValue = if (isSquareShape && hasPhoto) Color.Transparent else colorPair.background,
                animationSpec = tween(durationMillis = 300),
                label = "containerBgColor"
            )
            
            val imageAlignment = Alignment.Center
            val imagePadding = PaddingValues(0.dp)
            val initialsTextSize = if (showSideBar) 100.sp else 120.sp

            val editButton = @Composable {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = !isProcessingImage
                    ) {
                        showImageDialog = true
                    }
                ) {
                    IconButton(
                        onClick = { showImageDialog = true },
                        modifier = Modifier
                            .size(52.dp)
                            .background(Color.White.copy(alpha = 0.12f), CircleShape)
                            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)), CircleShape),
                        enabled = !isProcessingImage,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.12f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (hasPhoto) Icons.Outlined.Edit else Icons.Outlined.Add,
                            contentDescription = if (hasPhoto) "Edit Profile Photo" else "Add Profile Photo",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (hasPhoto) "Edit" else "Add",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    )
                }
            }

            val removeButton = @Composable {
                val removeAlpha = if (hasPhoto) 0.9f else 0.3f
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = hasPhoto && !isProcessingImage
                    ) {
                        if (hasPhoto) {
                            showConfirmRemovePhoto = true
                        }
                    }
                ) {
                    IconButton(
                        onClick = { showConfirmRemovePhoto = true },
                        modifier = Modifier
                            .size(52.dp)
                            .background(Color.White.copy(alpha = if (hasPhoto) 0.12f else 0.05f), CircleShape)
                            .border(BorderStroke(1.dp, Color.White.copy(alpha = if (hasPhoto) 0.25f else 0.1f)), CircleShape),
                        enabled = hasPhoto && !isProcessingImage,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = if (hasPhoto) 0.12f else 0.05f),
                            contentColor = if (hasPhoto) Color.White else Color.White.copy(alpha = 0.3f),
                            disabledContainerColor = Color.White.copy(alpha = 0.05f),
                            disabledContentColor = Color.White.copy(alpha = 0.3f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Remove Profile Photo",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Remove",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = removeAlpha)
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(dialogAlpha)
                    .background(GlassOverlayBg)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (!isProcessingImage) {
                            onDismissFullScreen()
                        }
                    }
            ) {
                // 1. Center Aligned Plain Circular Image Container (No overlapping controls)
                Box(
                    modifier = Modifier
                        .align(imageAlignment)
                        .padding(imagePadding)
                        .size(imageSize),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(animatedAspectRatio)
                            .clip(imageShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                // Toggle shape on click
                                isSquareShape = !isSquareShape
                            }
                            .background(containerBgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasPhoto) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(profile.profilePictureUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Full Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                onSuccess = { state ->
                                    val drawable = state.result.drawable
                                    val width = drawable.intrinsicWidth
                                    val height = drawable.intrinsicHeight
                                    if (width > 0 && height > 0) {
                                        imageAspectRatio = width.toFloat() / height.toFloat()
                                    }
                                }
                            )
                        } else {
                            Text(
                                text = initials,
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colorPair.text,
                                    fontSize = initialsTextSize
                                )
                            )
                        }

                        // Central loading spinner overlay during compression
                        if (isProcessingImage) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.45f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                    }
                }

                // 2. Action Controls with Scrim (Right for sidebar, Bottom for standard)
                if (!isReadOnly) {
                    if (showSideBar) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight()
                                .width(110.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.55f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(36.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .wrapContentSize()
                            ) {
                                editButton()
                                removeButton()
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(140.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.55f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(48.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(bottom = 16.dp)
                                    .wrapContentSize()
                            ) {
                                editButton()
                                removeButton()
                            }
                        }
                    }
                }
            }
        }
    }

    if (showImageDialog) {
        ImagePickerDialog(
            onDismissRequest = { showImageDialog = false },
            onCameraSelected = {
                val uri = context.createImageFileUri()
                tempCameraUri = uri
                cameraLauncher.launch(uri)
            },
            onGallerySelected = {
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )
    }

    if (showConfirmRemovePhoto) {
        NexaAlertDialog(
            onDismissRequest = { showConfirmRemovePhoto = false },
            title = "Remove Profile Photo?",
            message = "Are you sure you want to remove your profile picture?",
            confirmLabel = "Remove",
            onConfirm = {
                showConfirmRemovePhoto = false
                onDismissFullScreen()
                onRemovePhotoClick()
            },
            dismissLabel = "Cancel",
            isDestructive = true
        )
    }
}
