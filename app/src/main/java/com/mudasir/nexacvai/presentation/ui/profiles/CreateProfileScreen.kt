package com.mudasir.nexacvai.presentation.ui.profiles

import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mudasir.nexacvai.domain.model.Reference
import com.mudasir.nexacvai.presentation.ui.components.BasicInfoSkeleton
import com.mudasir.nexacvai.presentation.ui.components.ImagePickerDialog
import com.mudasir.nexacvai.presentation.ui.components.NexaTextField
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import java.io.File
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

fun Context.createImageFileUri(): Uri {
    val imagePath = File(cacheDir, "images").apply { mkdirs() }
    val file = File(imagePath, "profile_pic_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateProfileScreen(
    navController: NavController,
    viewModel: CreateProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isEditing = state.profileId != null

    // Navigate back when saved successfully
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            navController.popBackStack()
        }
    }

    var showDiscardDialog by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    var isTransitionComplete by remember {
        mutableStateOf(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
    }
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                isTransitionComplete = true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val handleBack = {
        if (viewModel.hasUnsavedChanges()) {
            showDiscardDialog = true
        } else {
            navController.popBackStack()
        }
    }

    BackHandler {
        handleBack()
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard changes?", style = MaterialTheme.typography.titleMedium) },
            text = { 
                Text(
                    "You have unsaved changes. Are you sure you want to discard them and go back?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDiscardDialog = false
                        navController.popBackStack()
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDiscardDialog = false }
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { 
                        Text(
                            text = if (isEditing) "Edit Profile" else "Create Profile", 
                            style = MaterialTheme.typography.titleMedium
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = { handleBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                if (state.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                } else {
                    LinearProgressIndicator(
                        progress = { (state.currentStep + 1) / state.totalSteps.toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.imePadding(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 16.dp
            ) {
                Column {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                        thickness = 1.dp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .then(
                                if (WindowInsets.isImeVisible) Modifier else Modifier.navigationBarsPadding()
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    // Left Column: Back button
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (state.currentStep > 0) {
                            val backInteractionSource = remember { MutableInteractionSource() }
                            val backPressed by backInteractionSource.collectIsPressedAsState()
                            val backScale by animateFloatAsState(if (backPressed) 0.98f else 1f, label = "backScale")
                            OutlinedButton(
                                onClick = { viewModel.previousStep() },
                                interactionSource = backInteractionSource,
                                modifier = Modifier.graphicsLayer(scaleX = backScale, scaleY = backScale)
                            ) {
                                Text("Back")
                            }
                        }
                    }

                    // Center Column: Save Midway button
                    Box(
                        modifier = Modifier.weight(1.2f),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = state.fullName.isNotBlank() && state.currentStep < state.totalSteps - 1,
                            enter = fadeIn() + scaleIn(initialScale = 0.9f),
                            exit = fadeOut() + scaleOut(targetScale = 0.9f)
                        ) {
                            val saveMidwayInteractionSource = remember { MutableInteractionSource() }
                            val saveMidwayPressed by saveMidwayInteractionSource.collectIsPressedAsState()
                            val saveMidwayScale by animateFloatAsState(if (saveMidwayPressed) 0.98f else 1f, label = "saveMidwayScale")
                            
                            OutlinedButton(
                                onClick = {
                                    if (!state.isSaving) {
                                        viewModel.saveProfile()
                                    }
                                },
                                enabled = true,
                                interactionSource = saveMidwayInteractionSource,
                                modifier = Modifier.graphicsLayer(scaleX = saveMidwayScale, scaleY = saveMidwayScale),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                            ) {
                                if (state.isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Save,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text("Save Draft")
                                    }
                                }
                            }
                        }
                    }

                    // Right Column: Next / Final Save button
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        if (state.currentStep < state.totalSteps - 1) {
                            val isNextEnabled by remember(state.currentStep, state.fullName, state.professionalTitle, state.skills, state.experiences) {
                                derivedStateOf {
                                    when (state.currentStep) {
                                        0 -> state.fullName.isNotBlank() && state.professionalTitle.isNotBlank()
                                        1 -> state.skills.size >= 3
                                        2 -> state.experiences.all { it.jobTitle.isNotBlank() && it.companyName.isNotBlank() }
                                        else -> true
                                    }
                                }
                            }
                            val nextInteractionSource = remember { MutableInteractionSource() }
                            val nextPressed by nextInteractionSource.collectIsPressedAsState()
                            val nextScale by animateFloatAsState(if (nextPressed) 0.98f else 1f, label = "nextScale")
                            Button(
                                onClick = { viewModel.nextStep() },
                                enabled = isNextEnabled,
                                interactionSource = nextInteractionSource,
                                modifier = Modifier.graphicsLayer(scaleX = nextScale, scaleY = nextScale)
                            ) {
                                Text("Next")
                            }
                        } else {
                            val saveInteractionSource = remember { MutableInteractionSource() }
                            val savePressed by saveInteractionSource.collectIsPressedAsState()
                            val saveScale by animateFloatAsState(if (savePressed) 0.98f else 1f, label = "saveScale")
                            Button(
                                onClick = {
                                    if (state.fullName.isNotBlank() && !state.isSaving) {
                                        viewModel.saveProfile()
                                    }
                                },
                                enabled = state.fullName.isNotBlank(),
                                interactionSource = saveInteractionSource,
                                modifier = Modifier.graphicsLayer(scaleX = saveScale, scaleY = saveScale)
                            ) {
                                if (state.isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Save Profile")
                                }
                            }
                        }
                    }
                }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                // Only shown when editing an existing profile (loading from Room).
                // For new profiles, isLoading starts as false — no spinner overhead.
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else if (!isTransitionComplete) {
                // Render a lightweight static skeleton to avoid blank screens during transition
                // while maintaining 120fps smooth animation.
                BasicInfoSkeleton()
            } else {
                AnimatedContent(
                    targetState = state.currentStep,
                    transitionSpec = {
                        val enterDuration = 200
                        val exitDuration = 200
                        if (targetState > initialState) {
                            (slideInHorizontally(
                                animationSpec = tween(enterDuration, easing = androidx.compose.animation.core.LinearOutSlowInEasing)
                            ) { width -> width / 4 } + fadeIn(tween(enterDuration))) togetherWith
                            (slideOutHorizontally(
                                animationSpec = tween(exitDuration, easing = androidx.compose.animation.core.FastOutLinearInEasing)
                            ) { width -> -width / 4 } + fadeOut(tween(exitDuration)))
                        } else {
                            (slideInHorizontally(
                                animationSpec = tween(enterDuration, easing = androidx.compose.animation.core.LinearOutSlowInEasing)
                            ) { width -> -width / 4 } + fadeIn(tween(enterDuration))) togetherWith
                            (slideOutHorizontally(
                                animationSpec = tween(exitDuration, easing = androidx.compose.animation.core.FastOutLinearInEasing)
                            ) { width -> width / 4 } + fadeOut(tween(exitDuration)))
                        } using androidx.compose.animation.SizeTransform(clip = false)
                    },
                    modifier = Modifier.fillMaxSize(),
                    label = "StepTransition"
                ) { step ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when (step) {
                            0 -> BasicInfoStep(state, viewModel)
                            1 -> SummaryStep(state, viewModel)
                            2 -> ExperiencesStep(state, viewModel)
                            6 -> ReferencesStep(state, viewModel)
                            else -> PlaceholderStep(step, state.totalSteps)
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun BasicInfoStep(state: CreateProfileState, viewModel: CreateProfileViewModel) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var showImageDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { viewModel.updateBasicInfo(profilePictureUri = it.toString()) }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempCameraUri?.let { viewModel.updateBasicInfo(profilePictureUri = it.toString()) }
            }
        }
    )

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

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Step 1: Basic Information",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Profile Photo",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                // Main Image Circle
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { showImageDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.profilePictureUri != null) {
                        AsyncImage(
                            model = state.profilePictureUri,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Placeholder",
                            modifier = Modifier.size(50.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Camera Overlay Circle (Full circle, no clipping)
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .size(32.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 2.dp
                ) {
                    IconButton(onClick = { showImageDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Edit Photo",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            
            if (state.profilePictureUri != null) {
                TextButton(
                    onClick = { viewModel.removeProfilePicture() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Remove")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        NexaTextField(
            value = state.fullName,
            onValueChange = { viewModel.updateBasicInfo(fullName = it) },
            label = "Full Name",
            placeholder = "Enter your full name",
            leadingIcon = Icons.Default.Person,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(18.dp))

        NexaTextField(
            value = state.professionalTitle,
            onValueChange = { viewModel.updateBasicInfo(title = it) },
            label = "Professional Title",
            placeholder = "e.g. Android Developer",
            leadingIcon = Icons.Default.Work,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Dynamic Emails
        state.emails.forEachIndexed { index, email ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(12.dp))
            }
            NexaTextField(
                value = email,
                onValueChange = { viewModel.updateEmail(index, it) },
                label = if (index == 0) "Primary Email" else "Email ${index + 1}",
                placeholder = "Enter email address",
                leadingIcon = Icons.Default.Email,
                trailingIcon = if (index > 0) {
                    {
                        IconButton(onClick = { viewModel.removeEmailField(index) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove Email",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                } else null,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
        if (state.emails.size < 3) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = { viewModel.addEmailField() }) {
                    Text("+ Add another email")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dynamic Phones
        state.phones.forEachIndexed { index, phone ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(12.dp))
            }
            val isLastPhone = index == state.phones.lastIndex
            NexaTextField(
                value = phone,
                onValueChange = { viewModel.updatePhone(index, it) },
                label = if (index == 0) "Primary Phone" else "Phone ${index + 1}",
                placeholder = "Enter phone number",
                leadingIcon = Icons.Default.Phone,
                trailingIcon = if (index > 0) {
                    {
                        IconButton(onClick = { viewModel.removePhoneField(index) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove Phone",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                } else null,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Phone,
                    imeAction = if (isLastPhone) ImeAction.Done else ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
        if (state.phones.size < 3) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = { viewModel.addPhoneField() }) {
                    Text("+ Add another phone")
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SummaryStep(state: CreateProfileState, viewModel: CreateProfileViewModel) {
    val focusManager = LocalFocusManager.current
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Step Title
        Text(
            text = "Step 2: Summary & Skills",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        // Card 1: Professional Summary & Objective
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Professional Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                 NexaTextField(
                    value = state.professionalSummary,
                    onValueChange = { viewModel.updateSummary(summary = it) },
                    label = "Professional Summary",
                    placeholder = "Describe your career highlights, goals, and core expertise in detail...",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    minLines = 6,
                    maxLines = 6,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    )
                )

                NexaTextField(
                    value = state.careerObjective,
                    onValueChange = { viewModel.updateSummary(objective = it) },
                    label = "Career Objective",
                    placeholder = "e.g. Seeking a challenging role as an Android Developer to leverage my design and development skills...",
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    minLines = 3,
                    maxLines = 4
                )
            }
        }

        // Card 2: Core Skills
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Core Skills",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Add the key technical skills, languages, or tools that align with your career goals. Added skills will be showcased on your CV.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Professional Guidance Banner (Dynamic)
                AnimatedVisibility(
                    visible = state.skills.size < 3,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Requirement Guide",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Please add at least 3 core skills to proceed (Added: ${state.skills.size}/3).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NexaTextField(
                        value = state.currentSkillInput,
                        onValueChange = viewModel::updateSkillInput,
                        label = "Add Skill",
                        placeholder = "e.g. Jetpack Compose",
                        leadingIcon = Icons.Default.Add,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { 
                                if (state.currentSkillInput.isNotBlank()) {
                                    viewModel.addSkill(state.currentSkillInput)
                                }
                            }
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    
                    val skillInteractionSource = remember { MutableInteractionSource() }
                    val isPressed by skillInteractionSource.collectIsPressedAsState()
                    val buttonScale by animateFloatAsState(if (isPressed && state.currentSkillInput.isNotBlank()) 0.98f else 1f, label = "buttonScale")
                    
                    Button(
                        onClick = { 
                            if (state.currentSkillInput.isNotBlank()) {
                                viewModel.addSkill(state.currentSkillInput)
                            }
                        },
                        enabled = state.currentSkillInput.isNotBlank(),
                        interactionSource = skillInteractionSource,
                        modifier = Modifier
                            .padding(top = 22.dp) // Align with input field box (excluding its label)
                            .graphicsLayer(scaleX = buttonScale, scaleY = buttonScale),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Add")
                    }
                }

                if (state.skills.isNotEmpty()) {
                    Text(
                        text = "Added Skills (${state.skills.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.skills.forEach { skill ->
                            Surface(
                                modifier = Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { viewModel.removeSkill(skill) }
                                ),
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = skill,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove Skill",
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No skills added yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExperiencesStep(state: CreateProfileState, viewModel: CreateProfileViewModel) {
    val focusManager = LocalFocusManager.current
    val calendar = remember { java.util.Calendar.getInstance() }

    // HOISTED DIALOG STATE
    var activeStartCalendarExpId by remember { mutableStateOf<String?>(null) }
    var activeEndCalendarExpId by remember { mutableStateOf<String?>(null) }
    var activeStartMonthYearExpId by remember { mutableStateOf<String?>(null) }
    var activeEndMonthYearExpId by remember { mutableStateOf<String?>(null) }

    // START CALENDAR DIALOG
    if (activeStartCalendarExpId != null) {
        val exp = state.experiences.find { it.id == activeStartCalendarExpId }
        if (exp != null) {
            val showDay = exp.startDate.count { it == '/' } == 2 || exp.endDate.count { it == '/' } == 2
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = dateStringToMillis(exp.startDate)
            )
            DatePickerDialog(
                onDismissRequest = { activeStartCalendarExpId = null },
                confirmButton = {
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val dateStr = millisToDateString(millis, showDay)
                                viewModel.updateExperience(exp.id, exp.copy(startDate = dateStr))
                            }
                            activeStartCalendarExpId = null
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { activeStartCalendarExpId = null }) { Text("Cancel") }
                },
                shape = RoundedCornerShape(24.dp)
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        headlineContentColor = MaterialTheme.colorScheme.primary,
                        selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                        selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                        todayContentColor = MaterialTheme.colorScheme.primary,
                        todayDateBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }

    // END CALENDAR DIALOG
    if (activeEndCalendarExpId != null) {
        val exp = state.experiences.find { it.id == activeEndCalendarExpId }
        if (exp != null) {
            val showDay = exp.startDate.count { it == '/' } == 2 || exp.endDate.count { it == '/' } == 2
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = dateStringToMillis(exp.endDate)
            )
            DatePickerDialog(
                onDismissRequest = { activeEndCalendarExpId = null },
                confirmButton = {
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val dateStr = millisToDateString(millis, showDay)
                                viewModel.updateExperience(exp.id, exp.copy(endDate = dateStr))
                            }
                            activeEndCalendarExpId = null
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { activeEndCalendarExpId = null }) { Text("Cancel") }
                },
                shape = RoundedCornerShape(24.dp)
            ) {
                DatePicker(state = datePickerState, colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surface, titleContentColor = MaterialTheme.colorScheme.onSurface, headlineContentColor = MaterialTheme.colorScheme.primary, selectedDayContainerColor = MaterialTheme.colorScheme.primary, selectedDayContentColor = MaterialTheme.colorScheme.onPrimary, todayContentColor = MaterialTheme.colorScheme.primary, todayDateBorderColor = MaterialTheme.colorScheme.primary))
            }
        }
    }

    // START MONTH YEAR PICKER
    if (activeStartMonthYearExpId != null) {
        val exp = state.experiences.find { it.id == activeStartMonthYearExpId }
        if (exp != null) {
            val parsed = remember(exp.startDate) { parseMonthAndYear(exp.startDate, calendar) }
            MonthYearPickerDialog(
                initialMonth = parsed.first,
                initialYear = parsed.second,
                onDismissRequest = { activeStartMonthYearExpId = null },
                onConfirm = { m, y ->
                    val formatted = String.format("%02d/%04d", m, y)
                    viewModel.updateExperience(exp.id, exp.copy(startDate = formatted))
                    activeStartMonthYearExpId = null
                }
            )
        }
    }

    // END MONTH YEAR PICKER
    if (activeEndMonthYearExpId != null) {
        val exp = state.experiences.find { it.id == activeEndMonthYearExpId }
        if (exp != null) {
            val parsed = remember(exp.endDate) { parseMonthAndYear(exp.endDate, calendar) }
            MonthYearPickerDialog(
                initialMonth = parsed.first,
                initialYear = parsed.second,
                onDismissRequest = { activeEndMonthYearExpId = null },
                onConfirm = { m, y ->
                    val formatted = String.format("%02d/%04d", m, y)
                    viewModel.updateExperience(exp.id, exp.copy(endDate = formatted))
                    activeEndMonthYearExpId = null
                }
            )
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Step 3: Work Experience",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Highlight your career history. Add details for each relevant role directly on the screen.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (state.experiences.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Work,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No work experience added yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val addFirstInteractionSource = remember { MutableInteractionSource() }
                    val addFirstPressed by addFirstInteractionSource.collectIsPressedAsState()
                    val addFirstScale by animateFloatAsState(if (addFirstPressed) 0.98f else 1f, label = "addFirstScale")
                    
                    Button(
                        onClick = { viewModel.addExperience(com.mudasir.nexacvai.domain.model.Experience()) },
                        interactionSource = addFirstInteractionSource,
                        modifier = Modifier.graphicsLayer(scaleX = addFirstScale, scaleY = addFirstScale)
                    ) {
                        Text("+ Add Experience")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                state.experiences.forEachIndexed { index, exp ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Card Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Work,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (exp.jobTitle.isNotBlank()) exp.jobTitle else "Experience #${index + 1}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                
                                val removeInteractionSource = remember { MutableInteractionSource() }
                                val removePressed by removeInteractionSource.collectIsPressedAsState()
                                val removeScale by animateFloatAsState(if (removePressed) 0.98f else 1f, label = "removeScale")
                                
                                IconButton(
                                    onClick = { viewModel.removeExperience(exp) },
                                    interactionSource = removeInteractionSource,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .graphicsLayer(scaleX = removeScale, scaleY = removeScale)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove Role",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                            NexaTextField(
                                value = exp.jobTitle,
                                onValueChange = { viewModel.updateExperience(exp.id, exp.copy(jobTitle = it)) },
                                label = "Job Title*",
                                placeholder = "e.g. Senior Android Engineer",
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            NexaTextField(
                                value = exp.companyName,
                                onValueChange = { viewModel.updateExperience(exp.id, exp.copy(companyName = it)) },
                                label = "Company Name*",
                                placeholder = "e.g. Google",
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Employment Type Dropdown
                            var isEmpTypeExpanded by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.fillMaxWidth()) {
                                NexaTextField(
                                    value = exp.employmentType,
                                    onValueChange = {},
                                    label = "Employment Type",
                                    placeholder = "Select Employment Type",
                                    trailingIcon = {
                                        Icon(
                                            imageVector = if (isEmpTypeExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    enabled = true,
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            isEmpTypeExpanded = true
                                        }
                                )

                                DropdownMenu(
                                    expanded = isEmpTypeExpanded,
                                    onDismissRequest = { isEmpTypeExpanded = false },
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    val empTypes = listOf(
                                        "Full-time" to Icons.Default.Work,
                                        "Part-time" to Icons.Default.AccessTime,
                                        "Internship" to Icons.Default.School,
                                        "Freelance" to Icons.Default.Person
                                    )
                                    empTypes.forEach { (type, icon) ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Text(
                                                        text = type, 
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            fontWeight = FontWeight.Medium
                                                        ),
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            },
                                            onClick = {
                                                viewModel.updateExperience(exp.id, exp.copy(employmentType = type))
                                                isEmpTypeExpanded = false
                                            },
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                                        )
                                    }
                                }
                            }

                            // Work Mode Dropdown
                            var isWorkModeExpanded by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.fillMaxWidth()) {
                                NexaTextField(
                                    value = exp.workMode,
                                    onValueChange = {},
                                    label = "Work Mode",
                                    placeholder = "Select Work Mode",
                                    trailingIcon = {
                                        Icon(
                                            imageVector = if (isWorkModeExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    enabled = true,
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            isWorkModeExpanded = true
                                        }
                                )

                                DropdownMenu(
                                    expanded = isWorkModeExpanded,
                                    onDismissRequest = { isWorkModeExpanded = false },
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    val modes = listOf(
                                        "Onsite" to Icons.Default.Business,
                                        "Remote" to Icons.Default.Home,
                                        "Hybrid" to Icons.Default.Domain
                                    )
                                    modes.forEach { (mode, icon) ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Text(
                                                        text = mode, 
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            fontWeight = FontWeight.Medium
                                                        ),
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            },
                                            onClick = {
                                                viewModel.updateExperience(exp.id, exp.copy(workMode = mode))
                                                isWorkModeExpanded = false
                                            },
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                                        )
                                    }
                                }
                            }

                            NexaTextField(
                                value = exp.location,
                                onValueChange = { viewModel.updateExperience(exp.id, exp.copy(location = it)) },
                                label = "Location",
                                placeholder = "e.g. Mountain View, CA",
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            var localIncludeDay by remember(exp.id) { mutableStateOf<Boolean?>(null) }
                            val showDay by remember(localIncludeDay, exp.startDate, exp.endDate) {
                                derivedStateOf {
                                    localIncludeDay ?: (exp.startDate.count { it == '/' } == 2 || exp.endDate.count { it == '/' } == 2)
                                }
                            }

                            // Date format selector
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        localIncludeDay = !showDay
                                        val newStart = if (!showDay) {
                                            convertToDayFormat(exp.startDate)
                                        } else {
                                            convertToMonthFormat(exp.startDate)
                                        }
                                        val newEnd = if (!showDay) {
                                            convertToDayFormat(exp.endDate)
                                        } else {
                                            convertToMonthFormat(exp.endDate)
                                        }
                                        viewModel.updateExperience(
                                            exp.id,
                                            exp.copy(startDate = newStart, endDate = newEnd)
                                        )
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                NexaCheckbox(
                                    checked = showDay,
                                    onCheckedChange = { isChecked ->
                                        localIncludeDay = isChecked
                                        val newStart = if (isChecked) {
                                            convertToDayFormat(exp.startDate)
                                        } else {
                                            convertToMonthFormat(exp.startDate)
                                        }
                                        val newEnd = if (isChecked) {
                                            convertToDayFormat(exp.endDate)
                                        } else {
                                            convertToMonthFormat(exp.endDate)
                                        }
                                        viewModel.updateExperience(
                                            exp.id,
                                            exp.copy(startDate = newStart, endDate = newEnd)
                                        )
                                    }
                                )
                                Text(
                                    text = "Include day in dates (DD/MM/YYYY)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            NexaTextField(
                                value = exp.startDate.filter { it.isDigit() },
                                onValueChange = { digits ->
                                    val filtered = digits.filter { it.isDigit() }
                                    val limited = if (showDay) filtered.take(8) else filtered.take(6)
                                    val formatted = if (showDay) {
                                        when {
                                            limited.length <= 2 -> limited
                                            limited.length <= 4 -> "${limited.substring(0, 2)}/${limited.substring(2)}"
                                            else -> "${limited.substring(0, 2)}/${limited.substring(2, 4)}/${limited.substring(4)}"
                                        }
                                    } else {
                                        if (limited.length <= 2) limited else "${limited.substring(0, 2)}/${limited.substring(2)}"
                                    }
                                    viewModel.updateExperience(exp.id, exp.copy(startDate = formatted))
                                },
                                label = "Start Date*",
                                placeholder = if (showDay) "DD/MM/YYYY" else "MM/YYYY",
                                leadingIcon = Icons.Default.CalendarToday,
                                onLeadingIconClick = {
                                    if (showDay) {
                                        activeStartCalendarExpId = exp.id
                                    } else {
                                        activeStartMonthYearExpId = exp.id
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                visualTransformation = remember(showDay) { DateVisualTransformation(showDay) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Currently working toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        val isChecked = !exp.isCurrentlyWorking
                                        viewModel.updateExperience(
                                            exp.id, 
                                            exp.copy(
                                                isCurrentlyWorking = isChecked,
                                                endDate = if (isChecked) "Present" else ""
                                            )
                                        )
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                NexaCheckbox(
                                    checked = exp.isCurrentlyWorking,
                                    onCheckedChange = { isChecked ->
                                        viewModel.updateExperience(
                                            exp.id, 
                                            exp.copy(
                                                isCurrentlyWorking = isChecked,
                                                endDate = if (isChecked) "Present" else ""
                                            )
                                        )
                                    }
                                )
                                Text(
                                    text = "I am currently working in this role",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (!exp.isCurrentlyWorking) {
                                NexaTextField(
                                    value = exp.endDate.filter { it.isDigit() },
                                    onValueChange = { digits ->
                                        val filtered = digits.filter { it.isDigit() }
                                        val limited = if (showDay) filtered.take(8) else filtered.take(6)
                                        val formatted = if (showDay) {
                                            when {
                                                limited.length <= 2 -> limited
                                                limited.length <= 4 -> "${limited.substring(0, 2)}/${limited.substring(2)}"
                                                else -> "${limited.substring(0, 2)}/${limited.substring(2, 4)}/${limited.substring(4)}"
                                            }
                                        } else {
                                            if (limited.length <= 2) limited else "${limited.substring(0, 2)}/${limited.substring(2)}"
                                        }
                                        viewModel.updateExperience(exp.id, exp.copy(endDate = formatted))
                                    },
                                    label = "End Date",
                                    placeholder = if (showDay) "DD/MM/YYYY" else "MM/YYYY",
                                    leadingIcon = Icons.Default.CalendarToday,
                                    onLeadingIconClick = {
                                        if (showDay) {
                                            activeEndCalendarExpId = exp.id
                                        } else {
                                            activeEndMonthYearExpId = exp.id
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next
                                    ),
                                    visualTransformation = remember(showDay) { DateVisualTransformation(showDay) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            NexaTextField(
                                value = exp.description,
                                onValueChange = { viewModel.updateExperience(exp.id, exp.copy(description = it)) },
                                label = "Description & Summary",
                                placeholder = "Describe your role, key details, and overview...",
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = false,
                                minLines = 3,
                                maxLines = 4,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    imeAction = ImeAction.Next
                                )
                            )

                            NexaTextField(
                                value = exp.responsibilities,
                                onValueChange = { viewModel.updateExperience(exp.id, exp.copy(responsibilities = it)) },
                                label = "Responsibilities",
                                placeholder = "Describe your main duties and daily tasks...",
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = false,
                                minLines = 3,
                                maxLines = 4,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    imeAction = ImeAction.Next
                                )
                            )

                            NexaTextField(
                                value = exp.achievements,
                                onValueChange = { viewModel.updateExperience(exp.id, exp.copy(achievements = it)) },
                                label = "Key Achievements",
                                placeholder = "Describe major highlights (e.g. improved app launch speed by 40%)...",
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = false,
                                minLines = 3,
                                maxLines = 4,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    imeAction = ImeAction.Next
                                )
                            )

                            var techInput by remember(exp.id) { mutableStateOf(exp.technologiesUsed.joinToString(", ")) }
                            LaunchedEffect(exp.technologiesUsed) {
                                val currentList = techInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                if (currentList != exp.technologiesUsed) {
                                    techInput = exp.technologiesUsed.joinToString(", ")
                                }
                            }

                            NexaTextField(
                                value = techInput,
                                onValueChange = { 
                                    techInput = it
                                    val techList = it.split(",")
                                        .map { tech -> tech.trim() }
                                        .filter { tech -> tech.isNotBlank() }
                                    viewModel.updateExperience(exp.id, exp.copy(technologiesUsed = techList))
                                },
                                label = "Technologies Used",
                                placeholder = "e.g. Kotlin, Jetpack Compose, Room (comma separated)",
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = false,
                                minLines = 2,
                                maxLines = 3,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() }
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                
                val addAnotherInteractionSource = remember { MutableInteractionSource() }
                val addAnotherPressed by addAnotherInteractionSource.collectIsPressedAsState()
                val addAnotherScale by animateFloatAsState(if (addAnotherPressed) 0.98f else 1f, label = "addAnotherScale")
                
                OutlinedButton(
                    onClick = { viewModel.addExperience(com.mudasir.nexacvai.domain.model.Experience()) },
                    interactionSource = addAnotherInteractionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer(scaleX = addAnotherScale, scaleY = addAnotherScale)
                ) {
                    Text("+ Add Another Experience")
                }
            }
        }
    }
}

@Composable
fun PlaceholderStep(step: Int, total: Int) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Step ${step + 1} of $total\n(Section UI coming soon)", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun ReferencesStep(state: CreateProfileState, viewModel: CreateProfileViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    
    var fullName by remember { mutableStateOf("") }
    var jobTitle by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var linkedIn by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Reference", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NexaTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = "Full Name*",
                        placeholder = "e.g. John Doe",
                        leadingIcon = Icons.Default.Person,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            keyboardType = KeyboardType.Text
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    NexaTextField(
                        value = jobTitle,
                        onValueChange = { jobTitle = it },
                        label = "Job Title*",
                        placeholder = "e.g. Product Manager",
                        leadingIcon = Icons.Default.Work,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            keyboardType = KeyboardType.Text
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    NexaTextField(
                        value = company,
                        onValueChange = { company = it },
                        label = "Company*",
                        placeholder = "e.g. Google",
                        leadingIcon = Icons.Default.Business,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            keyboardType = KeyboardType.Text
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    NexaTextField(
                        value = relationship,
                        onValueChange = { relationship = it },
                        label = "Relationship",
                        placeholder = "e.g. Former Manager",
                        leadingIcon = Icons.Default.Groups,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            keyboardType = KeyboardType.Text
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    NexaTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        placeholder = "e.g. reference@company.com",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            keyboardType = KeyboardType.Email
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    NexaTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Phone",
                        placeholder = "e.g. +1 234 567 890",
                        leadingIcon = Icons.Default.Phone,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            keyboardType = KeyboardType.Phone
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    NexaTextField(
                        value = linkedIn,
                        onValueChange = { linkedIn = it },
                        label = "LinkedIn URL",
                        placeholder = "e.g. linkedin.com/in/username",
                        leadingIcon = Icons.Default.Link,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            keyboardType = KeyboardType.Uri
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    NexaTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = "Notes",
                        placeholder = "Any additional context about this reference...",
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            keyboardType = KeyboardType.Text
                        ),
                        singleLine = false,
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addReference(Reference(
                            fullName = fullName,
                            jobTitle = jobTitle,
                            company = company,
                            relationship = relationship,
                            email = email.takeIf { it.isNotBlank() },
                            phone = phone.takeIf { it.isNotBlank() },
                            linkedInUrl = linkedIn.takeIf { it.isNotBlank() },
                            notes = notes.takeIf { it.isNotBlank() },
                            includeInResume = true
                        ))
                        showDialog = false
                        fullName = ""; jobTitle = ""; company = ""; relationship = ""; email = ""; phone = ""; linkedIn = ""; notes = ""
                    },
                    enabled = fullName.isNotBlank() && jobTitle.isNotBlank() && company.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Step 8: References", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Add professionals who can verify your skills and experience. References are optional.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (state.references.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No references added yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showDialog = true }) {
                        Text("+ Add Reference")
                    }
                }
            }
        } else {
            state.references.forEach { ref ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = ref.fullName, style = MaterialTheme.typography.titleMedium)
                            IconButton(onClick = { viewModel.removeReference(ref) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                        Text("${ref.jobTitle} at ${ref.company}", style = MaterialTheme.typography.bodyMedium)
                        if (ref.relationship.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                                Text(ref.relationship, color = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.padding(horizontal = 4.dp))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Text("+ Add Another Reference")
            }
        }
    }
}

private fun convertToDayFormat(date: String): String {
    if (date.isBlank() || date.equals("Present", ignoreCase = true)) return date
    val parts = date.split("/")
    if (parts.size == 2) {
        return "01/${parts[0]}/${parts[1]}"
    }
    return date
}

private fun convertToMonthFormat(date: String): String {
    if (date.isBlank() || date.equals("Present", ignoreCase = true)) return date
    val parts = date.split("/")
    if (parts.size == 3) {
        return "${parts[1]}/${parts[2]}"
    }
    return date
}

private fun parseMonthAndYear(dateStr: String, defaultCalendar: java.util.Calendar): Pair<Int, Int> {
    var month = defaultCalendar.get(java.util.Calendar.MONTH) + 1
    var year = defaultCalendar.get(java.util.Calendar.YEAR)
    if (dateStr.isNotBlank() && !dateStr.equals("Present", ignoreCase = true)) {
        try {
            val parts = dateStr.split("/")
            if (parts.size == 3) { // DD/MM/YYYY
                month = parts[1].toIntOrNull() ?: month
                year = parts[2].toIntOrNull() ?: year
            } else if (parts.size == 2) { // MM/YYYY
                month = parts[0].toIntOrNull() ?: month
                year = parts[1].toIntOrNull() ?: year
            }
        } catch (e: Exception) {
            // ignore
        }
    }
    return Pair(month, year)
}

@Composable
private fun MonthYearPickerDialog(
    initialMonth: Int,
    initialYear: Int,
    onDismissRequest: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var selectedMonth by remember { mutableStateOf(initialMonth) }
    var selectedYear by remember { mutableStateOf(initialYear) }
    var isEditingYear by remember { mutableStateOf(false) }
    var yearInputText by remember { mutableStateOf(selectedYear.toString()) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    LaunchedEffect(isEditingYear) {
        if (isEditingYear) {
            kotlinx.coroutines.delay(100)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedMonth, selectedYear) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        title = {
            Text(
                text = "Select Month & Year",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Year Selector Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { 
                        selectedYear-- 
                        yearInputText = selectedYear.toString()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowLeft, contentDescription = "Previous Year")
                    }
                    
                    if (isEditingYear) {
                        androidx.compose.foundation.text.BasicTextField(
                            value = yearInputText,
                            onValueChange = { newValue ->
                                if (newValue.length <= 4 && newValue.all { it.isDigit() }) {
                                    yearInputText = newValue
                                    val parsedYear = newValue.toIntOrNull()
                                    if (parsedYear != null && parsedYear in 1900..2100) {
                                        selectedYear = parsedYear
                                    }
                                }
                            },
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    val parsedYear = yearInputText.toIntOrNull()
                                    if (parsedYear != null && parsedYear in 1900..2100) {
                                        selectedYear = parsedYear
                                    }
                                    isEditingYear = false
                                }
                            ),
                            modifier = Modifier
                                .width(80.dp)
                                .focusRequester(focusRequester)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            singleLine = true
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.clickable {
                                isEditingYear = true
                                yearInputText = selectedYear.toString()
                            }
                        ) {
                            Text(
                                text = selectedYear.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Year",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    IconButton(onClick = { 
                        selectedYear++ 
                        yearInputText = selectedYear.toString()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowRight, contentDescription = "Next Year")
                    }
                }

                // Months Grid (3x4)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (row in 0 until 4) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (col in 0 until 3) {
                                val monthIndex = row * 3 + col
                                val isSelected = selectedMonth == monthIndex + 1
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        )
                                        .clickable { selectedMonth = monthIndex + 1 }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = months[monthIndex],
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun NexaCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    // Tactile bouncy spring animation
    val scale by animateFloatAsState(
        targetValue = if (checked) 1.08f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "checkboxScale"
    )

    // Smooth color state interpolation
    val backgroundColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 200),
        label = "checkboxBackground"
    )

    val borderColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 200),
        label = "checkboxBorder"
    )

    Box(
        modifier = modifier
            .size(22.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .border(
                width = 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onCheckedChange(!checked)
            },
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = checked,
            enter = fadeIn(animationSpec = androidx.compose.animation.core.tween(100)) + 
                    scaleIn(initialScale = 0.5f, animationSpec = androidx.compose.animation.core.tween(150)),
            exit = fadeOut(animationSpec = androidx.compose.animation.core.tween(100)) + 
                   scaleOut(targetScale = 0.5f, animationSpec = androidx.compose.animation.core.tween(150))
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

private fun dateStringToMillis(dateStr: String): Long? {
    if (dateStr.isBlank() || dateStr.equals("Present", ignoreCase = true)) return null
    return try {
        val format = if (dateStr.count { it == '/' } == 2) {
            java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        } else {
            java.text.SimpleDateFormat("MM/yyyy", java.util.Locale.getDefault())
        }
        format.parse(dateStr)?.time
    } catch (e: Exception) {
        null
    }
}

private fun millisToDateString(millis: Long, showDay: Boolean): String {
    val date = java.util.Date(millis)
    val format = if (showDay) {
        java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
    } else {
        java.text.SimpleDateFormat("MM/yyyy", java.util.Locale.getDefault())
    }
    return format.format(date)
}

private fun formatSmartDateInput(input: String, showDay: Boolean): String {
    if (input.isBlank() || input.equals("Present", ignoreCase = true)) return input
    val digits = input.filter { it.isDigit() }
    return if (showDay) {
        val limited = digits.take(8)
        when {
            limited.length <= 2 -> limited
            limited.length <= 4 -> {
                "${limited.substring(0, 2)}/${limited.substring(2)}"
            }
            else -> {
                "${limited.substring(0, 2)}/${limited.substring(2, 4)}/${limited.substring(4)}"
            }
        }
    } else {
        val limited = digits.take(6)
        if (limited.length <= 2) {
            limited
        } else {
            "${limited.substring(0, 2)}/${limited.substring(2)}"
        }
    }
}

private class DateVisualTransformation(private val showDay: Boolean) : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): TransformedText {
        val raw = text.text.filter { it.isDigit() }
        val formatted = if (showDay) {
            val limited = raw.take(8)
            when {
                limited.length <= 2 -> limited
                limited.length <= 4 -> {
                    "${limited.substring(0, 2)}/${limited.substring(2)}"
                }
                else -> {
                    "${limited.substring(0, 2)}/${limited.substring(2, 4)}/${limited.substring(4)}"
                }
            }
        } else {
            val limited = raw.take(6)
            if (limited.length <= 2) {
                limited
            } else {
                "${limited.substring(0, 2)}/${limited.substring(2)}"
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (showDay) {
                    return when {
                        offset <= 2 -> offset
                        offset <= 4 -> offset + 1
                        else -> offset + 2
                    }
                } else {
                    return when {
                        offset <= 2 -> offset
                        else -> offset + 1
                    }
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (showDay) {
                    val rawLen = raw.length
                    return when {
                        offset <= 2 -> offset
                        offset <= 5 -> (offset - 1).coerceAtMost(rawLen)
                        else -> (offset - 2).coerceAtMost(rawLen)
                    }
                } else {
                    val rawLen = raw.length
                    return when {
                        offset <= 2 -> offset
                        else -> (offset - 1).coerceAtMost(rawLen)
                    }
                }
            }
        }

        return TransformedText(
            text = androidx.compose.ui.text.AnnotatedString(formatted),
            offsetMapping = offsetMapping
        )
    }
}