package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ── NIRMALADEVI CARE WHITE COMBINATION THEME ──

// Canvas & Surfaces (Clean White Combination)
val WhiteCanvas = Color(0xFFF8FAFC)          // Slate-50: Crisp, clean light canvas background
val WhiteSurface = Color(0xFFFFFFFF)         // Pure white cards, elevated dialogs, topbar & bottombar
val WhiteSurfaceVariant = Color(0xFFF1F5F9)  // Slate-100: Subtle light slate for chips, badges, formula boxes
val WhiteSurfaceSubtle = Color(0xFFE8EEF5)   // Light tint for inputs and secondary cards
val WhiteBorder = Color(0xFFE2E8F0)          // Slate-200: Crisp 1dp clean border for cards & dividers
val WhiteBorderStrong = Color(0xFFCBD5E1)    // Slate-300: Accentuated border for focused inputs

// Typography (Crisp, High-Contrast Readability on White)
val TextPrimary = Color(0xFF0F172A)          // Slate-900: Deep Charcoal / Navy for high-contrast headlines & text
val TextSecondary = Color(0xFF334155)        // Slate-700: Readable primary body text
val TextMuted = Color(0xFF64748B)            // Slate-500: Secondary descriptions & captions
val TextPlaceholder = Color(0xFF94A3B8)      // Slate-400: Placeholders & inactive icons

// Brand Accents (Vibrant, Authoritative Chemical Trading Colors)
val AccentBlue = Color(0xFF0284C7)           // Sky-600 / Azure: Primary corporate industrial blue
val AccentBlueDark = Color(0xFF0369A1)       // Sky-700: Deep Navy/Azure for text & prominent buttons
val AccentBlueSoft = Color(0xFFE0F2FE)       // Sky-100: Soft pastel pill & active selection background
val AccentTeal = Color(0xFF0D9488)           // Teal-600: Chemical & water treatment accent
val AccentTealDark = Color(0xFF0F766E)       // Teal-700: Deep teal for icons & high-contrast text
val AccentTealSoft = Color(0xFFCCFBF1)       // Teal-100: Soft teal pill background
val AccentGreen = Color(0xFF16A34A)          // Green-600: Success & active confirmations
val AccentGreenDark = Color(0xFF15803D)      // Green-700
val AccentGreenSoft = Color(0xFFDCFCE7)      // Green-100: Soft green indicator
val AccentAmber = Color(0xFFD97706)          // Amber-600: Warnings & critical alerts
val AccentAmberSoft = Color(0xFFFEF3C7)      // Amber-100
val AccentRed = Color(0xFFDC2626)            // Red-600: Hazard badges & danger alerts
val AccentRedSoft = Color(0xFFFEE2E2)        // Red-100

// Compatibility Mappings for Existing Components
val BrandInk = WhiteCanvas                   // Base app background -> now Clean Slate-50 White
val BrandSteel = WhiteSurface                // Card & panel surfaces -> now Pure White #FFFFFF
val BrandSteelLight = WhiteSurfaceVariant    // Inner chips & formula boxes -> now Light Slate-100
val BrandAcid = AccentBlue                   // Primary accent -> now Vibrant Sky/Azure Blue
val BrandAcidDark = AccentBlueDark           // Darker accent
val BrandAzure = AccentBlue
val BrandAzureLight = AccentBlueDark
val BrandMist = WhiteSurfaceVariant
val BrandFog = WhiteCanvas
val BrandBorder = WhiteBorder
val BrandBorderDark = WhiteBorder
val BrandMid = TextMuted
val BrandTextDark = TextPrimary

// Social & Alert Action Colors
val WhatsAppGreen = Color(0xFF25D366)
val WhatsAppGreenDark = Color(0xFF128C7E)
val FacebookBlue = Color(0xFF1877F2)
val InstagramPurple = Color(0xFFE1306C)
val DangerRed = AccentRed
val WarningOrange = AccentAmber
val SuccessGreen = AccentGreen

