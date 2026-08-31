package com.mudasir.nexacvai.presentation.ui.profiles.components.create_profile

import android.content.Context
import android.content.res.Configuration
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import com.mudasir.nexacvai.presentation.ui.components.NexaBottomSheetDragHandle
import com.mudasir.nexacvai.presentation.ui.components.NexaModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class QualificationCategory(
    val title: String,
    val shortTitle: String,
    val options: List<String>
)

val StandardQualificationCategories = listOf(
    QualificationCategory(
        title = "Secondary & Intermediate",
        shortTitle = "Secondary / Inter",
        options = listOf(
            "High School Diploma",
            "Matriculation",
            "A-Levels",
            "O-Levels",
            "Intermediate in Pre-Engineering",
            "Intermediate in Pre-Medical",
            "Intermediate in Computer Science",
            "Intermediate in Commerce",
            "Intermediate in Arts"
        )
    ),
    QualificationCategory(
        title = "Diplomas & Associate",
        shortTitle = "Diplomas",
        options = listOf(
            "Diploma of Associate Engineer",
            "Associate of Science",
            "Associate of Arts",
            "Associate of Applied Science",
            "Higher National Diploma",
            "Vocational Qualification Certificate"
        )
    ),
    QualificationCategory(
        title = "Bachelor's Degrees",
        shortTitle = "Bachelor's",
        options = listOf(
            "Bachelor of Science",
            "Bachelor of Arts",
            "Bachelor of Business Administration",
            "Bachelor of Engineering",
            "Bachelor of Technology",
            "Bachelor of Computer Applications",
            "Bachelor of Medicine and Bachelor of Surgery",
            "Bachelor of Dental Surgery",
            "Bachelor of Science in Nursing",
            "Bachelor of Design",
            "Bachelor of Education",
            "Bachelor of Journalism & Mass Communication",
            "Bachelor of Commerce",
            "Bachelor of Fine Arts",
            "Bachelor of Architecture",
            "Bachelor of Laws"
        )
    ),
    QualificationCategory(
        title = "Master's Degrees",
        shortTitle = "Master's",
        options = listOf(
            "Master of Science",
            "Master of Arts",
            "Master of Business Administration",
            "Master of Engineering",
            "Master of Technology",
            "Master of Computer Applications",
            "Master of Surgery",
            "Master of Public Health",
            "Master of Design",
            "Master of Education",
            "Master of Philosophy",
            "Master of Commerce",
            "Master of Fine Arts",
            "Master of Laws"
        )
    ),
    QualificationCategory(
        title = "Doctorate & Post-Grad",
        shortTitle = "Doctorate",
        options = listOf(
            "Doctor of Philosophy",
            "Doctor of Medicine",
            "Doctor of Pharmacy",
            "Doctor of Dental Surgery",
            "Doctor of Physical Therapy",
            "Doctor of Veterinary Medicine",
            "Doctor of Osteopathic Medicine",
            "Doctor of Education",
            "Doctor of Business Administration",
            "Post-Graduate Diploma"
        )
    )
)

