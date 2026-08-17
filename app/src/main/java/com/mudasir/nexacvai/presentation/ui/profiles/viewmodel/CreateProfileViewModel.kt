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
                    educations = profile.educations,
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

    // Basic Navigation
    fun nextStep() {
        val current = _state.value.currentStep
        if (current < _state.value.totalSteps - 1) {
            _state.value = _state.value.copy(currentStep = current + 1)
        }
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
        _state.value = _state.value.copy(experiences = _state.value.experiences + exp)
    }
    
    fun removeExperience(exp: Experience) {
        _state.value = _state.value.copy(experiences = _state.value.experiences.filter { it.id != exp.id })
    }

    fun updateExperience(id: String, updated: Experience) {
        val list = _state.value.experiences.map {
            if (it.id == id) updated else it
        }
        _state.value = _state.value.copy(experiences = list)
    }

    fun addProject(project: Project) {
        _state.value = _state.value.copy(projects = _state.value.projects + project)
    }
    
    fun removeProject(project: Project) {
        _state.value = _state.value.copy(projects = _state.value.projects.filter { it.id != project.id })
    }

    fun updateProject(id: String, updated: Project) {
        val list = _state.value.projects.map {
            if (it.id == id) updated else it
        }
        _state.value = _state.value.copy(projects = list)
    }

    fun addEducation(edu: Education) {
        _state.value = _state.value.copy(educations = _state.value.educations + edu)
    }
    
    fun removeEducation(edu: Education) {
        _state.value = _state.value.copy(educations = _state.value.educations.filter { it.id != edu.id })
    }

    fun updateEducation(id: String, updated: Education) {
        val list = _state.value.educations.map {
            if (it.id == id) updated else it
        }
        _state.value = _state.value.copy(educations = list)
    }

    fun addCertification(cert: Certification) {
        _state.value = _state.value.copy(certifications = _state.value.certifications + cert)
    }
    
    fun removeCertification(cert: Certification) {
        _state.value = _state.value.copy(certifications = _state.value.certifications.filter { it.id != cert.id })
    }

    fun updateCertification(id: String, updated: Certification) {
        val list = _state.value.certifications.map {
            if (it.id == id) updated else it
        }
        _state.value = _state.value.copy(certifications = list)
    }

    fun updateReference(id: String, updated: Reference) {
        val list = _state.value.references.map {
            if (it.id == id) updated else it
        }
        _state.value = _state.value.copy(references = list)
    }

    fun addReference(ref: Reference) {
        _state.value = _state.value.copy(references = _state.value.references + ref)
    }

    fun removeReference(ref: Reference) {
        _state.value = _state.value.copy(references = _state.value.references.filter { it.id != ref.id })
    }

    fun addSocialLink(link: SocialLink) {
        _state.value = _state.value.copy(socialLinks = _state.value.socialLinks + link)
    }
    
    fun removeSocialLink(link: SocialLink) {
        _state.value = _state.value.copy(socialLinks = _state.value.socialLinks.filter { it.id != link.id })
    }

    fun addLanguage(lang: Language) {
        _state.value = _state.value.copy(languages = _state.value.languages + lang)
    }
    
    fun removeLanguage(lang: Language) {
        _state.value = _state.value.copy(languages = _state.value.languages.filter { it.id != lang.id })
    }

    fun updateSocialLink(id: String, updated: SocialLink) {
        val list = _state.value.socialLinks.map {
            if (it.id == id) updated else it
        }
        _state.value = _state.value.copy(socialLinks = list)
    }

    fun updateLanguage(id: String, updated: Language) {
        val list = _state.value.languages.map {
            if (it.id == id) updated else it
        }
        _state.value = _state.value.copy(languages = list)
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
            educations = upToDateState.educations,
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

    fun saveProfile() {
        if (isCurrentlySaving) return // synchronous double-save block
        isCurrentlySaving = true
        
        val currentState = _state.value
        if (currentState.fullName.isBlank()) {
            _state.value = currentState.copy(error = "Name is required")
            isCurrentlySaving = false
            return
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
