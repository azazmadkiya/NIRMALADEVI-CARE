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
    val companyOrName: String = "",
    val phone: String = "",
    val location: String = "Morbi, Gujarat",
    val items: List<QuoteItem> = emptyList(),
    val urgentDelivery: Boolean = false,
    val additionalNotes: String = ""
) {
    fun toWhatsAppMessage(): String {
        val sb = StringBuilder()
        sb.append("👋 *Chemical Inquiry - Nirmaladevi Care Pvt. Ltd.*\n\n")
        if (companyOrName.isNotBlank()) {
            sb.append("🏢 *From:* ${companyOrName.trim()}\n")
        }
        if (phone.isNotBlank()) {
            sb.append("📞 *Contact:* ${phone.trim()}\n")
        }
        if (location.isNotBlank()) {
            sb.append("📍 *Delivery Location:* ${location.trim()}\n")
        }
        if (urgentDelivery) {
            sb.append("⚡ *Requirement:* Urgent Delivery Requested\n")
        }
        sb.append("\n📦 *Required Chemicals List (${items.size}):*\n")
        items.forEachIndexed { index, item ->
            val supplyType = if (item.isWholesale) "Wholesale/Bulk" else "Retail"
            sb.append("${index + 1}. *${item.product.name}* (${item.product.formula})\n")
            sb.append("   - Grade: ${item.selectedGrade}\n")
            sb.append("   - Quantity: ${item.quantity} ${item.unit} [${supplyType}]\n")
            if (item.remarks.isNotBlank()) {
                sb.append("   - Note: ${item.remarks}\n")
            }
        }
        if (additionalNotes.isNotBlank()) {
            sb.append("\n📝 *Additional Notes:*\n${additionalNotes.trim()}\n")
        }
        sb.append("\nPlease provide quotation with best price, delivery time, and COA/purity test details. Thank you!")
        return sb.toString()
    }

    fun toEmailSubject(): String {
        val client = if (companyOrName.isNotBlank()) " - $companyOrName" else ""
        return "Quotation Inquiry for Chemicals$client [Nirmaladevi Care]"
    }
}
