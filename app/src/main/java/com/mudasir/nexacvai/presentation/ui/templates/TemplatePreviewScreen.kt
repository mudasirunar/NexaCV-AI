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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.mudasir.nexacvai.core.pdf.PdfGeneratorEngine
import com.mudasir.nexacvai.domain.model.template.PhotoShape
import com.mudasir.nexacvai.domain.model.template.ResumeTemplate
import com.mudasir.nexacvai.domain.model.template.TemplateCategory
import com.mudasir.nexacvai.domain.model.template.TemplateData
import com.mudasir.nexacvai.domain.model.template.TemplateStyle
import com.mudasir.nexacvai.presentation.ui.components.PdfDocumentViewer
import com.mudasir.nexacvai.presentation.ui.templates.components.FavoriteStarButton
import com.mudasir.nexacvai.presentation.ui.templates.components.shimmerEffect
import com.mudasir.nexacvai.presentation.ui.templates.viewmodel.TemplatesViewModel
import com.mudasir.nexacvai.ui.theme.getPdfCanvasBgColor
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatePreviewScreen(
    templateId: String,
    categoryName: String = "ALL",
    onNavigateBack: () -> Unit = {},
    onConfirmCreateCv: (templateId: String, profileId: Long?) -> Unit = { _, _ -> },
    viewModel: TemplatesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val selectedCategory = remember(categoryName) {
        try {
            TemplateCategory.valueOf(categoryName)
        } catch (e: Exception) {
            TemplateCategory.ALL
        }
    }

    val allTemplates = if (state.templates.isNotEmpty()) {
        state.templates
    } else {
        com.mudasir.nexacvai.data.templates.BuiltInTemplatesCatalog.ALL_TEMPLATES
    }

    val pagerTemplates = remember(allTemplates, selectedCategory, state.favoriteTemplateIds) {
        val filtered = when (selectedCategory) {
            TemplateCategory.ALL -> allTemplates
            TemplateCategory.FAVORITES -> allTemplates.filter { state.favoriteTemplateIds.contains(it.metadata.id) }
            TemplateCategory.CUSTOM -> allTemplates.filter { it.metadata.isImported || it.metadata.category == TemplateCategory.CUSTOM }
            else -> allTemplates.filter { it.metadata.category == selectedCategory }
        }
        if (filtered.isNotEmpty()) {
            filtered
        } else {
            allTemplates.filter { it.metadata.id == templateId }.ifEmpty { allTemplates }
        }
    }

    val initialPageIndex = remember(pagerTemplates, templateId) {
        val idx = pagerTemplates.indexOfFirst { it.metadata.id == templateId }
        if (idx >= 0) idx else 0
    }

    val pagerState = rememberPagerState(
        initialPage = initialPageIndex,
        pageCount = { pagerTemplates.size }
    )

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val widthDp = configuration.screenWidthDp
    val heightDp = configuration.screenHeightDp
    val smallestWidthDp = configuration.smallestScreenWidthDp
    val maxDim = maxOf(widthDp, heightDp).toFloat()
    val minDim = minOf(widthDp, heightDp).toFloat()
    val aspectRatio = if (minDim > 0) maxDim / minDim else 1f

    val isFoldable = smallestWidthDp >= 580 && aspectRatio < 1.35
    val isTablet = smallestWidthDp >= 580 && aspectRatio >= 1.35
    val isPhone = smallestWidthDp < 580

    val targetInitialZoom = when {
        isLandscape && (isPhone || isTablet) -> 1.5f
        else -> 1.0f
    }

    var currentZoomLevel by remember { mutableFloatStateOf(targetInitialZoom) }
    val isPagingEnabled = currentZoomLevel <= (targetInitialZoom + 0.05f)

    LaunchedEffect(pagerState.currentPage, targetInitialZoom) {
        currentZoomLevel = targetInitialZoom
    }

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

    val currentTemplate = pagerTemplates.getOrNull(pagerState.currentPage)
    val currentMeta = currentTemplate?.metadata
    val topPadding = 104.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(canvasBgColor) // Seamless background matching viewer canvas in both Light and Dark mode
    ) {
        if (pagerTemplates.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shimmerEffect()
            )
        } else {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = isPagingEnabled,
                beyondViewportPageCount = 1,
                key = { page -> pagerTemplates.getOrNull(page)?.metadata?.id ?: page },
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val template = pagerTemplates.getOrNull(page)
                if (template != null) {
                    key(template.metadata.id) {
                        TemplatePreviewPageItem(
                            template = template,
                            isTopBarVisible = isTopBarVisible,
                            onToggleTopBar = { isTopBarVisible = it },
                            onZoomChange = { zoom ->
                                if (pagerState.currentPage == page) {
                                    currentZoomLevel = zoom
                                }
                            },
                            topPadding = topPadding
                        )
                    }
                }
            }
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
                        text = currentMeta?.name ?: "Template Preview",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                actions = {
                    currentMeta?.let { activeMeta ->
                        val isFav = state.favoriteTemplateIds.contains(activeMeta.id)
                        key(activeMeta.id) {
                            FavoriteStarButton(
                                isFavorite = isFav,
                                onToggleFavorite = { viewModel.toggleFavorite(activeMeta.id) },
                                templateId = activeMeta.id,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }

                        val confirmInteractionSource = remember { MutableInteractionSource() }
                        val confirmPressed by confirmInteractionSource.collectIsPressedAsState()
                        val confirmScale by animateFloatAsState(if (confirmPressed) 0.96f else 1f, label = "createBtnScale")

                        Button(
                            onClick = {
                                Toast.makeText(context, "Selected ${activeMeta.name}", Toast.LENGTH_SHORT).show()
                                onConfirmCreateCv(activeMeta.id, null)
                            },
                            interactionSource = confirmInteractionSource,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .graphicsLayer(scaleX = confirmScale, scaleY = confirmScale)
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = "Use",
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

@Composable
private fun TemplatePreviewPageItem(
    template: ResumeTemplate,
    isTopBarVisible: Boolean,
    onToggleTopBar: (Boolean) -> Unit,
    onZoomChange: (Float) -> Unit,
    topPadding: Dp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val meta = template.metadata
    val cleanHex = remember(meta.previewPrimaryColorHex) {
        meta.previewPrimaryColorHex.removePrefix("#")
    }
    val previewFileName = remember(meta.id, cleanHex) {
        "preview_template_${meta.id}_$cleanHex.pdf"
    }
    val cachedFile = remember(meta.id, cleanHex) {
        File(context.cacheDir, previewFileName)
    }

    val previewPrimaryColor = remember(meta.previewPrimaryColorHex) {
        try {
            Color(android.graphics.Color.parseColor(meta.previewPrimaryColorHex))
        } catch (e: Exception) {
            Color(0xFF1E3A8A)
        }
    }

    val defaultGuidanceData = remember(template) {
        template.defaultData
    }

    val pdfEngine = remember(context) { PdfGeneratorEngine(context) }
    var generatedPdfFile by remember(meta.id, cleanHex) {
        mutableStateOf(if (cachedFile.exists() && cachedFile.length() > 0) cachedFile else null)
    }
    var isGeneratingPdf by remember(meta.id, cleanHex) {
        mutableStateOf(generatedPdfFile == null)
    }

    val templateStyle = remember(previewPrimaryColor, meta.supportsPhoto, meta.defaultPhotoShape) {
        TemplateStyle(
            primaryColor = previewPrimaryColor,
            showPhoto = meta.supportsPhoto,
            photoShape = meta.defaultPhotoShape
        )
    }

    LaunchedEffect(meta.id, cleanHex) {
        if (cachedFile.exists() && cachedFile.length() > 0) {
            generatedPdfFile = cachedFile
            isGeneratingPdf = false
        } else {
            isGeneratingPdf = true
            val file = pdfEngine.generateCvPdf(
                template = template,
                data = defaultGuidanceData,
                templateStyle = templateStyle,
                outputFileName = previewFileName
            )
            generatedPdfFile = file
            isGeneratingPdf = false
        }
    }

    if (isGeneratingPdf || generatedPdfFile == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .shimmerEffect()
        )
    } else {
        PdfDocumentViewer(
            pdfFile = generatedPdfFile,
            modifier = modifier
                .fillMaxSize()
                .padding(top = topPadding),
            isTopBarVisible = isTopBarVisible,
            onToggleTopBar = onToggleTopBar,
            onZoomChange = onZoomChange
        )
    }
}
