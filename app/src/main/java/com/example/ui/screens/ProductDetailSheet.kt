package com.example.ui.screens

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
    var selectedGrade by remember(product) { mutableStateOf(product.availableGrades.firstOrNull() ?: "Standard") }
    var selectedUnit by remember(product) { mutableStateOf(product.packagingOptions.firstOrNull() ?: "Carboy") }
    var quantity by remember { mutableIntStateOf(1) }
    var isWholesale by remember { mutableStateOf(true) }
    var remarks by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BrandSteel,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.3f))
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
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.tag.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        ),
                        color = BrandAzureLight
                    )
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp
                        ),
                        color = Color.White
                    )
                }

                FormulaBadge(
                    formula = product.formula,
                    backgroundColor = BrandAcid,
                    textColor = BrandInk,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            HazardBadge(hazard = product.hazardType)

            Spacer(modifier = Modifier.height(14.dp))

            // Specs Table Card
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = BrandSteelLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    DetailRow(label = "Standard Purity", value = product.purity)
                    Divider(color = BrandBorderDark, modifier = Modifier.padding(vertical = 6.dp))
                    DetailRow(label = "Specific Gravity / Density", value = product.densityOrSpGr)
                    Divider(color = BrandBorderDark, modifier = Modifier.padding(vertical = 6.dp))
                    DetailRow(label = "Supply Mode", value = "Bulk Tankers / Carboys / Retail Packs")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Description
            Text(
                text = "ABOUT & INDUSTRIAL USE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                ),
                color = BrandAcid
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 18.sp),
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Key Applications Chips
            Text(
                text = "PRIMARY APPLICATIONS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                ),
                color = BrandAcid
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                product.applications.forEach { app ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(BrandAcid)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = app,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Divider(color = BrandBorderDark)
            Spacer(modifier = Modifier.height(14.dp))

            // ── QUOTE INQUIRY CONFIGURATOR ──
            Text(
                text = "CUSTOMIZE QUOTATION / INQUIRY",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Grade Selection
            Text(
                text = "Select Grade:",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                product.availableGrades.forEach { grade ->
                    val isSelected = selectedGrade == grade
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) BrandAcid else BrandSteelLight,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) BrandAcid else BrandBorderDark
                        ),
                        modifier = Modifier
                            .clickable { selectedGrade = grade }
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = grade,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            ),
                            color = if (isSelected) BrandInk else Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Packaging & Supply Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Wholesale / Retail toggle
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Supply Type:",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(BrandSteelLight)
                            .border(1.dp, BrandBorderDark, RoundedCornerShape(6.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isWholesale = true }
                                .background(if (isWholesale) BrandAcid else Color.Transparent)
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Wholesale",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = if (isWholesale) BrandInk else Color.White
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isWholesale = false }
                                .background(if (!isWholesale) BrandAcid else Color.Transparent)
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Retail",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = if (!isWholesale) BrandInk else Color.White
                            )
                        }
                    }
                }

                // Quantity Stepper
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Quantity:",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(BrandSteelLight)
                            .border(1.dp, BrandBorderDark, RoundedCornerShape(6.dp)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = BrandAcid
                        )
                        IconButton(
                            onClick = { quantity++ },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Packaging Units
            Text(
                text = "Packaging Unit:",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                product.packagingOptions.forEach { opt ->
                    val isSelected = selectedUnit == opt
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) BrandAzure else BrandSteelLight,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) BrandAzureLight else BrandBorderDark
                        ),
                        modifier = Modifier.clickable { selectedUnit = opt }
                    ) {
                        Text(
                            text = opt,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp
                            ),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val quickMsg = "Hello Nirmaladevi Care, I need quote for ${product.name} (${product.formula}) - $selectedGrade, Quantity: $quantity $selectedUnit."
                        onDirectWhatsApp(quickMsg)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WhatsAppGreen),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WhatsAppGreen),
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
                        onAddToQuote(product, selectedGrade, quantity, selectedUnit, isWholesale, remarks)
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_to_quote_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandAcid,
                        contentColor = BrandInk
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
            color = Color.White.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
    }
}
