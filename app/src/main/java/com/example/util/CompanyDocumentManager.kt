package com.example.util

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

data class CompanyOfficialDocument(
    val id: String,
    val title: String,
    val subtitle: String,
    val fileName: String,
    val assetPath: String,
    val driveId: String,
    val driveDownloadUrl: String,
    val driveViewUrl: String,
    val category: String,
    val description: String,
    val approximateSize: String
)

sealed class DocumentDownloadResult {
    data class Success(
        val fileName: String,
        val filePath: String,
        val uri: Uri?,
        val message: String
    ) : DocumentDownloadResult()

    data class Error(
        val fileName: String,
        val message: String
    ) : DocumentDownloadResult()
}

object CompanyDocumentManager {

    const val TARGET_FOLDER_NAME = "NIRMALADEVI CARE"

    // 1. Official Catalogue NCPL PDF
    val DOC_CATALOGUE = CompanyOfficialDocument(
        id = "catalogue_ncpl",
        title = "Catalogue NCPL.pdf",
        subtitle = "Official Chemical Product Catalogue",
        fileName = "Catalogue NCPL.pdf",
        assetPath = "documents/catalogue_ncpl.pdf",
        driveId = "1xQ1EVjfEKvhkGcK9N72mVdWKfNuV06T5",
        driveDownloadUrl = "https://drive.google.com/uc?id=1xQ1EVjfEKvhkGcK9N72mVdWKfNuV06T5&export=download",
        driveViewUrl = "https://drive.google.com/file/d/1xQ1EVjfEKvhkGcK9N72mVdWKfNuV06T5/view?usp=sharing",
        category = "Catalogue",
        description = "Official Nirmaladevi Care Product Catalogue with industrial specifications, purity grades, and packaging details.",
        approximateSize = "120 KB"
    )

    // 2. Official GST Certificate PDF
    val DOC_GST = CompanyOfficialDocument(
        id = "gst_certificate",
        title = "Gst Certificate.pdf",
        subtitle = "GSTIN: 24AAHCN6833G1ZF",
        fileName = "Gst Certificate.pdf",
        assetPath = "documents/gst_certificate.pdf",
        driveId = "1eqr6al2DHQx8WmwTVIU8ffXTDvdh5uF8",
        driveDownloadUrl = "https://drive.google.com/uc?id=1eqr6al2DHQx8WmwTVIU8ffXTDvdh5uF8&export=download",
        driveViewUrl = "https://drive.google.com/file/d/1eqr6al2DHQx8WmwTVIU8ffXTDvdh5uF8/view?usp=sharing",
        category = "Tax",
        description = "Government of India GST Registration Certificate for inter-state & intra-state industrial chemical trade.",
        approximateSize = "162 KB"
    )

    // 3. Official MSME / Udyam Certificate PDF
    val DOC_MSME = CompanyOfficialDocument(
        id = "udyam_certificate",
        title = "Udyam Certificate (MSME).pdf",
        subtitle = "UDYAM-GJ-32-0031160",
        fileName = "Udyam Certificate (MSME).pdf",
        assetPath = "documents/udyam_certificate.pdf",
        driveId = "1pt4j4p70098X1jA__9SmHEGzn43aYTuh",
        driveDownloadUrl = "https://drive.google.com/uc?id=1pt4j4p70098X1jA__9SmHEGzn43aYTuh&export=download",
        driveViewUrl = "https://drive.google.com/file/d/1pt4j4p70098X1jA__9SmHEGzn43aYTuh/view?usp=sharing",
        category = "MSME",
        description = "Ministry of Micro, Small and Medium Enterprises (MSME) Government of India verified registration certificate.",
        approximateSize = "208 KB"
    )

