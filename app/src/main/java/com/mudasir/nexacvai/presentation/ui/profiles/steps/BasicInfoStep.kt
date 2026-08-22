package com.mudasir.nexacvai.presentation.ui.profiles.steps

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mudasir.nexacvai.core.utils.ImageCompressionHelper
import com.mudasir.nexacvai.presentation.ui.components.NexaDateTextField
import com.mudasir.nexacvai.presentation.ui.components.NexaTextField
import com.mudasir.nexacvai.presentation.ui.profiles.components.ImagePickerDialog
import com.mudasir.nexacvai.presentation.ui.profiles.createImageFileUri
import com.mudasir.nexacvai.presentation.ui.profiles.utils.NexaDatePicker
import com.mudasir.nexacvai.presentation.ui.profiles.utils.dateStringToMillis
import com.mudasir.nexacvai.presentation.ui.profiles.utils.millisToDateString
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.BasicInfoStepState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicInfoStep(state: BasicInfoStepState, viewModel: CreateProfileViewModel) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    var isProcessingImage by remember { mutableStateOf(false) }
    var showImageDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showDobPicker by remember { mutableStateOf(false) }

    if (showDobPicker) {
        key(showDobPicker) {
            NexaDatePicker(
                initialDateMillis = dateStringToMillis(state.dateOfBirth),
                onDismissRequest = { showDobPicker = false },
                onDateSelected = { millis ->
                    if (millis != null) {
                        val dateStr = millisToDateString(millis, true)
                        viewModel.updateBasicInfo(dateOfBirth = dateStr)
                    }
                    showDobPicker = false
                }
            )
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                coroutineScope.launch {
                    isProcessingImage = true
                    val compressedUri = ImageCompressionHelper.compressAndSaveProfilePicture(context, it, state.profileId ?: state.tempSessionId)
                    if (compressedUri != null) {
                        viewModel.updateBasicInfo(profilePictureUri = compressedUri)
                    }
                    isProcessingImage = false
                }
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempCameraUri?.let {
                    coroutineScope.launch {
                        isProcessingImage = true
                        val compressedUri = ImageCompressionHelper.compressAndSaveProfilePicture(context, it, state.profileId ?: state.tempSessionId)
                        if (compressedUri != null) {
                            viewModel.updateBasicInfo(profilePictureUri = compressedUri)
                        }
                        isProcessingImage = false
                    }
                }
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

    val scrollState = rememberScrollState()
    val nameRequester = remember { BringIntoViewRequester() }
    val titleRequester = remember { BringIntoViewRequester() }

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(state.validationTrigger) {
        if (state.validationTrigger > 0L) {
            if (state.fullNameError != null || state.professionalTitleError != null) {
                focusManager.clearFocus()
                keyboardController?.hide()
                if (state.fullNameError != null) {
                    scrollState.animateScrollTo(0)
                } else if (state.professionalTitleError != null) {
                    titleRequester.bringIntoView()
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth().verticalScroll(scrollState)) {
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
                    if (isProcessingImage) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else if (state.profilePictureUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(state.profilePictureUri)
                                .crossfade(true)
                                .build(),
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
                
                // Camera Overlay Circle
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
            label = "Full Name*",
            placeholder = "Enter your full name",
            leadingIcon = Icons.Default.Person,
            isError = state.fullNameError != null,
            errorMessage = state.fullNameError,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .bringIntoViewRequester(nameRequester),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(18.dp))

        NexaTextField(
            value = state.professionalTitle,
            onValueChange = { viewModel.updateBasicInfo(title = it) },
            label = "Professional Title*",
            placeholder = "e.g. Android Developer",
            leadingIcon = Icons.Default.Work,
            isError = state.professionalTitleError != null,
            errorMessage = state.professionalTitleError,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .bringIntoViewRequester(titleRequester),
            singleLine = false,
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(20.dp))

        NexaDateTextField(
            value = state.dateOfBirth,
            onValueChange = { formatted ->
                viewModel.updateBasicInfo(dateOfBirth = formatted)
            },
            label = "Date of Birth",
            showDay = true,
            onLeadingIconClick = { showDobPicker = true },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Dynamic Emails
        state.emails.forEachIndexed { index, email ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(12.dp))
            }
            key("email_$index") {
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
                    contentType = if (index == 0) ContentType.EmailAddress else ContentType("secondaryEmailAddress"),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
        if (state.emails.size < 3) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = { viewModel.addEmailField() }) {
                    Text("+ Add another email")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        state.phones.forEachIndexed { index, phone ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(12.dp))
            }
            val isLastPhone = index == state.phones.lastIndex
            key("phone_$index") {
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
                        imeAction = ImeAction.Next
                    ),
                    contentType = if (index == 0) ContentType.PhoneNumber else ContentType("secondaryPhoneNumber"),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
        if (state.phones.size < 3) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = { viewModel.addPhoneField() }) {
                    Text("+ Add another phone")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        NexaTextField(
            value = state.address,
            onValueChange = { viewModel.updateBasicInfo(address = it) },
            label = "Address",
            placeholder = "e.g. New York, USA",
            leadingIcon = Icons.Default.Place,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Experience Level",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Prefix-Decoding State calculation
        val rawExperience = state.yearsOfExperience.trim()
        val (selectedType, experienceValue) = remember(rawExperience) {
            when {
                rawExperience.startsWith("FRESH", ignoreCase = true) -> {
                    "Fresh" to ""
                }
                rawExperience.startsWith("MONTHS:", ignoreCase = true) -> {
                    "Months" to rawExperience.substringAfter("MONTHS:")
                }
                rawExperience.startsWith("YEARS:", ignoreCase = true) -> {
                    "Years" to rawExperience.substringAfter("YEARS:")
                }
                rawExperience.startsWith("CUSTOM:", ignoreCase = true) -> {
                    "Years" to rawExperience.substringAfter("CUSTOM:")
                }
                rawExperience.isBlank() -> {
                    "Years" to ""
                }
                rawExperience.equals("none", ignoreCase = true) || rawExperience.equals("fresh", ignoreCase = true) || rawExperience == "0" -> {
                    "Fresh" to ""
                }
                rawExperience.contains("month", ignoreCase = true) -> {
                    "Months" to rawExperience.filter { it.isDigit() || it == '+' }
                }
                rawExperience.contains("year", ignoreCase = true) || rawExperience.replace("+", "").trim().toIntOrNull() != null -> {
                    "Years" to rawExperience.filter { it.isDigit() || it == '+' }
                }
                else -> {
                    "Years" to rawExperience.filter { it.isDigit() || it == '+' }
                }
            }
        }
        
        val categories = listOf("Fresh", "Months", "Years")
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = selectedType == category
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        when (category) {
                            "Fresh" -> viewModel.updateBasicInfo(yearsOfExperience = "FRESH")
                            "Months" -> viewModel.updateBasicInfo(yearsOfExperience = "MONTHS:$experienceValue")
                            "Years" -> viewModel.updateBasicInfo(yearsOfExperience = "YEARS:$experienceValue")
                        }
                    },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        selectedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (selectedType == "Fresh") {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Fresh Candidate status is set. Your CV will emphasize your education, skills, and academic projects.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            NexaTextField(
                value = experienceValue,
                onValueChange = { viewModel.updateBasicInfo(yearsOfExperience = if (selectedType == "Months") "MONTHS:$it" else "YEARS:$it") },
                label = if (selectedType == "Months") "Months of Experience*" else "Years of Experience*",
                placeholder = if (selectedType == "Months") "e.g. 6" else "e.g. 3+",
                leadingIcon = if (selectedType == "Months") Icons.Default.DateRange else Icons.Default.Timeline,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                onlyDigitsAndPlus = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
