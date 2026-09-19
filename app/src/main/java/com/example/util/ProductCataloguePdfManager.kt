package com.example.util

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.ProductCatalogueData
import java.io.File
import java.io.FileOutputStream

sealed class DownloadResult {
    data class Success(val uri: Uri, val file: File, val message: String, val filePath: String) : DownloadResult()
    data class Error(val message: String) : DownloadResult()
}

object ProductCataloguePdfManager {
    const val FILE_NAME = "Nirmala_Devi_Care_Product_Catalogue.pdf"

    /**
     * Generates or retrieves the cached PDF file of the official Product Catalogue.
     */
    fun getOrCreatePdfFile(context: Context): File {
        val cacheFile = File(context.cacheDir, FILE_NAME)
        if (!cacheFile.exists() || cacheFile.length() == 0L) {
            generateCataloguePdf(context, cacheFile)
        }
        return cacheFile
    }

    /**
     * Builds the PDF using Android's native PdfDocument.
     */
    fun generateCataloguePdf(context: Context, destinationFile: File): File {
        val document = PdfDocument()

        // Standard A4 dimensions: 595 x 842 points
        val pageWidth = 595
        val pageHeight = 842
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        // Background: Clean White
        canvas.drawColor(Color.WHITE)

        // Paints
        val primaryBlue = Color.rgb(0, 163, 224) // #00A3E0 (Sky Blue)
        val deepNavy = Color.rgb(13, 35, 75) // #0D234B (Nirmala TM Blue)
        val accentOrange = Color.rgb(245, 124, 0) // #F57C00 (Products Amber)
        val darkText = Color.rgb(20, 24, 33) // #141821
        val lightGray = Color.rgb(220, 225, 230)

        // ── 1. TOP LEFT: "NIRMALA TM" LOGO ──
        val logoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = deepNavy
            style = Paint.Style.FILL
        }
        val logoRect = RectF(34f, 28f, 134f, 62f)
        canvas.drawRoundRect(logoRect, 10f, 10f, logoPaint)

