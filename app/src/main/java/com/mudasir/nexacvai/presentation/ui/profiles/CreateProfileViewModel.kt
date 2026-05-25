package com.mudasir.nexacvai.presentation.ui.profiles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudasir.nexacvai.domain.model.*
import com.mudasir.nexacvai.domain.usecase.GetProfileUseCase
import com.mudasir.nexacvai.domain.usecase.SaveProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.Immutable

@Immutable
data class CreateProfileState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val currentStep: Int = 0,
    val totalSteps: Int = 10,
    val error: String? = null,
    
    // Original Profile ID (if editing)
    val profileId: Long? = null,

    // Step 1: Basic Info
    val fullName: String = "",
    val professionalTitle: String = "",
    val emails: List<String> = listOf(""), 
    val phones: List<String> = listOf(""),
    val country: String = "",
    val city: String = "",
    val preferredRole: String = "",
    val yearsOfExperience: String = "",
    val profilePictureUri: String? = null,
    
    // Step 2: Summary
    val careerObjective: String = "",
    val professionalSummary: String = "",
    
    // Step 3: Skills
    val skills: List<String> = emptyList(),
    val currentSkillInput: String = "",
    
    // Step 4: Experiences
    val experiences: List<Experience> = emptyList(),
    
    // Step 5: Projects
    val projects: List<Project> = emptyList(),
    
    // Step 6: Educations
    val educations: List<Education> = emptyList(),
    
    // Step 7: Certifications
    val certifications: List<Certification> = emptyList(),
    
    // Step 8: References
    val references: List<Reference> = emptyList(),
    
    // Step 9: Social Links
    val socialLinks: List<SocialLink> = emptyList(),
    
    // Step 9: Languages
    val languages: List<Language> = emptyList(),
    
    // Step 10: Additional Info
    val hobbies: String = "",
    val volunteerWork: String = "",
    val awards: String = ""
)

class CreateProfileViewModel(
    private val saveProfileUseCase: SaveProfileUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(CreateProfileState())
    val state: StateFlow<CreateProfileState> = _state.asStateFlow()

    init {
        val profileId = savedStateHandle.get<Long>("profileId")
        if (profileId != null && profileId != -1L) {
            loadProfile(profileId)
        }
    }

    private fun loadProfile(id: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val profile = getProfileUseCase(id)
            if (profile != null) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    profileId = profile.id,
                    fullName = profile.fullName,
                    professionalTitle = profile.professionalTitle,
                    emails = profile.emails.ifEmpty { listOf("") },
                    phones = profile.phones.ifEmpty { listOf("") },
                    country = profile.country,
                    city = profile.city,
                    preferredRole = profile.preferredRole,
                    yearsOfExperience = profile.yearsOfExperience,
                    profilePictureUri = profile.profilePictureUri,
                    careerObjective = profile.careerObjective,
                    professionalSummary = profile.professionalSummary,
                    skills = profile.skills,
                    experiences = profile.experiences,
                    projects = profile.projects,
                    educations = profile.educations,
                    certifications = profile.certifications,
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
        country: String? = null,
        city: String? = null,
        preferredRole: String? = null,
        yearsOfExperience: String? = null,
        profilePictureUri: String? = null
    ) {
        _state.value = _state.value.copy(
            fullName = fullName ?: _state.value.fullName,
            professionalTitle = title ?: _state.value.professionalTitle,
            emails = emails ?: _state.value.emails,
            phones = phones ?: _state.value.phones,
            country = country ?: _state.value.country,
            city = city ?: _state.value.city,
            preferredRole = preferredRole ?: _state.value.preferredRole,
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

    fun updateSummary(objective: String? = null, summary: String? = null) {
        _state.value = _state.value.copy(
            careerObjective = objective ?: _state.value.careerObjective,
            professionalSummary = summary ?: _state.value.professionalSummary
        )
    }

    fun addSkill(skill: String) {
        if (skill.isNotBlank() && !_state.value.skills.contains(skill)) {
            val newSkills = _state.value.skills + skill.trim()
            _state.value = _state.value.copy(skills = newSkills, currentSkillInput = "")
        }
    }

    fun removeSkill(skill: String) {
        val newSkills = _state.value.skills.filter { it != skill }
        _state.value = _state.value.copy(skills = newSkills)
    }
    
    fun updateSkillInput(input: String) {
        _state.value = _state.value.copy(currentSkillInput = input)
    }

    fun addExperience(exp: Experience) {
        _state.value = _state.value.copy(experiences = _state.value.experiences + exp)
    }
    
    fun removeExperience(exp: Experience) {
        _state.value = _state.value.copy(experiences = _state.value.experiences.filter { it.id != exp.id })
    }

    fun addProject(project: Project) {
        _state.value = _state.value.copy(projects = _state.value.projects + project)
    }
    
    fun removeProject(project: Project) {
        _state.value = _state.value.copy(projects = _state.value.projects.filter { it.id != project.id })
    }

    fun addEducation(edu: Education) {
        _state.value = _state.value.copy(educations = _state.value.educations + edu)
    }
    
    fun removeEducation(edu: Education) {
        _state.value = _state.value.copy(educations = _state.value.educations.filter { it.id != edu.id })
    }

    fun addCertification(cert: Certification) {
        _state.value = _state.value.copy(certifications = _state.value.certifications + cert)
    }
    
    fun removeCertification(cert: Certification) {
        _state.value = _state.value.copy(certifications = _state.value.certifications.filter { it.id != cert.id })
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

    fun updateAdditionalInfo(hobbies: String? = null, volunteerWork: String? = null, awards: String? = null) {
        _state.value = _state.value.copy(
            hobbies = hobbies ?: _state.value.hobbies,
            volunteerWork = volunteerWork ?: _state.value.volunteerWork,
            awards = awards ?: _state.value.awards
        )
    }

    fun saveProfile() {
        val currentState = _state.value
        
        // Validation: Name and Title are strictly required
        if (currentState.fullName.isBlank()) {
            _state.value = currentState.copy(error = "Name is required")
            return
        }
        if (currentState.professionalTitle.isBlank()) {
            _state.value = currentState.copy(error = "Title is required")
            return
        }

        viewModelScope.launch {
            _state.value = currentState.copy(isSaving = true, error = null)
            
            // Clean empty entries
            val cleanedEmails = currentState.emails.filter { it.isNotBlank() }
            val cleanedPhones = currentState.phones.filter { it.isNotBlank() }
            
            val profile = UserProfile(
                id = currentState.profileId ?: 0L,
                fullName = currentState.fullName,
                professionalTitle = currentState.professionalTitle,
                emails = cleanedEmails,
                phones = cleanedPhones,
                country = currentState.country,
                city = currentState.city,
                preferredRole = currentState.preferredRole,
                yearsOfExperience = currentState.yearsOfExperience,
                profilePictureUri = currentState.profilePictureUri,
                careerObjective = currentState.careerObjective,
                professionalSummary = currentState.professionalSummary,
                skills = currentState.skills,
                experiences = currentState.experiences,
                projects = currentState.projects,
                educations = currentState.educations,
                certifications = currentState.certifications,
                references = currentState.references,
                socialLinks = currentState.socialLinks,
                languages = currentState.languages,
                hobbies = currentState.hobbies,
                volunteerWork = currentState.volunteerWork,
                awards = currentState.awards
            )
            
            saveProfileUseCase(profile)
            
            _state.value = currentState.copy(isSaving = false, isSaved = true)
        }
    }
}
