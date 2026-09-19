# 🧪 Nirmaladevi Care Pvt. Ltd. — Industrial Chemical Trading App

[![Android Build & Release](https://github.com/azazmadkiya/NIRMALADEVI-CARE/actions/workflows/build-apk.yml/badge.svg)](https://github.com/azazmadkiya/NIRMALADEVI-CARE/actions/workflows/build-apk.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![Platform](https://img.shields.io/badge/Platform-Android%2014%2B%20%7C%20API%2036-green.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)](LICENSE)

An industrial-grade Android application built for **Nirmaladevi Care Pvt. Ltd.** (Morbi, Gujarat, India) — a trusted chemical trading enterprise providing high-purity industrial acids, alkalis, specialty compounds, bleaching agents, and water treatment solutions.

---

## 📱 App Highlights & Key Features

### 1. 🧪 Comprehensive Chemical Catalogue (24+ Products)
Browse and filter chemical supplies across 4 primary industry categories:
- **Acids:** Sulphuric Acid ($H_2SO_4$ 98% CP & Commercial), Hydrochloric Acid ($HCl$ 33%), Nitric Acid ($HNO_3$ 68%), Hydrofluoric Acid ($HF$ 40-60%), Acetic Acid (Glacial), Battery Acid, Citric Acid, Phosphoric Acid (Tech & Food Grade), Sulfamic Descalent.
- **Alkalis:** Caustic Soda Flakes ($NaOH$ 99.5%), Caustic Soda Lye (48-50%), Caustic Potash ($KOH$), Liquor Ammonia (25%), Soda Ash Light ($Na_2CO_3$).
- **Salts & Bleach:** Sodium Hypochlorite (Hypo), Sodium Silicate (Water Glass), Bleaching Powder, Borax Powder, Boric Powder.
- **Water Treatment:** Poly Aluminium Chloride (PAC 30% Powder/Liquid), D.M. / Distilled Water.
- **Search & Formula Filters:** Search by chemical name, common name, formula ($H_2SO_4$, $NaOH$, $HCl$), or industrial application.

### 2. 📋 Interactive B2B Quotation & Order Builder
- Customize packaging type: **Carboys (35kg/50kg)**, **HDPE Bags (25kg/50kg)**, **Drums (200L/250kg)**, **IBC Tanks (1000L)**, or **Bulk Road Tankers**.
- Select chemical purity grade (Commercial, Technical, CP Laboratory, Food Grade).
- **1-Tap WhatsApp Dispatch (+91 82003 32632):** Generates a formatted commercial inquiry with itemized specs and sends directly via WhatsApp.
- **1-Tap Email Inquiry (`nirmaladevicarepvtltd@gmail.com`):** Direct quotation email generator.
- **Helpline Dialing:** Instant phone call trigger to the sales desk.

### 3. ⚗️ Factory & Chemist Utility Tools
- **Solution Dilution Calculator ($C_1 V_1 = C_2 V_2$):** Computes required concentrated stock chemical volume and D.M. water volume needed to prepare target concentrations with safety notices.
- **Mass-to-Volume Tanker Converter ($Mass = Volume \times Specific\ Gravity$):** Converts liters/gallons to Kilograms (Kg) and Metric Tons (MT) based on specific gravity.
- **Safety & Storage Compatibility Matrix:** Industrial guidance on acid vs. alkali segregation, ventilation, and mandatory Personal Protective Equipment (PPE).

### 4. 🏢 Company Profile & Morbi Warehouse Location
- Overview of credentials, company registration, and Morbi ceramic cluster distribution.
- **1-Tap Google Maps Navigation:** Direct route navigation to Panchasar Road, Morbi warehouse.
- Social media links (Facebook, Instagram).

### 5. ⚖️ Google Play Store Compliance & Privacy Policy
- Integrated **Legal & Privacy Policy** viewer complying with India's DPDPA and Google Play Developer Policies.
- Official [Privacy Policy (Markdown)](PRIVACY_POLICY.md) and [Web Documentation](privacy-policy.html).
- Chemical handling disclaimers and safety guidance.
- Ready-to-host HTML documents (`privacy-policy.html`, `app/src/main/assets/privacy_policy.html`, `terms-and-conditions.html`, `data-deletion.html`).

---

## 🛠️ Architecture & Tech Stack

- **Language:** 100% Kotlin
- **UI Framework:** Jetpack Compose with Material Design 3 (Dark Charcoal & Acid Lime Industrial Theme)
- **Architecture:** MVVM (Model-View-ViewModel) with `StateFlow` and `collectAsStateWithLifecycle`
- **Build System:** Gradle Kotlin DSL (`build.gradle.kts`) with Gradle Version Catalog (`libs.versions.toml`)
- **CI/CD Automation:** GitHub Actions (`.github/workflows/build-apk.yml`) for automated debug APK compilation and GitHub Release publishing
- **Testing:** Robolectric JVM tests & Roborazzi screenshot regression testing

---

## 🚀 Building & Running Locally

### Prerequisites
- Android Studio Ladybug (2024.2+) or newer
- JDK 17 (Temurin recommended)
- Android SDK 36 (Android 15/16)

### Run via Command Line
```bash
# Clone the repository
git clone https://github.com/azazmadkiya/NIRMALADEVI-CARE.git
cd NIRMALADEVI-CARE

# Build the debug APK
gradle :app:assembleDebug

# Run unit tests
gradle :app:testDebugUnitTest
```
The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📦 Automated GitHub Actions Release

This repository includes an automated CI/CD pipeline (`.github/workflows/build-apk.yml`):
- Automatically builds the debug APK on every push to `main` or via manual `workflow_dispatch`.
- Publishes downloadable APK builds under [GitHub Releases](https://github.com/azazmadkiya/NIRMALADEVI-CARE/releases).

---

## 📍 Contact & Support

**NIRMALADEVI CARE PVT. LTD.**  
Panchasar Road, Morbi, Gujarat – 363641, India  
- 📞 **Helpline:** [+91 82003 32632](tel:+918200332632)  
- 💬 **WhatsApp:** [+91 82003 32632](https://wa.me/918200332632)  
- ✉️ **Email:** [nirmaladevicarepvtltd@gmail.com](mailto:nirmaladevicarepvtltd@gmail.com)  

---

*© 2026 Nirmaladevi Care Pvt. Ltd. • Developed By Azaz Madkiya (Azazmadkiya). All rights reserved.*
