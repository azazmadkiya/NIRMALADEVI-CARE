package com.example.data.model

data class QuoteItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val product: ChemicalProduct,
    val selectedGrade: String,
    val quantity: Int,
    val unit: String,
    val isWholesale: Boolean,
    val remarks: String = ""
)

data class CustomerInquiry(
    val contactName: String = "",
    val companyName: String = "",
    val companyOrName: String = "",
    val phone: String = "",
    val location: String = "Morbi, Gujarat",
    val items: List<QuoteItem> = emptyList(),
    val urgentDelivery: Boolean = false,
    val additionalNotes: String = ""
) {
    val totalUnits: Int
        get() = items.sumOf { it.quantity }

    val displayName: String
        get() = when {
            contactName.isNotBlank() && companyName.isNotBlank() -> "$contactName ($companyName)"
            contactName.isNotBlank() -> contactName
            companyName.isNotBlank() -> companyName
            companyOrName.isNotBlank() -> companyOrName
            else -> "Valued Customer"
        }

    fun toWhatsAppMessage(): String {
        val sb = StringBuilder()
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("🧪 *NIRMALADEVI CARE PVT. LTD.*\n")
        sb.append("*Industrial Chemical Quotation Request*\n")
        sb.append("GSTIN: ${CompanyInfo.GST_NUMBER} · MSME: ${CompanyInfo.MSME_NUMBER}\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n\n")

        sb.append("🏢 *BUYER DETAILS:*\n")
        if (companyName.isNotBlank()) {
            sb.append("• Company / Firm: *${companyName.trim()}*\n")
        }
        if (contactName.isNotBlank()) {
            sb.append("• Contact Person: ${contactName.trim()}\n")
        } else if (companyOrName.isNotBlank()) {
            sb.append("• Contact Person: ${companyOrName.trim()}\n")
        }
        if (phone.isNotBlank()) {
            sb.append("• Mobile / WhatsApp: ${phone.trim()}\n")
        }
        if (location.isNotBlank()) {
            sb.append("• Delivery Destination: ${location.trim()}\n")
        }
        if (urgentDelivery) {
            sb.append("• Priority: ⚡ *Urgent Delivery (Priority Dispatch)*\n")
        }

        sb.append("\n────────────────────\n")
        sb.append("📦 *INQUIRY PRODUCTS LIST (${items.size} items · $totalUnits Total Units):*\n")
        sb.append("────────────────────\n\n")

        items.forEachIndexed { index, item ->
            val supplyType = if (item.isWholesale) "Wholesale / Bulk" else "Retail"
            sb.append("*${index + 1}. ${item.product.name}* (${item.product.formula})\n")
            sb.append("   • Grade / Purity: ${item.selectedGrade}\n")
            sb.append("   • Supply Mode: $supplyType\n")
            sb.append("   • Quantity: *${item.quantity} Unit(s)*\n")
            sb.append("   • Pack Size: *${item.unit}*\n")
            if (item.remarks.isNotBlank()) {
                sb.append("   • Note: ${item.remarks.trim()}\n")
            }
            // Add a clean space between products so products don't mix together
            sb.append("\n")
        }

        if (additionalNotes.isNotBlank()) {
            sb.append("────────────────────\n")
            sb.append("📝 *ADDITIONAL NOTES & SPECIFICATIONS:*\n")
            sb.append("${additionalNotes.trim()}\n\n")
        }

        sb.append("────────────────────\n")
        sb.append("Please provide best commercial quotation, delivery schedule, and COA / Test Certificate.\n\n")
        sb.append("Thank you!\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━")
        return sb.toString()
    }

    fun toEmailSubject(): String {
        val client = if (companyName.isNotBlank()) " - $companyName" else if (contactName.isNotBlank()) " - $contactName" else ""
        return "Chemical Quotation Request$client [Nirmaladevi Care]"
    }

    fun validate(): InquiryValidationResult {
        val errors = mutableMapOf<String, String>()

        if (items.isEmpty()) {
            errors["items"] = "Please select at least one chemical product for inquiry."
        }

        val trimmedName = contactName.trim()
        if (trimmedName.isBlank()) {
            errors["contactName"] = "Contact person name is required."
        } else if (trimmedName.length < 2) {
            errors["contactName"] = "Contact name must be at least 2 characters."
        }

        val trimmedCompany = companyName.trim()
        if (trimmedCompany.isBlank()) {
            errors["companyName"] = "Company or business name is required."
        } else if (trimmedCompany.length < 2) {
            errors["companyName"] = "Company name must be at least 2 characters."
        }

        val digitsOnly = phone.filter { it.isDigit() }
        if (phone.isBlank()) {
            errors["phone"] = "Phone or WhatsApp number is required."
        } else if (digitsOnly.length < 10) {
            errors["phone"] = "Please enter a valid 10-digit mobile or WhatsApp number."
        }

        val trimmedLocation = location.trim()
        if (trimmedLocation.isBlank()) {
            errors["location"] = "Delivery location / destination is required."
        } else if (trimmedLocation.length < 3) {
            errors["location"] = "Delivery location must be at least 3 characters."
        }

        return InquiryValidationResult(errors)
    }
}

data class InquiryValidationResult(
    val fieldErrors: Map<String, String> = emptyMap()
) {
    val isValid: Boolean
        get() = fieldErrors.isEmpty()

    val firstErrorMessage: String?
        get() = fieldErrors.values.firstOrNull()

    fun errorFor(fieldName: String): String? = fieldErrors[fieldName]
}

