package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CategorySelectionEntity
import com.example.data.local.SelectedProductEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChemicalUiState

/**
 * Product Selection Screen with checkbox items for each product category (Acids, Alkalis, etc.)
 * that triggers an immediate persistent update to the Room database whenever selected or deselected.
 */
@Composable
fun ProductSelectionScreen(
    uiState: ChemicalUiState,
    onCategoryToggle: (categoryId: String, isSelected: Boolean) -> Unit,
    onProductToggle: (productId: String, isSelected: Boolean) -> Unit,
    onSelectAllToggle: (isSelected: Boolean) -> Unit,
    onToggleCategoryExpanded: (categoryId: String) -> Unit,
    onExpandAll: () -> Unit,
    onCollapseAll: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleOnlySelected: () -> Unit,
    onAddToPendingInquiry: () -> Unit,
    onSendWhatsAppInquiry: (String) -> Unit,
    onNavigateToInquiries: () -> Unit,
    onNavigateToCatalogue: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Filter categories & products based on search query and "only selected" filter
    val searchQuery = uiState.selectionSearchQuery.trim().lowercase()

    val filteredCategories = remember(uiState.categorySelections, uiState.selectedProductsInDb, searchQuery, uiState.selectionFilterOnlySelected) {
        uiState.categorySelections.filter { cat ->
            val catProducts = uiState.selectedProductsInDb.filter { it.categoryId == cat.categoryId }

            val matchesSearch = searchQuery.isEmpty() ||
                    cat.categoryTitle.lowercase().contains(searchQuery) ||
                    catProducts.any { it.productName.lowercase().contains(searchQuery) || it.chemicalFormula.lowercase().contains(searchQuery) }

            val matchesSelectedFilter = !uiState.selectionFilterOnlySelected ||
                    cat.isSelected ||
                    catProducts.any { it.isSelected }

            matchesSearch && matchesSelectedFilter
        }
    }

    Scaffold(
        containerColor = WhiteCanvas,
        bottomBar = {
            if (uiState.selectedProductCount > 0) {
                SelectionBottomActionBar(
                    selectedCount = uiState.selectedProductCount,
                    selectedCategoriesCount = uiState.selectedCategoryCount,
                    onAddToPendingInquiry = onAddToPendingInquiry,
                    onSendWhatsApp = {
                        val selectedProducts = uiState.selectedProductsInDb.filter { it.isSelected }
                        val summary = selectedProducts.joinToString("\n") {
                            "- ${it.productName} (${it.chemicalFormula}) [${it.categoryTitle}]"
                        }
                        val message = "Hello Nirmaladevi Care, I have selected the following chemicals from your catalog for quotation inquiry:\n\n$summary\n\nPlease share price quote and delivery timeline to Morbi."
                        onSendWhatsAppInquiry(message)
                    },
                    onNavigateToInquiries = onNavigateToInquiries
                )
            }
        },
        modifier = modifier.fillMaxSize().testTag("product_selection_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card with Room DB Synchronization badge
            item(key = "header_banner") {
                SelectionHeaderBanner(
                    uiState = uiState,
                    onSelectAllToggle = onSelectAllToggle,
                    onExpandAll = onExpandAll,
                    onCollapseAll = onCollapseAll
                )
            }

            // Search and Filter Bar
            item(key = "search_filter_bar") {
                SelectionFilterToolbar(
                    searchQuery = uiState.selectionSearchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    filterOnlySelected = uiState.selectionFilterOnlySelected,
                    onToggleFilterOnlySelected = onToggleOnlySelected,
                    selectedCount = uiState.selectedProductCount,
                    onSelectAllToggle = onSelectAllToggle
                )
            }

            // Empty state when filter has no matches
            if (filteredCategories.isEmpty()) {
                item(key = "empty_state") {
                    EmptySelectionState(
                        searchQuery = uiState.selectionSearchQuery,
                        onlySelected = uiState.selectionFilterOnlySelected,
                        onClearFilters = {
                            onSearchQueryChange("")
                            if (uiState.selectionFilterOnlySelected) onToggleOnlySelected()
                        }
                    )
                }
            } else {
                // Category Checkbox Cards with nested products
                items(
                    items = filteredCategories,
                    key = { it.categoryId }
                ) { category ->
                    val productsInCategory = uiState.selectedProductsInDb.filter { it.categoryId == category.categoryId }
                    val isExpanded = uiState.expandedCategoryIds.contains(category.categoryId)

                    CategoryCheckboxCard(
                        category = category,
                        products = productsInCategory,
                        isExpanded = isExpanded,
                        searchQuery = searchQuery,
                        onCategoryToggle = { isSelected ->
                            onCategoryToggle(category.categoryId, isSelected)
                        },
                        onProductToggle = { productId, isSelected ->
                            onProductToggle(productId, isSelected)
                        },
                        onToggleExpand = {
                            onToggleCategoryExpanded(category.categoryId)
                        }
                    )
                }
            }

            // Room Database architecture footer note
            item(key = "footer_room_note") {
                RoomDatabaseFooterNote(
                    selectedCategoriesCount = uiState.selectedCategoryCount,
                    selectedProductCount = uiState.selectedProductCount
                )
            }
        }
    }
}