private val QualificationAliases: Map<String, List<String>> = mapOf(
    // Secondary & Intermediate
    "High School Diploma" to listOf("high school", "hs", "12th", "twelfth", "k12", "diploma", "secondary school", "ged"),
    "Matriculation" to listOf("matric", "ssc", "10th", "tenth", "secondary school certificate", "matriculate"),
    "A-Levels" to listOf("a levels", "alevel", "alevels", "a level", "gce a levels", "advanced level", "gce"),
    "O-Levels" to listOf("o levels", "olevel", "olevels", "o level", "gcse", "igcse", "ordinary level"),
    "Intermediate in Pre-Engineering" to listOf("fsc pre engineering", "fsc engineering", "pre engineering", "fsc", "pre eng", "hssc pre engineering", "inter engineering", "non medical"),
    "Intermediate in Pre-Medical" to listOf("fsc pre medical", "fsc medical", "pre medical", "fsc", "pre med", "hssc pre medical", "inter medical", "medical fsc"),
    "Intermediate in Computer Science" to listOf("ics", "i.c.s", "computer science inter", "inter cs", "hssc cs", "cs inter"),
    "Intermediate in Commerce" to listOf("icom", "i.com", "commerce inter", "inter commerce", "hssc commerce"),
    "Intermediate in Arts" to listOf("fa", "f.a", "arts inter", "inter arts", "humanities inter", "hssc arts"),

    // Diplomas & Associate
    "Diploma of Associate Engineer" to listOf("dae", "d.a.e", "associate engineer", "polytechnic", "diploma engineer", "technical diploma"),
    "Associate of Science" to listOf("as", "a.s", "associate science", "as degree"),
    "Associate of Arts" to listOf("aa", "a.a", "associate arts", "aa degree"),
    "Associate of Applied Science" to listOf("aas", "a.a.s", "applied science"),
    "Higher National Diploma" to listOf("hnd", "h.n.d", "higher national", "hnc"),
    "Vocational Qualification Certificate" to listOf("vocational", "trade certificate", "nvq", "technical certificate", "certification", "iti", "vocational training", "skill certificate"),

    // Bachelor's Degrees
    "Bachelor of Science" to listOf("bs", "b.s", "bsc", "b.sc", "bachelor science", "b.s.", "b.sc."),
    "Bachelor of Arts" to listOf("ba", "b.a", "bachelor arts", "b.a.", "b arts"),
    "Bachelor of Business Administration" to listOf("bba", "b.b.a", "business admin", "bachelor business", "bba honors", "business management"),
    "Bachelor of Engineering" to listOf("be", "b.e", "beng", "b.eng", "bachelor engineering", "engg", "engineering"),
    "Bachelor of Technology" to listOf("btech", "b.tech", "b tech", "bachelor technology"),
    "Bachelor of Computer Applications" to listOf("bca", "b.c.a", "bca degree", "computer applications", "software applications"),
    "Bachelor of Medicine and Bachelor of Surgery" to listOf("mbbs", "m.b.b.s", "m.b.b.s.", "mbchb", "bmbs", "doctor", "physician", "surgeon", "medicine and surgery", "medical doctor", "doctor degree"),
    "Bachelor of Dental Surgery" to listOf("bds", "b.d.s", "dentist", "dental surgery", "dental", "bds doctor", "dental doctor"),
    "Bachelor of Science in Nursing" to listOf("bsn", "b.s.n", "nursing", "nurse", "bsc nursing", "bs nursing"),
    "Bachelor of Design" to listOf("bdes", "b.des", "b design", "design bachelor", "graphic design", "fashion design", "product design", "ui ux"),
    "Bachelor of Education" to listOf("bed", "b.ed", "b ed", "education bachelor", "teaching", "teacher"),
    "Bachelor of Journalism & Mass Communication" to listOf("bjmc", "bj", "bmm", "journalism", "mass communication", "media", "mass comm"),
    "Bachelor of Commerce" to listOf("bcom", "b.com", "b com", "commerce bachelor", "accounting", "finance"),
    "Bachelor of Fine Arts" to listOf("bfa", "b.f.a", "b fine arts", "fine arts bachelor", "visual arts", "painting"),
    "Bachelor of Architecture" to listOf("barch", "b.arch", "b arch", "architecture bachelor", "architect"),
    "Bachelor of Laws" to listOf("llb", "l.l.b", "ll.b", "law bachelor", "lawyer", "advocate", "attorney", "legal", "law"),

    // Master's Degrees
    "Master of Science" to listOf("ms", "m.s", "msc", "m.sc", "master science", "m.s.", "m.sc."),
    "Master of Arts" to listOf("ma", "m.a", "master arts", "m.a.", "m arts"),
    "Master of Business Administration" to listOf("mba", "m.b.a", "business master", "emba", "executive mba", "mba finance", "mba marketing"),
    "Master of Engineering" to listOf("me", "m.e", "meng", "m.eng", "master engineering"),
    "Master of Technology" to listOf("mtech", "m.tech", "m tech", "master technology"),
    "Master of Computer Applications" to listOf("mca", "m.c.a", "mca degree"),
    "Master of Surgery" to listOf("ms surgery", "m.s surgery", "master surgery", "chm", "surgeon", "surgical master"),
    "Master of Public Health" to listOf("mph", "m.p.h", "public health master", "epidemiology", "health administration"),
    "Master of Design" to listOf("mdes", "m.des", "m design", "master design"),
    "Master of Education" to listOf("med", "m.ed", "m ed", "education master"),
    "Master of Philosophy" to listOf("mphil", "m.phil", "m phil", "philosophy master"),
    "Master of Commerce" to listOf("mcom", "m.com", "m com", "commerce master"),
    "Master of Fine Arts" to listOf("mfa", "m.f.a", "fine arts master"),
    "Master of Laws" to listOf("llm", "l.l.m", "ll.m", "master law", "law master"),

    // Doctorate & Post-Grad
    "Doctor of Philosophy" to listOf("phd", "ph.d", "ph.d.", "dphil", "doctorate", "philosophy doctor", "doctor of philosophy"),
    "Doctor of Medicine" to listOf("md", "m.d", "m.d.", "medical doctor", "physician", "doctor of medicine"),
    "Doctor of Pharmacy" to listOf("pharmd", "pharm.d", "pharm d", "pharmacist", "pharmacy doctor"),
    "Doctor of Dental Surgery" to listOf("dds", "d.d.s", "dmd", "d.m.d", "dental doctor", "dentist doctor"),
    "Doctor of Physical Therapy" to listOf("dpt", "d.p.t", "physiotherapy", "physiotherapist", "physical therapist"),
    "Doctor of Veterinary Medicine" to listOf("dvm", "d.v.m", "veterinary", "vet doctor", "animal doctor"),
    "Doctor of Osteopathic Medicine" to listOf("do", "d.o", "osteopathic doctor", "do physician"),
    "Doctor of Education" to listOf("edd", "ed.d", "ed d", "education doctorate"),
    "Doctor of Business Administration" to listOf("dba", "d.b.a", "business doctorate"),
    "Post-Graduate Diploma" to listOf("pgd", "p.g.d", "pg diploma", "postgraduate diploma", "post graduate")
)

