package com.example.data.model

data class CatalogueItem(
    val id: String,
    val name: String,
    val subtext: String = "",
    val column: Int // 1 = Left, 2 = Right
)

object ProductCatalogueData {
    const val TITLE = "NIRMALA DEVI CARE"
    const val SUBTITLE = "PRODUCTS PRIVATE LIMITED"
    const val PRODUCTS_HEADER = "PRODUCTS"
    const val ISO_BADGE = "CERTIFIED ISO 9001:2015 COMPANY"
    const val TM_BRAND = "NIRMALA"

    // Exact 16 products in Left Column as in Official Catalogue
    val LEFT_COLUMN: List<CatalogueItem> = listOf(
        CatalogueItem("acetic-acid", "ACETIC ACID", column = 1),
        CatalogueItem("battery-acid", "BATTERY CHARGING ACID", column = 1),
        CatalogueItem("bleaching-powder", "BLEACHING POWDER", column = 1),
        CatalogueItem("borax-powder", "BOREX POWEDER", column = 1),
        CatalogueItem("boric-powder", "BORRIC POWDER", column = 1),
        CatalogueItem("caustic-soda-flakes", "CAUSTIC SODA FLAKES", column = 1),
        CatalogueItem("caustic-soda-lye", "CAUSTIC SODA LYE", column = 1),
        CatalogueItem("caustic-potash", "CAUSTIC SODA POTASH", column = 1),
        CatalogueItem("citric-acid", "CITRIC ACID", column = 1),
        CatalogueItem("dm-water", "D.M.WATER / DISTILLED WATER", column = 1),
        CatalogueItem("hydrochloric-acid", "HYDROCLORIC (HCL) ACID", column = 1),
        CatalogueItem("hydrofluoric-acid", "HYDROFLUORIC (H.F) ACID", column = 1),
        CatalogueItem("hydrogen-peroxide", "HYDROGIN PEROXSIDE (HP)", column = 1),
        CatalogueItem("liquor-ammonia", "LIQUOR AMONIYA", column = 1),
        CatalogueItem("phosphoric-acid-tech", "PHOSPHRIC ACID (TECH. GRADE)", column = 1),
        CatalogueItem("phosphoric-acid-food", "PHOSPHRIC ACID (FOOD GRADE)", column = 1)
    )

    // Exact 8 products in Right Column as in Official Catalogue
    val RIGHT_COLUMN: List<CatalogueItem> = listOf(
        CatalogueItem("nitric-acid-58", "NITRIC ACID", column = 2),
        CatalogueItem("poly-aluminium-chloride", "POLY ALUMINIUM CHLORIDE (PAC POWDER)", column = 2),
        CatalogueItem("soda-ash-light", "SODA ASH LIGHT", column = 2),
        CatalogueItem("sodium-hypochlorite", "SODIUM HYPOCHLORITE (HYPO)", column = 2),
        CatalogueItem("sodium-silicate", "SODIUM SILICATE", column = 2),
        CatalogueItem("sulfamic-descalent", "SULFAMIC DESCALENT", column = 2),
        CatalogueItem("sulphuric-acid-cp", "SULPHURIC ACID (C.P GRADE)", column = 2),
        CatalogueItem("sulphuric-acid-comm", "SULPHURIC ACID (COM. GRADE)", column = 2)
    )

    val ALL_PRODUCTS: List<CatalogueItem> = LEFT_COLUMN + RIGHT_COLUMN

    // Divisions & Addresses matching Catalogue
    const val POSTAL_ADDRESS_TITLE = "Postal Address"
    const val POSTAL_ADDRESS = "Plot No. 3, Survey No. 1300p, Opp. Ganga Steel, Panchasar Road, Morbi, 363641"

    const val DIVISION_1_TITLE = "Divison : Pipaliya Char Rasta"
    const val DIVISION_1_ADDRESS = "Survey No.283, Chanchavadarda Ta-Maliya(mi) Near Kodal Uniquoters Pvt. Ltd, Chanchavadarda, Morbi, Gujarat, 363660"

    const val DIVISION_2_TITLE = "Divison : Rajpar Road"
    const val DIVISION_2_ADDRESS = "Survey No. 21, Plot No. 4, Indian Land, Rajpar Road, Rajpar, Morbi, Gujarat, 363641"

    // Contact
    const val WEBSITE = "www.nirmaladevicare.com"
    const val EMAIL = "nirmaladevicarepvtltd@gmail.com"
    const val PHONE_1 = "+91 98257 31735"
    const val PHONE_2 = "+91 98986 15543"
    const val PRIMARY_PHONE = "+91 82003 32632"
    const val PHONES = "+91 98257 31735, +91 98986 15543, +91 82003 32632"

    fun getCatalogueShareMessage(): String {
        return buildString {
            append("━━━━━━━━━━━━━━━━━━━━\n")
            append("📄 *NIRMALADEVI CARE PRIVATE LIMITED*\n")
            append("*Official Product Catalogue (ISO 9001:2015 Certified)*\n")
            append("━━━━━━━━━━━━━━━━━━━━\n\n")
            append("🧪 *KEY CHEMICAL PRODUCTS (24+ READY STOCK):*\n")
            ALL_PRODUCTS.forEachIndexed { index, item ->
                append("${index + 1}. • ${item.name}\n")
            }
            append("\n🏭 *OFFICE & SALES DEPOT DIVISIONS:*\n")
            append("1. *Head Office / Postal Address:*\n$POSTAL_ADDRESS\n\n")
            append("2. *Pipaliya Char Rasta Division:*\n$DIVISION_1_ADDRESS\n\n")
            append("3. *Rajpar Road Division:*\n$DIVISION_2_ADDRESS\n\n")
            append("────────────────────\n")
            append("🌐 Website: $WEBSITE\n")
            append("✉️ Email: $EMAIL\n")
            append("📞 Call / WhatsApp: $PHONES\n")
            append("━━━━━━━━━━━━━━━━━━━━")
        }
    }
}
