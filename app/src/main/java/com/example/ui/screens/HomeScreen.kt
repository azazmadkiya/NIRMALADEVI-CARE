package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ChemicalProduct
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChemicalUiState

@Composable
fun HomeScreen(
    uiState: ChemicalUiState,
    onCategorySelect: (com.example.data.model.ChemicalCategory) -> Unit,
    onSearchChange: (String) -> Unit,
    onProductClick: (ChemicalProduct) -> Unit,
    onQuickAddClick: (ChemicalProduct) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onBrowseProductsClick: () -> Unit,
    onContactClick: () -> Unit,
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onNavigateToSelection: () -> Unit = {},
    onOpenCataloguePdf: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteCanvas),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // ── HERO SECTION ──
        item {
            HeroSection(
                onBrowseProductsClick = onBrowseProductsClick,
                onContactClick = onContactClick,
                onOpenCataloguePdf = onOpenCataloguePdf,
                onProductFormulaClick = { formula ->
                    onSearchChange(formula)
                }
            )
        }

        // ── SEARCH & FILTER CONTROLS ──
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                ChemicalSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = onSearchChange
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    CategoryFilterChips(
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = onCategorySelect
                    )
                }
            }
        }

        // ── ROOM DB CATEGORY SELECTION BANNER ──
        item {
            Surface(
                color = WhiteSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.35f)),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onNavigateToSelection() }
                    .testTag("home_category_selection_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentBlueSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Checklist,
                            contentDescription = "Room Category Selection",
                            tint = AccentBlueDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Category Selection (Room DB)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            )
                            if (uiState.selectedProductCount > 0) {
                                Surface(
                                    color = AccentBlue,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "${uiState.selectedProductCount} active",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Check Acids, Alkalis, Salts & Cleaners with real-time Room DB sync",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Category Selection",
                        tint = AccentBlueDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // ── OFFICIAL PRODUCT CATALOGUE PDF BANNER ──
        item {
            Surface(
                color = WhiteSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentRed.copy(alpha = 0.4f)),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onOpenCataloguePdf() }
                    .testTag("home_product_catalogue_pdf_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentRedSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF Catalogue",
                            tint = AccentRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Official Product Catalogue",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            )
                            Surface(
                                color = AccentRedSoft,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "PDF DOWNLOAD",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AccentRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Download 24+ product catalogue PDF & godown details on mobile",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Open PDF",
                        tint = AccentBlueDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // ── SECTION HEADER ──
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "OUR CATALOGUE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            fontSize = 11.sp
                        ),
                        color = AccentBlueDark
                    )
                    Text(
                        text = "Chemical Products (${uiState.filteredProducts.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        ),
                        color = TextPrimary
                    )
                }
            }
        }

        // ── EMPTY STATE IF NO RESULTS ──
        if (uiState.filteredProducts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No chemicals match your search",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try searching by formula (e.g. HCl, H2SO4, NaOH) or clear filters.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                onSearchChange("")
                                onCategorySelect(com.example.data.model.ChemicalCategory.ALL)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlueDark),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.5f))
                        ) {
                            Text("Reset Search")
                        }
                    }
                }
            }
        } else {
            // ── PRODUCT CARDS ──
            items(
                items = uiState.filteredProducts,
                key = { it.id }
            ) { product ->
                ProductCard(
                    product = product,
                    isFavorite = uiState.favorites.contains(product.id),
                    onCardClick = { onProductClick(product) },
                    onQuickAdd = { onQuickAddClick(product) },
                    onToggleFavorite = { onToggleFavorite(product.id) }
                )
            }
        }

        // ── QUICK CALL TO ACTION FOOTER BANNER ──
        item {
            InquiryCtaBanner(
                onCallClick = onCallClick,
                onWhatsAppClick = onWhatsAppClick
            )
        }
    }
}

