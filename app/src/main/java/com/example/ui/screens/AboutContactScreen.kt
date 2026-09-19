package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.example.R
import com.example.data.model.CompanyInfo
import com.example.data.model.SalesDepot
import com.example.ui.theme.*
import com.example.util.DownloadResult
import com.example.util.ProductCataloguePdfManager

@Composable
fun AboutContactScreen(
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onShareWhatsAppMessage: (String) -> Unit = {},
    onEmailClick: () -> Unit,
    onOpenMapClick: () -> Unit,
    onOpenDepotMap: (url: String, fallbackAddress: String) -> Unit = { _, _ -> },
    onFacebookClick: () -> Unit,
    onInstagramClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onOpenCataloguePdf: () -> Unit = {},
    onShareAppClick: () -> Unit = {},
    onCallNumber: (String) -> Unit = { onCallClick() },
    onWhatsAppNumber: (number: String, message: String) -> Unit = { _, _ -> onWhatsAppClick() }
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteCanvas),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp)
    ) {
        // Company Header Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Official Brand Logo Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.25f)),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .testTag("about_brand_logo_card")
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ncpl_logo),
                            contentDescription = "NIRMALA Brand Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            shadowElevation = 1.dp
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ncpl_logo_foreground),
                                contentDescription = "NIRMALA Logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(3.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Column {
                            Text(
                                text = "NIRMALADEVI CARE",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp
                                ),
                                color = TextPrimary
                            )
                            Text(
                                text = "PVT. LTD. · CHEMICAL TRADING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp,
                                    fontSize = 10.sp
                                ),
                                color = AccentBlueDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "A leading chemical trading house based in Morbi, Gujarat — the ceramic capital of India. We specialize in supply and wholesale distribution of high-purity industrial acids, alkalis, specialty chemicals, bleaching compounds, and water treatment products.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 18.sp),
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Company Highlights
                    val highlights = listOf(
                        "Registered Private Limited Company",
                        "Wholesale & Retail Distribution",
                        "Acids, Alkalis & Specialty Chemicals",
                        "Industrial, Technical & Food Grades",
                        "Serving manufacturers across Gujarat",
                        "Responsive, trusted customer support"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        highlights.forEach { h ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(AccentBlue)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = h,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Contact Information Card
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "GET IN TOUCH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            fontSize = 11.sp
                        ),
                        color = AccentBlueDark
                    )
                    Text(
                        text = "Contact Information",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ContactRow(
                        icon = Icons.Default.Business,
                        label = "Company Name",
                        value = "NIRMALADEVI CARE PVT. LTD.",
                        onClick = null
                    )
                    HorizontalDivider(color = WhiteBorder, modifier = Modifier.padding(vertical = 10.dp))

                    ContactRow(
                        icon = Icons.Default.LocationOn,
                        label = "Head Office (Panchasar Road)",
                        value = CompanyInfo.ADDRESS,
                        onClick = onOpenMapClick
                    )
                    HorizontalDivider(color = WhiteBorder, modifier = Modifier.padding(vertical = 10.dp))

                    ContactRow(
                        icon = Icons.Default.Store,
                        label = "Sales Depot 1 (Rajpar Road)",
                        value = "Survey No.21, Plot No 4P, Rajpar Rd, Morbi – 363641",
                        onClick = { onOpenDepotMap(CompanyInfo.DEPOT_1.mapUrl, CompanyInfo.DEPOT_1.address) }
                    )
                    HorizontalDivider(color = WhiteBorder, modifier = Modifier.padding(vertical = 10.dp))

                    ContactRow(
                        icon = Icons.Default.LocalShipping,
                        label = "Sales Depot 2 (Maliya / Chanchavadarda)",
                        value = "Survey No.283, Ta-Maliya(mi) Near Kodal Uniquoters – 363660",
                        onClick = { onOpenDepotMap(CompanyInfo.DEPOT_2.mapUrl, CompanyInfo.DEPOT_2.address) }
                    )
                    HorizontalDivider(color = WhiteBorder, modifier = Modifier.padding(vertical = 12.dp))

                    // ── 3 Official Contact & Helpline Numbers ──
                    Text(
                        text = "DIRECT CONTACT & HELPLINE NUMBERS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp
                        ),
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Primary Direct Helpline
                    PhoneContactItem(
                        title = "Direct Helpline / Sales Desk",
                        phoneNumber = CompanyInfo.PHONE,
                        subtitle = "Morbi Central Desk · Ready Chemical Stock & Orders",
                        badge = "Primary Helpline",
                        isPrimary = true,
                        onCall = { onCallNumber(CompanyInfo.PHONE) },
                        onWhatsApp = {
                            onWhatsAppNumber(
                                CompanyInfo.PHONE,
                                "Hello Nirmaladevi Care, I am contacting you regarding chemical product inquiries."
                            )
                        },
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(CompanyInfo.PHONE))
                            Toast.makeText(context, "Helpline number copied: ${CompanyInfo.PHONE}", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Contact Number 1: +91 98257 31735
                    PhoneContactItem(
                        title = "Contact Number 1",
                        phoneNumber = CompanyInfo.PHONE_1,
                        subtitle = "Commercial Chemical Inquiries & Order Booking",
                        badge = "Sales Desk",
                        isPrimary = false,
                        onCall = { onCallNumber(CompanyInfo.PHONE_1) },
                        onWhatsApp = {
                            onWhatsAppNumber(
                                CompanyInfo.PHONE_1,
                                "Hello Nirmaladevi Care, I would like to inquire about industrial chemical supplies and rates."
                            )
                        },
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(CompanyInfo.PHONE_1))
                            Toast.makeText(context, "Contact 1 copied: ${CompanyInfo.PHONE_1}", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Contact Number 2: +91 98986 15543
                    PhoneContactItem(
                        title = "Contact Number 2",
                        phoneNumber = CompanyInfo.PHONE_2,
                        subtitle = "Customer Relations & Dispatch Logistics",
                        badge = "Support & Dispatch",
                        isPrimary = false,
                        onCall = { onCallNumber(CompanyInfo.PHONE_2) },
                        onWhatsApp = {
                            onWhatsAppNumber(
                                CompanyInfo.PHONE_2,
                                "Hello Nirmaladevi Care, I have an inquiry regarding chemical supply delivery status."
                            )
                        },
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(CompanyInfo.PHONE_2))
                            Toast.makeText(context, "Contact 2 copied: ${CompanyInfo.PHONE_2}", Toast.LENGTH_SHORT).show()
                        }
                    )

                    HorizontalDivider(color = WhiteBorder, modifier = Modifier.padding(vertical = 12.dp))

                    ContactRow(
                        icon = Icons.Default.Email,
                        label = "Email Address",
                        value = "nirmaladevicarepvtltd@gmail.com",
                        onClick = onEmailClick
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Social buttons
                    Text(
                        text = "CONNECT ON SOCIAL & CHAT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp
                        ),
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = onWhatsAppClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("contact_whatsapp_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WhatsAppGreenDark,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                        }

                        Button(
                            onClick = onFacebookClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("contact_facebook_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FacebookBlue,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Facebook", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                        }

                        Button(
                            onClick = onInstagramClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("contact_instagram_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = InstagramPurple,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Instagram", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                        }
                    }
                }
            }
        }

        // ── 1. Official Bank Details Card (HDFC Bank) ──
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("official_bank_details_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentBlueSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = "Bank",
                                    tint = AccentBlueDark,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "OFFICIAL BANK DETAILS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp,
                                        fontSize = 11.sp
                                    ),
                                    color = AccentBlueDark
                                )
                                Text(
                                    text = "For RTGS / NEFT / IMPS / Cheque",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = TextSecondary
                                )
                            }
                        }

                        Surface(
                            color = AccentBlueSoft,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = CompanyInfo.BANK_NAME,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                ),
                                color = AccentBlueDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = WhiteSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Beneficiary
                            BankDetailField(
                                label = "Beneficiary / Account Name",
                                value = CompanyInfo.BANK_BENEFICIARY,
                                isBold = true,
                                onCopy = {
                                    clipboardManager.setText(AnnotatedString(CompanyInfo.BANK_BENEFICIARY))
                                    Toast.makeText(context, "Account Name copied to clipboard!", Toast.LENGTH_SHORT).show()
                                }
                            )

                            HorizontalDivider(color = WhiteBorder)

                            // Bank & Branch
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1.1f)) {
                                    Text(
                                        text = "BANK NAME",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp
                                        ),
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = CompanyInfo.BANK_NAME,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "BRANCH",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp
                                        ),
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = CompanyInfo.BANK_BRANCH,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                }
                            }

                            HorizontalDivider(color = WhiteBorder)

                            // Bank A/c No
                            BankDetailField(
                                label = "Bank Account Number",
                                value = CompanyInfo.BANK_ACCOUNT_NUMBER,
                                isMonospace = true,
                                isBold = true,
                                isPrimaryColor = true,
                                onCopy = {
                                    clipboardManager.setText(AnnotatedString(CompanyInfo.BANK_ACCOUNT_NUMBER))
                                    Toast.makeText(context, "Account number copied to clipboard!", Toast.LENGTH_SHORT).show()
                                }
                            )

                            HorizontalDivider(color = WhiteBorder)

                            // RTGS / IFSC Code
                            BankDetailField(
                                label = "RTGS / IFSC Code",
                                value = CompanyInfo.BANK_IFSC_CODE,
                                isMonospace = true,
                                isBold = true,
                                isPrimaryColor = true,
                                onCopy = {
                                    clipboardManager.setText(AnnotatedString(CompanyInfo.BANK_IFSC_CODE))
                                    Toast.makeText(context, "IFSC Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                                }
                            )

                            HorizontalDivider(color = WhiteBorder)

                            // Account Type
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ACCOUNT TYPE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp
                                    ),
                                    color = TextMuted
                                )
                                Text(
                                    text = CompanyInfo.BANK_ACCOUNT_TYPE,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(CompanyInfo.getFormattedBankDetailsMessage()))
                                Toast.makeText(context, "Complete bank details copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("copy_bank_details_button"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlueDark),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Bank Info", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                onShareWhatsAppMessage(CompanyInfo.getFormattedBankDetailsMessage())
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("share_bank_whatsapp_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WhatsAppGreenDark,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Bank Info", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ── 2. Tax & Regulatory Registration Card (GST & MSME) ──
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tax_registration_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentTealSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = "Verified",
                                tint = AccentTealDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "TAX & BUSINESS REGISTRATIONS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    fontSize = 11.sp
                                ),
                                color = AccentTealDark
                            )
                            Text(
                                text = "Government Verified Business Credentials",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // GSTIN Block
                    RegistrationItemCard(
                        title = "GSTIN (Goods & Services Tax)",
                        registrationId = CompanyInfo.GST_NUMBER,
                        badge = "Gujarat · State Code 24",
                        description = "Registered Taxpayer for inter-state & intra-state chemical supplies",
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(CompanyInfo.GST_NUMBER))
                            Toast.makeText(context, "GST number copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // MSME Block
                    RegistrationItemCard(
                        title = "MSME (Udyam Registration)",
                        registrationId = CompanyInfo.MSME_NUMBER,
                        badge = "Govt. of India Verified",
                        description = "Ministry of Micro, Small and Medium Enterprises Registration",
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(CompanyInfo.MSME_NUMBER))
                            Toast.makeText(context, "MSME number copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // ── 3. Registered Head Office & Facilities (Office & Depots) ──
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "REGISTERED OFFICE & FACILITIES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        fontSize = 11.sp
                    ),
                    color = AccentBlueDark
                )
                Text(
                    text = "Corporate Office & Storage Depots",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Our corporate headquarters and administrative desk in Morbi, followed by our 2 strategically located chemical godowns and dispatch hubs.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
                    color = TextSecondary
                )
            }
        }

        // ── 1. REGISTERED / HEAD OFFICE (Commercial & Administrative Desk) ── FIRST / AAGE! ──
        item {
            Spacer(modifier = Modifier.height(10.dp))
            HeadOfficeCard(
                onOpenMap = onOpenMapClick,
                onCallClick = onCallClick,
                onShare = { onShareWhatsAppMessage(CompanyInfo.getFormattedHeadOfficeMessage()) },
                onCopy = {
                    clipboardManager.setText(AnnotatedString(CompanyInfo.ADDRESS))
                    Toast.makeText(context, "Head Office address copied!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // ── 2. Sales Depots & Storage Godowns (Warehouses & Dispatch Hubs) ── BAD ME DUSRE! ──
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "WAREHOUSES & DISPATCH HUBS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        fontSize = 11.sp
                    ),
                    color = AccentBlueDark
                )
                Text(
                    text = "Our 2 Sales Depots & Godowns",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Strategically located chemical storage godowns in Morbi & Maliya for prompt stock loading, bulk tanker handling, and fast customer delivery.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Action Bar: Share & Copy All Depots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onShareWhatsAppMessage(CompanyInfo.getAllDepotsFormattedMessage())
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("share_all_depots_whatsapp"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WhatsAppGreenDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WhatsAppGreenDark.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = WhatsAppGreenDark
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share Both Depots", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(CompanyInfo.getAllDepotsFormattedMessage()))
                            Toast.makeText(context, "All depot addresses copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("copy_all_depots_button"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlueDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy All Addresses", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Sales Depot 1 Card (Rajpar Road, Morbi)
        item {
            Spacer(modifier = Modifier.height(10.dp))
            DepotCard(
                depot = CompanyInfo.DEPOT_1,
                onOpenMap = { url, fallback -> onOpenDepotMap(url, fallback) },
                onShare = { msg -> onShareWhatsAppMessage(msg) },
                onCopy = { addr ->
                    clipboardManager.setText(AnnotatedString(addr))
                    Toast.makeText(context, "Depot 1 address copied!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Sales Depot 2 Card (Chanchavadarda, Maliya)
        item {
            Spacer(modifier = Modifier.height(12.dp))
            DepotCard(
                depot = CompanyInfo.DEPOT_2,
                onOpenMap = { url, fallback -> onOpenDepotMap(url, fallback) },
                onShare = { msg -> onShareWhatsAppMessage(msg) },
                onCopy = { addr ->
                    clipboardManager.setText(AnnotatedString(addr))
                    Toast.makeText(context, "Depot 2 address copied!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // ── 4. Official App Share Section ──
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AccentBlueDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("share_app_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SHARE OUR OFFICIAL APP",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    fontSize = 10.sp
                                ),
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "Nirmaladevi Care Digital Desk",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Help your business associates find the right industrial chemicals. Share our official app for real-time catalogues and direct sales support.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onShareAppClick,
                            modifier = Modifier
                                .weight(1.2f)
                                .height(42.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = AccentBlueDark
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share App", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(CompanyInfo.PLAY_STORE_LINK))
                                Toast.makeText(context, "Play Store link copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Link", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Why Choose Us Section
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "WHY NIRMALADEVI CARE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    fontSize = 11.sp
                ),
                color = AccentBlueDark
            )
            Text(
                text = "The Difference We Deliver",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            val whyCards = listOf(
                Pair("01", Pair("Verified Purity", "Every batch sourced from certified manufacturers — CP grade, technical grade, or food grade as required.")),
                Pair("02", Pair("Competitive Pricing", "Direct supplier relationships allow us to offer market-competitive rates on wholesale and retail quantities.")),
                Pair("03", Pair("Safe Handling", "Proper labelling, packaging, and documentation for all chemicals — ensuring safe transport and storage.")),
                Pair("04", Pair("Wide Range", "24+ chemicals under one roof — acids, alkalis, bleaching agents, descalants, and industrial salts."))
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                whyCards.forEach { (num, content) ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = num,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = AccentBlue.copy(alpha = 0.5f),
                                modifier = Modifier.width(42.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = content.first,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = content.second,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Legal & Privacy Policy Section (Google Play Store compliance)
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPrivacyPolicyClick() }
                    .testTag("open_privacy_policy_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentBlueSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PrivacyTip,
                            contentDescription = null,
                            tint = AccentBlueDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Legal & Privacy Policy",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Privacy Policy, Terms of Service, Chemical Disclaimers & Google Play Compliance",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                            color = TextMuted
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = null,
                        tint = AccentBlueDark,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // Footer
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "© 2025 Nirmaladevi Care Pvt. Ltd. All rights reserved.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Text(
                    text = "Panchasar Road, Morbi, Gujarat – 363641 · India",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPlaceholder,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AccentBlueSoft,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.25f))
                ) {
                    Text(
                        text = "Developed By - Azazmadkiya",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = AccentBlueDark,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ContactRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AccentBlueSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AccentBlueDark,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                ),
                color = TextMuted
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                ),
                color = if (onClick != null) AccentBlueDark else TextPrimary
            )
        }
        if (onClick != null) {
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = TextPlaceholder,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
fun PhoneContactItem(
    title: String,
    phoneNumber: String,
    subtitle: String? = null,
    badge: String? = null,
    isPrimary: Boolean = false,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit,
    onCopy: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isPrimary) AccentBlueSoft.copy(alpha = 0.45f) else WhiteSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPrimary) AccentBlue.copy(alpha = 0.35f) else WhiteBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onCall() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isPrimary) AccentBlue else AccentBlueSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = if (isPrimary) Color.White else AccentBlueDark,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = title.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            letterSpacing = 0.3.sp
                        ),
                        color = if (isPrimary) AccentBlueDark else TextMuted,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (badge != null) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isPrimary) AccentBlueDark else AccentBlueSoft
                        ) {
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPrimary) Color.White else AccentBlueDark
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(1.dp))

                Text(
                    text = phoneNumber,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    ),
                    color = TextPrimary,
                    maxLines = 1,
                    softWrap = false
                )

                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            lineHeight = 12.sp
                        ),
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Action icon buttons
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                IconButton(
                    onClick = onCall,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(AccentBlueSoft)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Call $phoneNumber",
                        tint = AccentBlueDark,
                        modifier = Modifier.size(13.dp)
                    )
                }

                IconButton(
                    onClick = onWhatsApp,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(AccentGreenSoft)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "WhatsApp $phoneNumber",
                        tint = WhatsAppGreenDark,
                        modifier = Modifier.size(13.dp)
                    )
                }

                IconButton(
                    onClick = onCopy,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(WhiteSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy $phoneNumber",
                        tint = TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BankDetailField(
    label: String,
    value: String,
    isMonospace: Boolean = false,
    isBold: Boolean = false,
    isPrimaryColor: Boolean = false,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCopy() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                ),
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                    fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
                    fontSize = if (isMonospace) 14.sp else 13.sp
                ),
                color = if (isPrimaryColor) AccentBlueDark else TextPrimary
            )
        }
        IconButton(
            onClick = onCopy,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy $label",
                tint = AccentBlueDark,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun RegistrationItemCard(
    title: String,
    registrationId: String,
    badge: String,
    description: String,
    onCopy: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = WhiteSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCopy() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = TextMuted
                )
                Surface(
                    color = AccentTealSoft,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        color = AccentTealDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = registrationId,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp,
                        fontSize = 14.5.sp
                    ),
                    color = TextPrimary
                )
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy $title",
                        tint = AccentBlueDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = TextSecondary
            )
        }
    }
}

