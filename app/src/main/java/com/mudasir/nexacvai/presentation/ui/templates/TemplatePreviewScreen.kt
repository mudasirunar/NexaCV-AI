package com.mudasir.nexacvai.presentation.ui.templates

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.mudasir.nexacvai.core.pdf.PdfGeneratorEngine
import com.mudasir.nexacvai.domain.model.template.TemplateData
import com.mudasir.nexacvai.domain.model.template.TemplateStyle
import com.mudasir.nexacvai.presentation.ui.components.PdfDocumentViewer
import com.mudasir.nexacvai.presentation.ui.templates.components.shimmerEffect
import com.mudasir.nexacvai.presentation.ui.templates.viewmodel.TemplatesViewModel
import com.mudasir.nexacvai.ui.theme.getPdfCanvasBgColor
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

    var isTopBarVisible by remember { mutableStateOf(true) }

    // Dynamic dark / light theme canvas color
    val canvasBgColor = getPdfCanvasBgColor()

    // Synchronize System Status Bar visibility with TopBar state
    val activity = LocalActivity.current
    val window = activity?.window
    val insetsController = remember(window) {
        window?.let { WindowCompat.getInsetsController(it, it.decorView) }
    }

    DisposableEffect(isTopBarVisible) {
        if (!isTopBarVisible) {
            insetsController?.hide(WindowInsetsCompat.Type.statusBars())
        } else {
            insetsController?.show(WindowInsetsCompat.Type.statusBars())
        }
        onDispose {
            insetsController?.show(WindowInsetsCompat.Type.statusBars())
        }
    }

    val templateStyle = remember(previewPrimaryColor, meta?.supportsPhoto, meta?.defaultPhotoShape) {
        TemplateStyle(
            primaryColor = previewPrimaryColor,
            showPhoto = meta?.supportsPhoto == true,
            photoShape = meta?.defaultPhotoShape ?: com.mudasir.nexacvai.domain.model.template.PhotoShape.CIRCLE
        )
    }

    LaunchedEffect(template, defaultGuidanceData, templateStyle) {
        if (template != null) {
            isGeneratingPdf = true
            generatedPdfFile = pdfEngine.generateCvPdf(template, defaultGuidanceData, templateStyle)
            isGeneratingPdf = false
        }
    }

    val topPadding = 104.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(canvasBgColor) // Seamless background matching viewer canvas in both Light and Dark mode
    ) {
        if (isGeneratingPdf || template == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shimmerEffect()
            )
        } else {
            // Reserved top padding (104dp portrait / 72dp landscape) ensures TopBar never obstructs page 1
            PdfDocumentViewer(
                pdfFile = generatedPdfFile,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = topPadding),
                isTopBarVisible = isTopBarVisible,
                onToggleTopBar = { visible ->
                    isTopBarVisible = visible
                }
            )
        }

        // Floating TopAppBar Overlay
        AnimatedVisibility(
            visible = isTopBarVisible,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
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
                                Toast.makeText(context, "Selected ${it.name}", Toast.LENGTH_SHORT).show()
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
        }
    }
}
