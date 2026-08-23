package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChemicalUiState

@Composable
fun ToolsScreen(
    uiState: ChemicalUiState,
    onCalculateDilution: (stock: String, target: String, volume: String) -> Unit,
    onCalculateMassVolume: (volume: String, spGr: String) -> Unit
) {
    var stockConc by remember { mutableStateOf(uiState.dilutionState.stockConcentration) }
    var targetConc by remember { mutableStateOf(uiState.dilutionState.targetConcentration) }
    var targetVol by remember { mutableStateOf(uiState.dilutionState.targetVolume) }

    var tankerVol by remember { mutableStateOf(uiState.massVolumeState.volumeLiters) }
    var spGravity by remember { mutableStateOf(uiState.massVolumeState.specificGravity) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandInk),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(bottom = 12.dp)) {
                Text(
                    text = "CHEMIST & FACTORY UTILITIES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        fontSize = 11.sp
                    ),
                    color = BrandAcid
                )
                Text(
                    text = "Chemical Tools & Safety",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = "Essential calculators for plant engineers, factory mixers, and chemical storage safety guidelines.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // ── TOOL 1: DILUTION CALCULATOR ──
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BrandSteel),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BrandAcid),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = BrandInk,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Solution Dilution Calculator",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Formula: C₁ × V₁ = C₂ × V₂",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                color = BrandAcid
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Preset Quick Chips
                    Text(
                        text = "Quick Presets:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val presets = listOf(
                            Triple("H₂SO₄ (98% → 10%)", "98", "10"),
                            Triple("HCl (33% → 5%)", "33", "5"),
                            Triple("HNO₃ (68% → 15%)", "68", "15"),
                            Triple("H₂O₂ (50% → 6%)", "50", "6"),
                            Triple("Caustic Lye (48% → 10%)", "48", "10")
                        )
                        items(presets) { (title, stock, target) ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BrandSteelLight,
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, BrandBorderDark),
                                modifier = Modifier.clickable {
                                    stockConc = stock
                                    targetConc = target
                                    onCalculateDilution(stock, target, targetVol)
                                }
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = BrandAcid,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = stockConc,
                            onValueChange = {
                                stockConc = it
                                onCalculateDilution(it, targetConc, targetVol)
                            },
                            label = { Text("Stock Conc. (C₁ %)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("stock_conc_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BrandAcid,
                                unfocusedBorderColor = BrandBorderDark
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = targetConc,
                            onValueChange = {
                                targetConc = it
                                onCalculateDilution(stockConc, it, targetVol)
                            },
                            label = { Text("Target Conc. (C₂ %)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("target_conc_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BrandAcid,
                                unfocusedBorderColor = BrandBorderDark
                            ),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = targetVol,
                        onValueChange = {
                            targetVol = it
                            onCalculateDilution(stockConc, targetConc, it)
                        },
                        label = { Text("Total Required Volume (V₂ in Liters)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("target_vol_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = BrandAcid,
                            unfocusedBorderColor = BrandBorderDark
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Result Box
                    uiState.dilutionState.let { state ->
                        if (state.calculationError != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DangerRed.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = state.calculationError,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DangerRed,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        } else if (state.calculatedStockRequired != null && state.calculatedWaterRequired != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BrandSteelLight,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BrandAcid.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "CALCULATION RESULT:",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        ),
                                        color = BrandAcid
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Stock Chemical Required:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                        Text(
                                            text = "${state.calculatedStockRequired} Liters",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = BrandAcid
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "D.M. Water Required:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                        Text(
                                            text = "${state.calculatedWaterRequired} Liters",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = BrandAzureLight
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Safety Rule
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DangerRed.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, DangerRed.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = DangerRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CRITICAL SAFETY: Always add acid SLOWLY to water with constant stirring. NEVER pour water into concentrated acid!",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = DangerRed
                            )
                        }
                    }
                }
            }
        }

        // ── TOOL 2: BULK TANKER VOLUME TO MASS CONVERTER ──
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BrandSteel),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BrandAzure),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Scale,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Tanker / Weight Converter",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Mass (Kg / MT) = Volume (L) × Specific Gravity",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                color = BrandAzureLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Preset Sp. Gr. Chips
                    Text(
                        text = "Specific Gravity Presets:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val densities = listOf(
                            Pair("Sulphuric (1.84)", "1.84"),
                            Pair("Phosphoric (1.68)", "1.68"),
                            Pair("Caustic Lye (1.52)", "1.52"),
                            Pair("Nitric (1.41)", "1.41"),
                            Pair("Sodium Silicate (1.45)", "1.45"),
                            Pair("Battery Acid (1.28)", "1.28"),
                            Pair("Hydrogen Peroxide (1.20)", "1.20"),
                            Pair("HCl (1.16)", "1.16"),
                            Pair("Water (1.00)", "1.00")
                        )
                        items(densities) { (name, sp) ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BrandSteelLight,
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, BrandBorderDark),
                                modifier = Modifier.clickable {
                                    spGravity = sp
                                    onCalculateMassVolume(tankerVol, sp)
                                }
                            ) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = BrandAzureLight,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = tankerVol,
                            onValueChange = {
                                tankerVol = it
                                onCalculateMassVolume(it, spGravity)
                            },
                            label = { Text("Volume (Liters)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("tanker_vol_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BrandAzureLight,
                                unfocusedBorderColor = BrandBorderDark
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = spGravity,
                            onValueChange = {
                                spGravity = it
                                onCalculateMassVolume(tankerVol, it)
                            },
                            label = { Text("Specific Gravity") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("sp_gr_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BrandAzureLight,
                                unfocusedBorderColor = BrandBorderDark
                            ),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    uiState.massVolumeState.let { mv ->
                        if (mv.calculatedMassKg != null && mv.calculatedMassMT != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BrandSteelLight,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BrandAzureLight.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Total Mass in Kilograms:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                        Text(
                                            text = "${mv.calculatedMassKg} kg",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Metric Tons (MT):",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                        Text(
                                            text = "${mv.calculatedMassMT} MT",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = BrandAcid
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── TOOL 3: SAFETY & STORAGE MATRIX ──
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BrandSteel),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = BrandAcid,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Industrial Chemical Safety Rules",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SafetyRuleItem(
                        icon = "⚠️",
                        title = "Segregated Storage",
                        desc = "Keep concentrated Acids (HCl, Sulphuric, Nitric) strictly separated from Alkalis (Caustic Soda, Ammonia) to prevent explosive exothermic reactions."
                    )
                    Divider(color = BrandBorderDark, modifier = Modifier.padding(vertical = 8.dp))
                    SafetyRuleItem(
                        icon = "🥽",
                        title = "Mandatory PPE",
                        desc = "Chemical safety goggles, neoprene/PVC acid-proof gloves, rubber aprons, and respiratory masks must be worn during unloading & handling."
                    )
                    Divider(color = BrandBorderDark, modifier = Modifier.padding(vertical = 8.dp))
                    SafetyRuleItem(
                        icon = "💧",
                        title = "Spillage Neutralization",
                        desc = "For Acid spills: Neutralize with Sodium Bicarbonate or Lime before rinsing. For Alkali/Caustic spills: Neutralize with weak vinegar / dilute acid."
                    )
                }
            }
        }
    }
}

@Composable
fun SafetyRuleItem(icon: String, title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(text = icon, fontSize = 18.sp, modifier = Modifier.padding(top = 2.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}
