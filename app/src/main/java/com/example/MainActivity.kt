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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChemicalCategory
import com.example.ui.components.AppTopBar
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChemicalViewModel
import java.net.URLEncoder

enum class NavTab(val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    CATALOGUE("Catalogue", Icons.Filled.Science, Icons.Outlined.Science),
    INQUIRY("Inquiry Quote", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
    TOOLS("Tools & Calc", Icons.Filled.Calculate, Icons.Outlined.Calculate),
    COMPANY("Company", Icons.Filled.Business, Icons.Outlined.Business)
}

class MainActivity : ComponentActivity() {

    private val viewModel: ChemicalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: ChemicalViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf(NavTab.CATALOGUE) }
    var isLegalOpen by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

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
    fun makePhoneCall() {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:+918200332632")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Call: +91 82003 32632", Toast.LENGTH_LONG).show()
        }
    }

    fun openWhatsApp(message: String) {
        try {
            val encodedMsg = URLEncoder.encode(message, "UTF-8")
            val url = "https://wa.me/918200332632?text=$encodedMsg"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp: +91 82003 32632", Toast.LENGTH_LONG).show()
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

    fun openGoogleMaps() {
        try {
            val mapUri = Uri.parse("https://maps.app.goo.gl/Sx54aiY4ezVeYyqY9")
            val intent = Intent(Intent.ACTION_VIEW, mapUri)
            context.startActivity(intent)
        } catch (e: Exception) {
            val geoUri = Uri.parse("geo:0,0?q=Panchasar+Road+Morbi+Gujarat")
            val fallbackIntent = Intent(Intent.ACTION_VIEW, geoUri)
            context.startActivity(fallbackIntent)
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BrandInk,
        topBar = {
            AppTopBar(
                onCallClick = { makePhoneCall() },
                onWhatsAppClick = { openWhatsApp("Hello Nirmaladevi Care, I would like to inquire about industrial chemicals.") },
                quoteItemCount = uiState.quoteItems.size,
                onQuoteClick = { currentTab = NavTab.INQUIRY }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = BrandSteel,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            if (tab == NavTab.INQUIRY && uiState.quoteItems.isNotEmpty()) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = BrandAcid,
                                            contentColor = BrandInk
                                        ) {
                                            Text(text = uiState.quoteItems.size.toString())
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.title
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title
                                )
                            }
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 10.sp
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandInk,
                            selectedTextColor = BrandAcid,
                            indicatorColor = BrandAcid,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = BrandSteelLight,
                        contentColor = Color.White,
                        actionColor = BrandAcid,
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
                        onWhatsAppClick = { openWhatsApp("Hello Nirmaladevi Care, I am interested in placing an industrial chemical order.") }
                    )
                }
                NavTab.INQUIRY -> {
                    QuoteScreen(
                        uiState = uiState,
                        onUpdateQuantity = { id, qty -> viewModel.updateQuoteItemQuantity(id, qty) },
                        onRemoveItem = { viewModel.removeQuoteItem(it) },
                        onClearAll = { viewModel.clearQuote() },
                        onUpdateCustomerInfo = { name, phone, loc, urgent, notes ->
                            viewModel.updateCustomerInfo(name, phone, loc, urgent, notes)
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
                        onCallClick = { makePhoneCall() },
                        onWhatsAppClick = { openWhatsApp("Hello Nirmaladevi Care, I would like to know more about your company and chemical products.") },
                        onEmailClick = { sendEmail("Business Inquiry", "Dear Nirmaladevi Care Team,\n\nWe would like to inquire about your chemical trading supplies.") },
                        onOpenMapClick = { openGoogleMaps() },
                        onFacebookClick = { openUrl("https://www.facebook.com/NirmaladevicarePvt.Ltd") },
                        onInstagramClick = { openUrl("https://www.instagram.com/nirmaladevicare") },
                        onPrivacyPolicyClick = { isLegalOpen = true }
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
    }
}
