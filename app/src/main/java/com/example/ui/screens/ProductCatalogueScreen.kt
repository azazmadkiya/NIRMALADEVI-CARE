package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChemicalCatalog
import com.example.data.model.ChemicalProduct
import com.example.data.model.ProductCatalogueData
import com.example.ui.theme.*
import com.example.util.DownloadResult
import com.example.util.ProductCataloguePdfManager

import androidx.activity.compose.BackHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCatalogueScreen(
    onDismiss: () -> Unit,
    onProductClick: (ChemicalProduct) -> Unit,
    onDirectInquiry: (String) -> Unit,
    onShareApp: () -> Unit = {}
) {
    BackHandler(onBack = onDismiss)
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedTab by remember { mutableStateOf(0) } // 0 = Official Document View, 1 = Chemical Specs List
    var downloadResult by remember { mutableStateOf<DownloadResult.Success?>(null) }
    var isDownloading by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    fun triggerDownload() {
        isDownloading = true
        try {
            val result = ProductCataloguePdfManager.downloadPdfToDevice(context)
            if (result is DownloadResult.Success) {
                downloadResult = result
                Toast.makeText(context, "✅ PDF saved to Downloads folder!", Toast.LENGTH_LONG).show()
            } else if (result is DownloadResult.Error) {
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error saving PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isDownloading = false
        }
    }

    Scaffold(
        containerColor = WhiteCanvas,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "PRODUCT CATALOGUE",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    fontSize = 14.sp
                                ),
                                color = TextPrimary,
                                maxLines = 1,
                                softWrap = false
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AccentBlueSoft
                            ) {
                                Text(
                                    text = "PDF",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = AccentBlueDark,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Nirmaladevi Care · 24+ Chemicals",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                            color = TextMuted,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("catalogue_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { ProductCataloguePdfManager.openPdf(context) },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("catalogue_topbar_open_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Open in PDF Viewer",
                            tint = AccentBlueDark,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    IconButton(
                        onClick = { triggerDownload() },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("catalogue_topbar_download_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download PDF",
                            tint = AccentBlueDark,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    IconButton(
                        onClick = { ProductCataloguePdfManager.shareOnWhatsApp(context) },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("catalogue_topbar_whatsapp_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share PDF",
                            tint = WhatsAppGreenDark,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WhiteSurface)
            )
        },
        bottomBar = {
            Surface(
                color = WhiteSurface,
                border = BorderStroke(1.dp, WhiteBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { triggerDownload() },
                            modifier = Modifier
                                .weight(1.4f)
                                .height(46.dp)
                                .testTag("catalogue_download_mobile_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentBlue,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isDownloading
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isDownloading) "Saving PDF..." else "Download PDF to Mobile",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = { ProductCataloguePdfManager.shareOnWhatsApp(context) },
                            modifier = Modifier
                                .weight(1.1f)
                                .height(46.dp)
                                .testTag("catalogue_share_whatsapp_button"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = WhatsAppGreenDark),
                            border = BorderStroke(1.5.dp, WhatsAppGreenDark),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = WhatsAppGreenDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Share WhatsApp",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = onShareApp,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(WhiteSurfaceVariant)
                                .testTag("catalogue_share_app_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Official App",
                                tint = AccentBlueDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { ProductCataloguePdfManager.openPdf(context) },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentBlueSoft)
                                .testTag("catalogue_open_external_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = "Open in PDF Viewer",
                                tint = AccentBlueDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("product_catalogue_scroll_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Download Success Confirmation Banner
            if (downloadResult != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AccentGreenSoft),
                        border = BorderStroke(1.dp, AccentGreenDark.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("catalogue_download_success_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AccentGreenDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "PDF Downloaded to Device!",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    color = AccentGreenDark
                                )
                                Text(
                                    text = downloadResult!!.filePath,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = TextSecondary
                                )
                            }
                            FilledTonalButton(
                                onClick = { ProductCataloguePdfManager.openPdf(context) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = AccentGreenDark,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("OPEN", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // View Selector: Official Document vs Interactive List
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(WhiteSurfaceVariant)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedTab == 0) WhiteSurface else Color.Transparent,
                        border = if (selectedTab == 0) BorderStroke(1.dp, WhiteBorder) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = 0 }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 9.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = if (selectedTab == 0) AccentBlueDark else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Official PDF Sheet View",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.5.sp
                                ),
                                color = if (selectedTab == 0) AccentBlueDark else TextMuted
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedTab == 1) WhiteSurface else Color.Transparent,
                        border = if (selectedTab == 1) BorderStroke(1.dp, WhiteBorder) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = 1 }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 9.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ListAlt,
                                contentDescription = null,
                                tint = if (selectedTab == 1) AccentBlueDark else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Product Specs & Order",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.5.sp
                                ),
                                color = if (selectedTab == 1) AccentBlueDark else TextMuted
                            )
                        }
                    }
                }
            }

            if (selectedTab == 0) {
                // ── OFFICIAL PDF SHEET VIEW (A4 Paper Representation) ──
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.5.dp, WhiteBorderStrong),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("catalogue_a4_paper_preview")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            // Top Header: NIRMALA TM Logo + ISO Seal
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left: NIRMALA TM Logo
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF0D234B)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = "NIRMALA",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 1.sp,
                                                fontSize = 12.sp
                                            )
                                        )
                                        Text(
                                            text = "TM",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 7.sp
                                            ),
                                            modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                                        )
                                    }
                                }

                                // Right: CERTIFIED ISO 9001:2015 COMPANY Seal
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, Color(0xFF0D234B), CircleShape)
                                        .padding(2.dp)
                                        .border(1.dp, Color(0xFF0D234B), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "CERTIFIED",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 5.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color(0xFF0D234B)
                                        )
                                        Text(
                                            text = "ISO",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black
                                            ),
                                            color = Color(0xFF0D234B)
                                        )
                                        Text(
                                            text = "9001:2015",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 6.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color(0xFF0D234B)
                                        )
                                        Text(
                                            text = "COMPANY",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 5.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color(0xFF0D234B)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Main Title: NIRMALA DEVI CARE
                            Text(
                                text = "NIRMALA DEVI CARE",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    fontSize = 22.sp
                                ),
                                color = Color(0xFF00A3E0),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Subtitle Row: "PRODUCTS" in Orange, "PRIVATE LIMITED" in Cyan
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PRODUCTS",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp,
                                        fontSize = 17.sp
                                    ),
                                    color = Color(0xFFF57C00)
                                )

                                Text(
                                    text = "PRIVATE LIMITED",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp,
                                        fontSize = 17.sp
                                    ),
                                    color = Color(0xFF00A3E0)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(thickness = 1.dp, color = Color(0xFFE2E8F0))
                            Spacer(modifier = Modifier.height(14.dp))

                            // ── TWO COLUMNS OF PRODUCTS ──
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Left Column (16 products)
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    ProductCatalogueData.LEFT_COLUMN.forEach { item ->
                                        CatalogueBulletItem(
                                            item = item,
                                            onClick = {
                                                val found = ChemicalCatalog.products.firstOrNull { it.id == item.id }
                                                if (found != null) onProductClick(found)
                                                else onDirectInquiry(item.name)
                                            }
                                        )
                                    }
                                }

                                // Right Column (8 products)
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    ProductCatalogueData.RIGHT_COLUMN.forEach { item ->
                                        CatalogueBulletItem(
                                            item = item,
                                            onClick = {
                                                val found = ChemicalCatalog.products.firstOrNull { it.id == item.id }
                                                if (found != null) onProductClick(found)
                                                else onDirectInquiry(item.name)
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            HorizontalDivider(thickness = 1.dp, color = Color(0xFFCBD5E1))
                            Spacer(modifier = Modifier.height(14.dp))

                            // ── 3 DIVISIONS BOX ──
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min), // Essential for vertical dividers to fill height
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Division 1: Postal Address
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Postal Address",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.5.sp
                                        ),
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "Plot No. 3, Survey No. 1300p, Opp. Ganga Steel, Panchasar Road, Morbi, 363641",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 7.5.sp,
                                            lineHeight = 10.sp
                                        ),
                                        color = TextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                // Vertical Divider 1
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .fillMaxHeight()
                                        .background(Color.LightGray.copy(alpha = 0.6f))
                                )

                                // Division 2: Pipaliya Char Rasta
                                Column(
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .padding(horizontal = 2.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Pipaliya Division",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.5.sp
                                        ),
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "Survey No.283, Chanchavadarda Ta-Maliya(mi) Near Kodal Uniquoters Pvt. Ltd",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 7.5.sp,
                                            lineHeight = 10.sp
                                        ),
                                        color = TextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                // Vertical Divider 2
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .fillMaxHeight()
                                        .background(Color.LightGray.copy(alpha = 0.6f))
                                )

                                // Division 3: Rajpar Road
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Rajpar Division",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.5.sp
                                        ),
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "Survey No. 21, Plot No. 4, Indian Land, Rajpar Road, Morbi, 363641",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 7.5.sp,
                                            lineHeight = 10.sp
                                        ),
                                        color = TextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFE2E8F0))
                            Spacer(modifier = Modifier.height(8.dp))

                            // Bottom Contact Row: Website, Email, Phone
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Language,
                                            contentDescription = null,
                                            modifier = Modifier.size(11.dp),
                                            tint = TextMuted
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = ProductCatalogueData.WEBSITE,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 7.5.sp,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = TextPrimary
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Email,
                                            contentDescription = null,
                                            modifier = Modifier.size(11.dp),
                                            tint = TextMuted
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = ProductCatalogueData.EMAIL,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 7.5.sp,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = TextPrimary
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        modifier = Modifier.size(11.dp),
                                        tint = AccentBlueDark
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Helpline: ${ProductCatalogueData.PRIMARY_PHONE}  ·  ${ProductCatalogueData.PHONE_1}  ·  ${ProductCatalogueData.PHONE_2}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 7.5.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // ── INTERACTIVE CHEMICAL LIST WITH SEARCH & ACTIONS ──
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search catalogue chemicals...", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = AccentBlueDark
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = WhiteBorder
                        ),
                        singleLine = true
                    )
                }

                val filteredItems = ProductCatalogueData.ALL_PRODUCTS.filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                }

                items(filteredItems) { item ->
                    val matchedProduct = ChemicalCatalog.products.firstOrNull { it.id == item.id }
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                        border = BorderStroke(1.dp, WhiteBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (matchedProduct != null) onProductClick(matchedProduct)
                                else onDirectInquiry(item.name)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AccentBlueSoft),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Science,
                                        contentDescription = null,
                                        tint = AccentBlueDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.5.sp
                                        ),
                                        color = TextPrimary
                                    )
                                    if (matchedProduct != null) {
                                        Text(
                                            text = "${matchedProduct.formula} · ${matchedProduct.purity}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (matchedProduct != null) {
                                    IconButton(
                                        onClick = { onProductClick(matchedProduct) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Specs",
                                            tint = AccentBlueDark,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { onDirectInquiry(item.name) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddShoppingCart,
                                        contentDescription = "Add to Inquiry",
                                        tint = WhatsAppGreenDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogueBulletItem(
    item: com.example.data.model.CatalogueItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(5.dp)
                .clip(CircleShape)
                .background(Color.Black)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp,
                    lineHeight = 14.sp
                ),
                color = Color(0xFF1E293B)
            )
            if (item.id == "dm-water") {
                Text(
                    text = "DISTILLED WATER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFF475569)
                )
            }
            if (item.id == "poly-aluminium-chloride") {
                Text(
                    text = "(PAC POWDER)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFF475569)
                )
            }
        }
    }
}
