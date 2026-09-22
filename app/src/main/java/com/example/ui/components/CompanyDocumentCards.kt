package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.CompanyDocumentManager
import com.example.util.CompanyOfficialDocument
import com.example.util.DocumentDownloadResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CompanyDocumentDownloadCard(
    document: CompanyOfficialDocument,
    modifier: Modifier = Modifier,
    onCustomDownloadComplete: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    var isDownloading by remember { mutableStateOf(false) }
    var downloadResult by remember { mutableStateOf<DocumentDownloadResult.Success?>(null) }

    fun triggerDownload() {
        if (isDownloading) return
        isDownloading = true
        coroutineScope.launch {
            val result = CompanyDocumentManager.downloadDocumentToPhoneStorage(context, document)
            withContext(Dispatchers.Main) {
                isDownloading = false
                when (result) {
                    is DocumentDownloadResult.Success -> {
                        downloadResult = result
                        Toast.makeText(
                            context,
                            "✅ ${document.fileName} downloaded to folder NIRMALADEVI CARE!",
                            Toast.LENGTH_LONG
                        ).show()
                        onCustomDownloadComplete(result.filePath)
                    }
                    is DocumentDownloadResult.Error -> {
                        Toast.makeText(context, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
        border = BorderStroke(1.dp, WhiteBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("doc_card_${document.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Icon, Title, Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AccentBlueSoft,
                    border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.3f)),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF Icon",
                            tint = AccentBlueDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = document.fileName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = AccentTealSoft
                        ) {
                            Text(
                                text = document.approximateSize,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = AccentTealDark,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Text(
                        text = document.subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = WhiteSurfaceVariant,
                    border = BorderStroke(1.dp, WhiteBorder)
                ) {
                    Text(
                        text = "PDF",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.5.sp
                        ),
                        color = AccentBlueDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = document.description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Phone storage location tag
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = AccentBlueSoft.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = "Folder",
                        tint = AccentBlueDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Phone Storage: Downloads/NIRMALADEVI CARE/${document.fileName}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = AccentBlueDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Download Success Confirmation
            AnimatedVisibility(
                visible = downloadResult != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                downloadResult?.let { res ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AccentGreenSoft,
                        border = BorderStroke(1.dp, AccentGreenDark.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AccentGreenDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Saved in NIRMALADEVI CARE folder",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    ),
                                    color = AccentGreenDark
                                )
                            }
                            TextButton(
                                onClick = { CompanyDocumentManager.openPdfViewer(context, document) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "OPEN",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = AccentGreenDark
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Primary: Download to Phone (NIRMALADEVI CARE)
                Button(
                    onClick = { triggerDownload() },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(38.dp)
                        .testTag("download_doc_${document.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isDownloading
                ) {
                    if (isDownloading) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            color = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Saving...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Download PDF", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Google Drive Link Button
                OutlinedButton(
                    onClick = { CompanyDocumentManager.openGoogleDriveLink(context, document) },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(38.dp)
                        .testTag("drive_doc_${document.id}"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlueDark),
                    border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = "Google Drive Link",
                        modifier = Modifier.size(15.dp),
                        tint = AccentBlueDark
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Google Drive", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Open in PDF Viewer
                IconButton(
                    onClick = { CompanyDocumentManager.openPdfViewer(context, document) },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentBlueSoft)
                        .testTag("open_doc_${document.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Open ${document.fileName}",
                        tint = AccentBlueDark,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Share PDF
                IconButton(
                    onClick = { CompanyDocumentManager.sharePdf(context, document) },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(WhiteSurfaceVariant)
                        .border(1.dp, WhiteBorder, RoundedCornerShape(8.dp))
                        .testTag("share_doc_${document.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share ${document.fileName}",
                        tint = WhatsAppGreenDark,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
    }
}

/**
 * Compact document action row designed to be embedded directly inside
 * the GST card, MSME card, or Bank Details card.
 */
@Composable
fun CompactDocumentActionRow(
    document: CompanyOfficialDocument,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isDownloading by remember { mutableStateOf(false) }
    var downloadResult by remember { mutableStateOf<DocumentDownloadResult.Success?>(null) }

    fun triggerDownload() {
        if (isDownloading) return
        isDownloading = true
        coroutineScope.launch {
            val result = CompanyDocumentManager.downloadDocumentToPhoneStorage(context, document)
            withContext(Dispatchers.Main) {
                isDownloading = false
                when (result) {
                    is DocumentDownloadResult.Success -> {
                        downloadResult = result
                        Toast.makeText(
                            context,
                            "✅ ${document.fileName} saved to Downloads/NIRMALADEVI CARE!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is DocumentDownloadResult.Error -> {
                        Toast.makeText(context, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = WhiteSurface,
        border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.25f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = AccentBlueDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = document.fileName,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        color = TextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = AccentTealSoft
                ) {
                    Text(
                        text = document.approximateSize,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                        ),
                        color = AccentTealDark,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "📁 Downloads to Phone Storage in folder: NIRMALADEVI CARE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.5.sp,
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace
                )
            )

            AnimatedVisibility(visible = downloadResult != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = AccentGreenDark,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Saved in Downloads/NIRMALADEVI CARE/${document.fileName}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGreenDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { triggerDownload() },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(34.dp)
                        .testTag("compact_download_${document.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                    enabled = !isDownloading
                ) {
                    if (isDownloading) {
                        CircularProgressIndicator(
                            strokeWidth = 1.5.dp,
                            color = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Saving...", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Download PDF", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = { CompanyDocumentManager.openGoogleDriveLink(context, document) },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(34.dp)
                        .testTag("compact_drive_${document.id}"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlueDark),
                    border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = "Drive Link",
                        modifier = Modifier.size(13.dp),
                        tint = AccentBlueDark
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Google Drive", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { CompanyDocumentManager.openPdfViewer(context, document) },
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("compact_open_${document.id}"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = BorderStroke(1.dp, WhiteBorder),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("View", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = { CompanyDocumentManager.sharePdf(context, document) },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(AccentBlueSoft)
                        .testTag("compact_share_${document.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = AccentBlueDark,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * Dedicated Section in About Screen for all official certificates and catalogue.
 */
@Composable
fun OfficialDocumentsSection(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isDownloadingAll by remember { mutableStateOf(false) }
    var downloadAllSuccessCount by remember { mutableStateOf<Int?>(null) }

    fun downloadAll() {
        if (isDownloadingAll) return
        isDownloadingAll = true
        coroutineScope.launch {
            val results = CompanyDocumentManager.downloadAllToPhoneStorage(context)
            withContext(Dispatchers.Main) {
                isDownloadingAll = false
                val successCount = results.count { it is DocumentDownloadResult.Success }
                downloadAllSuccessCount = successCount
                Toast.makeText(
                    context,
                    "✅ Downloaded $successCount/4 official documents to Downloads/NIRMALADEVI CARE!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
        border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("official_documents_section_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
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
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        tint = AccentBlueDark,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "OFFICIAL PDF DOWNLOADS & CERTIFICATES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            fontSize = 11.sp
                        ),
                        color = AccentBlueDark
                    )
                    Text(
                        text = "Catalogue, GST, MSME & Bank Details",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = AccentTealSoft.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, AccentTealDark.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderSpecial,
                        contentDescription = null,
                        tint = AccentTealDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = "Auto-Saved to Phone Storage Folder:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = AccentTealDark
                        )
                        Text(
                            text = "Downloads/NIRMALADEVI CARE/",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            ),
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Download All Button
            Button(
                onClick = { downloadAll() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("download_all_documents_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlueDark,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !isDownloadingAll
            ) {
                if (isDownloadingAll) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Downloading All 4 Documents to Folder...",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.DownloadForOffline,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Download All (4 Documents) to Phone Storage",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedVisibility(visible = downloadAllSuccessCount != null) {
                downloadAllSuccessCount?.let { count ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✅ Successfully saved $count documents to Downloads/NIRMALADEVI CARE/",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AccentGreenDark,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // The 4 Documents
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CompanyDocumentDownloadCard(document = CompanyDocumentManager.DOC_CATALOGUE)
                CompanyDocumentDownloadCard(document = CompanyDocumentManager.DOC_GST)
                CompanyDocumentDownloadCard(document = CompanyDocumentManager.DOC_MSME)
                CompanyDocumentDownloadCard(document = CompanyDocumentManager.DOC_BANK)
            }
        }
    }
}
