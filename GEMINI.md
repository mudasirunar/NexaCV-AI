# App Name: NexaCV AI (Next Generation Career Builder)

## 🧠 Vision
NexaCV AI is an intelligent career assistant Android app that allows users to create multiple professional profiles inside a single account and generate ATS-optimized resumes and cover letters using AI.

The system is designed as a **modular, offline-first, highly scalable Android application** using modern architecture principles.

The app does NOT rely fully on AI. Instead:
- App handles structure, logic, formatting, and optimization
- AI is only used for text refinement and generation tasks

---

# 🚀 Core Features

## 1. Authentication
- Firebase Google Authentication only
- Single sign-in per user account
- Persistent session handling

---

## 2. Multi-Profile System (IMPORTANT)
Each user can create multiple profiles under one account.

Example:
- Profile 1: Android Developer
- Profile 2: Freelancer
- Profile 3: Internship Profile

Each profile contains:
- Personal Info
- Skills
- Experience
- Projects
- Education
- Links (GitHub, LinkedIn)

Profiles are fully independent.

---

## 3. AI CV Generator System

### Input:
- Selected profile
- Job Description (pasted text)
- Selected template

### Output:
- ATS optimized CV
- Cover letter
- Skill matching analysis
- Missing skills report

### AI Strategy (IMPORTANT):
AI is NOT responsible for full generation.

AI only performs:
- Rewriting bullet points
- Improving summaries
- Generating cover letters
- Enhancing language

Everything else is handled by app logic.

Multiple AI APIs are supported:
- Primary: Google Gemini API
- Backup: Groq API / HuggingFace API

System automatically switches API if rate limit or failure occurs.

---

## 4. Template System (CRITICAL DESIGN)

### Approach:
Templates are NOT AI-generated.

We use **local XML-like structured templates rendered via Jetpack Compose**.

### Template types:
- Modern (minimal)
- Professional (corporate)
- Creative (developer portfolio style)
- ATS Simple (black & white optimized)

### Template engine design:
Each template is a composable layout:

- Header Section
- Skills Section
- Experience Section
- Projects Section

Each section is dynamically injected with data.

### Why not AI templates?
- AI is unpredictable
- Templates must be consistent
- Performance must remain fast

---

## 5. Dashboard System
Home screen includes:
- Profile selector
- Recent CV generations
- Quick “Generate CV” button
- Template preview carousel
- Job description input shortcut

---

## 6. Offline First Architecture
App must work without internet for:
- Profile creation
- Profile editing
- Template preview
- Cached CV viewing

Only AI generation requires internet.

---

## 7. Export System
- Export CV as PDF
- Export Cover Letter as PDF
- Optional DOCX export (future)

---

# 🏗 Architecture

## Pattern:
- Clean Architecture
- MVVM Pattern
- Repository Pattern
- Dependency Injection (Hilt)

---

## Layer Structure:

### 1. Presentation Layer
- Jetpack Compose UI
- ViewModels
- UI State (StateFlow)

### 2. Domain Layer
- Use Cases:
  - CreateProfileUseCase
  - GenerateCVUseCase
  - AnalyzeJobUseCase
- Business logic only

### 3. Data Layer
- Room Database
- Firebase Auth
- AI API services
- Repositories

---

# 📦 Package Structure

com.nexacv.ai
│
├── presentation
│ ├── ui
│ ├── viewmodel
│ ├── navigation
│
├── domain
│ ├── model
│ ├── usecase
│
├── data
│ ├── local (Room DB)
│ ├── remote (AI APIs, Firebase)
│ ├── repository
│
├── di (Hilt modules)
├── core
│ ├── utils
│ ├── constants
│ ├── threading

---

# 🧠 AI System Design

## Prompt Strategy:
AI requests must be:
- small
- structured
- token optimized

### Example AI Input:
User Skills: Kotlin, Compose, Firebase
Job: Android Developer requires MVVM, REST API

Task:
Generate ATS optimized CV summary and cover letter only.


---

