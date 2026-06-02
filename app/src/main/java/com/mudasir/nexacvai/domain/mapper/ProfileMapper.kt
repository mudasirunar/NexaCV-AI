package com.mudasir.nexacvai.domain.mapper

import com.mudasir.nexacvai.data.local.entity.*
import com.mudasir.nexacvai.domain.model.*

// ---------------------------------------------------------------------------
// User Profile Mappers
// ---------------------------------------------------------------------------

fun UserProfileWithDetails.toDomain(): UserProfile {
    return UserProfile(
        id = profile.id,
        fullName = profile.fullName,
        professionalTitle = profile.professionalTitle,
        emails = profile.emails,
        phones = profile.phones,
        dateOfBirth = profile.dateOfBirth,
        address = profile.address,
        yearsOfExperience = profile.yearsOfExperience,
        profilePictureUri = profile.profilePictureUri,
        professionalSummary = profile.professionalSummary,
        skills = profile.skills,
        experiences = experiences.map { it.toDomain() },
        projects = projects.map { it.toDomain() },
        educations = educations.map { it.toDomain() },
        certifications = certifications.map { it.toDomain() },
        references = references.map { it.toDomain() },
        socialLinks = socialLinks.map { it.toDomain() },
        languages = languages.map { it.toDomain() },
        hobbies = profile.hobbies,
        volunteerWork = profile.volunteerWork,
        awards = profile.awards,
        createdAt = profile.createdAt,
        updatedAt = profile.updatedAt
    )
}

fun UserProfile.toEntity(): UserProfileEntity {
    return UserProfileEntity(
        id = id,
        fullName = fullName,
        professionalTitle = professionalTitle,
        emails = emails,
        phones = phones,
        dateOfBirth = dateOfBirth,
        address = address,
        yearsOfExperience = yearsOfExperience,
        profilePictureUri = profilePictureUri,
        professionalSummary = professionalSummary,
        skills = skills,
        hobbies = hobbies,
        volunteerWork = volunteerWork,
        awards = awards,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// ---------------------------------------------------------------------------
// Experience Mappers
// ---------------------------------------------------------------------------

fun ExperienceEntity.toDomain(): Experience {
    return Experience(
        id = id,
        jobTitle = jobTitle,
        companyName = companyName,
        location = location,
        startDate = startDate,
        endDate = endDate,
        isCurrentlyWorking = isCurrentlyWorking,
        description = description
    )
}

fun Experience.toEntity(profileId: Long): ExperienceEntity {
    return ExperienceEntity(
        id = id,
        profileId = profileId,
        jobTitle = jobTitle,
        companyName = companyName,
        location = location,
        startDate = startDate,
        endDate = endDate,
        isCurrentlyWorking = isCurrentlyWorking,
        description = description
    )
}

// ---------------------------------------------------------------------------
// Project Mappers
// ---------------------------------------------------------------------------

fun ProjectEntity.toDomain(): Project {
    return Project(
        id = id,
        projectName = projectName,
        description = description,
        technologiesUsed = technologiesUsed,
        projectLink = projectLink,
        roleInProject = roleInProject,
        startDate = startDate,
        endDate = endDate
    )
}

fun Project.toEntity(profileId: Long): ProjectEntity {
    return ProjectEntity(
        id = id,
        profileId = profileId,
        projectName = projectName,
        description = description,
        technologiesUsed = technologiesUsed,
        projectLink = projectLink,
        roleInProject = roleInProject,
        startDate = startDate,
        endDate = endDate
    )
}

// ---------------------------------------------------------------------------
// Education Mappers
// ---------------------------------------------------------------------------

fun EducationEntity.toDomain(): Education {
    return Education(
        id = id,
        degree = degree,
        fieldOfStudy = fieldOfStudy,
        instituteName = instituteName,
        startDate = startDate,
        endDate = endDate,
        isCurrentlyStudying = isCurrentlyStudying,
        grade = grade,
        description = description
    )
}

fun Education.toEntity(profileId: Long): EducationEntity {
    return EducationEntity(
        id = id,
        profileId = profileId,
        degree = degree,
        fieldOfStudy = fieldOfStudy,
        instituteName = instituteName,
        startDate = startDate,
        endDate = endDate,
        isCurrentlyStudying = isCurrentlyStudying,
        grade = grade,
        description = description
    )
}

// ---------------------------------------------------------------------------
// Certification Mappers
// ---------------------------------------------------------------------------

fun CertificationEntity.toDomain(): Certification {
    return Certification(
        id = id,
        certificationName = certificationName,
        issuingOrganization = issuingOrganization,
        issueDate = issueDate,
        expiryDate = expiryDate,
        credentialUrl = credentialUrl
    )
}

fun Certification.toEntity(profileId: Long): CertificationEntity {
    return CertificationEntity(
        id = id,
        profileId = profileId,
        certificationName = certificationName,
        issuingOrganization = issuingOrganization,
        issueDate = issueDate,
        expiryDate = expiryDate,
        credentialUrl = credentialUrl
    )
}

// ---------------------------------------------------------------------------
// Reference Mappers
// ---------------------------------------------------------------------------

fun ReferenceEntity.toDomain(): Reference {
    return Reference(
        id = id,
        fullName = fullName,
        jobTitle = jobTitle,
        company = company,
        email = email,
        phone = phone,
        linkedInUrl = linkedInUrl,
        notes = notes
    )
}

fun Reference.toEntity(profileId: Long): ReferenceEntity {
    return ReferenceEntity(
        id = id,
        profileId = profileId,
        fullName = fullName,
        jobTitle = jobTitle,
        company = company,
        email = email,
        phone = phone,
        linkedInUrl = linkedInUrl,
        notes = notes
    )
}

// ---------------------------------------------------------------------------
// SocialLink Mappers
// ---------------------------------------------------------------------------

fun SocialLinkEntity.toDomain(): SocialLink {
    return SocialLink(
        id = id,
        label = label,
        url = url
    )
}

fun SocialLink.toEntity(profileId: Long): SocialLinkEntity {
    return SocialLinkEntity(
        id = id,
        profileId = profileId,
        label = label,
        url = url
    )
}

// ---------------------------------------------------------------------------
// Language Mappers
// ---------------------------------------------------------------------------

fun LanguageEntity.toDomain(): Language {
    return Language(
        id = id,
        languageName = languageName,
        proficiency = proficiency
    )
}

fun Language.toEntity(profileId: Long): LanguageEntity {
    return LanguageEntity(
        id = id,
        profileId = profileId,
        languageName = languageName,
        proficiency = proficiency
    )
}
