package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChemicalCategory
import com.example.data.model.CompanyInfo
import com.example.ui.components.AppTopBar
import com.example.ui.components.ContactPhonePickerDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChemicalViewModel
import java.net.URLEncoder

enum class NavTab(val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    CATALOGUE("Catalog", Icons.Filled.Science, Icons.Outlined.Science),
    SELECTION("Select", Icons.Filled.Checklist, Icons.Outlined.Checklist),
    PENDING_INQUIRY("Inquiry", Icons.Filled.EditNote, Icons.Outlined.EditNote),
    INQUIRY("Cart", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
    TOOLS("Tools", Icons.Filled.Calculate, Icons.Outlined.Calculate),
    COMPANY("About", Icons.Filled.Business, Icons.Outlined.Business)
}

class MainActivity : ComponentActivity() {

    private val viewModel: ChemicalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: ChemicalViewModel) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf(NavTab.CATALOGUE) }
    var isLegalOpen by remember { mutableStateOf(false) }
    var isCataloguePdfOpen by remember { mutableStateOf(false) }
    var showSplashScreen by remember { mutableStateOf(true) }
    var showPhonePickerSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Initialize Room Database repository
    LaunchedEffect(Unit) {
        viewModel.initDatabase(context)
    }

    // Handle Snackbar messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSnackbarMessage()
        }
    }

    // Helper Intent Functions
    fun makePhoneCall(phoneNumber: String = CompanyInfo.PHONE) {
        try {
            val cleanNumber = phoneNumber.replace(" ", "")
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$cleanNumber")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Call: $phoneNumber", Toast.LENGTH_LONG).show()
        }
    }

    fun openWhatsApp(message: String, phoneNumber: String = CompanyInfo.PHONE) {
        try {
            val cleanPhone = phoneNumber.replace("+", "").replace(" ", "")
            val encodedMsg = URLEncoder.encode(message, "UTF-8")
            val url = "https://wa.me/$cleanPhone?text=$encodedMsg"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp: $phoneNumber", Toast.LENGTH_LONG).show()
        }
    }

    fun sendEmail(subject: String, body: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:nirmaladevicarepvtltd@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            context.startActivity(Intent.createChooser(intent, "Send Chemical Inquiry"))
        } catch (e: Exception) {
            Toast.makeText(context, "Email: nirmaladevicarepvtltd@gmail.com", Toast.LENGTH_LONG).show()
        }
    }

    fun openGoogleMaps(
        mapUrl: String = "https://maps.app.goo.gl/Sx54aiY4ezVeYyqY9",
        fallbackQuery: String = "Panchasar Road, Morbi, Gujarat"
    ) {
        try {
            val mapUri = Uri.parse(mapUrl)
            val intent = Intent(Intent.ACTION_VIEW, mapUri)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val geoUri = Uri.parse("geo:0,0?q=${Uri.encode(fallbackQuery)}")
                val fallbackIntent = Intent(Intent.ACTION_VIEW, geoUri)
                context.startActivity(fallbackIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, fallbackQuery, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open link", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareApp() {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, CompanyInfo.SHARE_APP_MESSAGE.trimIndent())
            }
            context.startActivity(Intent.createChooser(intent, "Share Nirmaladevi Care App"))
        } catch (e: Exception) {
            Toast.makeText(context, "Sharing failed", Toast.LENGTH_SHORT).show()
        }
    }

    Crossfade(
        targetState = showSplashScreen,
        animationSpec = tween(durationMillis = 450),
        label = "splash_transition"
    ) { isSplash ->
        if (isSplash) {
            SplashScreen(
                onFinished = { showSplashScreen = false }
            )
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = WhiteCanvas,
                topBar = {
            AppTopBar(
                onCallClick = { showPhonePickerSheet = true },
                onWhatsAppClick = { openWhatsApp("Hello Nirmaladevi Care, I would like to inquire about industrial chemicals.") },
                quoteItemCount = uiState.selectedProductCount + uiState.pendingInquiries.size + uiState.quoteItems.size,
                onQuoteClick = {
                    currentTab = if (uiState.selectedProductCount > 0) NavTab.SELECTION else if (uiState.pendingInquiries.isNotEmpty()) NavTab.PENDING_INQUIRY else NavTab.SELECTION
                },
                onCataloguePdfClick = { isCataloguePdfOpen = true },
                onShareClick = { shareApp() }
            )
        },
        bottomBar = {
            Surface(
                color = WhiteSurface,
                tonalElevation = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    HorizontalDivider(thickness = 1.dp, color = WhiteBorder)
                    NavigationBar(
                        containerColor = WhiteSurface,
                        tonalElevation = 0.dp,
                        windowInsets = WindowInsets(0, 0, 0, 0),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .testTag("bottom_nav_bar")
                    ) {
                        NavTab.values().forEach { tab ->
                            val isSelected = currentTab == tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentTab = tab },
                                alwaysShowLabel = true,
                                icon = {
                                    val badgeCount = when (tab) {
                                        NavTab.SELECTION -> uiState.selectedProductCount
                                        NavTab.PENDING_INQUIRY -> uiState.pendingInquiries.size
                                        NavTab.INQUIRY -> uiState.quoteItems.size
                                        else -> 0
                                    }
                                    if (badgeCount > 0) {
                                        BadgedBox(
                                            badge = {
                                                Badge(
                                                    containerColor = AccentBlue,
                                                    contentColor = Color.White
                                                ) {
                                                    Text(
                                                        text = badgeCount.toString(),
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                                contentDescription = tab.title,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.title,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = tab.title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 9.5.sp,
                                            letterSpacing = (-0.3).sp
                                        ),
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AccentBlueDark,
                                    selectedTextColor = AccentBlueDark,
                                    indicatorColor = AccentBlueSoft,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = TextPrimary,
                        contentColor = Color.White,
                        actionColor = AccentBlue,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavTab.CATALOGUE -> {
                    HomeScreen(
                        uiState = uiState,
                        onCategorySelect = { viewModel.selectCategory(it) },
                        onSearchChange = { viewModel.updateSearchQuery(it) },
                        onProductClick = { viewModel.openProductDetail(it) },
                        onQuickAddClick = { viewModel.openProductDetail(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onBrowseProductsClick = {
                            viewModel.selectCategory(ChemicalCategory.ALL)
                            viewModel.updateSearchQuery("")
                        },
                        onContactClick = { currentTab = NavTab.COMPANY },
                        onCallClick = { makePhoneCall() },
                        onWhatsAppClick = { openWhatsApp("Hello Nirmaladevi Care, I am interested in placing an industrial chemical order.") },
                        onNavigateToSelection = { currentTab = NavTab.SELECTION },
                        onOpenCataloguePdf = { isCataloguePdfOpen = true }
                    )
                }
                NavTab.SELECTION -> {
                    ProductSelectionScreen(
                        uiState = uiState,
                        onCategoryToggle = { catId, isSelected ->
                            viewModel.toggleCategorySelectionInDb(catId, isSelected)
                        },
                        onProductToggle = { prodId, isSelected ->
                            viewModel.toggleProductSelectionInDb(prodId, isSelected)
                        },
                        onSelectAllToggle = { isSelected ->
                            viewModel.selectAllInDb(isSelected)
                        },
                        onToggleCategoryExpanded = { catId ->
                            viewModel.toggleCategoryExpanded(catId)
                        },
                        onExpandAll = { viewModel.expandAllCategories() },
                        onCollapseAll = { viewModel.collapseAllCategories() },
                        onSearchQueryChange = { q -> viewModel.updateSelectionSearchQuery(q) },
                        onToggleOnlySelected = { viewModel.toggleFilterOnlySelected() },
                        onAddToPendingInquiry = {
                            viewModel.addSelectedProductsToPendingInquiry(
                                userName = uiState.customerName.ifBlank { "Valued Customer" },
                                companyName = uiState.companyName.ifBlank { "Industrial Mill Morbi" }
                            )
                        },
                        onSendWhatsAppInquiry = { msg -> openWhatsApp(msg) },
                        onNavigateToInquiries = { currentTab = NavTab.PENDING_INQUIRY },
                        onNavigateToCatalogue = { currentTab = NavTab.CATALOGUE }
                    )
                }
                NavTab.PENDING_INQUIRY -> {
                    PendingInquiryScreen(
                        uiState = uiState,
                        onSaveInquiry = { name, company, qty, product, unit, notes ->
                            viewModel.addPendingInquiry(name, company, qty, product, unit, notes = notes)
                        },
                        onUpdateQuantity = { id, qty -> viewModel.updatePendingQuantity(id, qty) },
                        onDeleteInquiry = { id -> viewModel.deletePendingInquiry(id) },
                        onClearAll = { viewModel.clearAllPendingInquiries() },
                        onSendWhatsApp = { msg -> openWhatsApp(msg) },
                        onSendEmail = { sub, body -> sendEmail(sub, body) },
                        onNavigateToCatalogue = { currentTab = NavTab.CATALOGUE }
                    )
                }
                NavTab.INQUIRY -> {
                    QuoteScreen(
                        uiState = uiState,
                        onUpdateQuantity = { id, qty -> viewModel.updateQuoteItemQuantity(id, qty) },
                        onRemoveItem = { viewModel.removeQuoteItem(it) },
                        onClearAll = { viewModel.clearQuote() },
                        onUpdateCustomerInfo = { name, company, phone, loc, urgent, notes ->
                            viewModel.updateCustomerInfo(name, company, phone, loc, urgent, notes)
                        },
                        onSendWhatsApp = { msg -> openWhatsApp(msg) },
                        onSendEmail = { sub, body -> sendEmail(sub, body) },
                        onCallClick = { makePhoneCall() },
                        onNavigateToCatalogue = { currentTab = NavTab.CATALOGUE }
                    )
                }
                NavTab.TOOLS -> {
                    ToolsScreen(
                        uiState = uiState,
                        onCalculateDilution = { stock, target, vol ->
                            viewModel.calculateDilution(stock, target, vol)
                        },
                        onCalculateMassVolume = { vol, spGr ->
                            viewModel.calculateMassVolume(vol, spGr)
                        }
                    )
                }
                NavTab.COMPANY -> {
                    AboutContactScreen(
                        onCallClick = { showPhonePickerSheet = true },
                        onWhatsAppClick = { openWhatsApp("Hello Nirmaladevi Care, I would like to know more about your company and chemical products.") },
                        onCallNumber = { num -> makePhoneCall(num) },
                        onWhatsAppNumber = { num, msg -> openWhatsApp(msg, num) },
                        onShareWhatsAppMessage = { msg -> openWhatsApp(msg) },
                        onEmailClick = { sendEmail("Business Inquiry", "Dear Nirmaladevi Care Team,\n\nWe would like to inquire about your chemical trading supplies.") },
                        onOpenMapClick = { openGoogleMaps() },
                        onOpenDepotMap = { url, fallback -> openGoogleMaps(url, fallback) },
                        onFacebookClick = { openUrl("https://www.facebook.com/NirmaladevicarePvt.Ltd") },
                        onInstagramClick = { openUrl("https://www.instagram.com/nirmaladevicare") },
                        onPrivacyPolicyClick = { isLegalOpen = true },
                        onOpenCataloguePdf = { isCataloguePdfOpen = true },
                        onShareAppClick = { shareApp() }
                    )
                }
            }
        }

        // Legal and Privacy Policy Full Screen Dialog
        if (isLegalOpen) {
            LegalAndPrivacyScreen(
                onBackClick = { isLegalOpen = false },
                onContactSupportClick = {
                    sendEmail(
                        "Legal & Privacy Policy Inquiry - Nirmaladevi Care",
                        "Dear Nirmaladevi Care Privacy & Legal Team,\n\nI have an inquiry regarding data privacy / legal terms:\n\n"
                    )
                }
            )
        }

        // Product Detail Bottom Sheet
        if (uiState.isProductDetailOpen && uiState.selectedProductForDetail != null) {
            ProductDetailSheet(
                product = uiState.selectedProductForDetail!!,
                onDismiss = { viewModel.closeProductDetail() },
                onAddToQuote = { product, grade, qty, unit, wholesale, remarks ->
                    viewModel.addToQuote(product, grade, qty, unit, wholesale, remarks)
                },
                onDirectWhatsApp = { msg -> openWhatsApp(msg) }
            )
        }

        // Official Product Catalogue PDF Full Screen Dialog
        if (isCataloguePdfOpen) {
            ProductCatalogueScreen(
                onDismiss = { isCataloguePdfOpen = false },
                onProductClick = { product ->
                    isCataloguePdfOpen = false
                    viewModel.openProductDetail(product)
                },
                onDirectInquiry = { productName ->
                    isCataloguePdfOpen = false
                    openWhatsApp("Hello Nirmaladevi Care, I am inquiring about $productName from your official product catalogue.")
                },
                onShareApp = { shareApp() }
            )
        }

        // Contact Phone Picker Bottom Sheet
        if (showPhonePickerSheet) {
            ContactPhonePickerDialog(
                onDismissRequest = { showPhonePickerSheet = false },
                onCallNumber = { num -> makePhoneCall(num) },
                onWhatsAppNumber = { num -> openWhatsApp("Hello Nirmaladevi Care, I am contacting you from your mobile app.", num) },
                onCopyNumber = { num ->
                    clipboardManager.setText(AnnotatedString(num))
                    Toast.makeText(context, "Copied to clipboard: $num", Toast.LENGTH_SHORT).show()
                }
            )
        }
        }
        }
    }
}
