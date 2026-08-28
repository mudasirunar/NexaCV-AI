package com.mudasir.nexacvai.data.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mudasir.nexacvai.domain.model.template.*
import com.mudasir.nexacvai.domain.model.template.sampledata.SampleGuidanceProfiles

/**
 * Enterprise Catalog of 22 Visually Distinct Built-in Resume Templates across 4 Categories:
 * - ATS-Friendly (4): ATS Clean Standard (Marcus Vance), ATS Corporate Minimal (Sarah Jenkins), ATS Categorized Skills (Alex Mercer), ATS Hybrid Timeline (Lucas Silva).
 * - Modern & Tech (6): Modern Horizon Accent (Amara Okafor), Modern Tech Sidebar (Alex Mercer), Developer Terminal Slate (Alex Mercer), Cyber Security Matrix (David Chen), Tech Architecture Grid (Julian Thorne), Minimalist Serif (Prof. James Sterling).
 * - Executive & Corporate (6): Executive Leadership Grid (Elena Rostova), Executive Sidebar Accent (Marcus Vance), Executive Formal Slate (Sarah Jenkins), Corporate Banking (Marcus Vance), Global Enterprise Director (Elena Rostova), Managing Director Formal (Julian Thorne).
 * - Creative & Healthcare (6): Creative Studio Curve (Amara Okafor), UX Studio Sidebar (Amara Okafor), Clinical Specialist Sidebar (Dr. Sophia Lin), Clinical Doctor Credentials (Dr. Sophia Lin), Healthcare Specialist (Dr. Sophia Lin), Artisan Design Studio (Julian Thorne).
 */
object BuiltInTemplatesCatalog {

