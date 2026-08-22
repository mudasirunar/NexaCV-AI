package com.mudasir.nexacvai.presentation.ui.profiles

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.mudasir.nexacvai.presentation.ui.components.NexaButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.mudasir.nexacvai.presentation.ui.components.NexaAlertDialog
import com.mudasir.nexacvai.presentation.ui.profiles.components.create_profile.BasicInfoSkeleton
import com.mudasir.nexacvai.presentation.ui.profiles.steps.BasicInfoStep
import com.mudasir.nexacvai.presentation.ui.profiles.steps.EducationCertsStep
import com.mudasir.nexacvai.presentation.ui.profiles.steps.ExperienceProjectsStep
import com.mudasir.nexacvai.presentation.ui.profiles.steps.SocialsExtrasStep
import com.mudasir.nexacvai.presentation.ui.profiles.steps.SummaryStep
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.BasicInfoStepState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileViewModel
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.EducationCertsStepState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.ExperienceProjectsStepState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.SocialsExtrasStepState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.SummaryStepState
import androidx.hilt.navigation.compose.hiltViewModel
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
    viewModel: CreateProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isEditing = state.profileId != null
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val basicInfoState = remember(state.fullName, state.professionalTitle, state.fullNameError, state.professionalTitleError, state.emails, state.phones, state.dateOfBirth, state.address, state.yearsOfExperience, state.profilePictureUri, state.profileId, state.tempSessionId, state.validationTrigger) {
        BasicInfoStepState(
            fullName = state.fullName,
            professionalTitle = state.professionalTitle,
            fullNameError = state.fullNameError,
            professionalTitleError = state.professionalTitleError,
            emails = state.emails,
            phones = state.phones,
            dateOfBirth = state.dateOfBirth,
            address = state.address,
            yearsOfExperience = state.yearsOfExperience,
            profilePictureUri = state.profilePictureUri,
            profileId = state.profileId,
            tempSessionId = state.tempSessionId,
            validationTrigger = state.validationTrigger
        )
    }

    val summaryState = remember(state.professionalSummary, state.skills, state.skillsError, state.currentSkillInput, state.duplicateSkillError, state.validationTrigger) {
        SummaryStepState(
            professionalSummary = state.professionalSummary,
            skills = state.skills,
            skillsError = state.skillsError,
            currentSkillInput = state.currentSkillInput,
            duplicateSkillError = state.duplicateSkillError,
            validationTrigger = state.validationTrigger
        )
    }

    val expProjState = remember(state.experiences, state.projects, state.experienceError, state.projectError, state.validationTrigger) {
        ExperienceProjectsStepState(
            experiences = state.experiences,
            projects = state.projects,
            experienceError = state.experienceError,
            projectError = state.projectError,
            validationTrigger = state.validationTrigger
        )
    }

    val eduCertState = remember(state.educations, state.certifications, state.educationError, state.certificationError, state.validationTrigger) {
        EducationCertsStepState(
            educations = state.educations,
            certifications = state.certifications,
            educationError = state.educationError,
            certificationError = state.certificationError,
            validationTrigger = state.validationTrigger
        )
    }

    val socialsState = remember(state.socialLinks, state.languages, state.references, state.socialLinksError, state.languagesError, state.referencesError, state.hobbies, state.volunteerWork, state.awards, state.validationTrigger) {
        SocialsExtrasStepState(
            socialLinks = state.socialLinks,
            languages = state.languages,
            references = state.references,
            socialLinksError = state.socialLinksError,
            languagesError = state.languagesError,
            referencesError = state.referencesError,
            hobbies = state.hobbies,
            volunteerWork = state.volunteerWork,
            awards = state.awards,
            validationTrigger = state.validationTrigger
        )
    }

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
            isDestructive = false
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
                modifier = Modifier
                    .imePadding()
                    .then(
                        if (WindowInsets.isImeVisible) Modifier else Modifier.navigationBarsPadding()
                    ),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Column {
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), thickness = 1.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (state.currentStep > 0) {
                            NexaButton(
                                onClick = { viewModel.previousStep() },
                                text = "Back",
                                modifier = Modifier.height(38.dp),
                                hasBorder = true,
                                borderColor = MaterialTheme.colorScheme.outline,
                                fillColor = MaterialTheme.colorScheme.surface,
                                fillOpacity = 0.0f,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            )
                        }
                    }
                        Box(
                            modifier = Modifier.weight(1.2f),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.animation.AnimatedVisibility(
                                visible = state.fullName.isNotBlank() && state.currentStep < state.totalSteps - 1,
                                enter = fadeIn() + scaleIn(initialScale = 0.9f),
                                exit = fadeOut() + scaleOut(targetScale = 0.9f)
                            ) {
                                NexaButton(
                                    onClick = {
                                        focusManager.clearFocus()
                                        keyboardController?.hide()
                                        if (!state.isSaving) {
                                            viewModel.saveDraft()
                                        }
                                    },
                                    modifier = Modifier.height(38.dp),
                                    enabled = !state.isSaving,
                                    hasBorder = true,
                                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                    fillColor = MaterialTheme.colorScheme.primary,
                                    fillOpacity = 0.08f,
                                    contentColor = MaterialTheme.colorScheme.primary,
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

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            if (state.currentStep < state.totalSteps - 1) {
                                NexaButton(
                                    onClick = {
                                        focusManager.clearFocus()
                                        keyboardController?.hide()
                                        viewModel.nextStep()
                                    },
                                    text = "Next",
                                    enabled = !state.isSaving,
                                    modifier = Modifier.height(38.dp),
                                    hasBorder = false,
                                    fillColor = MaterialTheme.colorScheme.primary,
                                    fillOpacity = 1.0f,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    contentPadding = PaddingValues(horizontal = 16.dp)
                                )
                            } else {
                                NexaButton(
                                    onClick = {
                                        focusManager.clearFocus()
                                        keyboardController?.hide()
                                        if (!state.isSaving) {
                                            viewModel.saveProfile()
                                        }
                                    },
                                    enabled = !state.isSaving,
                                    modifier = Modifier.height(38.dp),
                                    hasBorder = false,
                                    fillColor = MaterialTheme.colorScheme.primary,
                                    fillOpacity = 1.0f,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else if (!isTransitionComplete) {
                BasicInfoSkeleton()
            } else {
                AnimatedContent(
                    targetState = state.currentStep,
                    transitionSpec = {
                        val enterDuration = 250
                        val exitDuration = 250
                        if (targetState > initialState) {
                            (slideInHorizontally(
                                animationSpec = tween(enterDuration, easing = LinearOutSlowInEasing)
                            ) { width -> width } + fadeIn(tween(enterDuration))) togetherWith
                            (slideOutHorizontally(
                                animationSpec = tween(exitDuration, easing = FastOutLinearInEasing)
                            ) { width -> -width } + fadeOut(tween(exitDuration)))
                        } else {
                            (slideInHorizontally(
                                animationSpec = tween(enterDuration, easing = LinearOutSlowInEasing)
                            ) { width -> -width } + fadeIn(tween(enterDuration))) togetherWith
                            (slideOutHorizontally(
                                animationSpec = tween(exitDuration, easing = FastOutLinearInEasing)
                            ) { width -> width } + fadeOut(tween(exitDuration)))
                        } using SizeTransform(clip = false)
                    },
                    modifier = Modifier.fillMaxSize(),
                    label = "StepTransition"
                ) { step ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp)
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