    // 4. Official Bank Details PDF
    val DOC_BANK = CompanyOfficialDocument(
        id = "bank_details",
        title = "Bank Details.PDF",
        subtitle = "HDFC Bank · A/c: 50200097827378",
        fileName = "Bank Details.PDF",
        assetPath = "documents/bank_details.pdf",
        driveId = "1lrPU7zp0KtvNn-tcKMDvOTErZL4ymFtu",
        driveDownloadUrl = "https://drive.google.com/uc?id=1lrPU7zp0KtvNn-tcKMDvOTErZL4ymFtu&export=download",
        driveViewUrl = "https://drive.google.com/file/d/1lrPU7zp0KtvNn-tcKMDvOTErZL4ymFtu/view?usp=sharing",
        category = "Bank",
        description = "Official banking and RTGS/NEFT/IMPS payment transfer details letterhead of Nirmaladevi Care Pvt. Ltd.",
        approximateSize = "3.5 KB"
    )

    val ALL_DOCUMENTS = listOf(DOC_CATALOGUE, DOC_GST, DOC_MSME, DOC_BANK)

    /**
     * Gets or prepares a cached file from assets or cache directory.
     */
    fun getCachedDocumentFile(context: Context, doc: CompanyOfficialDocument): File {
        val docsDir = File(context.cacheDir, "official_documents")
        if (!docsDir.exists()) docsDir.mkdirs()
        val file = File(docsDir, doc.fileName)

        if (!file.exists() || file.length() == 0L) {
            try {
                context.assets.open(doc.assetPath).use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                // If asset not found, create empty placeholder or generate catalogue
                if (doc.id == DOC_CATALOGUE.id) {
                    ProductCataloguePdfManager.generateCataloguePdf(context, file)
                }
            }
        }
        return file
    }

    /**
     * Downloads document directly to phone storage inside folder "NIRMALADEVI CARE".
     * Saves to: Downloads/NIRMALADEVI CARE/<FileName>
     */
    suspend fun downloadDocumentToPhoneStorage(
        context: Context,
        doc: CompanyOfficialDocument
    ): DocumentDownloadResult = withContext(Dispatchers.IO) {
        try {
            // First check if cached/asset copy exists
            val localSource = getCachedDocumentFile(context, doc)
            val inputStream: InputStream = if (localSource.exists() && localSource.length() > 0L) {
                localSource.inputStream()
            } else {
                // Fetch from Google Drive direct download URL with redirects
                fetchFromGoogleDrive(doc.driveDownloadUrl)
                    ?: return@withContext DocumentDownloadResult.Error(
                        doc.fileName,
                        "Unable to obtain PDF data. Please check internet connection."
                    )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val relativePath = "${Environment.DIRECTORY_DOWNLOADS}/$TARGET_FOLDER_NAME"
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, doc.fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.RELATIVE_PATH, relativePath)
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: return@withContext DocumentDownloadResult.Error(
                        doc.fileName,
                        "Failed to create file in phone Downloads/$TARGET_FOLDER_NAME"
                    )

                resolver.openOutputStream(uri)?.use { out ->
                    inputStream.use { inp ->
                        inp.copyTo(out)
                    }
                }

                contentValues.clear()
                contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)

                // Scan file
                try {
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf("/storage/emulated/0/Download/$TARGET_FOLDER_NAME/${doc.fileName}"),
                        arrayOf("application/pdf"),
                        null
                    )
                } catch (_: Exception) {}

                DocumentDownloadResult.Success(
                    fileName = doc.fileName,
                    filePath = "Downloads/$TARGET_FOLDER_NAME/${doc.fileName}",
                    uri = uri,
                    message = "Saved in Phone Storage: Downloads/$TARGET_FOLDER_NAME/${doc.fileName}"
                )
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val targetDir = File(downloadsDir, TARGET_FOLDER_NAME)
                if (!targetDir.exists()) targetDir.mkdirs()

                val targetFile = File(targetDir, doc.fileName)
                FileOutputStream(targetFile).use { out ->
                    inputStream.use { inp ->
                        inp.copyTo(out)
                    }
                }