    val ALL_TEMPLATES: List<ResumeTemplate> by lazy {
        listOf(
            // --- CATEGORY 1: ATS-FRIENDLY (4 TEMPLATES — 2 WITH PHOTO, 2 WITHOUT PHOTO) ---
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_ats_clean",
                    name = "ATS Clean Standard",
                    description = "Clean single-column ATS parser-safe structure with prominent section rules and optional candidate photo. Best for finance, business analysts, and corporate roles.",
                    category = TemplateCategory.ATS,
                    supportsPhoto = true,
                    defaultPhotoShape = PhotoShape.CIRCLE,
                    previewPrimaryColorHex = "#1E293B",
                    previewAccentColorHex = "#475569"
                ),
                defaultData = SampleGuidanceProfiles.MALE_FINANCE_DIRECTOR
            ),
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_ats_executive",
                    name = "ATS Corporate Minimal",
                    description = "High-contrast minimalist layout with dark slate headers and executive divider rules. Optimized for senior managers, legal counsel, and compliance specialists.",
                    category = TemplateCategory.ATS,
                    supportsPhoto = true,
                    defaultPhotoShape = PhotoShape.CIRCLE,
                    previewPrimaryColorHex = "#1E1B4B",
                    previewAccentColorHex = "#4338CA"
                ),
                defaultData = SampleGuidanceProfiles.FEMALE_FEDERAL_SPECIALIST
            ),
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_ats_tech_matrix",
                    name = "ATS Categorized Skills",
                    description = "Single-column technical layout featuring categorized skills tables and bulleted achievements. Ideal for software engineers, QA, and systems architects.",
                    category = TemplateCategory.ATS,
                    supportsPhoto = false,
                    defaultPhotoShape = PhotoShape.CIRCLE,
                    previewPrimaryColorHex = "#0F172A",
                    previewAccentColorHex = "#0284C7"
                ),
                defaultData = SampleGuidanceProfiles.MALE_TECH_ARCHITECT
            ),
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_ats_hybrid",
                    name = "ATS Hybrid Timeline",
                    description = "Date-aligned chronological timeline emphasizing education, core skills, and internships. Perfect for fresh graduates, entry-level roles, and career changers.",
                    category = TemplateCategory.ATS,
                    supportsPhoto = false,
                    defaultPhotoShape = PhotoShape.CIRCLE,
                    previewPrimaryColorHex = "#0284C7",
                    previewAccentColorHex = "#0369A1"
                ),
                defaultData = SampleGuidanceProfiles.FRESH_GRADUATE_ENGINEER
            ),

            // --- CATEGORY 2: MODERN & TECH (6 TEMPLATES) ---
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_modern_wavy",
                    name = "Modern Horizon Accent",
                    description = "Dynamic top header accent band with candidate avatar and structured milestone blocks. Best for product managers, UX designers, and digital marketers.",
                    category = TemplateCategory.MODERN,
                    supportsPhoto = true,
                    defaultPhotoShape = PhotoShape.CIRCLE,
                    previewPrimaryColorHex = "#2563EB",
                    previewAccentColorHex = "#3B82F6"
                ),
                defaultData = SampleGuidanceProfiles.FEMALE_CREATIVE_UX_LEAD
            ),
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_modern_tech",
                    name = "Modern Tech Sidebar",
                    description = "Split 30/70 left sidebar layout separating tools, contact links, and languages from career timeline. Ideal for mobile and full-stack developers.",
                    category = TemplateCategory.MODERN,
                    supportsPhoto = true,
                    defaultPhotoShape = PhotoShape.ROUNDED_SQUARE,
                    previewPrimaryColorHex = "#0D9488",
                    previewAccentColorHex = "#14B8A6"
                ),
                defaultData = SampleGuidanceProfiles.MALE_TECH_ARCHITECT
            ),
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_developer_slate",
                    name = "Developer Terminal Slate",
                    description = "Dark terminal-styled header with monospace code accents (// OVERVIEW, @company) and tech stack tags. Tailored for backend and DevOps engineers.",
                    category = TemplateCategory.MODERN,
                    supportsPhoto = true,
                    defaultPhotoShape = PhotoShape.PASSPORT_RECT,
                    previewPrimaryColorHex = "#0F172A",
                    previewAccentColorHex = "#38BDF8"
                ),
                defaultData = SampleGuidanceProfiles.MALE_TECH_ARCHITECT
            ),
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_cyber_code",
                    name = "Cyber Security Matrix",
                    description = "Teal-accented layout with zero-trust technical matrix and security credential callouts. Designed for InfoSec, security analysts, and infrastructure leads.",
                    category = TemplateCategory.MODERN,
                    supportsPhoto = true,
                    defaultPhotoShape = PhotoShape.CIRCLE,
                    previewPrimaryColorHex = "#0D9488",
                    previewAccentColorHex = "#2DD4BF"
                ),
                defaultData = SampleGuidanceProfiles.MALE_CYBER_SECURITY_LEAD
            ),
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_tech_lead_grid",
                    name = "Tech Architecture Grid",
                    description = "Modular grid layout highlighting system design, cloud architecture, and engineering tools. Built for engineering managers and solutions architects.",
                    category = TemplateCategory.MODERN,
                    supportsPhoto = true,
                    defaultPhotoShape = PhotoShape.ROUNDED_SQUARE,
                    previewPrimaryColorHex = "#4F46E5",
                    previewAccentColorHex = "#6366F1"
                ),
                defaultData = SampleGuidanceProfiles.ARCHITECTURAL_DESIGNER
            ),
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_minimal_serif",
                    name = "Minimalist Serif",
                    description = "Refined editorial serif typography with delicate divider rules. Recommended for researchers, university professors, and academic fellows.",
                    category = TemplateCategory.MODERN,
                    supportsPhoto = true,
                    defaultPhotoShape = PhotoShape.CIRCLE,
                    previewPrimaryColorHex = "#374151",
                    previewAccentColorHex = "#4B5563"
                ),
                defaultData = SampleGuidanceProfiles.MALE_ACADEMIC_RESEARCHER
            ),

            // --- CATEGORY 3: EXECUTIVE & CORPORATE (6 TEMPLATES) ---
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_exec_competency",
                    name = "Executive Leadership Grid",
                    description = "Commanding navy header with a 3-column executive competency matrix and P&L achievements. Built for VP, C-suite, and enterprise leaders.",
                    category = TemplateCategory.EXECUTIVE,
                    supportsPhoto = true,
                    defaultPhotoShape = PhotoShape.CIRCLE,
                    previewPrimaryColorHex = "#1E1B4B",
                    previewAccentColorHex = "#312E81"
                ),
                defaultData = SampleGuidanceProfiles.FEMALE_EXECUTIVE_LEADER
            ),
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_exec_sidebar",
                    name = "Executive Sidebar Accent",
                    description = "Split-sidebar layout highlighting board directorships, core competencies, and references. Best for finance directors and management consultants.",
                    category = TemplateCategory.EXECUTIVE,
                    supportsPhoto = true,
                    defaultPhotoShape = PhotoShape.PASSPORT_RECT,
                    previewPrimaryColorHex = "#1E293B",
                    previewAccentColorHex = "#334155"
                ),
                defaultData = SampleGuidanceProfiles.MALE_FINANCE_DIRECTOR
            ),
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_exec_slate",
                    name = "Executive Formal Slate",
                    description = "Centered classic executive title block with formal margins and revenue growth callouts. Ideal for operations directors and senior consultants.",
                    category = TemplateCategory.EXECUTIVE,
                    supportsPhoto = true,
                    defaultPhotoShape = PhotoShape.CIRCLE,
                    previewPrimaryColorHex = "#334155",
                    previewAccentColorHex = "#475569"
                ),
                defaultData = SampleGuidanceProfiles.FEMALE_FEDERAL_SPECIALIST
            ),
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_corporate_banking",
                    name = "Corporate Banking",
                    description = "Navy and gold formal accent rules tailored for investment banking, private equity, and wealth management executives.",
                    category = TemplateCategory.EXECUTIVE,
                    supportsPhoto = true,
                    defaultPhotoShape = PhotoShape.PASSPORT_RECT,
                    previewPrimaryColorHex = "#B45309",
                    previewAccentColorHex = "#D97706"
                ),
                defaultData = SampleGuidanceProfiles.MALE_FINANCE_DIRECTOR
            ),
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_global_director",
                    name = "Global Enterprise Director",
                    description = "Broad header banner with revenue milestones and multi-region governance experience. Optimized for international business leaders.",
                    category = TemplateCategory.EXECUTIVE,
                    supportsPhoto = true,
                    defaultPhotoShape = PhotoShape.CIRCLE,
                    previewPrimaryColorHex = "#0369A1",
                    previewAccentColorHex = "#0284C7"
                ),
                defaultData = SampleGuidanceProfiles.FEMALE_EXECUTIVE_LEADER
            ),
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_managing_director",
                    name = "Managing Director Formal",
                    description = "Double-rule traditional executive structure focusing on corporate governance and strategic scale. Built for managing directors and board members.",
                    category = TemplateCategory.EXECUTIVE,
                    supportsPhoto = true,
                    defaultPhotoShape = PhotoShape.PASSPORT_RECT,
                    previewPrimaryColorHex = "#0F172A",
                    previewAccentColorHex = "#1E293B"
                ),
                defaultData = SampleGuidanceProfiles.ARCHITECTURAL_DESIGNER
            ),

            // --- CATEGORY 4: CREATIVE & HEALTHCARE (6 TEMPLATES) ---
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_creative_wavy",
                    name = "Creative Studio Curve",
                    description = "Vibrant curved top accent with structured portfolio project cards and creative skill badges. Perfect for art directors, illustrators, and UI designers.",
                    category = TemplateCategory.CREATIVE,
                    supportsPhoto = true,
                    defaultPhotoShape = PhotoShape.CIRCLE,
                    previewPrimaryColorHex = "#E11D48",
                    previewAccentColorHex = "#F43F5E"
                ),
                defaultData = SampleGuidanceProfiles.FEMALE_CREATIVE_UX_LEAD
            ),
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_ux_designer",
                    name = "UX Studio Sidebar",
                    description = "Purple studio sidebar with portfolio links, design process badges, and tools. Tailored for product designers, UX researchers, and design leads.",
                    category = TemplateCategory.CREATIVE,
                    supportsPhoto = true,
                    defaultPhotoShape = PhotoShape.ROUNDED_SQUARE,
                    previewPrimaryColorHex = "#9333EA",
                    previewAccentColorHex = "#A855F7"
                ),
                defaultData = SampleGuidanceProfiles.FEMALE_CREATIVE_UX_LEAD
            ),
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_clinical_sidebar",
                    name = "Clinical Specialist Sidebar",
                    description = "Medical blue sidebar panel showcasing clinical credentials, hospital affiliations, and board certifications. Built for physicians and specialists.",
                    category = TemplateCategory.CREATIVE,
                    supportsPhoto = true,
                    defaultPhotoShape = PhotoShape.ROUNDED_SQUARE,
                    previewPrimaryColorHex = "#0284C7",
                    previewAccentColorHex = "#0EA5E9"
                ),
                defaultData = SampleGuidanceProfiles.FEMALE_MEDICAL_SPECIALIST
            ),
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_clinical_doctor",
                    name = "Clinical Doctor Credentials",
                    description = "Header banner featuring dedicated medical board certification boxes and clinical appointments. Designed for doctors, surgeons, and medical leads.",
                    category = TemplateCategory.CREATIVE,
                    supportsPhoto = false,
                    defaultPhotoShape = PhotoShape.ROUNDED_SQUARE,
                    previewPrimaryColorHex = "#0284C7",
                    previewAccentColorHex = "#0EA5E9"
                ),
                defaultData = SampleGuidanceProfiles.FEMALE_MEDICAL_SPECIALIST
            ),
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_healthcare_specialist",
                    name = "Healthcare Specialist",
                    description = "Structured appointment timeline with patient care highlights and clinical research publications. Ideal for nurses and healthcare administrators.",
                    category = TemplateCategory.CREATIVE,
                    supportsPhoto = true,
                    defaultPhotoShape = PhotoShape.PASSPORT_RECT,
                    previewPrimaryColorHex = "#059669",
                    previewAccentColorHex = "#10B981"
                ),
                defaultData = SampleGuidanceProfiles.FEMALE_MEDICAL_SPECIALIST
            ),
            GenericResumeTemplate(
                metadata = TemplateMetadata(
                    id = "template_artisan_studio",
                    name = "Artisan Design Studio",
                    description = "Warm amber studio palette with refined architectural typography rules. Ideal for interior designers, architects, and creative directors.",
                    category = TemplateCategory.CREATIVE,
                    supportsPhoto = true,
                    defaultPhotoShape = PhotoShape.CIRCLE,
                    previewPrimaryColorHex = "#D97706",
                    previewAccentColorHex = "#F59E0B"
                ),
                defaultData = SampleGuidanceProfiles.ARCHITECTURAL_DESIGNER
            )
        )
    }
}

/**
 * Reusable ResumeTemplate implementation wrapping metadata and sample guidance data.
 */
class GenericResumeTemplate(
    override val metadata: TemplateMetadata,
    override val defaultData: TemplateData
) : ResumeTemplate {

    @Composable
    override fun Render(
        data: TemplateData,
        style: TemplateStyle,
        modifier: Modifier
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(style.backgroundColor)
                .padding(16.dp)
        ) {
            Text(
                text = data.fullName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = style.textColor
            )
            if (data.professionalTitle.isNotBlank()) {
                Text(
                    text = data.professionalTitle,
                    fontSize = 12.sp,
                    color = style.secondaryTextColor
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = data.summary,
                fontSize = 10.sp,
                color = style.textColor
            )
        }
    }
}
