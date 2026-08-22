package com.mudasir.nexacvai.presentation.ui.profiles.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudasir.nexacvai.domain.model.*
import com.mudasir.nexacvai.domain.usecase.GetProfileUseCase
import com.mudasir.nexacvai.domain.usecase.SaveProfileUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val saveProfileUseCase: SaveProfileUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialProfileId = savedStateHandle.get<Long>("profileId")?.takeIf { it != -1L }

    private val _state = MutableStateFlow(
        CreateProfileState(
            profileId = initialProfileId,
            isLoading = initialProfileId != null
        )
    )
    val state: StateFlow<CreateProfileState> = _state.asStateFlow()
    private var isCurrentlySaving = false
    
    // Track the initial state to detect unsaved changes
    private var initialProfile: UserProfile = UserProfile()

    init {
        initialProfileId?.let { id ->
            loadProfile(id)
        }
    }

    private fun loadProfile(id: Long) {
        viewModelScope.launch {
            val profile = getProfileUseCase(id)
            if (profile != null) {
                initialProfile = profile
                _state.value = _state.value.copy(
                    isLoading = false,
                    profileId = profile.id,
                    fullName = profile.fullName,
                    professionalTitle = profile.professionalTitle,
                    emails = profile.emails.ifEmpty { listOf("") },
                    phones = profile.phones.ifEmpty { listOf("") },
                    dateOfBirth = profile.dateOfBirth,
                    address = profile.address,
                    yearsOfExperience = profile.yearsOfExperience,
                    profilePictureUri = profile.profilePictureUri,
                    professionalSummary = profile.professionalSummary,
                    skills = profile.skills,
                    experiences = profile.experiences,
                    projects = profile.projects,
                    educations = profile.educations.ifEmpty { listOf(Education()) },
                    certifications = profile.certifications,
                    references = profile.references,
                    socialLinks = profile.socialLinks,
                    languages = profile.languages,
                    hobbies = profile.hobbies,
                    volunteerWork = profile.volunteerWork,
                    awards = profile.awards
                )
            } else {
                _state.value = _state.value.copy(isLoading = false, error = "Profile not found")
            }
        }
    }

    // Basic Navigation & Validation
    fun validateStep(step: Int): Boolean {
        when (step) {
            0 -> {
                val name = _state.value.fullName.trim()
                val title = _state.value.professionalTitle.trim()
                val nameErr = if (name.isBlank()) "Full name is required" else null
                val titleErr = if (title.isBlank()) "Professional title is required" else null
                
                if (nameErr != null || titleErr != null) {
                    _state.value = _state.value.copy(
                        fullNameError = nameErr,
                        professionalTitleError = titleErr,
                        validationTrigger = System.currentTimeMillis()
                    )
                    return false
                }
                _state.value = _state.value.copy(
                    fullNameError = null,
                    professionalTitleError = null
                )
                return true
            }
            1 -> {
                if (_state.value.skills.isEmpty()) {
                    _state.value = _state.value.copy(
                        skillsError = "Please add at least 1 core skill to proceed.",
                        validationTrigger = System.currentTimeMillis()
                    )
                    return false
                }
                _state.value = _state.value.copy(skillsError = null)
                return true
            }
            2 -> {
                val invalidExp = _state.value.experiences.any { it.jobTitle.isBlank() || it.companyName.isBlank() }
                val invalidProj = _state.value.projects.any { it.projectName.isBlank() }
                if (invalidExp || invalidProj) {
                    _state.value = _state.value.copy(
                        experienceError = if (invalidExp) "Please enter Job Title and Company for all work experience entries." else null,
                        projectError = if (invalidProj) "Please enter a Project Name for all project entries." else null,
                        validationTrigger = System.currentTimeMillis()
                    )
                    return false
                }
                _state.value = _state.value.copy(experienceError = null, projectError = null)
                return true
            }
            3 -> {
                val validEducations = _state.value.educations.filter { it.degree.isNotBlank() && it.instituteName.isNotBlank() }
                if (validEducations.isEmpty()) {
                    _state.value = _state.value.copy(
                        educationError = "Please enter Degree and Institute for at least 1 education record.",
                        validationTrigger = System.currentTimeMillis()
                    )
                    return false
                }
                val invalidEdu = _state.value.educations.any { it.degree.isBlank() || it.instituteName.isBlank() }
                val invalidCert = _state.value.certifications.any { it.certificationName.isBlank() || it.issuingOrganization.isBlank() }
                if (invalidEdu || invalidCert) {
                    _state.value = _state.value.copy(
                        educationError = if (invalidEdu) "Please enter Degree and Institute for all education entries." else null,
                        certificationError = if (invalidCert) "Please enter Certificate Name and Organization for all certifications." else null,
                        validationTrigger = System.currentTimeMillis()
                    )
                    return false
                }
                _state.value = _state.value.copy(educationError = null, certificationError = null)
                return true
            }
            4 -> {
                val invalidSocial = _state.value.socialLinks.any { it.label.isBlank() || it.label == "Other" || it.url.isBlank() }
                val invalidRef = _state.value.references.any { it.fullName.isBlank() || it.jobTitle.isBlank() || it.company.isBlank() }
                val invalidLang = _state.value.languages.any { it.languageName.isBlank() || it.proficiency.isBlank() }
                if (invalidSocial || invalidRef || invalidLang) {
                    _state.value = _state.value.copy(
                        socialLinksError = if (invalidSocial) "Please enter a platform label and URL for all social links." else null,
                        referencesError = if (invalidRef) "Please enter Full Name, Job Title, and Company for all references." else null,
                        languagesError = if (invalidLang) "Please enter Language Name and Proficiency for all languages." else null,
                        validationTrigger = System.currentTimeMillis()
                    )
                    return false
                }
                _state.value = _state.value.copy(
                    socialLinksError = null,
                    referencesError = null,
                    languagesError = null
                )
                return true
            }
            else -> return true
        }
    }

    fun nextStep(): Boolean {
        val current = _state.value.currentStep
        if (!validateStep(current)) {
            return false
        }
        if (current < _state.value.totalSteps - 1) {
            _state.value = _state.value.copy(currentStep = current + 1)
            return true
        }
        return false
    }

    fun previousStep() {
        val current = _state.value.currentStep
        if (current > 0) {
            _state.value = _state.value.copy(currentStep = current - 1)
        }
    }
    
    fun setStep(step: Int) {
        if (step in 0 until _state.value.totalSteps) {
            _state.value = _state.value.copy(currentStep = step)
        }
    }

    fun updateBasicInfo(
        fullName: String? = null,
        title: String? = null,
        emails: List<String>? = null,
        phones: List<String>? = null,
        dateOfBirth: String? = null,
        address: String? = null,
        yearsOfExperience: String? = null,
        profilePictureUri: String? = null
    ) {
        _state.value = _state.value.copy(
            fullName = fullName ?: _state.value.fullName,
            professionalTitle = title ?: _state.value.professionalTitle,
            fullNameError = if (fullName != null) null else _state.value.fullNameError,
            professionalTitleError = if (title != null) null else _state.value.professionalTitleError,
            emails = emails ?: _state.value.emails,
            phones = phones ?: _state.value.phones,
            dateOfBirth = dateOfBirth ?: _state.value.dateOfBirth,
            address = address ?: _state.value.address,
            yearsOfExperience = yearsOfExperience ?: _state.value.yearsOfExperience,
            profilePictureUri = profilePictureUri ?: _state.value.profilePictureUri
        )
    }

    fun removeProfilePicture() {
        _state.value = _state.value.copy(profilePictureUri = null)
    }

    fun updateEmail(index: Int, value: String) {
        val currentEmails = _state.value.emails.toMutableList()
        if (index in currentEmails.indices) {
            currentEmails[index] = value
            _state.value = _state.value.copy(emails = currentEmails)
        }
    }

    fun addEmailField() {
        if (_state.value.emails.size < 3) {
            _state.value = _state.value.copy(emails = _state.value.emails + "")
        }
    }

    fun removeEmailField(index: Int) {
        val currentEmails = _state.value.emails.toMutableList()
        if (index in currentEmails.indices && currentEmails.size > 1) {
            currentEmails.removeAt(index)
            _state.value = _state.value.copy(emails = currentEmails)
        }
    }

    fun updatePhone(index: Int, value: String) {
        val currentPhones = _state.value.phones.toMutableList()
        if (index in currentPhones.indices) {
            currentPhones[index] = value
            _state.value = _state.value.copy(phones = currentPhones)
        }
    }

    fun addPhoneField() {
        if (_state.value.phones.size < 3) {
            _state.value = _state.value.copy(phones = _state.value.phones + "")
        }
    }

    fun removePhoneField(index: Int) {
        val currentPhones = _state.value.phones.toMutableList()
        if (index in currentPhones.indices && currentPhones.size > 1) {
            currentPhones.removeAt(index)
            _state.value = _state.value.copy(phones = currentPhones)
        }
    }

    fun updateSummary(summary: String? = null) {
        val capitalizedSummary = summary?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        _state.value = _state.value.copy(
            professionalSummary = capitalizedSummary ?: _state.value.professionalSummary
        )
    }

    private var duplicateSkillErrorJob: Job? = null

    fun addSkill(skill: String) {
        val trimmedSkill = skill.trim()
        if (trimmedSkill.isBlank()) return

        val exists = _state.value.skills.any { it.equals(trimmedSkill, ignoreCase = true) }
        if (exists) {
            duplicateSkillErrorJob?.cancel()
            _state.value = _state.value.copy(
                duplicateSkillError = "Skill '$trimmedSkill' has already been added."
            )
            duplicateSkillErrorJob = viewModelScope.launch {
                delay(3000L)
                _state.value = _state.value.copy(duplicateSkillError = null)
            }
        } else {
            duplicateSkillErrorJob?.cancel()
            val newSkills = _state.value.skills + trimmedSkill
            _state.value = _state.value.copy(
                skills = newSkills,
                skillsError = null,
                currentSkillInput = "",
                duplicateSkillError = null
            )
        }
    }

    fun removeSkill(skill: String) {
        duplicateSkillErrorJob?.cancel()
        val newSkills = _state.value.skills.filter { it != skill }
        _state.value = _state.value.copy(
            skills = newSkills,
            duplicateSkillError = null
        )
    }
    
    fun updateSkillInput(input: String) {
        if (_state.value.duplicateSkillError != null) {
            duplicateSkillErrorJob?.cancel()
            _state.value = _state.value.copy(
                currentSkillInput = input,
                duplicateSkillError = null
            )
        } else {
            _state.value = _state.value.copy(currentSkillInput = input)
        }
    }

    fun addExperience(exp: Experience) {
        _state.value = _state.value.copy(
            experiences = _state.value.experiences + exp,
            experienceError = null
        )
    }
    
    fun removeExperience(exp: Experience) {
        val remaining = _state.value.experiences.filter { it.id != exp.id }
        val remainingInvalid = remaining.any { it.jobTitle.isBlank() || it.companyName.isBlank() }
        _state.value = _state.value.copy(
            experiences = remaining,
            experienceError = if (remainingInvalid) _state.value.experienceError else null
        )
    }

    fun updateExperience(id: String, updated: Experience) {
        val list = _state.value.experiences.map {
            if (it.id == id) updated else it
        }
        val remainingInvalid = list.any { it.jobTitle.isBlank() || it.companyName.isBlank() }
        _state.value = _state.value.copy(
            experiences = list,
            experienceError = if (remainingInvalid) _state.value.experienceError else null
        )
    }

    fun addProject(project: Project) {
        _state.value = _state.value.copy(
            projects = _state.value.projects + project,
            projectError = null
        )
    }
    
    fun removeProject(project: Project) {
        val remaining = _state.value.projects.filter { it.id != project.id }
        val remainingInvalid = remaining.any { it.projectName.isBlank() }
        _state.value = _state.value.copy(
            projects = remaining,
            projectError = if (remainingInvalid) _state.value.projectError else null
        )
    }

    fun updateProject(id: String, updated: Project) {
        val list = _state.value.projects.map {
            if (it.id == id) updated else it
        }
        val remainingInvalid = list.any { it.projectName.isBlank() }
        _state.value = _state.value.copy(
            projects = list,
            projectError = if (remainingInvalid) _state.value.projectError else null
        )
    }

    fun addEducation(edu: Education) {
        _state.value = _state.value.copy(
            educations = _state.value.educations + edu,
            educationError = null
        )
    }
    
    fun removeEducation(edu: Education) {
        val remaining = _state.value.educations.filter { it.id != edu.id }
        val remainingInvalid = remaining.any { it.degree.isBlank() || it.instituteName.isBlank() }
        _state.value = _state.value.copy(
            educations = remaining,
            educationError = if (remaining.isEmpty() && _state.value.educationError != null) "Please add at least 1 education record to proceed." else if (remainingInvalid) _state.value.educationError else null
        )
    }

    fun updateEducation(id: String, updated: Education) {
        val list = _state.value.educations.map {
            if (it.id == id) updated else it
        }
        val remainingInvalid = list.any { it.degree.isBlank() || it.instituteName.isBlank() }
        _state.value = _state.value.copy(
            educations = list,
            educationError = if (remainingInvalid) _state.value.educationError else null
        )
    }

    fun addCertification(cert: Certification) {
        _state.value = _state.value.copy(
            certifications = _state.value.certifications + cert,
            certificationError = null
        )
    }
    
    fun removeCertification(cert: Certification) {
        val remaining = _state.value.certifications.filter { it.id != cert.id }
        val remainingInvalid = remaining.any { it.certificationName.isBlank() || it.issuingOrganization.isBlank() }
        _state.value = _state.value.copy(
            certifications = remaining,
            certificationError = if (remainingInvalid) _state.value.certificationError else null
        )
    }

    fun updateCertification(id: String, updated: Certification) {
        val list = _state.value.certifications.map {
            if (it.id == id) updated else it
        }
        val remainingInvalid = list.any { it.certificationName.isBlank() || it.issuingOrganization.isBlank() }
        _state.value = _state.value.copy(
            certifications = list,
            certificationError = if (remainingInvalid) _state.value.certificationError else null
        )
    }

    fun updateReference(id: String, updated: Reference) {
        val list = _state.value.references.map {
            if (it.id == id) updated else it
        }
        val remainingInvalid = list.any { it.fullName.isBlank() || it.jobTitle.isBlank() || it.company.isBlank() }
        _state.value = _state.value.copy(
            references = list,
            referencesError = if (remainingInvalid) _state.value.referencesError else null
        )
    }

    fun addReference(ref: Reference) {
        _state.value = _state.value.copy(
            references = _state.value.references + ref,
            referencesError = null
        )
    }

    fun removeReference(ref: Reference) {
        val remaining = _state.value.references.filter { it.id != ref.id }
        val remainingInvalid = remaining.any { it.fullName.isBlank() || it.jobTitle.isBlank() || it.company.isBlank() }
        _state.value = _state.value.copy(
            references = remaining,
            referencesError = if (remainingInvalid) _state.value.referencesError else null
        )
    }

    fun addSocialLink(link: SocialLink) {
        _state.value = _state.value.copy(
            socialLinks = _state.value.socialLinks + link,
            socialLinksError = null
        )
    }
    
    fun removeSocialLink(link: SocialLink) {
        val remaining = _state.value.socialLinks.filter { it.id != link.id }
        val remainingInvalid = remaining.any { it.label.isBlank() || it.label == "Other" || it.url.isBlank() }
        _state.value = _state.value.copy(
            socialLinks = remaining,
            socialLinksError = if (remainingInvalid) _state.value.socialLinksError else null
        )
    }

    fun addLanguage(lang: Language) {
        _state.value = _state.value.copy(
            languages = _state.value.languages + lang,
            languagesError = null
        )
    }
    
    fun removeLanguage(lang: Language) {
        val remaining = _state.value.languages.filter { it.id != lang.id }
        val remainingInvalid = remaining.any { it.languageName.isBlank() || it.proficiency.isBlank() }
        _state.value = _state.value.copy(
            languages = remaining,
            languagesError = if (remainingInvalid) _state.value.languagesError else null
        )
    }

    fun updateSocialLink(id: String, updated: SocialLink) {
        val list = _state.value.socialLinks.map {
            if (it.id == id) updated else it
        }
        val remainingInvalid = list.any { it.label.isBlank() || it.label == "Other" || it.url.isBlank() }
        _state.value = _state.value.copy(
            socialLinks = list,
            socialLinksError = if (remainingInvalid) _state.value.socialLinksError else null
        )
    }

    fun updateLanguage(id: String, updated: Language) {
        val list = _state.value.languages.map {
            if (it.id == id) updated else it
        }
        val remainingInvalid = list.any { it.languageName.isBlank() || it.proficiency.isBlank() }
        _state.value = _state.value.copy(
            languages = list,
            languagesError = if (remainingInvalid) _state.value.languagesError else null
        )
    }

    fun updateAdditionalInfo(hobbies: String? = null, volunteerWork: String? = null, awards: String? = null) {
        _state.value = _state.value.copy(
            hobbies = hobbies ?: _state.value.hobbies,
            volunteerWork = volunteerWork ?: _state.value.volunteerWork,
            awards = awards ?: _state.value.awards
        )
    }

    private fun getCurrentProfile(): UserProfile {
        val upToDateState = _state.value
        val cleanedEmails = upToDateState.emails.filter { it.isNotBlank() }
        val cleanedPhones = upToDateState.phones.filter { it.isNotBlank() }
        val cleanedEducations = upToDateState.educations.filter { it.degree.isNotBlank() || it.instituteName.isNotBlank() }

        return UserProfile(
            id = upToDateState.profileId ?: 0L,
            uuid = initialProfile.uuid,
            fullName = upToDateState.fullName,
            professionalTitle = upToDateState.professionalTitle,
            emails = cleanedEmails,
            phones = cleanedPhones,
            dateOfBirth = upToDateState.dateOfBirth,
            address = upToDateState.address,
            yearsOfExperience = upToDateState.yearsOfExperience,
            profilePictureUri = upToDateState.profilePictureUri,
            professionalSummary = upToDateState.professionalSummary,
            skills = upToDateState.skills,
            experiences = upToDateState.experiences,
            projects = upToDateState.projects,
            educations = cleanedEducations,
            certifications = upToDateState.certifications,
            references = upToDateState.references,
            socialLinks = upToDateState.socialLinks,
            languages = upToDateState.languages,
            hobbies = upToDateState.hobbies,
            volunteerWork = upToDateState.volunteerWork,
            awards = upToDateState.awards,
            sourceProfileId = initialProfile.sourceProfileId,
            sourceProfileName = initialProfile.sourceProfileName,
            isCopyTagDismissed = initialProfile.isCopyTagDismissed,
            createdAt = if (upToDateState.profileId == null || upToDateState.profileId == 0L) {
                System.currentTimeMillis()
            } else {
                initialProfile.createdAt
            },
            updatedAt = System.currentTimeMillis()
        )
    }

    fun hasUnsavedChanges(): Boolean {
        val current = getCurrentProfile()
        // Compare data ignoring dynamic timestamps
        return current.copy(
            createdAt = initialProfile.createdAt, 
            updatedAt = initialProfile.updatedAt
        ) != initialProfile.copy(
            createdAt = initialProfile.createdAt, 
            updatedAt = initialProfile.updatedAt
        )
    }

    fun saveDraft() {
        saveInternal(isDraft = true)
    }

    fun saveProfile() {
        saveInternal(isDraft = false)
    }

    private fun saveInternal(isDraft: Boolean) {
        if (isCurrentlySaving) return // synchronous double-save block
        isCurrentlySaving = true

        val currentState = _state.value
        if (currentState.fullName.trim().isBlank()) {
            _state.value = currentState.copy(
                currentStep = 0,
                fullNameError = "Full name is required",
                validationTrigger = System.currentTimeMillis()
            )
            isCurrentlySaving = false
            return
        }

        // For full profile completion (non-draft), perform strict validation across all steps
        if (!isDraft) {
            for (step in 0 until currentState.totalSteps) {
                if (!validateStep(step)) {
                    _state.value = _state.value.copy(
                        currentStep = step,
                        validationTrigger = System.currentTimeMillis()
                    )
                    isCurrentlySaving = false
                    return
                }
            }
        }

        // If there are no unsaved changes, complete the action without writing to database or modifying timestamps
        if (!hasUnsavedChanges()) {
            _state.value = currentState.copy(isSaved = true)
            isCurrentlySaving = false
            return
        }

        // Set isSaving synchronously before starting any asynchronous work
        _state.value = currentState.copy(isSaving = true, error = null)

        viewModelScope.launch {
            try {
                val profile = getCurrentProfile()
                val newId = saveProfileUseCase(profile)
                
                // Update initialProfile so subsequent back presses don't trigger discard warnings
                initialProfile = profile.copy(id = newId)

                // Return updated state, containing the new profileId so subsequent clicks are updates!
                _state.value = _state.value.copy(
                    profileId = newId,
                    isSaving = false,
                    isSaved = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    error = e.localizedMessage ?: "Failed to save profile"
                )
            } finally {
                isCurrentlySaving = false
            }
        }
    }
}
