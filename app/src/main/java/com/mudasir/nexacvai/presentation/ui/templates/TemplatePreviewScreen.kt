package com.mudasir.nexacvai.presentation.ui.templates

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mudasir.nexacvai.core.pdf.PdfGeneratorEngine
import com.mudasir.nexacvai.domain.model.template.TemplateData
import com.mudasir.nexacvai.domain.model.template.TemplateStyle
import com.mudasir.nexacvai.presentation.ui.components.PdfDocumentViewer
import com.mudasir.nexacvai.presentation.ui.templates.components.shimmerEffect
import com.mudasir.nexacvai.presentation.ui.templates.viewmodel.TemplatesViewModel
import java.io.File

/**
 * Fully immersive A4 PDF Document Preview Screen.
 * Renders high-fidelity PDF documents using the default template guidance profile.
 * Profile selection and custom data injection are handled during document creation.
 */
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

    val defaultGuidanceData = remember(template) {
        template?.defaultData ?: TemplateData.SAMPLE_FILLER
    }

    val pdfEngine = remember(context) { PdfGeneratorEngine(context) }
    var generatedPdfFile by remember { mutableStateOf<File?>(null) }
    var isGeneratingPdf by remember { mutableStateOf(true) }

    val templateStyle = remember(previewPrimaryColor, meta?.supportsPhoto) {
        TemplateStyle(
            primaryColor = previewPrimaryColor,
            showPhoto = meta?.supportsPhoto == true
        )
    }

    LaunchedEffect(template, defaultGuidanceData, templateStyle) {
        if (template != null) {
            isGeneratingPdf = true
            generatedPdfFile = pdfEngine.generateCvPdf(template, defaultGuidanceData, templateStyle)
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
                                Toast.makeText(
                                    context,
                                    "Selected ${it.name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onConfirmCreateCv(it.id, null)
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
            contentAlignment = Alignment.Center
        ) {
            if (isGeneratingPdf || template == null) {
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
    }
}