private fun normalizeSearchText(text: String): String {
    return text.lowercase()
        .replace(".", "")
        .replace("-", " ")
        .replace("/", " ")
        .replace(",", " ")
        .replace("&", "and")
        .replace(Regex("\\s+"), " ")
        .trim()
}

private fun matchesQualification(option: String, categoryTitle: String, rawQuery: String): Boolean {
    val cleanQuery = normalizeSearchText(rawQuery)
    if (cleanQuery.isEmpty()) return true

    val cleanOption = normalizeSearchText(option)

    // 1. Direct option title match: substring OR word prefix match
    if (cleanOption.contains(cleanQuery)) {
        return true
    }
    val optionWords = cleanOption.split(" ")
    if (optionWords.any { it.startsWith(cleanQuery) }) {
        return true
    }

    // 2. Alias / Acronym matching (strict prefix or word match, NOT inverse substring!)
    val aliases = QualificationAliases[option] ?: emptyList()
    for (rawAlias in aliases) {
        val cleanAlias = normalizeSearchText(rawAlias)
        // Check if alias starts with query (e.g. "mb" matches "mbbs", "mba")
        if (cleanAlias.startsWith(cleanQuery)) {
            return true
        }
        // Check if alias equals query
        if (cleanAlias == cleanQuery) {
            return true
        }
        // If alias is multi-word (e.g. "pre engineering", "medical fsc"), check word-level prefix or containment
        val aliasWords = cleanAlias.split(" ")
        if (aliasWords.size > 1 && (cleanAlias.contains(cleanQuery) || aliasWords.any { it.startsWith(cleanQuery) })) {
            return true
        }
    }

    // 3. Multi-token search (e.g. "pre med", "bachelor arts", "doc phil")
    val queryTokens = cleanQuery.split(" ").filter { it.isNotBlank() }
    if (queryTokens.size > 1) {
        val searchCorpus = "$cleanOption " + aliases.joinToString(" ") { normalizeSearchText(it) }
        val searchWords = searchCorpus.split(" ").filter { it.isNotBlank() }
        val allTokensMatch = queryTokens.all { token ->
            searchWords.any { it.startsWith(token) } || searchCorpus.contains(token)
        }
        if (allTokensMatch) {
            return true
        }
    }

    return false
}