@Composable
fun HeroSection(
    onBrowseProductsClick: () -> Unit,
    onContactClick: () -> Unit,
    onOpenCataloguePdf: () -> Unit = {},
    onProductFormulaClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFEBF5FF), WhiteCanvas)
                )
            )
            .padding(16.dp)
    ) {
        Column {
            // Badge
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = AccentBlueSoft,
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.35f)),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(AccentBlue)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WHOLESALE & RETAIL · MORBI, GUJARAT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp
                        ),
                        color = AccentBlueDark
                    )
                }
            }

            // Official Brand Logo Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.25f)),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .testTag("hero_brand_logo")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ncpl_logo),
                    contentDescription = "NIRMALA Brand Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Headline
            Text(
                text = "Industrial\nChemicals",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp,
                    lineHeight = 36.sp,
                    letterSpacing = 0.5.sp
                ),
                color = TextPrimary
            )
            Text(
                text = "You Can Trust.",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp,
                    lineHeight = 36.sp
                ),
                color = AccentBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Nirmaladevi Care Pvt. Ltd. is a leading chemical trading company supplying high-purity industrial, technical, and food-grade chemicals across Gujarat and India.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onBrowseProductsClick,
                    modifier = Modifier
                        .weight(1.1f)
                        .height(42.dp)
                        .testTag("hero_browse_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ListAlt,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "24+ Products",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                FilledTonalButton(
                    onClick = onOpenCataloguePdf,
                    modifier = Modifier
                        .weight(1.05f)
                        .height(42.dp)
                        .testTag("hero_catalogue_pdf_button"),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = AccentRedSoft,
                        contentColor = AccentRed
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = AccentRed,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PDF Sheet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                OutlinedButton(
                    onClick = onContactClick,
                    modifier = Modifier
                        .weight(0.95f)
                        .height(42.dp)
                        .testTag("hero_contact_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlueDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneInTalk,
                        contentDescription = null,
                        tint = AccentBlueDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Contact",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(WhiteSurface)
                    .border(1.dp, WhiteBorder, RoundedCornerShape(8.dp))
                    .padding(vertical = 10.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(value = "24+", label = "Products", highlight = "+")
                Divider(
                    color = WhiteBorder,
                    modifier = Modifier
                        .height(32.dp)
                        .width(1.dp)
                )
                StatItem(value = "B2B / B2C", label = "Bulk & Retail", highlight = "")
                Divider(
                    color = WhiteBorder,
                    modifier = Modifier
                        .height(32.dp)
                        .width(1.dp)
                )
                StatItem(value = "Morbi", label = "Gujarat, IN", highlight = " ★")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Formula Chips Scroll
            Text(
                text = "POPULAR FORMULAS · TAP TO FILTER",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                color = TextMuted,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            val popularFormulas = listOf("H₂SO₄", "HCl", "HNO₃", "H₂O₂", "NaOH", "KOH", "C₆H₈O₇", "NaOCl")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(popularFormulas) { formula ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = WhiteSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.25f)),
                        modifier = Modifier.clickable { onProductFormulaClick(formula) }
                    ) {
                        Text(
                            text = formula,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = AccentBlueDark,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String, highlight: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                ),
                color = TextPrimary
            )
            if (highlight.isNotEmpty()) {
                Text(
                    text = highlight,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    ),
                    color = AccentBlue
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = TextMuted
        )
    }
}

@Composable
fun ProductCard(
    product: ChemicalProduct,
    isFavorite: Boolean,
    onCardClick: () -> Unit,
    onQuickAdd: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onCardClick() }
            .testTag("product_card_${product.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                            fontSize = 10.sp
                        ),
                        color = AccentBlueDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                FormulaBadge(
                    formula = product.formula,
                    backgroundColor = AccentBlueSoft,
                    textColor = AccentBlueDark
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = product.description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Badges & Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (product.hasWholesale) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = AccentBlueSoft,
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, AccentBlue.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "WHOLESALE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = AccentBlueDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (product.hasRetail) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = AccentTealSoft,
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, AccentTeal.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "RETAIL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = AccentTealDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) DangerRed else TextPlaceholder,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    FilledTonalButton(
                        onClick = onQuickAdd,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = AccentBlue,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("quick_inquire_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "INQUIRE",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InquiryCtaBanner(
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.LocalShipping,
                contentDescription = null,
                tint = AccentBlueDark,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Need Bulk Road Tanker or Urgent Supply?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Text(
                text = "Direct delivery to ceramic factories, industrial units, and chemical processors across Morbi & Gujarat.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(vertical = 6.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onWhatsAppClick,
                    modifier = Modifier.weight(1f),
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
                    Text("WhatsApp", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onCallClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call Desk", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

