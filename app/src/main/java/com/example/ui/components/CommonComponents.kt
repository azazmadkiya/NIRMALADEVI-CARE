package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ChemicalCategory
import com.example.data.model.CompanyInfo
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    quoteItemCount: Int = 0,
    onQuoteClick: () -> Unit,
    onCataloguePdfClick: () -> Unit = {},
    onShareClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        shadowElevation = 1.dp
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ncpl_logo_foreground),
                            contentDescription = "NIRMALA Brand Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Column {
                        Text(
                            text = "NIRMALADEVI CARE",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                fontSize = 13.5.sp
                            ),
                            color = TextPrimary,
                            maxLines = 1,
                            softWrap = false
                        )
                        Text(
                            text = "PVT. LTD. · CHEMICAL TRADING",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                fontSize = 8.5.sp
                            ),
                            color = AccentBlueDark,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            },
            actions = {
                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier
                        .testTag("topbar_share_button")
                        .size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share App",
                        tint = AccentBlue,
                        modifier = Modifier.size(19.dp)
                    )
                }
                IconButton(
                    onClick = onCataloguePdfClick,
                    modifier = Modifier
                        .testTag("topbar_catalogue_pdf_button")
                        .size(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(AccentRedSoft)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PictureAsPdf,
                        contentDescription = "Open Product Catalogue",
                        tint = AccentRed,
                        modifier = Modifier.size(19.dp)
                    )
                }
                IconButton(
                    onClick = onWhatsAppClick,
                    modifier = Modifier
                        .testTag("topbar_whatsapp_button")
                        .size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Chat,
                        contentDescription = "WhatsApp",
                        tint = WhatsAppGreenDark,
                        modifier = Modifier.size(19.dp)
                    )
                }
                IconButton(
                    onClick = onCallClick,
                    modifier = Modifier
                        .testTag("topbar_call_button")
                        .size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Phone,
                        contentDescription = "Call Us",
                        tint = AccentBlueDark,
                        modifier = Modifier.size(19.dp)
                    )
                }
                BadgedBox(
                    badge = {
                        if (quoteItemCount > 0) {
                            Badge(
                                containerColor = AccentBlue,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = quoteItemCount.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    IconButton(
                        onClick = onQuoteClick,
                        modifier = Modifier
                            .testTag("topbar_quote_button")
                            .size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "Inquiry Cart",
                            tint = AccentBlueDark,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = WhiteSurface,
                titleContentColor = TextPrimary,
                actionIconContentColor = TextPrimary
            )
        )
        // Subtle divider border below TopAppBar for crisp white card separation
        HorizontalDivider(
            thickness = 1.dp,
            color = WhiteBorder
        )
    }
}

@Composable
fun ChemicalSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        color = WhiteSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, WhiteBorder),
        shadowElevation = 1.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search icon",
                tint = AccentBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = "Search 24+ chemicals, formulas (HCl, NaOH, H₂SO₄)...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPlaceholder,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chemical_search_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = AccentBlue
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryFilterChips(
    selectedCategory: ChemicalCategory,
    onCategorySelected: (ChemicalCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ChemicalCategory.values().forEach { category ->
            val isSelected = selectedCategory == category
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) AccentBlue else WhiteSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) AccentBlue else WhiteBorder
                ),
                shadowElevation = if (isSelected) 2.dp else 0.dp,
                modifier = Modifier
                    .clickable { onCategorySelected(category) }
                    .testTag("category_chip_${category.id}")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            fontSize = 12.sp
                        ),
                        color = if (isSelected) Color.White else TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun FormulaBadge(
    formula: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = AccentBlueSoft,
    textColor: Color = AccentBlueDark
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, AccentBlue.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Text(
            text = formula,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun HazardBadge(hazard: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = AccentRedSoft,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, AccentRed.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = AccentRed,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = hazard,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = AccentRed,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactPhonePickerDialog(
    onDismissRequest: () -> Unit,
    onCallNumber: (String) -> Unit,
    onWhatsAppNumber: (String) -> Unit,
    onCopyNumber: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = WhiteSurface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AccentBlueSoft,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = AccentBlueDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "CALL NIRMALADEVI CARE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            fontSize = 10.sp
                        ),
                        color = AccentBlueDark
                    )
                    Text(
                        text = "Select Contact Number",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            CompanyInfo.CONTACT_PHONES.forEach { item ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (item.isPrimary) AccentBlueSoft.copy(alpha = 0.45f) else WhiteSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (item.isPrimary) AccentBlue.copy(alpha = 0.35f) else WhiteBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onCallNumber(item.number)
                            onDismissRequest()
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (item.isPrimary) AccentBlue else AccentBlueSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = if (item.isPrimary) Color.White else AccentBlueDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.title.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.5.sp
                                    ),
                                    color = if (item.isPrimary) AccentBlueDark else TextMuted
                                )
                                if (item.isPrimary) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = AccentBlueDark
                                    ) {
                                        Text(
                                            text = "PRIMARY",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 7.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            ),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = item.number,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = TextPrimary
                            )
                            if (item.subtitle.isNotBlank()) {
                                Text(
                                    text = item.subtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.5.sp
                                    ),
                                    color = TextSecondary
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = {
                                    onCallNumber(item.number)
                                    onDismissRequest()
                                },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(AccentBlueDark)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Call ${item.number}",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    onWhatsAppNumber(item.number)
                                    onDismissRequest()
                                },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(WhatsAppGreenDark)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = "WhatsApp ${item.number}",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    onCopyNumber(item.number)
                                },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(WhiteSurface)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy ${item.number}",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

