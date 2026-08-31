package com.mudasir.nexacvai.presentation.ui.profiles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.mudasir.nexacvai.R
import androidx.compose.ui.res.painterResource
import com.mudasir.nexacvai.presentation.navigation.Screen
import com.mudasir.nexacvai.presentation.ui.profiles.components.view_profile.*
import com.mudasir.nexacvai.presentation.ui.profiles.components.UserProfileImageDialog
import com.mudasir.nexacvai.presentation.ui.profiles.components.ImportExportBottomSheet
import com.mudasir.nexacvai.presentation.ui.profiles.components.ImportExportSheetContent
import com.mudasir.nexacvai.presentation.ui.profiles.components.ProfileCopySheet
import com.mudasir.nexacvai.presentation.ui.profiles.components.ProfileCopySheetContent
import com.mudasir.nexacvai.presentation.ui.components.NexaAlertDialog
import com.mudasir.nexacvai.core.utils.NameUtils
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.mudasir.nexacvai.ui.theme.AvatarColorPairs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ViewProfileScreen(
    navController: NavController,
    viewModel: ViewProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val profile = state.profile
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeletedSourceDialog by remember { mutableStateOf(false) }
    var showFullScreenImage by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream"),
        onResult = { uri ->
            uri?.let {
                viewModel.exportProfileToUri(
                    context = context,
                    uri = it
                )
            } ?: run {
                viewModel.dismissExportConfirm()
            }
        }
    )

    val firstName = remember(profile?.fullName) {
        val name = profile?.fullName?.trim().orEmpty()
        if (name.isEmpty()) "Profile"
        else name.split("\\s+".toRegex()).firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Profile"
    }

    val initials = remember(profile?.fullName) {
        NameUtils.getInitials(profile?.fullName.orEmpty())
    }

    val colorPair = remember(profile?.id) {
        val idVal = profile?.id ?: 0L
        val index = kotlin.math.abs((idVal % AvatarColorPairs.size).toInt())
        AvatarColorPairs[index]
    }

    if (showDeleteDialog && profile != null) {
        NexaAlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = "Delete Profile?",
            message = "Are you sure you want to permanently delete the profile for \"${profile.fullName.ifBlank { "Untitled Profile" }}\"?",
            confirmLabel = "Delete",
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteProfile(profile)
                navController.navigate(Screen.Profiles.route) {
                    popUpTo(Screen.Profiles.route) { inclusive = true }
                }
            },
            dismissLabel = "Cancel",
            isDestructive = true
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    if (profile != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = firstName,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Text(
                                text = "'s Profile",
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1
                            )
                        }
                    } else {
                        Text(
                            text = "Profile Details",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (profile != null) {
                        var isMenuExpanded by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { isMenuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More Options",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            DropdownMenu(
                                expanded = isMenuExpanded,
                                onDismissRequest = { isMenuExpanded = false },
                                shape = RoundedCornerShape(12.dp),
                                containerColor = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .width(180.dp)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Edit Profile") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Profile",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    onClick = {
                                        isMenuExpanded = false
                                        navController.navigate("${Screen.CreateProfile.route}?profileId=${profile.id}")
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Export Profile") },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_export),
                                            contentDescription = "Export Profile",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    onClick = {
                                        isMenuExpanded = false
                                        viewModel.selectProfileForExport(profile)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Copy Profile") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy Profile",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    onClick = {
                                        isMenuExpanded = false
                                        viewModel.duplicateCurrentProfile(context)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            text = "Delete Profile",
                                            color = MaterialTheme.colorScheme.error
                                        ) 
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Profile",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    },
                                    onClick = {
                                        isMenuExpanded = false
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    ViewProfileSkeleton()
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.error ?: "Unknown error",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                profile != null -> {
                    SelectionContainer {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // 1. Header Card (Avatar + Name + Title)
                            ProfileHeaderSection(
                                profile = profile,
                                colorPair = colorPair,
                                initials = initials,
                                onAvatarClick = { showFullScreenImage = true }
                            )

                            // 2. Contact & Quick Info (Mandatory Check)
                            ContactAndQuickInfoSection(profile = profile)

                            // 3. Summary Section
                            if (profile.professionalSummary.isNotBlank()) {
                                DetailSectionCard(
                                    title = "Professional Summary",
                                    icon = Icons.Outlined.Description
                                ) {
                                    Text(
                                        text = profile.professionalSummary,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 24.sp
                                    )
                                }
                            }

                            // 4. Skills Section
                            if (profile.skills.isNotEmpty()) {
                                DetailSectionCard(
                                    title = "Skills",
                                    icon = Icons.Outlined.Extension
                                ) {
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        profile.skills.forEach { skill ->
                                            SuggestionChip(
                                                onClick = {},
                                                label = { Text(skill) },
                                                colors = SuggestionChipDefaults.suggestionChipColors(
                                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                                    labelColor = MaterialTheme.colorScheme.primary
                                                ),
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                            )
                                        }
                                    }
                                }
                            }

                            // 5. Experience Section
                            if (profile.experiences.isNotEmpty()) {
                                DetailSectionCard(
                                    title = "Experience",
                                    icon = Icons.Outlined.BusinessCenter
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                        profile.experiences.forEachIndexed { index, exp ->
                                            ExperienceItemView(exp = exp)
                                            if (index < profile.experiences.lastIndex) {
                                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                                            }
                                        }
                                    }
                                }
                            }

                            // 6. Projects Section
                            if (profile.projects.isNotEmpty()) {
                                DetailSectionCard(
                                    title = "Projects",
                                    icon = Icons.Outlined.Folder
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                        profile.projects.forEachIndexed { index, project ->
                                            ProjectItemView(project = project)
                                            if (index < profile.projects.lastIndex) {
                                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                                            }
                                        }
                                    }
                                }
                            }

                            // 7. Education Section
                            if (profile.educations.isNotEmpty()) {
                                DetailSectionCard(
                                    title = "Education",
                                    icon = Icons.Outlined.School
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                        profile.educations.forEachIndexed { index, edu ->
                                            EducationItemView(edu = edu)
                                            if (index < profile.educations.lastIndex) {
                                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                                            }
                                        }
                                    }
                                }
                            }

                            // 8. Certifications Section
                            if (profile.certifications.isNotEmpty()) {
                                DetailSectionCard(
                                    title = "Certifications",
                                    icon = Icons.Outlined.Verified
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        profile.certifications.forEachIndexed { index, cert ->
                                            CertificationItemView(cert = cert)
                                            if (index < profile.certifications.lastIndex) {
                                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                                            }
                                        }
                                    }
                                }
                            }

                            // 9. Languages Section
                            if (profile.languages.isNotEmpty()) {
                                DetailSectionCard(
                                    title = "Languages",
                                    icon = Icons.Outlined.Language
                                ) {
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        profile.languages.forEach { lang ->
                                            InputChip(
                                                selected = false,
                                                onClick = {},
                                                label = { Text("${lang.languageName} • ${lang.proficiency}") },
                                                colors = InputChipDefaults.inputChipColors(
                                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                                    labelColor = MaterialTheme.colorScheme.onSurface
                                                ),
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                                            )
                                        }
                                    }
                                }
                            }

                            // 10. Social Links Section
                            if (profile.socialLinks.isNotEmpty()) {
                                DetailSectionCard(
                                    title = "Social Links",
                                    icon = Icons.Outlined.Link
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        profile.socialLinks.forEach { link ->
                                            SocialLinkItemView(link = link)
                                        }
                                    }
                                }
                            }

                            // 11. References Section
                            if (profile.references.isNotEmpty()) {
                                DetailSectionCard(
                                    title = "References",
                                    icon = Icons.Outlined.People
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                        profile.references.forEachIndexed { index, ref ->
                                            ReferenceItemView(ref = ref)
                                            if (index < profile.references.lastIndex) {
                                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                                            }
                                        }
                                    }
                                }
                            }

                            // 12. Additional Information Section (Hobbies, Volunteer, Awards)
                            val hasAdditionalInfo = profile.hobbies.isNotBlank() || 
                                                     profile.volunteerWork.isNotBlank() || 
                                                     profile.awards.isNotBlank()
                            if (hasAdditionalInfo) {
                                DetailSectionCard(
                                    title = "Additional Information",
                                    icon = Icons.Outlined.Favorite
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        if (profile.hobbies.isNotBlank()) {
                                            AdditionalInfoItem(label = "Hobbies & Interests", content = profile.hobbies)
                                        }
                                        if (profile.volunteerWork.isNotBlank()) {
                                            AdditionalInfoItem(label = "Volunteer Work", content = profile.volunteerWork)
                                        }
                                        if (profile.awards.isNotBlank()) {
                                            AdditionalInfoItem(label = "Awards & Honors", content = profile.awards)
                                        }
                                    }
                                }
                            }
                            
                            // 13. Profile Metadata & Activity Section (Creation, Update, Copy Source & Stats)
                            ProfileMetadataSection(
                                profile = profile,
                                sourceProfileNameOverride = state.liveSourceProfileName,
                                onSourceProfileClick = { sourceId ->
                                    if (state.isSourceProfileAlive) {
                                        navController.navigate("${Screen.ViewProfile.route}?profileId=$sourceId")
                                    } else {
                                        showDeletedSourceDialog = true
                                    }
                                }
                            )

                            // Extra bottom spacing to ensure comfortable scrolling above any navigation/scaffold bottom bounds
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }

            if (profile != null) {
                UserProfileImageDialog(
                    showFullScreenImage = showFullScreenImage,
                    onDismissFullScreen = { showFullScreenImage = false },
                    profile = profile,
                    colorPair = colorPair,
                    isReadOnly = true
                )
            }

            if (showDeletedSourceDialog && profile != null) {
                val sourceName = state.liveSourceProfileName ?: profile.sourceProfileName ?: "Original Profile"
                NexaAlertDialog(
                    onDismissRequest = { showDeletedSourceDialog = false },
                    title = "Original Profile Deleted",
                    message = "The original profile (\"$sourceName\") from which this profile was copied is no longer available because it has been deleted.",
                    confirmLabel = "Got It",
                    onConfirm = { showDeletedSourceDialog = false },
                    dismissLabel = null,
                    icon = Icons.Outlined.Info
                )
            }

            // Export Confirmation Bottom Sheet
            if (state.showExportConfirm && state.exportingProfile != null) {
                val pToExport = state.exportingProfile!!
                ImportExportBottomSheet(
                    content = ImportExportSheetContent.ExportConfirm(listOf(pToExport)),
                    onExportConfirm = {
                        viewModel.hideExportDialog()
                        val fileName = "${pToExport.fullName.trim().replace("\\s+".toRegex(), "_")}_profile.nexacv"
                        exportLauncher.launch(fileName)
                    },
                    onDismiss = { viewModel.dismissExportConfirm() }
                )
            }

            // Profile Copy Progress / Success Sheet
            if (state.duplicateState != DuplicateProgressState.Idle) {
                val sheetContent = when (state.duplicateState) {
                    DuplicateProgressState.Duplicating -> ProfileCopySheetContent.Copying
                    DuplicateProgressState.Success -> ProfileCopySheetContent.Success(
                        count = 1,
                        profileName = state.duplicatedProfileName
                    )
                    else -> null
                }
                if (sheetContent != null) {
                    ProfileCopySheet(
                        content = sheetContent,
                        onViewProfile = {
                            val newId = state.newlyDuplicatedProfileId
                            viewModel.dismissDuplicateSheet()
                            if (newId != null) {
                                navController.navigate("${Screen.ViewProfile.route}?profileId=$newId")
                            }
                        },
                        onDone = { viewModel.dismissDuplicateSheet() }
                    )
                }
            }


        }
    }
}
