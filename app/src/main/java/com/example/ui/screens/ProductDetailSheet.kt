package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChemicalProduct
import com.example.ui.components.FormulaBadge
import com.example.ui.components.HazardBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailSheet(
    product: ChemicalProduct,
    onDismiss: () -> Unit,
    onAddToQuote: (
        product: ChemicalProduct,
        grade: String,
        quantity: Int,
        unit: String,
        isWholesale: Boolean,
        remarks: String
    ) -> Unit,
    onDirectWhatsApp: (String) -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }
    var isWholesale by remember { mutableStateOf(true) }
    var selectedGrade by remember { mutableStateOf(product.availableGrades.firstOrNull() ?: "Standard Grade") }
    var selectedPackaging by remember { mutableStateOf(product.packagingOptions.firstOrNull() ?: "Standard Package") }
    var remarks by remember { mutableStateOf("") }
    var showTechnicalDetails by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = WhiteSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(TextMuted.copy(alpha = 0.35f))
            )
        },
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
                .testTag("product_detail_sheet")
        ) {
            // ── TOP BAR: TITLE & CLOSE ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CUSTOMIZE QUOTATION / INQUIRY",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        ),
                        color = AccentBlueDark
                    )
                    Text(
                        text = "Nirmaladevi Care Chemical Desk",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextMuted
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── 1. PRODUCT NAME (SHOW ONLY PRODUCT NAME) ──
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteSurfaceVariant),
                border = BorderStroke(1.dp, WhiteBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("inquiry_product_name_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.tag.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            ),
                            color = AccentBlueDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 19.sp
                            ),
                            color = TextPrimary
                        )
                    }

                    FormulaBadge(
                        formula = product.formula,
                        backgroundColor = AccentBlueSoft,
                        textColor = AccentBlueDark,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── 2. SUPPLY TYPE (RAHNE DO / KEEP) ──
            Text(
                text = "Supply Type:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(WhiteSurfaceVariant)
                    .border(1.dp, WhiteBorder, RoundedCornerShape(8.dp))
                    .testTag("inquiry_supply_type_toggle")
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isWholesale = true }
                        .background(if (isWholesale) AccentBlue else Color.Transparent)
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = if (isWholesale) Color.White else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Wholesale",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = if (isWholesale) Color.White else TextSecondary
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isWholesale = false }
                        .background(if (!isWholesale) AccentBlue else Color.Transparent)
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = if (!isWholesale) Color.White else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Retail",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = if (!isWholesale) Color.White else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── 3. QUANTITY (RAHNE DO / KEEP) ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quantity:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = "$quantity units",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AccentBlueDark
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(WhiteSurfaceVariant)
                    .border(1.dp, WhiteBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { if (quantity > 1) quantity-- },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease Quantity",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    ),
                    color = AccentBlueDark
                )

                IconButton(
                    onClick = { quantity++ },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase Quantity",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Preset Chips for Quantity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(1, 5, 10, 25, 50, 100).forEach { preset ->
                    val isCurrent = quantity == preset
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isCurrent) AccentBlueSoft else WhiteSurface,
                        border = BorderStroke(1.dp, if (isCurrent) AccentBlue else WhiteBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { quantity = preset }
                    ) {
                        Text(
                            text = preset.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            ),
                            color = if (isCurrent) AccentBlueDark else TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            // ── PACKAGING / PACK SIZE SELECTION ──
            if (product.packagingOptions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Pack Size / Packaging:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    product.packagingOptions.chunked(2).forEach { rowOptions ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowOptions.forEach { pkg ->
                                val isSelected = selectedPackaging == pkg
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) AccentBlueSoft else WhiteSurfaceVariant,
                                    border = BorderStroke(1.dp, if (isSelected) AccentBlue else WhiteBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedPackaging = pkg }
                                ) {
                                    Text(
                                        text = pkg,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp
                                        ),
                                        color = if (isSelected) AccentBlueDark else TextSecondary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                        maxLines = 1
                                    )
                                }
                            }
                            if (rowOptions.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // ── GRADE / PURITY SELECTION ──
            if (product.availableGrades.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Grade / Purity:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    product.availableGrades.forEach { grade ->
                        val isSelected = selectedGrade == grade
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) AccentBlueSoft else WhiteSurfaceVariant,
                            border = BorderStroke(1.dp, if (isSelected) AccentBlue else WhiteBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedGrade = grade }
                        ) {
                            Text(
                                text = grade,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) AccentBlueDark else TextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── 4. NOTES (ADD OPTION TO NOTES JIS ME LIKH SAKE) ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notes / Requirements:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = "Optional",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = remarks,
                onValueChange = { remarks = it },
                placeholder = {
                    Text(
                        text = "Write your notes here (e.g. 50 kg bags, Technical Grade, urgent delivery by Monday, COA certificate)...",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = TextPlaceholder
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("inquiry_notes_input"),
                shape = RoundedCornerShape(8.dp),
                minLines = 3,
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = WhiteSurface,
                    unfocusedContainerColor = WhiteSurface,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = WhiteBorder
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ── ACTION BUTTONS: INSTANT WHATSAPP & ADD TO INQUIRY ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val supplyStr = if (isWholesale) "Wholesale / Bulk" else "Retail"
                        val quickMsg = buildString {
                            append("━━━━━━━━━━━━━━━━━━━━\n")
                            append("🧪 *NIRMALADEVI CARE PVT. LTD.*\n")
                            append("*Quick Chemical Inquiry*\n")
                            append("━━━━━━━━━━━━━━━━━━━━\n\n")
                            append("Hello Nirmaladevi Care Team,\n")
                            append("I am inquiring about the following chemical product:\n\n")
                            append("📦 *PRODUCT DETAILS:*\n")
                            append("• Chemical: *${product.name}* (${product.formula})\n")
                            append("• Grade / Purity: $selectedGrade\n")
                            append("• Supply Mode: $supplyStr\n")
                            append("• Quantity: *$quantity Unit(s)*\n")
                            append("• Pack Size: *$selectedPackaging*\n")
                            if (remarks.isNotBlank()) {
                                append("• Note: ${remarks.trim()}\n")
                            }
                            append("\n────────────────────\n")
                            append("Please share best quotation, delivery schedule, and material availability.\n\n")
                            append("Thank you!\n")
                            append("━━━━━━━━━━━━━━━━━━━━")
                        }
                        onDirectWhatsApp(quickMsg)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("inquiry_whatsapp_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WhatsAppGreenDark),
                    border = BorderStroke(1.dp, WhatsAppGreenDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Instant WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        onAddToQuote(product, selectedGrade, quantity, selectedPackaging, isWholesale, remarks)
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("add_to_quote_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add to Inquiry", fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = WhiteBorder)
            Spacer(modifier = Modifier.height(8.dp))

            // ── OPTIONAL EXPANDABLE: VIEW TECHNICAL SPECS & APPLICATIONS ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { showTechnicalDetails = !showTechnicalDetails }
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = AccentBlueDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Technical Specs & Applications",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = AccentBlueDark
                    )
                }
                Icon(
                    imageVector = if (showTechnicalDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (showTechnicalDetails) "Collapse" else "Expand",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(visible = showTechnicalDetails) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    HazardBadge(hazard = product.hazardType)

                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = WhiteSurfaceVariant),
                        border = BorderStroke(1.dp, WhiteBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            DetailRow(label = "Standard Purity", value = product.purity)
                            HorizontalDivider(color = WhiteBorder, modifier = Modifier.padding(vertical = 6.dp))
                            DetailRow(label = "Specific Gravity / Density", value = product.densityOrSpGr)
                            HorizontalDivider(color = WhiteBorder, modifier = Modifier.padding(vertical = 6.dp))
                            DetailRow(label = "Supply Mode", value = "Bulk Tankers / Carboys / Retail Packs")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "ABOUT & INDUSTRIAL USE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp
                        ),
                        color = AccentBlueDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 18.sp),
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "PRIMARY APPLICATIONS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp
                        ),
                        color = AccentBlueDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        product.applications.forEach { app ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(AccentBlue)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = app,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
    }
}
