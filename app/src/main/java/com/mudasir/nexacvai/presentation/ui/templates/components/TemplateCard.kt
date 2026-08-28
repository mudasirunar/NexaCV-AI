package com.mudasir.nexacvai.presentation.ui.templates.components

import android.graphics.Bitmap
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mudasir.nexacvai.core.pdf.PdfGeneratorEngine
import com.mudasir.nexacvai.core.pdf.TemplateThumbnailGenerator
import com.mudasir.nexacvai.domain.model.template.ResumeTemplate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TemplateCard(
    template: ResumeTemplate,
    onSelectTemplate: () -> Unit,
    modifier: Modifier = Modifier,
    isFlipped: Boolean = false,
    onToggleFlip: () -> Unit = {}
) {
    val context = LocalContext.current
    val pdfEngine = remember(context) { PdfGeneratorEngine(context) }
    var thumbnailBitmap by remember(template.metadata.id) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(template.metadata.id) {
        thumbnailBitmap = TemplateThumbnailGenerator.generateThumbnail(context, pdfEngine, template)
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(if (isPressed) 0.98f else 1f, label = "templateCardPressScale")

    // Smooth 3D Flip Rotation
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing),
        label = "cardFlipRotation"
    )

    val meta = template.metadata
    val previewPrimaryColor = remember(meta.previewPrimaryColorHex) {
        try {
            Color(android.graphics.Color.parseColor(meta.previewPrimaryColorHex))
        } catch (e: Exception) {
            Color(0xFF1E3A8A)
        }
    }
    val previewAccentColor = remember(meta.previewAccentColorHex) {
        try {
            Color(android.graphics.Color.parseColor(meta.previewAccentColorHex))
        } catch (e: Exception) {
            Color(0xFF3B82F6)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
                scaleX = pressScale
                scaleY = pressScale
            }
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (isFlipped) {
                        onToggleFlip()
                    } else {
                        onSelectTemplate()
                    }
                },
                onLongClick = {
                    onToggleFlip()
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (rotation <= 90f) {
            // FRONT OF CARD
            Column(modifier = Modifier.fillMaxSize()) {
                // Document Studio Workbench Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Real A4 Paper Document Sheet (Aspect Ratio 1 : 1.414)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f / 1.414f)
                            .shadow(4.dp, RoundedCornerShape(4.dp))
                            .clip(RoundedCornerShape(4.dp))
                            .clipToBounds()
                            .background(Color.White)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        val bitmap = thumbnailBitmap
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "${meta.name} preview",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .shimmerEffect()
                            )
                        }
                    }
                }

                // Front Footer: Left Column (Title) & Right Column (Info Icon + Color Dots)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column: Template Name (Full Size Title, Wraps to 2 lines if needed)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 6.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = meta.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Right Column: Info Icon (Top) & Color Dots (Bottom)
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Info Button (Triggers 3D Flip)
                        IconButton(
                            onClick = onToggleFlip,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "Template Info",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        // Dual Color Theme Dots
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(previewPrimaryColor)
                                    .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(previewAccentColor)
                                    .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            )
                        }
                    }
                }
            }
        } else {
            // BACK OF CARD (Flipped State - counter-rotate by 180 to read normally)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header Row on Flipped Side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = meta.category.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }

                    // Flip Back Icon Button
                    IconButton(
                        onClick = onToggleFlip,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Flip Back",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Middle Content: Title, Description & Color Theme Dots
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 8.dp, bottom = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = meta.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            lineHeight = 17.sp
                        )
                    )

                    Text(
                        text = meta.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.5.sp,
                            lineHeight = 15.5.sp
                        )
                    )

                    // Color Theme Indicator Dots on Flipped Side
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(previewPrimaryColor)
                                .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(previewAccentColor)
                                .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        )
                    }
                }

                // Bottom Action CTA Button
                Surface(
                    onClick = onSelectTemplate,
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Open Preview",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
