package com.mudasir.nexacvai.presentation.ui.profiles

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mudasir.nexacvai.domain.model.Reference
import com.mudasir.nexacvai.presentation.ui.components.ImagePickerDialog
import com.mudasir.nexacvai.presentation.ui.components.NexaTextField
import org.koin.androidx.compose.koinViewModel
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

    // Navigate back when saved successfully
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            navController.popBackStack()
        }
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
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                LinearProgressIndicator(
                    progress = { (state.currentStep + 1) / state.totalSteps.toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.imePadding(),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .then(
                            if (WindowInsets.isImeVisible) Modifier else Modifier.navigationBarsPadding()
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (state.currentStep > 0) {
                        OutlinedButton(onClick = { viewModel.previousStep() }) {
                            Text("Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (state.currentStep < state.totalSteps - 1) {
                        val isNextEnabled = when (state.currentStep) {
                            0 -> state.fullName.isNotBlank() && state.professionalTitle.isNotBlank()
                            else -> true
                        }
                        Button(
                            onClick = { viewModel.nextStep() },
                            enabled = isNextEnabled
                        ) {
                            Text("Next")
                        }
                    } else {
                        Button(
                            onClick = { viewModel.saveProfile() },
                            enabled = state.fullName.isNotBlank() && state.professionalTitle.isNotBlank() && !state.isSaving
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
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        AnimatedContent(
            targetState = state.currentStep,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith 
                    slideOutHorizontally { width -> -width } + fadeOut()
                } else {
                    slideInHorizontally { width -> -width } + fadeIn() togetherWith 
                    slideOutHorizontally { width -> width } + fadeOut()
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    2 -> SkillsStep(state, viewModel)
                    7 -> ReferencesStep(state, viewModel)
                    else -> PlaceholderStep(step, state.totalSteps)
                }
                Spacer(modifier = Modifier.height(32.dp))
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

@Composable
fun SummaryStep(state: CreateProfileState, viewModel: CreateProfileViewModel) {
    Text("Step 2: Professional Summary", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
    
    NexaTextField(
        value = state.professionalSummary,
        onValueChange = { viewModel.updateSummary(summary = it) },
        label = "Professional Summary",
        placeholder = "Describe your career highlights, goals, and core expertise...",
        modifier = Modifier.fillMaxWidth(),
        singleLine = false,
        minLines = 6
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillsStep(state: CreateProfileState, viewModel: CreateProfileViewModel) {
    Text("Step 3: Skills", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
    
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
            leadingIcon = Icons.Default.Star,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { viewModel.addSkill(state.currentSkillInput) }
            ),
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        Button(
            onClick = { viewModel.addSkill(state.currentSkillInput) },
            modifier = Modifier.padding(top = 22.dp) // Align with the input box center since label is above
        ) {
            Text("Add")
        }
    }
    
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        state.skills.forEach { skill ->
            InputChip(
                selected = false,
                onClick = { viewModel.removeSkill(skill) },
                label = { Text(skill) },
                trailingIcon = { Icon(Icons.Default.Close, "Remove") }
            )
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
