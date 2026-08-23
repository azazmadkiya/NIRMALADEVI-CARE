package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerInquiry
import com.example.data.model.QuoteItem
import com.example.ui.components.FormulaBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChemicalUiState

@Composable
fun QuoteScreen(
    uiState: ChemicalUiState,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onClearAll: () -> Unit,
    onUpdateCustomerInfo: (name: String, phone: String, location: String, urgent: Boolean, notes: String) -> Unit,
    onSendWhatsApp: (String) -> Unit,
    onSendEmail: (subject: String, body: String) -> Unit,
    onCallClick: () -> Unit,
    onNavigateToCatalogue: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var name by remember(uiState.customerName) { mutableStateOf(uiState.customerName) }
    var phone by remember(uiState.customerPhone) { mutableStateOf(uiState.customerPhone) }
    var location by remember(uiState.deliveryLocation) { mutableStateOf(uiState.deliveryLocation) }
    var urgent by remember(uiState.isUrgent) { mutableStateOf(uiState.isUrgent) }
    var notes by remember(uiState.notes) { mutableStateOf(uiState.notes) }

    fun syncCustomer() {
        onUpdateCustomerInfo(name, phone, location, urgent, notes)
    }

    val inquiry = remember(name, phone, location, urgent, notes, uiState.quoteItems) {
        CustomerInquiry(
            companyOrName = name,
            phone = phone,
            location = location,
            items = uiState.quoteItems,
            urgentDelivery = urgent,
            additionalNotes = notes
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandInk),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.padding(bottom = 12.dp)) {
                Text(
                    text = "QUOTATION & INQUIRY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        fontSize = 11.sp
                    ),
                    color = BrandAcid
                )
                Text(
                    text = "Request Chemical Quote",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = "Select required industrial chemicals, specify grades & quantities, and directly dispatch to Nirmaladevi Care sales desk.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        if (uiState.quoteItems.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandSteel),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorderDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCartCheckout,
                            contentDescription = null,
                            tint = BrandAcid,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Your Inquiry List is Empty",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Browse our 24+ chemical products catalogue (Acids, Alkalis, Salts, Water Treatment) and add items to your quote.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToCatalogue,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandAcid,
                                contentColor = BrandInk
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Browse Catalogue", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        } else {
            // Quote items list
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Selected Items (${uiState.quoteItems.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    TextButton(
                        onClick = onClearAll,
                        colors = ButtonDefaults.textButtonColors(contentColor = DangerRed)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear All", fontSize = 12.sp)
                    }
                }
            }

            items(uiState.quoteItems, key = { it.id }) { item ->
                QuoteItemCard(
                    item = item,
                    onIncrease = { onUpdateQuantity(item.id, item.quantity + 1) },
                    onDecrease = { onUpdateQuantity(item.id, item.quantity - 1) },
                    onRemove = { onRemoveItem(item.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                // Customer details form
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandSteel),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorderDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "BUYER & DELIVERY DETAILS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 11.sp
                            ),
                            color = BrandAcid
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                syncCustomer()
                            },
                            label = { Text("Company Name / Contact Person") },
                            placeholder = { Text("e.g. Morbi Ceramics Ltd. / Amit Patel") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("inquiry_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BrandAcid,
                                unfocusedBorderColor = BrandBorderDark,
                                focusedLabelColor = BrandAcid,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                phone = it
                                syncCustomer()
                            },
                            label = { Text("Phone / WhatsApp Number") },
                            placeholder = { Text("+91 98765 43210") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("inquiry_phone_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BrandAcid,
                                unfocusedBorderColor = BrandBorderDark,
                                focusedLabelColor = BrandAcid,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = location,
                            onValueChange = {
                                location = it
                                syncCustomer()
                            },
                            label = { Text("Delivery Destination / Factory Location") },
                            placeholder = { Text("Panchasar Road, Morbi, Gujarat") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("inquiry_location_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BrandAcid,
                                unfocusedBorderColor = BrandBorderDark,
                                focusedLabelColor = BrandAcid,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    urgent = !urgent
                                    syncCustomer()
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = urgent,
                                onCheckedChange = {
                                    urgent = it
                                    syncCustomer()
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = BrandAcid,
                                    checkmarkColor = BrandInk,
                                    uncheckedColor = Color.White.copy(alpha = 0.6f)
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Urgent Delivery Required (Ready stock priority)",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (urgent) BrandAcid else Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = notes,
                            onValueChange = {
                                notes = it
                                syncCustomer()
                            },
                            label = { Text("Special Requirements / COA / Test Certificate") },
                            placeholder = { Text("e.g., Require COA certificate, specific pH/concentration, delivery by Friday") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BrandAcid,
                                unfocusedBorderColor = BrandBorderDark,
                                focusedLabelColor = BrandAcid,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                            ),
                            maxLines = 3
                        )
                    }
                }
            }

            // Quick Message Preview & One-tap Dispatch
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandSteelLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandAcid.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SEND INQUIRY TO SALES DESK",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontSize = 11.sp
                                ),
                                color = BrandAcid
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(inquiry.toWhatsAppMessage()))
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy text",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Big WhatsApp Primary Action
                        Button(
                            onClick = { onSendWhatsApp(inquiry.toWhatsAppMessage()) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("send_whatsapp_inquiry_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WhatsAppGreen,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SEND VIA WHATSAPP (+91 82003 32632)",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Email & Direct Call Options
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onSendEmail(
                                        inquiry.toEmailSubject(),
                                        inquiry.toWhatsAppMessage()
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("send_email_inquiry_button"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = BrandAzureLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Email Quote", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = onCallClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("call_desk_quote_button"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandAcid),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BrandAcid),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Call Desk", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuoteItemCard(
    item: QuoteItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = BrandSteel),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorderDark),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.product.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    FormulaBadge(
                        formula = item.product.formula,
                        backgroundColor = BrandSteelLight,
                        textColor = BrandAcid
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${item.selectedGrade} · ${if (item.isWholesale) "Wholesale" else "Retail"} · ${item.unit}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Color.White.copy(alpha = 0.6f)
                )

                if (item.remarks.isNotBlank()) {
                    Text(
                        text = "Note: ${item.remarks}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = BrandAcid.copy(alpha = 0.8f)
                    )
                }
            }

            // Stepper
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(BrandSteelLight)
                    .border(1.dp, BrandBorderDark, RoundedCornerShape(6.dp))
            ) {
                IconButton(onClick = onDecrease, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (item.quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = if (item.quantity == 1) DangerRed else Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = item.quantity.toString(),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = BrandAcid,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
                IconButton(onClick = onIncrease, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