                val uri = Uri.fromFile(targetFile)
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(targetFile.absolutePath),
                    arrayOf("application/pdf"),
                    null
                )

                DocumentDownloadResult.Success(
                    fileName = doc.fileName,
                    filePath = targetFile.absolutePath,
                    uri = uri,
                    message = "Saved in Phone Storage: Downloads/$TARGET_FOLDER_NAME/${doc.fileName}"
                )
            }
        } catch (e: Exception) {
            DocumentDownloadResult.Error(doc.fileName, e.message ?: "Download error")
        }
    }

    /**
     * Downloads all 4 documents in batch to phone storage in folder NIRMALADEVI CARE.
     */
    suspend fun downloadAllToPhoneStorage(context: Context): List<DocumentDownloadResult> =
        withContext(Dispatchers.IO) {
            ALL_DOCUMENTS.map { doc ->
                downloadDocumentToPhoneStorage(context, doc)
            }
        }

    /**
     * Open Google Drive link in web browser or Google Drive app.
     */
    fun openGoogleDriveLink(context: Context, doc: CompanyOfficialDocument) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(doc.driveViewUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(doc.driveDownloadUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Unable to open Google Drive link", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Opens PDF in external PDF reader app via FileProvider.
     */
    fun openPdfViewer(context: Context, doc: CompanyOfficialDocument) {
        try {
            val file = getCachedDocumentFile(context, doc)
            val authority = "${context.packageName}.provider"
            val uri = FileProvider.getUriForFile(context, authority, file)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Open ${doc.fileName}"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "No PDF reader app found. You can view via Google Drive or WhatsApp.",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shares the PDF file across apps (WhatsApp, Mail, Drive, etc.).
     */
    fun sharePdf(context: Context, doc: CompanyOfficialDocument) {
        try {
            val file = getCachedDocumentFile(context, doc)
            val authority = "${context.packageName}.provider"
            val uri = FileProvider.getUriForFile(context, authority, file)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "NIRMALADEVI CARE - ${doc.title}")
                putExtra(
                    Intent.EXTRA_TEXT,
                    """
                    Official Document: ${doc.title}
                    ${doc.description}
                    
                    Company: NIRMALADEVI CARE PRIVATE LIMITED
                    Morbi, Gujarat, India
                    
                    Google Drive Link:
                    ${doc.driveViewUrl}
                    """.trimIndent()
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share ${doc.title}"))
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to share PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shares the Google Drive link directly.
     */
    fun shareDriveLink(context: Context, doc: CompanyOfficialDocument) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Download ${doc.title} - Nirmaladevi Care")
                putExtra(
                    Intent.EXTRA_TEXT,
                    """
                    📄 *${doc.title}*
                    NIRMALADEVI CARE PRIVATE LIMITED (Morbi, Gujarat)
                    ${doc.subtitle}
                    
                    🔗 *Click to View & Download on Google Drive:*
                    ${doc.driveViewUrl}
                    """.trimIndent()
                )
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Google Drive Link"))
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to share link", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchFromGoogleDrive(urlString: String): InputStream? {
        return try {
            var currentUrl = urlString
            var connection: HttpURLConnection
            var redirects = 0
            while (redirects < 5) {
                val url = URL(currentUrl)
                connection = url.openConnection() as HttpURLConnection
                connection.instanceFollowRedirects = true
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                val status = connection.responseCode
                if (status == HttpURLConnection.HTTP_MOVED_TEMP ||
                    status == HttpURLConnection.HTTP_MOVED_PERM ||
                    status == 303 || status == 307 || status == 308
                ) {
                    val newUrl = connection.getHeaderField("Location")
                    if (newUrl != null) {
                        currentUrl = newUrl
                        redirects++
                        continue
                    }
                }
                if (status == HttpURLConnection.HTTP_OK) {
                    return connection.inputStream
                }
                break
            }
            null
        } catch (e: Exception) {
            null
        }
    }
}