/**
 * A modern, responsive Modal Bottom Sheet for searching and selecting
 * standard qualification presets with category filters.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QualificationSelectionBottomSheet(
    onSelectQualification: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val sheetHeightFraction = if (isLandscape) 0.96f else 0.72f

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategoryIndex by rememberSaveable { mutableIntStateOf(0) } // 0 = All

    val filteredCategories = remember(searchQuery, selectedCategoryIndex) {
        val trimmedQuery = searchQuery.trim()

        StandardQualificationCategories.mapNotNull { category ->
            val matchesCategoryFilter = selectedCategoryIndex == 0 ||
                    StandardQualificationCategories.indexOf(category) == (selectedCategoryIndex - 1)

            if (!matchesCategoryFilter) return@mapNotNull null

            val matchingOptions = if (trimmedQuery.isEmpty()) {
                category.options
            } else {
                category.options.filter { option ->
                    matchesQualification(option, category.title, trimmedQuery)
                }
            }

            if (matchingOptions.isNotEmpty()) {
                category.copy(options = matchingOptions)
            } else null
        }
    }

    NexaModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            NexaBottomSheetDragHandle(
                topPadding = if (isLandscape) 6.dp else 12.dp,
                bottomPadding = 4.dp
            )
        }
    ) {
        val dialogView = LocalView.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        val context = LocalContext.current

        val dismissKeyboard: () -> Unit = {
            keyboardController?.hide()
            focusManager.clearFocus(force = true)
            try {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(dialogView.windowToken, 0)
            } catch (_: Exception) {}
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(sheetHeightFraction)
                .navigationBarsPadding()
                .padding(bottom = if (isLandscape) 8.dp else 16.dp)
        ) {
            // Sheet Header (Compact in landscape to preserve screen space)
            if (!isLandscape) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 0.dp)
                ) {
                    Text(
                        text = "Select Qualification",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Quickly insert a standard degree or qualification",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 2.dp, bottom = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Qualification",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "e.g. MBBS, Computer Science, MBA...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { dismissKeyboard() },
                        onSearch = { dismissKeyboard() },
                        onNext = { dismissKeyboard() },
                        onGo = { dismissKeyboard() },
                        onSend = { dismissKeyboard() }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "All" chip
                FilterChip(
                    selected = selectedCategoryIndex == 0,
                    onClick = { selectedCategoryIndex = 0 },
                    label = {
                        Text(
                            text = "All",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedCategoryIndex == 0) FontWeight.SemiBold else FontWeight.Normal
                            )
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    border = if (selectedCategoryIndex == 0) {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    } else {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                // Category chips
                StandardQualificationCategories.forEachIndexed { index, cat ->
                    val chipIndex = index + 1
                    val isSelected = selectedCategoryIndex == chipIndex
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryIndex = chipIndex },
                        label = {
                            Text(
                                text = cat.shortTitle,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = if (isSelected) {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            // Results List / Empty State
            if (filteredCategories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(if (isLandscape) 8.dp else 12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(if (isLandscape) 52.dp else 64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(if (isLandscape) 26.dp else 32.dp)
                                )
                            }
                        }
                        Text(
                            text = "No matching qualifications",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "You can always type your custom qualification directly into the text field.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    filteredCategories.forEach { category ->
                        if (selectedCategoryIndex == 0) {
                            item(key = "header_${category.title}") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    HorizontalDivider(
                                        modifier = Modifier.weight(1f),
                                        thickness = 1.dp,
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    )
                                    Text(
                                        text = category.title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier.weight(1f),
                                        thickness = 1.dp,
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        }

                        items(category.options, key = { "${category.title}_$it" }) { option ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectQualification(option)
                                    }
                                    .padding(horizontal = 20.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
