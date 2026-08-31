package com.mudasir.nexacvai.domain.model

fun UserProfile.matchesSearchQuery(query: String, liveSourceProfileName: String? = null): Boolean {
    val trimmed = query.trim()
    if (trimmed.isEmpty()) return true

    val target = trimmed.lowercase()

    fun String?.containsQuery(): Boolean {
        return this?.lowercase()?.contains(target) == true
    }

    fun List<String>?.anyContainsQuery(): Boolean {
        return this?.any { it.containsQuery() } == true
    }

    // Direct Profile Metadata
    if (fullName.containsQuery()) return true
    if (professionalTitle.containsQuery()) return true
    if (professionalSummary.containsQuery()) return true
    if (address.containsQuery()) return true
    if (dateOfBirth.containsQuery()) return true
    if (yearsOfExperience.containsQuery()) return true
    if (hobbies.containsQuery()) return true
    if (volunteerWork.containsQuery()) return true
    if (awards.containsQuery()) return true
    if (liveSourceProfileName.containsQuery()) return true
    if (sourceProfileName.containsQuery()) return true

    // Lists of primitives
    if (emails.anyContainsQuery()) return true
    if (phones.anyContainsQuery()) return true
    if (skills.anyContainsQuery()) return true

    // Experiences
    if (experiences.any { exp ->
            exp.companyName.containsQuery() ||
            exp.jobTitle.containsQuery() ||
            exp.location.containsQuery() ||
            exp.description.containsQuery()
        }) return true

    // Projects
    if (projects.any { proj ->
            proj.projectName.containsQuery() ||
            proj.roleInProject.containsQuery() ||
            proj.description.containsQuery() ||
            proj.projectLink.containsQuery() ||
            proj.technologiesUsed.anyContainsQuery()
        }) return true

    // Educations
    if (educations.any { edu ->
            edu.instituteName.containsQuery() ||
            edu.degree.containsQuery() ||
            edu.fieldOfStudy.containsQuery() ||
            edu.grade.containsQuery() ||
            edu.description.containsQuery()
        }) return true

    // Certifications
    if (certifications.any { cert ->
            cert.certificationName.containsQuery() ||
            cert.issuingOrganization.containsQuery() ||
            cert.credentialUrl.containsQuery()
        }) return true

    // References
    if (references.any { ref ->
            ref.fullName.containsQuery() ||
            ref.jobTitle.containsQuery() ||
            ref.company.containsQuery() ||
            ref.email.containsQuery() ||
            ref.phone.containsQuery() ||
            ref.linkedInUrl.containsQuery() ||
            ref.notes.containsQuery()
        }) return true

    // Social Links
    if (socialLinks.any { social ->
            social.label.containsQuery() ||
            social.url.containsQuery()
        }) return true

    // Languages
    if (languages.any { lang ->
            lang.languageName.containsQuery() ||
            lang.proficiency.containsQuery()
        }) return true

    return false
}