## AI Output Rules:
- No hallucinated skills
- No fake experience
- Only rephrase and optimize existing data

---

# ⚡ Performance Rules (CRITICAL)

## UI Performance:
- NO heavy computation in main thread
- Use Coroutine Dispatchers:
  - IO → database/network
  - Default → processing
  - Main → UI only

## Compose Optimization:
- Use LazyColumn for lists
- Avoid unnecessary recompositions
- Use remember + derivedStateOf

## Threading Rules:
- All AI calls must run in IO dispatcher
- Room DB operations async only

---

# 🧾 Room Database Design

**CRITICAL RULE:** All data models/entities MUST include `createdAt` and `updatedAt` timestamps (Long, epoch millis). This is strictly required for future Firebase synchronization and conflict resolution.

Entities:

### UserProfile
- id
- name
- profilePictureUri (Optional image for CV)
- skills
- experience
- education
- createdAt
- updatedAt

### CVGeneration
- id
- profileId
- jobDescription
- generatedCV
- timestamp / createdAt
- updatedAt

### Template
- id (Primary Key)
- templateKey (String ID mapping to Compose functions like "ats", "modern")
- primaryColorHex (Optional user customization)
- fontStyle (Optional user customization)
- createdAt
- updatedAt

---

# 🎨 UI/UX Guidelines

- Material 3 design
- Dark mode support
- Smooth transitions
- Card-based UI system
- Bottom Navigation:
  - Home
  - Profiles
  - Generate CV
  - History
  - Settings

---

# 🧩 Key Engineering Principles & SOLID Architecture (STRICT)

- **Single Responsibility Principle (SRP):** One file/function = one responsibility. ViewModels should only handle presentation logic for their specific screen.
- **Helper/Utility Classes:** Isolate specific logic into dedicated helpers (e.g., `PreferenceManager` for DataStore, `PdfHelper` for PDF generation, `AiHelper` for AI API interactions). Do NOT clutter ViewModels or UI files with core logic.
- **Dependency Injection:** Strictly use **Dagger Hilt** for all dependency injection.
- **Clean Architecture:** Keep Domain (Use Cases, Models), Data (Repositories, Room, Network), and Presentation (Compose UI, ViewModels) strictly separated.
- **State Persistence (CRITICAL):** All UI state (especially form inputs, text fields, and user selections) MUST survive configuration changes (screen rotations, theme toggles). Always hoist state into a `ViewModel` using `StateFlow`. Never use simple `remember { mutableStateOf() }` inside a Composable for important user data, as it will be lost on recreation.

## 📁 Folder Structure System (Feature-Based)
For the UI layer, we use **Feature-Based Packaging** rather than grouping by file type.
**Why?** Grouping all screens in a single `screens/` folder becomes unmanageable as the app grows. Instead, each feature gets its own folder:
```text
presentation/
  ui/
    home/          -> HomeScreen.kt, HomeViewModel.kt, HomeState.kt
    profiles/      -> ProfilesScreen.kt, ProfileViewModel.kt, ProfileComponents.kt
    generate/      -> GenerateScreen.kt, GenerateViewModel.kt
```
This ensures everything related to a specific screen/feature is isolated and easy to scale.

---

# 📌 Important Constraints

- Do NOT overuse AI APIs
- Always cache results locally
- Ensure app works offline for core features
- Ensure smooth performance even on low-end devices
- Avoid redundant recompositions in Compose

---

# 🚀 Final Goal

This app should feel like:
- A startup-level SaaS product
- A production-ready Android application
- A portfolio project suitable for internships and junior Android roles

---

# 🎨 App Design & UI Identity (MUST STRICTLY FOLLOW)

## Core UI Philosophy
“Structured minimal productivity UI”
The app should feel like **Notion, Google Drive, LinkedIn, Apple Settings**.
- **Professional, calm, structured, trustworthy** (NOT playful or flashy).
- **NO** neon gradients, heavy blur/glow effects, colorful random cards.
- **Low saturation colors, strong spacing (16-24dp), clear hierarchy.**