/**
 * Top header banner explaining the real-time Room DB sync and live selection metrics.
 */
@Composable
private fun SelectionHeaderBanner(
    uiState: ChemicalUiState,
    onSelectAllToggle: (Boolean) -> Unit,
    onExpandAll: () -> Unit,
    onCollapseAll: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("selection_header_banner")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentBlueSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Checklist,
                            contentDescription = "Category Selection",
                            tint = AccentBlueDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Category Selection",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = "Room Local Database Sync",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AccentBlueDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // Room DB Live Status Pill
                Surface(
                    color = AccentBlueSoft,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AccentBlue)
                        )
                        Text(
                            text = "Room DB Active",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AccentBlueDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Select industrial chemical categories (Acids, Alkalis, Salts, Water Treatment, etc.) to customize your procurement quotation. Every checkbox toggle immediately persists to Room Database.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Metrics Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricChip(
                    label = "Categories Selected",
                    value = "${uiState.selectedCategoryCount} / ${uiState.categorySelections.size}",
                    icon = Icons.Filled.Category,
                    modifier = Modifier.weight(1f)
                )
                MetricChip(
                    label = "Products Selected",
                    value = "${uiState.selectedProductCount} / ${uiState.selectedProductsInDb.size}",
                    icon = Icons.Filled.Science,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Toggle Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onSelectAllToggle(true) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = AccentBlueSoft,
                            contentColor = AccentBlueDark
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("select_all_categories_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DoneAll,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Select All", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    OutlinedButton(
                        onClick = { onSelectAllToggle(false) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = TextSecondary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorderStrong),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("deselect_all_categories_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ClearAll,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear All", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(
                        onClick = onExpandAll,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            "Expand All",
                            style = MaterialTheme.typography.labelSmall.copy(color = AccentBlueDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        )
                    }
                    TextButton(
                        onClick = onCollapseAll,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            "Collapse",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 11.sp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricChip(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        color = WhiteSurfaceVariant,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AccentBlueDark,
                modifier = Modifier.size(18.dp)
            )
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
/**
 * Filter toolbar with real-time search and filter chips.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectionFilterToolbar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterOnlySelected: Boolean,
    onToggleFilterOnlySelected: () -> Unit,
    selectedCount: Int,
    onSelectAllToggle: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    "Search categories or chemicals (e.g. Acid, Caustic, H2SO4)...",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextPlaceholder)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = AccentBlue,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "Clear Search",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = WhiteSurface,
                unfocusedContainerColor = WhiteSurface,
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = WhiteBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("selection_search_field")
        )

        // Filter Chips Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = !filterOnlySelected,
                onClick = { if (filterOnlySelected) onToggleFilterOnlySelected() },
                label = { Text("All Categories") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AccentBlue,
                    selectedLabelColor = Color.White,
                    containerColor = WhiteSurface,
                    labelColor = TextSecondary
                ),
                shape = RoundedCornerShape(8.dp),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = !filterOnlySelected,
                    borderColor = if (!filterOnlySelected) AccentBlue else WhiteBorder
                )
            )

            FilterChip(
                selected = filterOnlySelected,
                onClick = onToggleFilterOnlySelected,
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Selected Only")
                        if (selectedCount > 0) {
                            Surface(
                                color = if (filterOnlySelected) Color.White else AccentBlueSoft,
                                shape = CircleShape
                            ) {
                                Text(
                                    text = selectedCount.toString(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (filterOnlySelected) AccentBlueDark else AccentBlueDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AccentBlue,
                    selectedLabelColor = Color.White,
                    containerColor = WhiteSurface,
                    labelColor = TextSecondary
                ),
                shape = RoundedCornerShape(8.dp),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = filterOnlySelected,
                    borderColor = if (filterOnlySelected) AccentBlue else WhiteBorder
                ),
                modifier = Modifier.testTag("filter_selected_only_chip")
            )
        }
    }
}

/**
 * Card representing a chemical product category (Acids, Alkalis, Salts, etc.)
 * with an interactive category-level checkbox and expandable nested products.
 */
@Composable
private fun CategoryCheckboxCard(
    category: CategorySelectionEntity,
    products: List<SelectedProductEntity>,
    isExpanded: Boolean,
    searchQuery: String,
    onCategoryToggle: (Boolean) -> Unit,
    onProductToggle: (productId: String, isSelected: Boolean) -> Unit,
    onToggleExpand: () -> Unit
) {
    val selectedCount = products.count { it.isSelected }
    val totalCount = products.size
    val isAllSelected = selectedCount == totalCount && totalCount > 0
    val isPartiallySelected = selectedCount > 0 && selectedCount < totalCount

    val categoryIcon = getCategoryIcon(category.categoryId)
    val borderColor by animateColorAsState(
        targetValue = if (isAllSelected || isPartiallySelected) AccentBlue.copy(alpha = 0.7f) else WhiteBorder,
        label = "categoryBorderColor"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_category_${category.categoryId}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Category Header Row with Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Category Checkbox
                if (isPartiallySelected) {
                    TriStateCheckbox(
                        state = ToggleableState.Indeterminate,
                        onClick = {
                            // If partial, toggle to fully selected
                            onCategoryToggle(true)
                        },
                        colors = CheckboxDefaults.colors(
                            checkmarkColor = Color.White,
                            checkedColor = AccentBlue,
                            uncheckedColor = TextMuted
                        ),
                        modifier = Modifier.testTag("checkbox_category_${category.categoryId}")
                    )
                } else {
                    Checkbox(
                        checked = isAllSelected,
                        onCheckedChange = { isChecked ->
                            onCategoryToggle(isChecked)
                        },
                        colors = CheckboxDefaults.colors(
                            checkmarkColor = Color.White,
                            checkedColor = AccentBlue,
                            uncheckedColor = TextMuted
                        ),
                        modifier = Modifier.testTag("checkbox_category_${category.categoryId}")
                    )
                }

                // Category Icon
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isAllSelected || isPartiallySelected) AccentBlueSoft
                            else WhiteSurfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = categoryIcon,
                        contentDescription = category.categoryTitle,
                        tint = if (isAllSelected || isPartiallySelected) AccentBlueDark else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Category Title and Selection Badge
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = category.categoryTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                        if (isAllSelected) {
                            Surface(
                                color = AccentBlueSoft,
                                shape = RoundedCornerShape(4.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "ROOM DB ACTIVE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AccentBlueDark,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 8.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "$selectedCount of $totalCount chemicals selected",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (selectedCount > 0) AccentBlueDark else TextMuted,
                            fontWeight = if (selectedCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    )
                }

                // Quick Select / Deselect Pill
                Surface(
                    color = if (isAllSelected) AccentBlueSoft else WhiteSurfaceVariant,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isAllSelected) AccentBlue.copy(alpha = 0.3f) else WhiteBorder
                    ),
                    modifier = Modifier.clickable {
                        onCategoryToggle(!isAllSelected)
                    }
                ) {
                    Text(
                        text = if (isAllSelected) "Deselect" else "Select All",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isAllSelected) AccentBlueDark else TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Expand / Collapse Chevron
                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = TextMuted
                    )
                }
            }

            // Expandable Products Nested List
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WhiteSurfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HorizontalDivider(
                        color = WhiteBorder,
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    products.forEach { product ->
                        val isHighlighted = searchQuery.isNotEmpty() &&
                                (product.productName.lowercase().contains(searchQuery) ||
                                 product.chemicalFormula.lowercase().contains(searchQuery))

                        ProductCheckboxRow(
                            product = product,
                            isHighlighted = isHighlighted,
                            onToggle = { isChecked ->
                                onProductToggle(product.productId, isChecked)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual chemical product checkbox row item.
 */
@Composable
private fun ProductCheckboxRow(
    product: SelectedProductEntity,
    isHighlighted: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val rowBackground = if (product.isSelected) {
        AccentBlueSoft.copy(alpha = 0.45f)
    } else if (isHighlighted) {
        WhiteSurfaceSubtle
    } else {
        Color.Transparent
    }

    Surface(
        color = rowBackground,
        shape = RoundedCornerShape(8.dp),
        border = if (product.isSelected) {
            androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.3f))
        } else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!product.isSelected) }
            .testTag("row_product_${product.productId}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Product Checkbox
            Checkbox(
                checked = product.isSelected,
                onCheckedChange = { isChecked -> onToggle(isChecked) },
                colors = CheckboxDefaults.colors(
                    checkmarkColor = Color.White,
                    checkedColor = AccentBlue,
                    uncheckedColor = TextMuted
                ),
                modifier = Modifier
                    .size(20.dp)
                    .testTag("checkbox_product_${product.productId}")
            )

            // Product Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = product.productName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (product.isSelected) TextPrimary else TextPrimary.copy(alpha = 0.85f),
                            fontWeight = if (product.isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (product.chemicalFormula.isNotEmpty()) {
                        FormulaBadge(formula = product.chemicalFormula)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (product.purity.isNotEmpty()) {
                        Text(
                            text = product.purity,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = AccentBlueDark,
                                fontSize = 11.sp
                            )
                        )
                    }
                    if (product.packaging.isNotEmpty()) {
                        Text(
                            text = "• ${product.packaging}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Live status badge
            if (product.isSelected) {
                Surface(
                    color = AccentBlueSoft,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "ROOM DB",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AccentBlueDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Formula badge chip.
 */
@Composable
private fun FormulaBadge(formula: String) {
    Surface(
        color = WhiteSurface,
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, AccentBlue.copy(alpha = 0.4f))
    ) {
        Text(
            text = formula,
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccentBlueDark,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
        )
    }
}

/**
 * Bottom action bar shown when at least one product is selected in Room DB.
 */
@Composable
private fun SelectionBottomActionBar(
    selectedCount: Int,
    selectedCategoriesCount: Int,
    onAddToPendingInquiry: () -> Unit,
    onSendWhatsApp: () -> Unit,
    onNavigateToInquiries: () -> Unit
) {
    Surface(
        color = WhiteSurface,
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier.fillMaxWidth().testTag("selection_bottom_action_bar")
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AccentBlue)
                    )
                    Text(
                        text = "$selectedCount chemical${if (selectedCount > 1) "s" else ""} selected in Room DB",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                TextButton(
                    onClick = onNavigateToInquiries,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        "View Inquiries",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AccentBlueDark,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = AccentBlueDark,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Add to Pending Inquiries Button
                Button(
                    onClick = onAddToPendingInquiry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("add_selected_to_inquiries_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Add to Inquiry DB",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Send via WhatsApp Button
                Button(
                    onClick = onSendWhatsApp,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WhatsAppGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("whatsapp_selected_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WhatsApp Quote",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

/**
 * Empty state view when filters return no matches.
 */
@Composable
private fun EmptySelectionState(
    searchQuery: String,
    onlySelected: Boolean,
    onClearFilters: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.SearchOff,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = if (onlySelected) "No chemicals currently selected" else "No matching chemical categories found",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = if (onlySelected)
                    "Check boxes for Acids, Alkalis, Salts, or other categories above to track selections in Room DB."
                else
                    "No categories match \"$searchQuery\". Try searching for Acids, Alkalis, or clear filters.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    lineHeight = 18.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Button(
                onClick = onClearFilters,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Show All Categories")
            }
        }
    }
}

/**
 * Footer information card summarizing Room DB local persistence behavior.
 */
@Composable
private fun RoomDatabaseFooterNote(
    selectedCategoriesCount: Int,
    selectedProductCount: Int
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
        modifier = Modifier.fillMaxWidth().testTag("room_database_footer_note")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Storage,
                contentDescription = "Room Local Database",
                tint = AccentBlueDark,
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Room Local Database Architecture",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
                Text(
                    text = "All category selections ($selectedCategoriesCount active) and chemical products ($selectedProductCount checked) are persisted offline in SQLite via Room DAO and observed with Kotlin Flows.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}

private fun getCategoryIcon(categoryId: String): ImageVector {
    return when (categoryId) {
        "acid" -> Icons.Filled.Science
        "alkali" -> Icons.Filled.Biotech
        "other" -> Icons.Filled.Grain
        "water" -> Icons.Filled.WaterDrop
        "descalent" -> Icons.Filled.CleaningServices
        else -> Icons.Filled.Category
    }
}
