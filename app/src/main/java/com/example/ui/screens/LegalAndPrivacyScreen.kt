package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompanyInfo
import com.example.ui.theme.*

enum class LegalTab(val title: String) {
    PRIVACY("Privacy Policy"),
    TERMS("Terms of Service"),
    DISCLAIMER("Chemical Disclaimer"),
    COMPLIANCE("Play Store Compliance")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalAndPrivacyScreen(
    onBackClick: () -> Unit,
    onContactSupportClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(LegalTab.PRIVACY) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Legal & Privacy Policy",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("legal_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onContactSupportClick,
                        modifier = Modifier.testTag("legal_support_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Support Email",
                            tint = AccentBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhiteSurface,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = WhiteCanvas
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Selector
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = WhiteSurface,
                contentColor = AccentBlue,
                edgePadding = 12.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        color = AccentBlue,
                        height = 3.dp
                    )
                }
            ) {
                LegalTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = tab.title,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == tab) AccentBlueDark else TextSecondary,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.testTag("legal_tab_${tab.name.lowercase()}")
                    )
                }
            }

            // Tab Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                when (selectedTab) {
                    LegalTab.PRIVACY -> {
                        item { PrivacyPolicyContent() }
                    }
                    LegalTab.TERMS -> {
                        item { TermsOfServiceContent() }
                    }
                    LegalTab.DISCLAIMER -> {
                        item { ChemicalDisclaimerContent() }
                    }
                    LegalTab.COMPLIANCE -> {
                        item { PlayStoreComplianceContent(onContactSupportClick) }
                    }
                }
            }
        }
    }
}

@Composable
fun PrivacyPolicyContent() {
    Column {
        PolicyHeader(
            title = "Privacy Policy for Nirmaladevi Care",
            lastUpdated = "Effective Date: September 19, 2026 | Version 2.1 (Build 3)"
        )

        Spacer(modifier = Modifier.height(14.dp))

        PolicySection(
            title = "1. Introduction",
            body = "Nirmaladevi Care Pvt. Ltd. (\"we\", \"our\", or \"us\") operates the Nirmaladevi Care mobile application. We are committed to protecting your personal data and privacy. This Privacy Policy explains how we collect, use, disclose, and safeguard your information when you use our mobile application."
        )

        PolicySection(
            title = "2. Information We Collect",
            body = "• Customer Inquiry Information: When you create a quotation inquiry, you may optionally provide your company name, contact phone number, factory location, and chemical requirement notes.\n• Communications: When you contact us via WhatsApp, phone, or email, we receive your message contents and contact details.\n• Device & App Usage: The app operates primarily offline with on-device calculation tools. We do NOT track your GPS location in the background or collect private personal files."
        )

        PolicySection(
            title = "3. How We Use Your Information",
            body = "We use the information you provide solely to:\n• Generate commercial chemical quotations and invoices.\n• Communicate regarding product availability, purity certificates (COA), and dispatch schedules.\n• Provide customer service and technical guidance for chemical applications."
        )

        PolicySection(
            title = "4. Data Sharing & Disclosure",
            body = "We do not sell, rent, or trade your personal or business inquiry data to third parties. Data is only shared directly between you and Nirmaladevi Care Pvt. Ltd. through your chosen communication method (e.g. WhatsApp, Email, Phone)."
        )

        PolicySection(
            title = "5. Data Security",
            body = "We implement reasonable administrative and technical security measures to protect your inquiries. Quotation drafts and calculator inputs remain locally on your mobile device unless you explicitly choose to dispatch them."
        )

        PolicySection(
            title = "6. Third-Party Services & Links",
            body = "Our app contains external links to Google Maps, WhatsApp, Facebook, and Instagram. Please note that these third-party platforms have their own privacy policies."
        )

        PolicySection(
            title = "7. Contact Data Protection Officer",
            body = "If you have questions about this Privacy Policy or wish to request deletion of any inquiry data, contact us at:\n\nNIRMALADEVI CARE PVT. LTD.\nPanchasar Road, Morbi, Gujarat – 363641, India\nEmail: ${CompanyInfo.EMAIL}\nHelpline: ${CompanyInfo.PHONE}\nContact 1: ${CompanyInfo.PHONE_1}\nContact 2: ${CompanyInfo.PHONE_2}"
        )
    }
}

