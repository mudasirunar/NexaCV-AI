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

Entities:

### UserProfile
- id
- name
- profilePictureUri (Optional image for CV)
- skills
- experience
- education

### CVGeneration
- id
- profileId
- jobDescription
- generatedCV
- timestamp

### Template
- id
- name
- type
- layoutConfig

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

# 🧩 Key Engineering Principles

- Single Responsibility Principle
- Separation of Concerns
- No business logic in UI
- Reusable components
- Stateless UI where possible

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
- Use `remember` and `derivedStateOf` to prevent redundant recompositions.derivedStateOf` to prevent redundant recompositions.