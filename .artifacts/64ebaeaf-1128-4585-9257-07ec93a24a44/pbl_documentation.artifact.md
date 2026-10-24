# Saathi: AI-Powered Early Student Wellbeing & Support System

This document provides a professional abstract and problem statement for your PBL (Project Based Learning) presentation.

## 1. Project Title
**Saathi: An AI-Assisted Early Mental Wellbeing and Support System for Students**

## 2. Abstract
Mental health challenges among students (both school and college) are rising due to academic pressure, exam anxiety, and social isolation. Most students do not seek help until they reach a crisis point. **Saathi** is an innovative Android application designed to bridge this gap through early identification and supportive companionship. Using the **Groq AI (Llama 3)** engine, Saathi provides a safe, non-judgmental "Hinglish" companion for students to vent and seek guidance. The system features a gamified **Wellness Plant** to encourage consistency, a **Biometric-locked Private Journal**, and a **CBT-based Thought Reframer**. Critically, the app monitors mood patterns and digital behavior (e.g., late-night usage) to proactively suggest connecting with a "Trusted Person" or professional counselor (Tele-MANAS) if high distress is detected, ensuring timely human intervention.

## 3. Problem Statement
The current mental health support infrastructure for Indian students faces several challenges:
1. **Stigma & Silence:** Students fear being judged for their emotions or the apps they use.
2. **Late Detection:** Warning signs of depression or critical stress often go unnoticed by parents and teachers until it's too late.
3. **Accessibility:** Lack of immediate, low-cost support during late-night study sessions or high-pressure exam periods.
4. **Relational Gaps:** Parents often don't know how to initiate supportive conversations without causing panic.

## 4. Objectives
- To provide an immediate, AI-powered emotional support system using natural language processing (NLP).
- To track daily mood, stress, and behavioral patterns (Screen-time/Sleep) to identify long-term wellbeing trends.
- To bridge the communication gap between students and parents through "No-Panic" alerts and reverse feedback.
- To ensure absolute privacy of sensitive student thoughts through biometric security and app-identity disguise.
- To provide a proactive alert system that connects students in distress with institutional counselors or national helplines.

## 5. Innovation & Uniqueness
- **Hinglish AI Companion:** Optimized for Indian students' natural speaking style.
- **Stealth Mode:** Option to disguise the app identity to protect against social stigma.
- **Institutional Integration:** Direct link to school/college counselors for on-campus support.
- **Reverse Feedback System:** Assigns gentle supportive tasks to parents based on the child's mood.
- **Behavioral Analysis:** Detects insomnia and screen-time spikes as early warning signs.

## 6. Technical Stack
- **Frontend:** Jetpack Compose (Kotlin), Material 3, Canvas API.
- **AI Engine:** Groq Cloud API (Llama 3.1 8B Model).
- **Backend:** Firebase Authentication & Firestore.
- **Local DB:** Room Database for private offline-first data.
- **Architecture:** MVVM with Hilt Dependency Injection.
- **Security:** Android Biometric Library & Activity Aliasing.