## Color System Rule (STRICT)
**NEVER use hardcoded colors (e.g., `Color(0xFF...)` or `Color.Red`) in UI files.**
1. **Always** use `MaterialTheme.colorScheme` (e.g., `MaterialTheme.colorScheme.primary`).
2. If a specific color is needed and not in the scheme, define it in `Color.kt` first, then map it in `Theme.kt`.
3. The app is built with a **Professional Blue** palette. Dynamic Colors are disabled by default to enforce the brand.

## Dark Mode First
Dark mode is treated as a "developer workspace".
- Light Mode Background: `#F8FAFC`
- Dark Mode Background: `#0B1220` (Not pure black)

## UI Component Rules
- **Cards:** Radius 12–16dp, subtle shadows, optional light gray border. Stacked vertically with generous spacing. Max 1 primary action per card.
- **Buttons:** Primary (Solid Blue, rounded 12-14dp), Secondary (Transparent with blue border), Destructive (Red `#EF4444` ONLY for deletions). Include scale animation on press (0.98).
- **Typography:** Inter/Roboto/SF Pro style. Title (22-28sp bold), Section (18-20sp), Body (14-16sp), Caption (12sp). *No fancy fonts.*
- **Interactions:** Always include small polish like shimmer loading, fade-in previews, smooth card transitions.

## Performance UI Rules
- App must be optimized. NO UI lag.
- Use `remember` and `derivedStateOf` to prevent redundant recompositions.derivedStateOf` to prevent redundant recompositions. Dark Mode Background: `#0B1220` (Not pure black)

## UI Component Rules
- **Cards:** Radius 12–16dp, subtle shadows, optional light gray border. Stacked vertically with generous spacing. Max 1 primary action per card.
- **Buttons:** Primary (Solid Blue, rounded 12-14dp), Secondary (Transparent with blue border), Destructive (Red `#EF4444` ONLY for deletions). Include scale animation on press (0.98).
- **Typography:** Inter/Roboto/SF Pro style. Title (22-28sp bold), Section (18-20sp), Body (14-16sp), Caption (12sp). *No fancy fonts.*
- **Interactions:** Always include small polish like shimmer loading, fade-in previews, smooth card transitions.

## Performance UI Rules
- App must be optimized. NO UI lag.
- Use `remember` and `derivedStateOf` to prevent redundant recompositions.derivedStateOf` to prevent redundant recompositions.

---

# 🧱 STRICT CLEAN ARCHITECTURE BOUNDARY RULES (MANDATORY)

## RULE 1
Always enforce strict Clean Architecture boundaries:
`UI (View) → ViewModel → UseCase → Repository → DataSource (DataSource / DAO)`
No direct connection bypassing intermediate layers is ever allowed.

## RULE 2
Never use DAO, database entity, or raw JSON structures directly inside the ViewModels, UseCases, or Compose UI screens. All layers above the Repository must deal solely with pure Domain Models.

## RULE 3
Never store domain models directly inside Room Entity classes. Domain models must represent pure business structures, whereas Database Entities represent strict database table schemes.

## RULE 4
Always split massive object graphs (like User Profiles containing experiences, projects, educations) into normalized database tables with profileId foreign keys and CASCADE deletes rather than dumping everything in a single God Entity using TypeConverters.

## RULE 5
The Repository must serve as a real abstraction and data orchestrator (handling entity-to-domain mapping, synchronization, transactional child inserts, and edits) rather than just being a forwarding wrapper to Room DAOs.

## RULE 6
ViewModels must remain extremely lightweight, UI-only focused, and must solely manage UI states and delegate core triggers to UseCases. ViewModels must never contain mapping logic or direct repository/database updates.

## RULE 7
Always maintain structural and package boundaries cleanly:
- `domain/model/`: Pure Kotlin/Java business logic objects.
- `domain/usecase/`: Clean business flow scripts.
- `domain/mapper/`: Mapping layers mapping entities to domains.
- `domain/repository/`: Abstract repositories.
- `data/`: DAO, Entities, database setups, and Repository implementations.