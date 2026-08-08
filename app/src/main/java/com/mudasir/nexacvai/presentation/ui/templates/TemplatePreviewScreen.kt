package com.mudasir.nexacvai.presentation.ui.templates

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mudasir.nexacvai.core.pdf.PdfGeneratorEngine
import com.mudasir.nexacvai.domain.model.template.TemplateData
import com.mudasir.nexacvai.domain.model.template.TemplateStyle
import com.mudasir.nexacvai.domain.model.template.toTemplateData
import com.mudasir.nexacvai.presentation.ui.components.PdfDocumentViewer
import com.mudasir.nexacvai.presentation.ui.templates.components.ProfileSelectionBottomSheet
import com.mudasir.nexacvai.presentation.ui.templates.components.shimmerEffect
import com.mudasir.nexacvai.presentation.ui.templates.viewmodel.TemplatesViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatePreviewScreen(
    templateId: String,
    onNavigateBack: () -> Unit = {},
    onConfirmCreateCv: (templateId: String, profileId: Long?) -> Unit = { _, _ -> },
    viewModel: TemplatesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var showProfilePicker by remember { mutableStateOf(false) }

    val template = remember(state.templates, templateId) {
        state.templates.find { it.metadata.id == templateId }
    }

    val meta = template?.metadata
    val previewPrimaryColor = remember(meta?.previewPrimaryColorHex) {
        try {
            Color(android.graphics.Color.parseColor(meta?.previewPrimaryColorHex ?: "#1E3A8A"))
        } catch (e: Exception) {
            Color(0xFF1E3A8A)
        }
    }

    val activeData = remember(state.selectedProfile) {
        state.selectedProfile?.toTemplateData() ?: TemplateData.SAMPLE_FILLER
    }

    val pdfEngine = remember(context) { PdfGeneratorEngine(context) }
    var generatedPdfFile by remember { mutableStateOf<File?>(null) }
    var isGeneratingPdf by remember { mutableStateOf(true) }

    val templateStyle = remember(previewPrimaryColor, state.showPhotoInTemplate, meta?.supportsPhoto) {
        TemplateStyle(
            primaryColor = previewPrimaryColor,
            showPhoto = state.showPhotoInTemplate && (meta?.supportsPhoto == true)
        )
    }

    LaunchedEffect(template, activeData, templateStyle) {
        if (template != null) {
            isGeneratingPdf = true
            generatedPdfFile = pdfEngine.generateCvPdf(template, activeData, templateStyle)
            isGeneratingPdf = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                title = {
                    Text(
                        text = meta?.name ?: "Template Preview",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                actions = {
                    meta?.let {
                        val confirmInteractionSource = remember { MutableInteractionSource() }
                        val confirmPressed by confirmInteractionSource.collectIsPressedAsState()
                        val confirmScale by animateFloatAsState(if (confirmPressed) 0.96f else 1f, label = "createBtnScale")

                        Button(
                            onClick = {
                                android.widget.Toast.makeText(
                                    context,
                                    "Selected ${it.name}",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                                onConfirmCreateCv(it.id, state.selectedProfile?.id)
                            },
                            interactionSource = confirmInteractionSource,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .graphicsLayer(scaleX = confirmScale, scaleY = confirmScale)
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = "Use Template",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (template == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Interactive Active Profile Card & Photo Controls
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Selected Profile Avatar
                                val avatarUri = state.selectedProfile?.profilePictureUri ?: activeData.profilePictureUri
                                if (!avatarUri.isNullOrBlank()) {
                                    AsyncImage(
                                        model = avatarUri,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = state.selectedProfile?.fullName ?: "Sample Guidance Placeholder",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = state.selectedProfile?.professionalTitle ?: "Informative Placeholder Data",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Change Profile Action Button
                            OutlinedButton(
                                onClick = { showProfilePicker = true },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Select Profile",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        if (meta?.supportsPhoto == true) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
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
                                        imageVector = Icons.Default.PhotoCamera,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Show Profile Photo in Layout",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }

                                Switch(
                                    checked = state.showPhotoInTemplate,
                                    onCheckedChange = { viewModel.togglePhotoInTemplate(it) }
                                )
                            }
                        }
                    }
                }

                // Interactive Real A4 PDF Document Viewer Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(660.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                ) {
                    if (isGeneratingPdf || state.isInjectingProfile) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .shimmerEffect()
                        )
                    } else {
                        PdfDocumentViewer(
                            pdfFile = generatedPdfFile,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Rich Profile Selection Modal Sheet
    if (showProfilePicker) {
        ProfileSelectionBottomSheet(
            profiles = state.profiles,
            selectedProfile = state.selectedProfile,
            onSelectProfile = { profile ->
                viewModel.selectProfileForInjection(profile)
            },
            onDismissRequest = { showProfilePicker = false }
        )
    }
}
