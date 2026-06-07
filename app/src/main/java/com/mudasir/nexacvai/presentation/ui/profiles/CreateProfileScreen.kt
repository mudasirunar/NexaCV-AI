package com.mudasir.nexacvai.presentation.ui.profiles

import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.mudasir.nexacvai.presentation.ui.profiles.components.create_profile.BasicInfoSkeleton
import com.mudasir.nexacvai.presentation.ui.components.NexaAlertDialog
import org.koin.androidx.compose.koinViewModel
import com.mudasir.nexacvai.presentation.ui.profiles.steps.*
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.*
import java.io.File

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

    val basicInfoState = remember(state.fullName, state.professionalTitle, state.emails, state.phones, state.dateOfBirth, state.address, state.yearsOfExperience, state.profilePictureUri, state.profileId, state.tempSessionId) {
        BasicInfoStepState(
            fullName = state.fullName,
            professionalTitle = state.professionalTitle,
            emails = state.emails,
            phones = state.phones,
            dateOfBirth = state.dateOfBirth,
            address = state.address,
            yearsOfExperience = state.yearsOfExperience,
            profilePictureUri = state.profilePictureUri,
            profileId = state.profileId,
            tempSessionId = state.tempSessionId
        )
    }

    val summaryState = remember(state.professionalSummary, state.skills, state.currentSkillInput) {
        SummaryStepState(
            professionalSummary = state.professionalSummary,
            skills = state.skills,
            currentSkillInput = state.currentSkillInput
        )
    }

    val expProjState = remember(state.experiences, state.projects) {
        ExperienceProjectsStepState(
            experiences = state.experiences,
            projects = state.projects
        )
    }

    val eduCertState = remember(state.educations, state.certifications) {
        EducationCertsStepState(
            educations = state.educations,
            certifications = state.certifications
        )
    }

    val socialsState = remember(state.socialLinks, state.languages, state.references, state.hobbies, state.volunteerWork, state.awards) {
        SocialsExtrasStepState(
            socialLinks = state.socialLinks,
            languages = state.languages,
            references = state.references,
            hobbies = state.hobbies,
            volunteerWork = state.volunteerWork,
            awards = state.awards
        )
    }

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
            if (event == Lifecycle.Event.ON_RESUME) {
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
        NexaAlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = "Discard changes?",
            message = "You have unsaved changes. Are you sure you want to discard them and go back?",
            confirmLabel = "Discard",
            onConfirm = {
                showDiscardDialog = false
                navController.popBackStack()
            },
            dismissLabel = "Cancel",
            isDestructive = true
        )
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isLandscapePhone = isLandscape && configuration.smallestScreenWidthDp < 600
    val isImeVisible = WindowInsets.isImeVisible
 
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .animateContentSize(animationSpec = tween(250))
            ) {
                AnimatedVisibility(
                    visible = !(isLandscapePhone && isImeVisible),
                    enter = expandVertically(animationSpec = tween(250)) + fadeIn(tween(250)),
                    exit = shrinkVertically(animationSpec = tween(250)) + fadeOut(tween(250))
                ) {
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
                        ),
                        windowInsets = WindowInsets(0.dp)
                    )
                }
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
                Spacer(modifier = Modifier.height(8.dp))
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
                            .padding(horizontal = 16.dp, vertical = 10.dp)
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
                                    modifier = Modifier
                                        .height(38.dp)
                                        .graphicsLayer(scaleX = backScale, scaleY = backScale),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp)
                                ) {
                                    Text("Back", style = MaterialTheme.typography.labelLarge)
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
                                    modifier = Modifier
                                        .height(38.dp)
                                        .graphicsLayer(scaleX = saveMidwayScale, scaleY = saveMidwayScale),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp)
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
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text("Save Draft", style = MaterialTheme.typography.labelLarge)
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
                                val isNextEnabled by remember(state.currentStep, state.fullName, state.professionalTitle, state.skills, state.experiences, state.projects, state.educations, state.certifications, state.references, state.socialLinks, state.languages) {
                                    derivedStateOf {
                                        when (state.currentStep) {
                                            0 -> state.fullName.isNotBlank() && state.professionalTitle.isNotBlank()
                                            1 -> state.skills.size >= 1
                                            2 -> state.experiences.all { it.jobTitle.isNotBlank() && it.companyName.isNotBlank() } &&
                                                 state.projects.all { it.projectName.isNotBlank() }
                                            3 -> state.educations.isNotEmpty() && state.educations.all { it.degree.isNotBlank() && it.instituteName.isNotBlank() } &&
                                                 state.certifications.all { it.certificationName.isNotBlank() && it.issuingOrganization.isNotBlank() }
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
                                    modifier = Modifier
                                        .height(38.dp)
                                        .graphicsLayer(scaleX = nextScale, scaleY = nextScale),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp)
                                ) {
                                    Text("Next", style = MaterialTheme.typography.labelLarge)
                                }
                            } else {
                                val isSaveEnabled by remember(state.fullName, state.socialLinks, state.references, state.languages) {
                                    derivedStateOf {
                                        state.fullName.isNotBlank() &&
                                        state.socialLinks.all { it.label.isNotBlank() && it.label != "Other" && it.url.isNotBlank() } &&
                                        state.references.all { it.fullName.isNotBlank() && it.jobTitle.isNotBlank() && it.company.isNotBlank() } &&
                                        state.languages.all { it.languageName.isNotBlank() && it.proficiency.isNotBlank() }
                                    }
                                }
                                val saveInteractionSource = remember { MutableInteractionSource() }
                                val savePressed by saveInteractionSource.collectIsPressedAsState()
                                val saveScale by animateFloatAsState(if (savePressed) 0.98f else 1f, label = "saveScale")
                                Button(
                                    onClick = {
                                        if (isSaveEnabled && !state.isSaving) {
                                            viewModel.saveProfile()
                                        }
                                    },
                                    enabled = isSaveEnabled,
                                    interactionSource = saveInteractionSource,
                                    modifier = Modifier
                                        .height(38.dp)
                                        .graphicsLayer(scaleX = saveScale, scaleY = saveScale),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp)
                                ) {
                                    if (state.isSaving) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text("Save Profile", style = MaterialTheme.typography.labelLarge)
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
                BasicInfoSkeleton()
            } else {
                AnimatedContent(
                    targetState = state.currentStep,
                    transitionSpec = {
                        val enterDuration = 250
                        val exitDuration = 250
                        if (targetState > initialState) {
                            (slideInHorizontally(
                                animationSpec = tween(enterDuration, easing = androidx.compose.animation.core.LinearOutSlowInEasing)
                            ) { width -> width } + fadeIn(tween(enterDuration))) togetherWith
                            (slideOutHorizontally(
                                animationSpec = tween(exitDuration, easing = androidx.compose.animation.core.FastOutLinearInEasing)
                            ) { width -> -width } + fadeOut(tween(exitDuration)))
                        } else {
                            (slideInHorizontally(
                                animationSpec = tween(enterDuration, easing = androidx.compose.animation.core.LinearOutSlowInEasing)
                            ) { width -> -width } + fadeIn(tween(enterDuration))) togetherWith
                            (slideOutHorizontally(
                                animationSpec = tween(exitDuration, easing = androidx.compose.animation.core.FastOutLinearInEasing)
                            ) { width -> width } + fadeOut(tween(exitDuration)))
                        } using SizeTransform(clip = false)
                    },
                    modifier = Modifier.fillMaxSize(),
                    label = "StepTransition"
                ) { step ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        when (step) {
                            0 -> BasicInfoStep(basicInfoState, viewModel)
                            1 -> SummaryStep(summaryState, viewModel)
                            2 -> ExperienceProjectsStep(expProjState, viewModel)
                            3 -> EducationCertsStep(eduCertState, viewModel)
                            4 -> SocialsExtrasStep(socialsState, viewModel)
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}