        // White curved arc inside banner
        val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        }
        canvas.drawArc(RectF(38f, 44f, 130f, 66f), 180f, 180f, false, arcPaint)

        // White Text "NIRMALA"
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 15f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("NIRMALA", 84f, 50f, textPaint)

        // "TM" superscript
        val tmPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 7f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("TM", 121f, 40f, tmPaint)

        // ── 2. TOP RIGHT: "CERTIFIED ISO 9001:2015 COMPANY" SEAL ──
        val sealCenterX = 530f
        val sealCenterY = 46f
        val sealRadius = 26f

        // Outer circle
        val sealStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = deepNavy
            style = Paint.Style.STROKE
            strokeWidth = 1.8f
        }
        canvas.drawCircle(sealCenterX, sealCenterY, sealRadius, sealStrokePaint)
        // Inner circle
        canvas.drawCircle(sealCenterX, sealCenterY, sealRadius - 4f, sealStrokePaint)

        // Seal text
        val sealTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = deepNavy
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        sealTextPaint.textSize = 5.2f
        canvas.drawText("CERTIFIED", sealCenterX, sealCenterY - 12f, sealTextPaint)
        sealTextPaint.textSize = 9.5f
        canvas.drawText("ISO", sealCenterX, sealCenterY - 2f, sealTextPaint)
        sealTextPaint.textSize = 6.2f
        canvas.drawText("9001:2015", sealCenterX, sealCenterY + 6.5f, sealTextPaint)
        sealTextPaint.textSize = 5.2f
        canvas.drawText("COMPANY", sealCenterX, sealCenterY + 14f, sealTextPaint)

        // ── 3. MAIN HEADER: "NIRMALA DEVI CARE" ──
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = primaryBlue
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("NIRMALA DEVI CARE", pageWidth / 2f, 102f, titlePaint)

        // Sub-row: "PRODUCTS" on left in orange, "PRIVATE LIMITED" in cyan/sky blue
        val productsTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = accentOrange
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText("PRODUCTS", 40f, 134f, productsTitlePaint)

        val pvtLtdPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = primaryBlue
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("PRIVATE LIMITED", pageWidth - 36f, 134f, pvtLtdPaint)

        // ── 4. TWO-COLUMN PRODUCT LIST ──
        val bulletPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = darkText
            style = Paint.Style.FILL
        }

        val productPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = darkText
            textSize = 10.2f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val subProductPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = darkText
            textSize = 9.2f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        // Left Column: 16 Products
        val leftX = 40f
        var currentY = 168f
        val lineSpacing = 31.5f

        ProductCatalogueData.LEFT_COLUMN.forEach { item ->
            // Draw bullet point
            canvas.drawCircle(leftX + 4f, currentY - 3.5f, 2.8f, bulletPaint)

            if (item.id == "dm-water") {
                // Two lines: "D.M.WATER" and "DISTILLED WATER"
                canvas.drawText("D.M.WATER", leftX + 16f, currentY, productPaint)
                canvas.drawText("DISTILLED WATER", leftX + 16f, currentY + 12f, subProductPaint)
                currentY += (lineSpacing + 2f)
            } else {
                canvas.drawText(item.name, leftX + 16f, currentY, productPaint)
                currentY += lineSpacing
            }
        }

        // Right Column: 8 Products
        val rightX = 300f
        var rightY = 168f
        val rightLineSpacing = 32.5f

        ProductCatalogueData.RIGHT_COLUMN.forEach { item ->
            canvas.drawCircle(rightX + 4f, rightY - 3.5f, 2.8f, bulletPaint)

            if (item.id == "poly-aluminium-chloride") {
                // Two lines: "POLY ALUMINIUM CHLORIDE" and "(PAC POWDER)"
                canvas.drawText("POLY ALUMINIUM CHLORIDE", rightX + 16f, rightY, productPaint)
                canvas.drawText("(PAC POWDER)", rightX + 16f, rightY + 12f, subProductPaint)
                rightY += (rightLineSpacing + 2f)
            } else {
                canvas.drawText(item.name, rightX + 16f, rightY, productPaint)
                rightY += rightLineSpacing
            }
        }

        // ── 5. FOOTER DIVISIONS BOX (3 COLUMNS) ──
        val divisionTopY = 690f
        val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            strokeWidth = 2.4f
        }

        // Divider 1 & Divider 2
        canvas.drawLine(195f, divisionTopY + 5f, 195f, divisionTopY + 70f, dividerPaint)
        canvas.drawLine(380f, divisionTopY + 5f, 380f, divisionTopY + 70f, dividerPaint)

        val divTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = darkText
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val divAddressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = darkText
            textSize = 7.0f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
        }

        // Column 1: Postal Address
        val col1CenterX = 114f
        canvas.drawText("Postal Address", col1CenterX, divisionTopY + 12f, divTitlePaint)
        canvas.drawText("Plot No. 3, Survey No. 1300p, Opp.", col1CenterX, divisionTopY + 26f, divAddressPaint)
        canvas.drawText("Ganga Steel, Panchasar Road,", col1CenterX, divisionTopY + 37f, divAddressPaint)
        canvas.drawText("Morbi, 363641", col1CenterX, divisionTopY + 48f, divAddressPaint)

        // Column 2: Pipaliya Char Rasta Division
        val col2CenterX = 287f
        canvas.drawText("Divison : Pipaliya Char Rasta", col2CenterX, divisionTopY + 12f, divTitlePaint)
        canvas.drawText("Survey No.283, Chanchavadarda Ta-", col2CenterX, divisionTopY + 26f, divAddressPaint)
        canvas.drawText("Maliya(mi) Near Kodal Uniquoters Pvt. Ltd,", col2CenterX, divisionTopY + 37f, divAddressPaint)
        canvas.drawText("Chanchavadarda, Morbi, Gujarat, 363660", col2CenterX, divisionTopY + 48f, divAddressPaint)

        // Column 3: Rajpar Road Division
        val col3CenterX = 475f
        canvas.drawText("Divison : Rajpar Road", col3CenterX, divisionTopY + 12f, divTitlePaint)
        canvas.drawText("Survey No. 21, Plot No. 4, Indian", col3CenterX, divisionTopY + 26f, divAddressPaint)
        canvas.drawText("Land, Rajpar Road, Rajpar, Morbi,", col3CenterX, divisionTopY + 37f, divAddressPaint)
        canvas.drawText("Gujarat, 363641", col3CenterX, divisionTopY + 48f, divAddressPaint)

        // ── 6. BOTTOM CONTACT BAR ──
        val contactLineY = 788f
        val contactDividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = lightGray
            strokeWidth = 1f
        }
        canvas.drawLine(30f, contactLineY - 16f, pageWidth - 30f, contactLineY - 16f, contactDividerPaint)

        val contactPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = darkText
            textSize = 7.8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val contactString = "${ProductCatalogueData.WEBSITE}    |    ${ProductCatalogueData.EMAIL}    |    ${ProductCatalogueData.PHONES}"
        canvas.drawText(contactString, pageWidth / 2f, contactLineY, contactPaint)

        // Finish Page
        document.finishPage(page)

        // Write to destination file
        FileOutputStream(destinationFile).use { out ->
            document.writeTo(out)
        }
        document.close()

        return destinationFile
    }

    /**
     * Downloads the PDF file directly to the user's mobile device Downloads folder.
     */
    fun downloadPdfToDevice(context: Context): DownloadResult {
        return try {
            val sourceFile = getOrCreatePdfFile(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, FILE_NAME)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: return fallbackInternalCopy(context, sourceFile)

                resolver.openOutputStream(uri)?.use { outputStream ->
                    sourceFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                contentValues.clear()
                contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)

                DownloadResult.Success(
                    uri = uri,
                    file = sourceFile,
                    message = "Product Catalogue PDF downloaded successfully to device Downloads!",
                    filePath = "Downloads/$FILE_NAME"
                )
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                downloadsDir.mkdirs()
                val targetFile = File(downloadsDir, FILE_NAME)
                sourceFile.copyTo(targetFile, overwrite = true)

                // Notify media scanner
                val uri = Uri.fromFile(targetFile)
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(targetFile.absolutePath),
                    arrayOf("application/pdf"),
                    null
                )

                DownloadResult.Success(
                    uri = uri,
                    file = targetFile,
                    message = "Product Catalogue PDF downloaded successfully to device Downloads!",
                    filePath = targetFile.absolutePath
                )
            }
        } catch (e: Exception) {
            val sourceFile = getOrCreatePdfFile(context)
            fallbackInternalCopy(context, sourceFile)
        }
    }

    private fun fallbackInternalCopy(context: Context, sourceFile: File): DownloadResult {
        return try {
            val authority = "${context.packageName}.provider"
            val uri = FileProvider.getUriForFile(context, authority, sourceFile)
            DownloadResult.Success(
                uri = uri,
                file = sourceFile,
                message = "Catalogue PDF generated and ready on your device!",
                filePath = sourceFile.name
            )
        } catch (e: Exception) {
            DownloadResult.Error("Could not save PDF: ${e.message}")
        }
    }

    /**
     * Opens the PDF with any external viewer installed on the user's mobile device.
     */
    fun openPdf(context: Context, file: File = getOrCreatePdfFile(context)) {
        try {
            val authority = "${context.packageName}.provider"
            val uri = FileProvider.getUriForFile(context, authority, file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Open Product Catalogue PDF"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No PDF viewer app found on device. You can share via WhatsApp.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shares the PDF file across apps (WhatsApp, Gmail, Drive, etc.).
     */
    fun sharePdf(context: Context, file: File = getOrCreatePdfFile(context)) {
        try {
            val authority = "${context.packageName}.provider"
            val uri = FileProvider.getUriForFile(context, authority, file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Nirmaladevi Care - Official Product Catalogue")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Official Product Catalogue of Nirmaladevi Care Pvt. Ltd. (ISO 9001:2015 Certified) - Industrial Chemical Trading & Supply, Morbi, Gujarat.\n\nWebsite: ${ProductCatalogueData.WEBSITE}\nContact: ${ProductCatalogueData.PRIMARY_PHONE}"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Product Catalogue PDF"))
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to share PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Direct WhatsApp share with PDF and formatted message.
     */
    fun shareOnWhatsApp(context: Context, file: File = getOrCreatePdfFile(context)) {
        try {
            val authority = "${context.packageName}.provider"
            val uri = FileProvider.getUriForFile(context, authority, file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, ProductCatalogueData.getCatalogueShareMessage())
                setPackage("com.whatsapp")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to regular chooser
            sharePdf(context, file)
        }
    }
}
