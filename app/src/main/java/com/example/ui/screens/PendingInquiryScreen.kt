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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PendingInquiryEntity
import com.example.data.model.ChemicalCatalog
import com.example.data.model.ChemicalProduct
import com.example.ui.components.FormulaBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChemicalUiState
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen featuring an inquiry form with fields for user name, company name, and quantity,
 * alongside a list of products selected for inquiry, managed locally in a Room database.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PendingInquiryScreen(
    uiState: ChemicalUiState,
    onSaveInquiry: (userName: String, companyName: String, quantity: Int, product: ChemicalProduct, unit: String, notes: String) -> Unit,
    onUpdateQuantity: (id: Long, quantity: Int) -> Unit,
    onDeleteInquiry: (id: Long) -> Unit,
    onClearAll: () -> Unit,
    onSendWhatsApp: (String) -> Unit,
    onSendEmail: (subject: String, body: String) -> Unit,
    onNavigateToCatalogue: () -> Unit
) {
    val context = LocalContext.current

    // Form Field States - strictly blank with no prepopulated default text
    var userName by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf(ChemicalCatalog.products.first()) }
    var selectedUnit by remember { mutableStateOf("Carboys (35 kg)") }
    var notes by remember { mutableStateOf("") }

    // Validation state
    var userNameError by remember { mutableStateOf<String?>(null) }
    var companyNameError by remember { mutableStateOf<String?>(null) }
    var quantityError by remember { mutableStateOf<String?>(null) }
    var formSuccessMessage by remember { mutableStateOf<String?>(null) }
    var showProductPicker by remember { mutableStateOf(false) }
    var showClearConfirmation by remember { mutableStateOf(false) }

    val packagingOptions = listOf(
        "Carboys (35 kg)",
        "Carboys (50 kg)",
        "Drums (200 kg)",
        "Bags (25 kg)",
        "Bags (50 kg)",
        "ISO Tanker (15-30 MT)"
    )

    fun validateAndSubmit() {
        var isValid = true

        val trimmedName = userName.trim()
        if (trimmedName.isBlank()) {
            userNameError = "Contact person name is required"
            isValid = false
        } else if (trimmedName.length < 2) {
            userNameError = "Name must be at least 2 characters"
            isValid = false
        } else {
            userNameError = null
        }

        val trimmedCompany = companyName.trim()
        if (trimmedCompany.isBlank()) {
            companyNameError = "Company or business name is required"
            isValid = false
        } else {
            companyNameError = null
        }

        val qtyInt = quantityText.toIntOrNull()
        if (qtyInt == null || qtyInt <= 0) {
            quantityError = "Please enter a valid quantity greater than 0"
            isValid = false
        } else {
            quantityError = null
        }

        if (isValid && qtyInt != null) {
            onSaveInquiry(
                trimmedName,
                trimmedCompany,
                qtyInt,
                selectedProduct,
                selectedUnit,
                notes.trim()
            )
            formSuccessMessage = "Saved ${selectedProduct.name} ($qtyInt $selectedUnit) to Room DB"
            Toast.makeText(context, "Saved to Room Database!", Toast.LENGTH_SHORT).show()
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteCanvas)
    ) {
        val isWideScreen = maxWidth >= 760.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // Header Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, WhiteBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Pending Inquiries",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Room Database Indicator Badge
                            Surface(
                                color = AccentBlueSoft,
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(AccentBlue, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Room DB",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = AccentBlueDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Managed in local SQLite database via Room • Nirmaladevi Care",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }

                    // Count Badge
                    Surface(
                        color = AccentBlue,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "${uiState.pendingInquiries.size} Products",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Main Content: Side-by-Side on Wide Screens, Stacked LazyColumn on Compact Screens
            if (isWideScreen) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left: Form Card
                    Box(modifier = Modifier.weight(1f)) {
                        InquiryFormCard(
                            userName = userName,
                            onUserNameChange = {
                                userName = it
                                userNameError = null
                            },
                            userNameError = userNameError,
                            companyName = companyName,
                            onCompanyNameChange = {
                                companyName = it
                                companyNameError = null
                            },
                            companyNameError = companyNameError,
                            quantityText = quantityText,
                            onQuantityChange = {
                                quantityText = it
                                quantityError = null
                            },
                            quantityError = quantityError,
                            selectedProduct = selectedProduct,
                            onSelectProductClick = { showProductPicker = true },
                            selectedUnit = selectedUnit,
                            onSelectUnit = { selectedUnit = it },
                            packagingOptions = packagingOptions,
                            notes = notes,
                            onNotesChange = { notes = it },
                            onSubmit = { validateAndSubmit() },
                            onReset = {
                                userName = ""
                                companyName = ""
                                quantityText = ""
                                notes = ""
                                formSuccessMessage = null
                            },
                            successMessage = formSuccessMessage
                        )
                    }

                    // Right: Products Selected for Inquiry List
                    Box(modifier = Modifier.weight(1.2f)) {
                        SelectedProductsListCard(
                            pendingInquiries = uiState.pendingInquiries,
                            onUpdateQuantity = onUpdateQuantity,
                            onDeleteInquiry = onDeleteInquiry,
                            onClearAllClick = { showClearConfirmation = true },
                            onSendWhatsApp = onSendWhatsApp,
                            onSendEmail = onSendEmail,
                            onNavigateToCatalogue = onNavigateToCatalogue,
                            onLoadSampleData = {
                                val acid = ChemicalCatalog.products.first { it.id == "sulphuric-acid-comm" }
                                val soda = ChemicalCatalog.products.first { it.id == "caustic-soda-flakes" }
                                onSaveInquiry("Rajesh Patel", "Morbi Ceramics Ltd.", 25, acid, "Carboys (35 kg)", "Urgent dispatch needed")
                                onSaveInquiry("Amit Shah", "Apex Tile Industries", 50, soda, "Bags (50 kg)", "Morbi plant delivery")
                            }
                        )
                    }
                }
            } else {
                // Compact Screen: Single vertically scrollable column with Form and Selected Products List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // Form Section
                    item {
                        InquiryFormCard(
                            userName = userName,
                            onUserNameChange = {
                                userName = it
                                userNameError = null
                            },
                            userNameError = userNameError,
                            companyName = companyName,
                            onCompanyNameChange = {
                                companyName = it
                                companyNameError = null
                            },
                            companyNameError = companyNameError,
                            quantityText = quantityText,
                            onQuantityChange = {
                                quantityText = it
                                quantityError = null
                            },
                            quantityError = quantityError,
                            selectedProduct = selectedProduct,
                            onSelectProductClick = { showProductPicker = true },
                            selectedUnit = selectedUnit,
                            onSelectUnit = { selectedUnit = it },
                            packagingOptions = packagingOptions,
                            notes = notes,
                            onNotesChange = { notes = it },
                            onSubmit = { validateAndSubmit() },
                            onReset = {
                                userName = ""
                                companyName = ""
                                quantityText = ""
                                notes = ""
                                formSuccessMessage = null
                            },
                            successMessage = formSuccessMessage
                        )
                    }

                    // Products Selected for Inquiry Section
                    item {
                        SelectedProductsListCard(
                            pendingInquiries = uiState.pendingInquiries,
                            onUpdateQuantity = onUpdateQuantity,
                            onDeleteInquiry = onDeleteInquiry,
                            onClearAllClick = { showClearConfirmation = true },
                            onSendWhatsApp = onSendWhatsApp,
                            onSendEmail = onSendEmail,
                            onNavigateToCatalogue = onNavigateToCatalogue,
                            onLoadSampleData = {
                                val acid = ChemicalCatalog.products.first { it.id == "sulphuric-acid-comm" }
                                val soda = ChemicalCatalog.products.first { it.id == "caustic-soda-flakes" }
                                onSaveInquiry("Rajesh Patel", "Morbi Ceramics Ltd.", 25, acid, "Carboys (35 kg)", "Urgent dispatch needed")
                                onSaveInquiry("Amit Shah", "Apex Tile Industries", 50, soda, "Bags (50 kg)", "Morbi plant delivery")
                            }
                        )
                    }
                }
            }
        }
    }

    // Product Selection Dialog
    if (showProductPicker) {
        AlertDialog(
            onDismissRequest = { showProductPicker = false },
            title = {
                Text(
                    text = "Select Chemical for Inquiry",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    Text(
                        text = "Choose from Nirmaladevi Care industrial chemicals:",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(ChemicalCatalog.products) { prod ->
                            val isChosen = prod.id == selectedProduct.id
                            Surface(
                                onClick = {
                                    selectedProduct = prod
                                    showProductPicker = false
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isChosen) AccentBlueSoft else WhiteSurface,
                                border = BorderStroke(
                                    1.dp,
                                    if (isChosen) AccentBlue else WhiteBorder
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = prod.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                        Text(
                                            text = "${prod.category.title} • ${prod.packagingOptions.firstOrNull() ?: "Standard Packaging"}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextSecondary,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                    FormulaBadge(formula = prod.formula)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProductPicker = false }) {
                    Text("Close", color = AccentBlueDark, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = WhiteSurface,
            shape = RoundedCornerShape(12.dp)
        )
    }

    // Clear All Confirmation Dialog
    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            title = {
                Text("Clear All Pending Inquiries?", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "This will remove all pending inquiry items stored in the local Room database. Are you sure?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAll()
                        showClearConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Clear All", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmation = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = WhiteSurface,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

/**
 * Form Card containing fields for user name, company name, quantity, and product details.
 */
@Composable
private fun InquiryFormCard(
    userName: String,
    onUserNameChange: (String) -> Unit,
    userNameError: String?,
    companyName: String,
    onCompanyNameChange: (String) -> Unit,
    companyNameError: String?,
    quantityText: String,
    onQuantityChange: (String) -> Unit,
    quantityError: String?,
    selectedProduct: ChemicalProduct,
    onSelectProductClick: () -> Unit,
    selectedUnit: String,
    onSelectUnit: (String) -> Unit,
    packagingOptions: List<String>,
    notes: String,
    onNotesChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onReset: () -> Unit,
    successMessage: String?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("inquiry_form_card"),
        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, WhiteBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = null,
                        tint = AccentBlueDark,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Inquiry Entry Form",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                TextButton(onClick = onReset) {
                    Text("Reset", color = TextSecondary, fontSize = 12.sp)
                }
            }

            Text(
                text = "Fill in user and company details with requested quantity to persist in Room DB.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Success feedback banner
            AnimatedVisibility(
                visible = successMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = AccentBlueSoft,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentBlueDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = successMessage ?: "",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextPrimary)
                        )
                    }
                }
            }

            // Field 1: User Name
            OutlinedTextField(
                value = userName,
                onValueChange = onUserNameChange,
                label = { Text("User Name / Contact Person *") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = if (userNameError != null) DangerRed else if (userName.isNotBlank()) AccentBlueDark else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                },
                isError = userNameError != null,
                supportingText = if (userNameError != null) {
                    { Text(userNameError, color = DangerRed, style = MaterialTheme.typography.labelSmall) }
                } else null,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_user_name"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = WhiteBorder,
                    focusedLabelColor = AccentBlueDark,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = WhiteSurface,
                    unfocusedContainerColor = WhiteSurface,
                    errorBorderColor = DangerRed,
                    errorLabelColor = DangerRed,
                    errorTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Field 2: Company Name
            OutlinedTextField(
                value = companyName,
                onValueChange = onCompanyNameChange,
                label = { Text("Company / Factory Name *") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = null,
                        tint = if (companyNameError != null) DangerRed else if (companyName.isNotBlank()) AccentBlueDark else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                },
                isError = companyNameError != null,
                supportingText = if (companyNameError != null) {
                    { Text(companyNameError, color = DangerRed, style = MaterialTheme.typography.labelSmall) }
                } else null,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_company_name"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = WhiteBorder,
                    focusedLabelColor = AccentBlueDark,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = WhiteSurface,
                    unfocusedContainerColor = WhiteSurface,
                    errorBorderColor = DangerRed,
                    errorLabelColor = DangerRed,
                    errorTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Selected Chemical Product Picker Trigger
            Text(
                text = "Chemical Product *",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Surface(
                onClick = onSelectProductClick,
                color = WhiteSurfaceVariant,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, WhiteBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("select_product_trigger")
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
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Science,
                            contentDescription = null,
                            tint = AccentBlueDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = selectedProduct.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "${selectedProduct.category.title} • Purity: ${selectedProduct.purity}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FormulaBadge(formula = selectedProduct.formula)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Change Product",
                            tint = AccentBlueDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Field 3: Quantity with stepper controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = onQuantityChange,
                    label = { Text("Quantity *") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Numbers,
                            contentDescription = null,
                            tint = if (quantityError != null) DangerRed else AccentBlueDark,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    isError = quantityError != null,
                    supportingText = if (quantityError != null) {
                        { Text(quantityError, color = DangerRed, style = MaterialTheme.typography.labelSmall) }
                    } else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_quantity"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = WhiteBorder,
                        focusedLabelColor = AccentBlueDark,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = WhiteSurface,
                        unfocusedContainerColor = WhiteSurface,
                        errorBorderColor = DangerRed,
                        errorLabelColor = DangerRed,
                        errorTextColor = TextPrimary
                    )
                )

                // Stepper Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = if (quantityError != null) 0.dp else 4.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = {
                            val cur = quantityText.toIntOrNull() ?: 1
                            if (cur > 1) onQuantityChange((cur - 1).toString())
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = WhiteSurfaceVariant,
                            contentColor = TextPrimary
                        ),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(18.dp))
                    }

                    FilledTonalIconButton(
                        onClick = {
                            val cur = quantityText.toIntOrNull() ?: 0
                            onQuantityChange((cur + 5).toString())
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = AccentBlue,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Quick quantity preset chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val presets = listOf("10", "25", "50", "100", "250")
                items(presets) { preset ->
                    val isSelected = quantityText == preset
                    FilterChip(
                        selected = isSelected,
                        onClick = { onQuantityChange(preset) },
                        label = { Text("$preset units", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentBlueSoft,
                            selectedLabelColor = AccentBlueDark,
                            containerColor = WhiteSurfaceVariant,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = WhiteBorder,
                            selectedBorderColor = AccentBlue
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Packaging Unit Selector
            Text(
                text = "Packaging Unit",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(packagingOptions) { unitOption ->
                    val isSelected = unitOption == selectedUnit
                    Surface(
                        onClick = { onSelectUnit(unitOption) },
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) AccentBlueSoft else WhiteSurfaceVariant,
                        border = BorderStroke(1.dp, if (isSelected) AccentBlue else WhiteBorder)
                    ) {
                        Text(
                            text = unitOption,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) AccentBlueDark else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Notes / Remarks
            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                label = { Text("Delivery Remarks / Notes (Optional)") },
                placeholder = { Text("e.g., Deliver to Morbi GIDC Unit 2") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = WhiteBorder,
                    focusedLabelColor = AccentBlueDark,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = WhiteSurface,
                    unfocusedContainerColor = WhiteSurface
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Save to Room DB Button
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_inquiry_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SAVE TO ROOM DATABASE",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
}

/**
 * Card displaying the list of products selected for inquiry retrieved from Room DB.
 */
@Composable
private fun SelectedProductsListCard(
    pendingInquiries: List<PendingInquiryEntity>,
    onUpdateQuantity: (id: Long, quantity: Int) -> Unit,
    onDeleteInquiry: (id: Long) -> Unit,
    onClearAllClick: () -> Unit,
    onSendWhatsApp: (String) -> Unit,
    onSendEmail: (subject: String, body: String) -> Unit,
    onNavigateToCatalogue: () -> Unit,
    onLoadSampleData: () -> Unit
) {
    val totalUnits = pendingInquiries.sumOf { it.quantity }
    val dateFormatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("selected_products_list_card"),
        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, WhiteBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Card Title Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Inventory,
                        contentDescription = null,
                        tint = AccentBlueDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Products Selected for Inquiry",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                if (pendingInquiries.isNotEmpty()) {
                    TextButton(onClick = onClearAllClick) {
                        Text(
                            text = "Clear All",
                            color = DangerRed,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Text(
                text = "Live reactive list observed from local Room Database Flow.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Content: Empty State or Items List
            if (pendingInquiries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AccentBlueSoft,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.ShoppingCart,
                                    contentDescription = null,
                                    tint = AccentBlueDark,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Products in Pending Inquiry",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = "Fill the form with user, company, and quantity\nto save inquiry records in Room DB.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = onLoadSampleData,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlueDark),
                                border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Load Sample Inquiry", fontSize = 12.sp)
                            }
                            Button(
                                onClick = onNavigateToCatalogue,
                                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Browse Products", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // List of Inquiry Items
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    pendingInquiries.forEach { item ->
                        InquiryItemCard(
                            item = item,
                            dateStr = dateFormatter.format(Date(item.timestamp)),
                            onIncrease = { onUpdateQuantity(item.id, item.quantity + 5) },
                            onDecrease = { onUpdateQuantity(item.id, (item.quantity - 5).coerceAtLeast(1)) },
                            onDelete = { onDeleteInquiry(item.id) }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Summary & Quick Send Card
                    Surface(
                        color = WhiteSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, WhiteBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Inquiry Summary:",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary
                                    )
                                )
                                Text(
                                    text = "${pendingInquiries.size} Products • $totalUnits Units",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = AccentBlueDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Dispatch Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val builder = StringBuilder()
                                        builder.append("━━━━━━━━━━━━━━━━━━━━\n")
                                        builder.append("🧪 *NIRMALADEVI CARE PVT. LTD.*\n")
                                        builder.append("*Industrial Chemical Quotation Request*\n")
                                        builder.append("━━━━━━━━━━━━━━━━━━━━\n\n")
                                        val first = pendingInquiries.firstOrNull()
                                        if (first != null) {
                                            builder.append("🏢 *BUYER DETAILS:*\n")
                                            if (first.companyName.isNotBlank()) builder.append("• Company / Firm: *${first.companyName.trim()}*\n")
                                            if (first.userName.isNotBlank()) builder.append("• Contact Person: ${first.userName.trim()}\n")
                                        }
                                        builder.append("\n────────────────────\n")
                                        builder.append("📦 *SELECTED CHEMICAL PRODUCTS (${pendingInquiries.size} Items · $totalUnits Units):*\n")
                                        builder.append("────────────────────\n\n")
                                        pendingInquiries.forEachIndexed { i, inq ->
                                            builder.append("*${i + 1}. ${inq.productName}* (${inq.chemicalFormula})\n")
                                            builder.append("   • Quantity: *${inq.quantity} ${inq.packagingUnit}*\n")
                                            if (inq.notes.isNotBlank()) {
                                                builder.append("   • Note: ${inq.notes.trim()}\n")
                                            }
                                            // Empty line between products so they do not mix together
                                            builder.append("\n")
                                        }
                                        builder.append("────────────────────\n")
                                        builder.append("Please provide best commercial quotation, delivery schedule, and COA / Test Certificate.\n\n")
                                        builder.append("Thank you!\n")
                                        builder.append("━━━━━━━━━━━━━━━━━━━━")
                                        onSendWhatsApp(builder.toString())
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = WhatsAppGreen,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("WhatsApp Quote", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val first = pendingInquiries.firstOrNull()
                                        val client = if (first != null) "${first.companyName} (${first.userName})" else "Client Inquiry"
                                        val subject = "Chemical Quotation Request - $client [Nirmaladevi Care]"
                                        val body = buildString {
                                            append("Dear Nirmaladevi Care Pvt. Ltd. Team,\n\n")
                                            append("We request a quotation for the following chemical products:\n\n")
                                            pendingInquiries.forEachIndexed { idx, inq ->
                                                append("${idx + 1}. ${inq.productName} [Formula: ${inq.chemicalFormula}]\n")
                                                append("   Quantity: ${inq.quantity} ${inq.packagingUnit}\n")
                                                append("   Inquired by: ${inq.userName} / ${inq.companyName}\n")
                                                if (inq.notes.isNotBlank()) append("   Instructions: ${inq.notes}\n")
                                                append("\n")
                                            }
                                            append("Delivery Destination: Morbi, Gujarat\n")
                                            append("Looking forward to your quotation.\n")
                                        }
                                        onSendEmail(subject, body)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                    border = BorderStroke(1.dp, WhiteBorder),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Email Inquiry", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual Card representing an item selected for inquiry from Room DB.
 */
@Composable
private fun InquiryItemCard(
    item: PendingInquiryEntity,
    dateStr: String,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = WhiteSurface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, WhiteBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("inquiry_item_${item.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Row 1: Product Name, Formula, and Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.productName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (item.chemicalFormula.isNotBlank()) {
                        FormulaBadge(formula = item.chemicalFormula)
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("delete_inquiry_${item.id}")
                ) {
                    Icon(
                        Icons.Outlined.DeleteOutline,
                        contentDescription = "Delete Item",
                        tint = DangerRed.copy(alpha = 0.85f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Row 2: Customer and Company
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = AccentBlueDark,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.userName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    Icons.Default.Business,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.companyName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Row 3: Quantity Controls and Packaging Unit
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = WhiteSurfaceVariant,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, WhiteBorder)
                ) {
                    Text(
                        text = item.packagingUnit,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Stepper
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalIconButton(
                        onClick = onDecrease,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = WhiteSurfaceVariant,
                            contentColor = TextPrimary
                        ),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Minus", modifier = Modifier.size(14.dp))
                    }

                    Text(
                        text = "${item.quantity}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AccentBlueDark,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    FilledTonalIconButton(
                        onClick = onIncrease,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = AccentBlue,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Plus", modifier = Modifier.size(14.dp))
                    }
                }
            }

            if (item.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Note: ${item.notes}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Saved: $dateStr",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 10.sp
                )
            )
        }
    }
}