@Composable
fun TermsOfServiceContent() {
    Column {
        PolicyHeader(
            title = "Terms of Service & Commercial Usage",
            lastUpdated = "Effective Date: August 23, 2025"
        )

        Spacer(modifier = Modifier.height(14.dp))

        PolicySection(
            title = "1. Acceptance of Terms",
            body = "By downloading, browsing, or utilizing the Nirmaladevi Care mobile application, you agree to comply with and be bound by these Terms of Service. If you do not agree, please discontinue using the application."
        )

        PolicySection(
            title = "2. Nature of Application",
            body = "This application serves as an informational catalogue, B2B quotation builder, and dilution calculation utility for Nirmaladevi Care Pvt. Ltd., an authorized chemical trading enterprise registered in Gujarat, India."
        )

        PolicySection(
            title = "3. Quotations & Pricing",
            body = "• All chemical quotations generated through this application are indicative inquiries and subject to final confirmation by Nirmaladevi Care based on prevailing raw material market rates, applicable GST (18%/12%), and freight charges to your designated factory.\n• Deliveries are fulfilled via authorized industrial road tankers, IBC tanks, or HDPE carboys."
        )

        PolicySection(
            title = "4. Intellectual Property",
            body = "The trade names, brand logos, product descriptions, calculation formulas, and UI assets are the property of Nirmaladevi Care Pvt. Ltd. Unauthorized replication or distribution is prohibited."
        )
    }
}

@Composable
fun ChemicalDisclaimerContent() {
    Column {
        PolicyHeader(
            title = "Industrial Chemical Handling Disclaimer",
            lastUpdated = "Mandatory Safety Information"
        )

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = AccentRedSoft,
            border = androidx.compose.foundation.BorderStroke(1.dp, AccentRed.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = AccentRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SAFETY FIRST & REGULATORY NOTICE",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = AccentRed
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Chemicals listed in this app (including Sulphuric Acid, Hydrochloric Acid, Hydrofluoric Acid, Nitric Acid, and Caustic Soda) are hazardous industrial substances. They must only be handled by trained factory personnel with appropriate Personal Protective Equipment (PPE) in accordance with government safety norms and Material Safety Data Sheets (MSDS).",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        PolicySection(
            title = "1. Calculator Results Disclaimer",
            body = "The Dilution and Mass-Volume Calculators provided in this application are mathematical tools provided in good faith for estimation purposes. Users must verify exact laboratory titration values and specific gravity before large-scale commercial mixing."
        )

        PolicySection(
            title = "2. Storage & Compatibility",
            body = "Always consult individual MSDS for storage temperature, compatibility, and ventilation requirements. Never store strong oxidizers or acids adjacent to alkalis or organic solvents."
        )
    }
}

@Composable
fun PlayStoreComplianceContent(onContactSupportClick: () -> Unit) {
    Column {
        PolicyHeader(
            title = "Google Play Store Compliance & App Info",
            lastUpdated = "Version 2.1 (Build 3) • Production Release"
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "APPLICATION METADATA",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    ),
                    color = AccentBlueDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                ComplianceRow("App Name", "Nirmaladevi Care")
                ComplianceRow("Package ID", "com.nirmaladevicare")
                ComplianceRow("Version", "2.1 (VersionCode: 3)")
                ComplianceRow("Developer", "Azaz Madkiya (Azazmadkiya)")
                ComplianceRow("Corporate Entity", CompanyInfo.LEGAL_NAME)
                ComplianceRow("GSTIN", CompanyInfo.GST_NUMBER)
                ComplianceRow("MSME (Udyam)", CompanyInfo.MSME_NUMBER)
                ComplianceRow("Principal Banker", "${CompanyInfo.BANK_NAME} (${CompanyInfo.BANK_BRANCH})")
                ComplianceRow("Category", "Business / Industrial & Chemical Trading")
                ComplianceRow("Primary Helpline", CompanyInfo.PHONE)
                ComplianceRow("Contact Number 1", CompanyInfo.PHONE_1)
                ComplianceRow("Contact Number 2", CompanyInfo.PHONE_2)
                ComplianceRow("Target Android Version", "Android 14 / 15 / 16 (API 36)")
                ComplianceRow("Data Safety", "No tracking, No Ads, User-initiated Inquiry Only")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onContactSupportClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("compliance_support_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentBlue,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SupportAgent,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Contact Developer & Support Desk", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PolicyHeader(title: String, lastUpdated: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = WhiteSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = lastUpdated,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                ),
                color = AccentBlueDark
            )
        }
    }
}

@Composable
fun PolicySection(title: String, body: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = AccentBlueDark
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                lineHeight = 19.sp
            ),
            color = TextSecondary
        )
    }
}

@Composable
fun ComplianceRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
    }
}