@Composable
fun DepotCard(
    depot: SalesDepot,
    onOpenMap: (String, String) -> Unit,
    onShare: (String) -> Unit,
    onCopy: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("depot_card_${depot.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentBlueSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (depot.depotNumber == 1) Icons.Default.Store else Icons.Default.LocalShipping,
                            contentDescription = "Depot ${depot.depotNumber}",
                            tint = AccentBlueDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "SALES DEPOT & GODOWN ${depot.depotNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 10.5.sp
                            ),
                            color = AccentBlueDark
                        )
                        Text(
                            text = depot.shortName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AccentBlueSoft
                ) {
                    Text(
                        text = "PIN ${depot.pincode}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        ),
                        color = AccentBlueDark,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Address Box
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = WhiteSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = AccentRed,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(top = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = depot.address,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 18.sp,
                                    fontSize = 12.5.sp
                                ),
                                color = TextPrimary
                            )
                        }

                        IconButton(
                            onClick = { onCopy(depot.address) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Address",
                                tint = AccentBlueDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    if (depot.landmark.isNotBlank() && depot.landmark != "Rajpar Road") {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Landmark: ${depot.landmark}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            color = AccentBlueDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description / Note
            Text(
                text = depot.description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Storage & Dispatch Capabilities Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                depot.storageTypes.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = WhiteSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onOpenMap(depot.mapUrl, depot.address) },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(40.dp)
                        .testTag("depot_${depot.id}_map_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("OPEN IN MAPS", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { onShare(CompanyInfo.getFormattedDepotMessage(depot)) },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WhatsAppGreenDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WhatsAppGreenDark.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = WhatsAppGreenDark
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = { onCopy(depot.address) },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(WhiteSurfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Address",
                        tint = AccentBlueDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HeadOfficeCard(
    onOpenMap: () -> Unit,
    onCopy: () -> Unit,
    onCallClick: () -> Unit = {},
    onShare: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, AccentBlue.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("head_office_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentBlueSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = "Head Office",
                            tint = AccentBlueDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "REGISTERED / HEAD OFFICE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    fontSize = 10.5.sp
                                ),
                                color = AccentBlueDark
                            )
                        }
                        Text(
                            text = "Commercial & Administrative Desk",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp
                            ),
                            color = TextPrimary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = AccentBlueSoft
                ) {
                    Text(
                        text = "CORPORATE HQ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = AccentBlueDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = WhiteSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = AccentRed,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = CompanyInfo.ADDRESS,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                ),
                                color = TextPrimary
                            )
                        }

                        IconButton(
                            onClick = onCopy,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Address",
                                tint = AccentBlueDark,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Central corporate office handling formal commercial billing, tax invoices, accounts, customer contracts & administrative operations.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        ),
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Operational Tags
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Registered Office", "Commercial Orders", "Accounts & Invoicing", "Panchasar Rd").forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = WhiteSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Direct Helpline & Contacts Banner
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = AccentBlueSoft.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = AccentBlueDark,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "OFFICIAL DIRECT HELPLINES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                fontSize = 8.5.sp
                            ),
                            color = AccentBlueDark
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = CompanyInfo.PHONE,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = TextPrimary,
                            maxLines = 1,
                            softWrap = false
                        )
                        Text(
                            text = " · ",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentBlueDark
                            )
                        )
                        Text(
                            text = CompanyInfo.PHONE_1,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = TextPrimary,
                            maxLines = 1,
                            softWrap = false
                        )
                        Text(
                            text = " · ",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentBlueDark
                            )
                        )
                        Text(
                            text = CompanyInfo.PHONE_2,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = TextPrimary,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenMap,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(40.dp)
                        .testTag("head_office_map_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlueDark,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("OPEN IN MAPS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("head_office_share_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WhatsAppGreenDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WhatsAppGreenDark.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = WhatsAppGreenDark
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = onCallClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentBlueSoft)
                        .testTag("head_office_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Call Head Office",
                        tint = AccentBlueDark,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onCopy,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(WhiteSurfaceVariant)
                        .testTag("head_office_copy_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Address",
                        tint = AccentBlueDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

