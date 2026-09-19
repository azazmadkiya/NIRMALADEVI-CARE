package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.ShoppingCart
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CompanyInfo
import com.example.data.model.CustomerInquiry
import com.example.data.model.QuoteItem
import com.example.ui.components.FormulaBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChemicalUiState
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuoteScreen(
    uiState: ChemicalUiState,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onClearAll: () -> Unit,
    onUpdateCustomerInfo: (name: String, company: String, phone: String, location: String, urgent: Boolean, notes: String) -> Unit,
    onSendWhatsApp: (String) -> Unit,
    onSendEmail: (subject: String, body: String) -> Unit,
    onCallClick: () -> Unit,
    onNavigateToCatalogue: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var name by remember(uiState.customerName) { mutableStateOf(uiState.customerName) }
    var company by remember(uiState.companyName) { mutableStateOf(uiState.companyName) }
    var phone by remember(uiState.customerPhone) { mutableStateOf(uiState.customerPhone) }
    var location by remember(uiState.deliveryLocation) { mutableStateOf(uiState.deliveryLocation) }
    var urgent by remember(uiState.isUrgent) { mutableStateOf(uiState.isUrgent) }
    var notes by remember(uiState.notes) { mutableStateOf(uiState.notes) }

    var validationError by remember { mutableStateOf<String?>(null) }
    var fieldErrors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }

    var editingItemForQuantity by remember { mutableStateOf<QuoteItem?>(null) }
    var submittedInquiry by remember { mutableStateOf<CustomerInquiry?>(null) }
    var inquiryRefNumber by remember { mutableStateOf("") }
    var showClearConfirmation by remember { mutableStateOf(false) }

    fun syncCustomer() {
        onUpdateCustomerInfo(name, company, phone, location, urgent, notes)
    }

    val currentInquiry = remember(name, company, phone, location, urgent, notes, uiState.quoteItems) {
        CustomerInquiry(
            contactName = name,
            companyName = company,
            companyOrName = if (company.isNotBlank()) company else name,
            phone = phone,
            location = location,
            items = uiState.quoteItems,
            urgentDelivery = urgent,
            additionalNotes = notes
        )
    }

    // Helper to validate and return whether form is valid
    fun validateForm(): Boolean {
        hasAttemptedSubmit = true
        val result = currentInquiry.validate()
        fieldErrors = result.fieldErrors
        validationError = if (!result.isValid) {
            result.firstErrorMessage ?: "Please correct the highlighted errors before submitting."
        } else {
            null
        }
        return result.isValid
    }


    val totalItemsCount = uiState.quoteItems.size
    val totalUnitsCount = uiState.quoteItems.sumOf { it.quantity }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteCanvas)
            .testTag("inquiry_quote_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp)
    ) {
        // ── Header ──
        item {
            Column(modifier = Modifier.padding(bottom = 14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = AccentBlueSoft,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "INQUIRY & QUOTATION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp,
                                fontSize = 10.sp
                            ),
                            color = AccentBlueDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (totalItemsCount > 0) {
                        Surface(
                            color = AccentTealSoft,
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, AccentTeal.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "$totalItemsCount Products · $totalUnitsCount Units",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = AccentTealDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Chemical Inquiry Request",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp
                    ),
                    color = TextPrimary
                )

                Text(
                    text = "Review your selected products, fill in your contact details (Name, Company, Quantity), and submit directly to Nirmaladevi Care.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // ── Empty State ──
        if (uiState.quoteItems.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                    border = BorderStroke(1.dp, WhiteBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                        .testTag("empty_inquiry_card")
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(AccentBlueSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingCart,
                                contentDescription = null,
                                tint = AccentBlueDark,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No Chemicals Selected for Inquiry",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Browse our chemical catalogue (Acids, Alkalis, Salts, Water Treatment) and add required items with your custom packaging units.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onNavigateToCatalogue,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentBlue,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("browse_catalogue_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Browse Catalogue", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        } else {
            // ── Section 1: Selected Products for Inquiry ──
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteSurfaceVariant),
                    border = BorderStroke(1.dp, WhiteBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "1. SELECTED PRODUCTS (${totalItemsCount})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontSize = 11.sp
                                ),
                                color = AccentBlueDark
                            )
                            Text(
                                text = "Total Quantity: $totalUnitsCount Units across all items",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TextSecondary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onNavigateToCatalogue,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("add_more_products_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddCircleOutline,
                                    contentDescription = "Add More Products",
                                    tint = AccentBlueDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = { showClearConfirmation = true },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("clear_all_quote_items_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteOutline,
                                    contentDescription = "Clear All",
                                    tint = AccentRed.copy(alpha = 0.85f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // List of selected quote items
            items(uiState.quoteItems, key = { it.id }) { item ->
                SelectedProductInquiryCard(
                    item = item,
                    onIncrease = { onUpdateQuantity(item.id, item.quantity + 1) },
                    onDecrease = { onUpdateQuantity(item.id, item.quantity - 1) },
                    onEditQuantityClick = { editingItemForQuantity = item },
                    onRemove = { onRemoveItem(item.id) }
                )
            }

            // ── Section 2: Contact & Company Details Form ──
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                    border = BorderStroke(1.dp, WhiteBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("inquiry_contact_details_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContactPhone,
                                contentDescription = null,
                                tint = AccentBlueDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "2. CONTACT & BUYER DETAILS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontSize = 11.sp
                                ),
                                color = AccentBlueDark
                            )
                        }

                        Text(
                            text = "Fill in your contact information below to receive official pricing, delivery timelines, and test certificates.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        // Contact Person Name
                        val nameError = if (hasAttemptedSubmit) fieldErrors["contactName"] else null
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                if (hasAttemptedSubmit) {
                                    fieldErrors = fieldErrors - "contactName"
                                    if (fieldErrors.isEmpty()) validationError = null
                                } else {
                                    validationError = null
                                }
                                syncCustomer()
                            },
                            isError = nameError != null,
                            supportingText = if (nameError != null) {
                                { Text(nameError, color = AccentRed, style = MaterialTheme.typography.labelSmall) }
                            } else null,
                            label = { Text("Contact Person Name *") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (nameError != null) AccentRed else if (name.isNotBlank()) AccentBlueDark else TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("inquiry_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = WhiteBorder,
                                focusedLabelColor = AccentBlue,
                                unfocusedLabelColor = TextMuted,
                                errorBorderColor = AccentRed,
                                errorLabelColor = AccentRed,
                                errorTextColor = TextPrimary
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Company / Factory Name
                        val companyError = if (hasAttemptedSubmit) fieldErrors["companyName"] else null
                        OutlinedTextField(
                            value = company,
                            onValueChange = {
                                company = it
                                if (hasAttemptedSubmit) {
                                    fieldErrors = fieldErrors - "companyName"
                                    if (fieldErrors.isEmpty()) validationError = null
                                } else {
                                    validationError = null
                                }
                                syncCustomer()
                            },
                            isError = companyError != null,
                            supportingText = if (companyError != null) {
                                { Text(companyError, color = AccentRed, style = MaterialTheme.typography.labelSmall) }
                            } else null,
                            label = { Text("Company / Factory / Business Name *") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Business,
                                    contentDescription = null,
                                    tint = if (companyError != null) AccentRed else if (company.isNotBlank()) AccentBlueDark else TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("inquiry_company_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = WhiteBorder,
                                focusedLabelColor = AccentBlue,
                                unfocusedLabelColor = TextMuted,
                                errorBorderColor = AccentRed,
                                errorLabelColor = AccentRed,
                                errorTextColor = TextPrimary
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Phone / WhatsApp Number
                        val phoneError = if (hasAttemptedSubmit) fieldErrors["phone"] else null
                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                phone = it
                                if (hasAttemptedSubmit) {
                                    fieldErrors = fieldErrors - "phone"
                                    if (fieldErrors.isEmpty()) validationError = null
                                } else {
                                    validationError = null
                                }
                                syncCustomer()
                            },
                            isError = phoneError != null,
                            supportingText = if (phoneError != null) {
                                { Text(phoneError, color = AccentRed, style = MaterialTheme.typography.labelSmall) }
                            } else null,
                            label = { Text("Phone / WhatsApp Number *") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = if (phoneError != null) AccentRed else if (phone.isNotBlank()) WhatsAppGreen else TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("inquiry_phone_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = WhiteBorder,
                                focusedLabelColor = AccentBlue,
                                unfocusedLabelColor = TextMuted,
                                errorBorderColor = AccentRed,
                                errorLabelColor = AccentRed,
                                errorTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Delivery Location
                        val locationError = if (hasAttemptedSubmit) fieldErrors["location"] else null
                        OutlinedTextField(
                            value = location,
                            onValueChange = {
                                location = it
                                if (hasAttemptedSubmit) {
                                    fieldErrors = fieldErrors - "location"
                                    if (fieldErrors.isEmpty()) validationError = null
                                } else {
                                    validationError = null
                                }
                                syncCustomer()
                            },
                            isError = locationError != null,
                            supportingText = if (locationError != null) {
                                { Text(locationError, color = AccentRed, style = MaterialTheme.typography.labelSmall) }
                            } else null,
                            label = { Text("Delivery Destination / Plant Location *") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = if (locationError != null) AccentRed else AccentBlueDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("inquiry_location_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = WhiteBorder,
                                focusedLabelColor = AccentBlue,
                                unfocusedLabelColor = TextMuted,
                                errorBorderColor = AccentRed,
                                errorLabelColor = AccentRed,
                                errorTextColor = TextPrimary
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Urgent Delivery Checkbox
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (urgent) AccentAmberSoft else WhiteSurfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (urgent) AccentAmber else WhiteBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    urgent = !urgent
                                    syncCustomer()
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Checkbox(
                                    checked = urgent,
                                    onCheckedChange = {
                                        urgent = it
                                        syncCustomer()
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = AccentAmber,
                                        checkmarkColor = Color.White,
                                        uncheckedColor = TextMuted
                                    ),
                                    modifier = Modifier.testTag("inquiry_urgent_checkbox")
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "Urgent Delivery Required",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (urgent) AccentAmber else TextPrimary
                                    )
                                    Text(
                                        text = "Prioritize inquiry for immediate ready stock dispatch from Morbi depot",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Special Requirements / Notes
                        OutlinedTextField(
                            value = notes,
                            onValueChange = {
                                notes = it
                                syncCustomer()
                            },
                            label = { Text("Special Requirements / COA / Test Certificate Notes") },
                            placeholder = { Text("e.g., Require COA certificate, specific purity, delivery by Friday, GST billing") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Notes,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("inquiry_notes_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = WhiteBorder,
                                focusedLabelColor = AccentBlue,
                                unfocusedLabelColor = TextMuted
                            ),
                            maxLines = 3
                        )
                    }
                }
            }

            // ── Validation Error Banner ──
            if (validationError != null) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = AccentRedSoft,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, AccentRed.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = AccentRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = validationError ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                ),
                                color = AccentRed
                            )
                        }
                    }
                }
            }

            // ── Section 3: Submit Inquiry Request Button & Dispatch Desk ──
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                    border = BorderStroke(1.dp, WhiteBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "3. SUBMIT INQUIRY REQUEST",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        fontSize = 11.sp
                                    ),
                                    color = AccentBlueDark
                                )
                                Text(
                                    text = "Ready to send ${totalItemsCount} products (${totalUnitsCount} total units)",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = TextSecondary
                                )
                            }

                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(currentInquiry.toWhatsAppMessage()))
                                    Toast.makeText(context, "Inquiry text copied to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy text",
                                    tint = AccentBlueDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Primary Large Submit Button
                        Button(
                            onClick = {
                                if (validateForm()) {
                                    syncCustomer()
                                    inquiryRefNumber = "NC-INQ-${Random.nextInt(1000, 9999)}"
                                    submittedInquiry = currentInquiry
                                    onClearAll()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("submit_inquiry_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentBlue,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SUBMIT INQUIRY REQUEST",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Direct Instant Dispatch Row (WhatsApp & Email)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (validateForm()) {
                                        syncCustomer()
                                        onSendWhatsApp(currentInquiry.toWhatsAppMessage())
                                        onClearAll()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
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
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "WhatsApp",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    if (validateForm()) {
                                        syncCustomer()
                                        onSendEmail(
                                            currentInquiry.toEmailSubject(),
                                            currentInquiry.toWhatsAppMessage()
                                        )
                                        onClearAll()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("send_email_inquiry_button"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlueDark),
                                border = BorderStroke(1.dp, WhiteBorder),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = AccentBlueDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Email", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = onCallClick,
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(44.dp)
                                    .testTag("call_desk_quote_button"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlueDark),
                                border = BorderStroke(1.dp, WhiteBorder),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = AccentBlueDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // ── Section 4: Official Company Bank & Tax Registration Card ──
            item {
                Spacer(modifier = Modifier.height(16.dp))

                var isBankExpanded by remember { mutableStateOf(false) }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                    border = BorderStroke(1.dp, WhiteBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quote_bank_info_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isBankExpanded = !isBankExpanded },
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
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AccentBlueSoft),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = AccentBlueDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "OFFICIAL BANK & GST DETAILS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            fontSize = 10.5.sp
                                        ),
                                        color = AccentBlueDark
                                    )
                                    Text(
                                        text = "HDFC Bank · Morvi | GST: ${CompanyInfo.GST_NUMBER}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = TextSecondary
                                    )
                                }
                            }

                            IconButton(
                                onClick = { isBankExpanded = !isBankExpanded },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isBankExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (isBankExpanded) "Collapse" else "Expand",
                                    tint = AccentBlueDark
                                )
                            }
                        }

                        if (isBankExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = WhiteBorder)
                            Spacer(modifier = Modifier.height(12.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = WhiteSurfaceVariant,
                                border = BorderStroke(1.dp, WhiteBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Beneficiary:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                        Text(CompanyInfo.BANK_BENEFICIARY, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Bank & Branch:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                        Text("${CompanyInfo.BANK_NAME} (${CompanyInfo.BANK_BRANCH})", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("A/c Number:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                CompanyInfo.BANK_ACCOUNT_NUMBER,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = FontFamily.Monospace
                                                ),
                                                color = AccentBlueDark
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copy Account Number",
                                                tint = AccentBlueDark,
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clickable {
                                                        clipboardManager.setText(AnnotatedString(CompanyInfo.BANK_ACCOUNT_NUMBER))
                                                        Toast.makeText(context, "Account Number copied!", Toast.LENGTH_SHORT).show()
                                                    }
                                            )
                                        }
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("RTGS / IFSC Code:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                CompanyInfo.BANK_IFSC_CODE,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = FontFamily.Monospace
                                                ),
                                                color = AccentBlueDark
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copy IFSC Code",
                                                tint = AccentBlueDark,
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clickable {
                                                        clipboardManager.setText(AnnotatedString(CompanyInfo.BANK_IFSC_CODE))
                                                        Toast.makeText(context, "IFSC Code copied!", Toast.LENGTH_SHORT).show()
                                                    }
                                            )
                                        }
                                    }
                                    HorizontalDivider(color = WhiteBorder)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("GSTIN:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                        Text(CompanyInfo.GST_NUMBER, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace), color = TextPrimary)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("MSME (Udyam):", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                        Text(CompanyInfo.MSME_NUMBER, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace), color = TextPrimary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(CompanyInfo.getFormattedBankDetailsMessage()))
                                    Toast.makeText(context, "Bank & GST details copied!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlueDark),
                                border = BorderStroke(1.dp, WhiteBorder),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy Complete Bank & GST Details", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                var isDepotExpanded by remember { mutableStateOf(false) }
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                    border = BorderStroke(1.dp, WhiteBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quote_depot_info_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isDepotExpanded = !isDepotExpanded },
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
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AccentBlueSoft),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = AccentBlueDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "SALES DEPOTS & DISPATCH GODOWNS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            fontSize = 10.5.sp
                                        ),
                                        color = AccentBlueDark
                                    )
                                    Text(
                                        text = "2 Godowns: Morbi (Rajpar Rd) & Maliya",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = TextSecondary
                                    )
                                }
                            }

                            IconButton(
                                onClick = { isDepotExpanded = !isDepotExpanded },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isDepotExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (isDepotExpanded) "Collapse" else "Expand",
                                    tint = AccentBlueDark
                                )
                            }
                        }

                        if (isDepotExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = WhiteBorder)
                            Spacer(modifier = Modifier.height(12.dp))

                            CompanyInfo.SALES_DEPOTS.forEachIndexed { index, depot ->
                                if (index > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = WhiteSurfaceVariant,
                                    border = BorderStroke(1.dp, WhiteBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Depot ${depot.depotNumber}: ${depot.shortName}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                ),
                                                color = AccentBlueDark
                                            )
                                            IconButton(
                                                onClick = {
                                                    clipboardManager.setText(AnnotatedString(depot.address))
                                                    Toast.makeText(context, "Depot ${depot.depotNumber} address copied!", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ContentCopy,
                                                    contentDescription = "Copy",
                                                    tint = AccentBlueDark,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = depot.address,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(CompanyInfo.getAllDepotsFormattedMessage()))
                                    Toast.makeText(context, "All depot addresses copied!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlueDark),
                                border = BorderStroke(1.dp, WhiteBorder),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy All Depot Locations", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Dialog 1: Direct Quantity Edit Dialog ──
    editingItemForQuantity?.let { item ->
        QuantityEditDialog(
            item = item,
            onDismiss = { editingItemForQuantity = null },
            onConfirmQuantity = { newQty ->
                onUpdateQuantity(item.id, newQty)
                editingItemForQuantity = null
            }
        )
    }

    // ── Dialog 2: Inquiry Submission Confirmation Dialog ──
    submittedInquiry?.let { inq ->
        InquirySubmissionSuccessDialog(
            inquiry = inq,
            referenceNumber = inquiryRefNumber,
            onDismiss = { submittedInquiry = null },
            onSendWhatsApp = {
                onSendWhatsApp(inq.toWhatsAppMessage())
                submittedInquiry = null
            },
            onSendEmail = {
                onSendEmail(inq.toEmailSubject(), inq.toWhatsAppMessage())
                submittedInquiry = null
            },
            onCallDesk = {
                onCallClick()
            },
            onCopyMessage = {
                clipboardManager.setText(AnnotatedString(inq.toWhatsAppMessage()))
                Toast.makeText(context, "Inquiry details copied to clipboard!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // ── Dialog 3: Clear All Confirmation ──
    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            title = {
                Text(
                    text = "Clear Inquiry List?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove all ${uiState.quoteItems.size} chemical products from your inquiry list?",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAll()
                        showClearConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentRed,
                        contentColor = Color.White
                    )
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearConfirmation = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = WhiteSurface,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

/**
 * Custom Card component displaying each selected chemical product with:
 * - Chemical name, formula badge, grade, packaging unit
 * - Quantity stepper (- / +) and clickable quantity button to type custom numbers directly
 * - Remove action
 */
@Composable
fun SelectedProductInquiryCard(
    item: QuoteItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onEditQuantityClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
        border = BorderStroke(1.dp, WhiteBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("inquiry_item_${item.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.product.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        FormulaBadge(
                            formula = item.product.formula,
                            backgroundColor = WhiteSurfaceVariant,
                            textColor = AccentBlueDark
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${item.selectedGrade} · ${if (item.isWholesale) "Wholesale" else "Retail"}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextSecondary
                    )
                }

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove product",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Packaging Unit & Quantity Control Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(WhiteSurfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Packaging Unit Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = AccentBlueDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = item.unit,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        color = TextPrimary
                    )
                }

                // Stepper + Direct Quantity Edit
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(WhiteSurface)
                        .border(1.dp, WhiteBorder, RoundedCornerShape(6.dp))
                ) {
                    IconButton(
                        onClick = onDecrease,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (item.quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                            contentDescription = "Decrease",
                            tint = if (item.quantity == 1) AccentRed else TextPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // Tappable Quantity text to open direct number input dialog
                    Surface(
                        color = Color.Transparent,
                        modifier = Modifier.clickable { onEditQuantityClick() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = item.quantity.toString(),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                color = AccentBlueDark,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit quantity",
                                tint = AccentBlueDark.copy(alpha = 0.6f),
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onIncrease,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = TextPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            if (item.remarks.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Specification Note: ${item.remarks}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = AccentBlueDark,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

/**
 * Dialog to type direct custom quantity (e.g. 50, 200, 500 units) with preset chips
 */
@Composable
fun QuantityEditDialog(
    item: QuoteItem,
    onDismiss: () -> Unit,
    onConfirmQuantity: (Int) -> Unit
) {
    var quantityText by remember { mutableStateOf(item.quantity.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteSurface),
            border = BorderStroke(1.dp, WhiteBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Set Quantity",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )

                Text(
                    text = "${item.product.name} (${item.unit})",
                    style = MaterialTheme.typography.bodySmall,
                    color = AccentBlueDark,
                    modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                )

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() } && input.length <= 6) {
                            quantityText = input
                        }
                    },
                    label = { Text("Quantity (${item.unit})") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = WhiteBorder,
                        focusedLabelColor = AccentBlue,
                        unfocusedLabelColor = TextMuted
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("direct_quantity_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick presets
                Text(
                    text = "Quick Presets:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(5, 10, 25, 50, 100).forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = WhiteSurfaceVariant,
                            border = BorderStroke(1.dp, WhiteBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { quantityText = preset.toString() }
                        ) {
                            Text(
                                text = preset.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val parsed = quantityText.toIntOrNull() ?: 1
                            onConfirmQuantity(parsed.coerceAtLeast(1))
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Update Quantity", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Confirmation dialog after user submits the inquiry with Name, Company, Quantity
 */
@Composable
fun InquirySubmissionSuccessDialog(
    inquiry: CustomerInquiry,
    referenceNumber: String,
    onDismiss: () -> Unit,
    onSendWhatsApp: () -> Unit,
    onSendEmail: () -> Unit,
    onCallDesk: () -> Unit,
    onCopyMessage: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteSurface),
            border = BorderStroke(1.5.dp, AccentBlue),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("inquiry_submission_success_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success Badge
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(AccentTealSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = AccentTealDark,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Inquiry Request Prepared!",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    ),
                    color = TextPrimary
                )

                Surface(
                    color = AccentBlueSoft,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                ) {
                    Text(
                        text = "Ref: $referenceNumber",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = AccentBlueDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                // Summary Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = WhiteSurfaceVariant,
                    border = BorderStroke(1.dp, WhiteBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        if (inquiry.companyName.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Company: ",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextMuted
                                )
                                Text(
                                    text = inquiry.companyName,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                            }
                        }

                        if (inquiry.contactName.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Contact Person: ",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextMuted
                                )
                                Text(
                                    text = inquiry.contactName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Phone: ",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = TextMuted
                            )
                            Text(
                                text = inquiry.phone,
                                style = MaterialTheme.typography.bodySmall,
                                color = WhatsAppGreen
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Products: ",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = TextMuted
                            )
                            Text(
                                text = "${inquiry.items.size} chemicals (${inquiry.totalUnits} total units)",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = AccentBlueDark
                            )
                        }

                        if (inquiry.urgentDelivery) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = AccentAmber,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Urgent Priority Delivery Flagged",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = AccentAmber
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Dispatch to Nirmaladevi Care sales desk via:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Action 1: WhatsApp
                Button(
                    onClick = onSendWhatsApp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("dialog_send_whatsapp_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WhatsAppGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Send via WhatsApp Desk",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action 2: Email & Copy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onSendEmail,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlueDark),
                        border = BorderStroke(1.dp, WhiteBorder),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = AccentBlueDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Email", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onCopyMessage,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlueDark),
                        border = BorderStroke(1.dp, WhiteBorder),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Text", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
                ) {
                    Text("Close / Keep Inquiry")
                }
            }
        }
    }
}
