package com.example.data.model

data class SalesDepot(
    val id: String,
    val depotNumber: Int,
    val name: String,
    val shortName: String,
    val area: String,
    val address: String,
    val mapUrl: String,
    val pincode: String,
    val landmark: String,
    val description: String,
    val badge: String,
    val storageTypes: List<String>
)

data class CompanyContactPhone(
    val title: String,
    val number: String,
    val subtitle: String = "",
    val isPrimary: Boolean = false
)

object CompanyInfo {
    const val LEGAL_NAME = "NIRMALADEVI CARE PRIVATE LIMITED"
    const val SHORT_NAME = "Nirmaladevi Care Pvt. Ltd."
    const val TAGLINE = "Industrial Chemical Trading & Supply"

    // Bank Details
    const val BANK_NAME = "HDFC BANK"
    const val BANK_BRANCH = "MORVI-GUJARAT"
    const val BANK_BENEFICIARY = "NIRMALADEVI CARE PRIVATE LIMITED"
    const val BANK_ACCOUNT_NUMBER = "50200097827378"
    const val BANK_IFSC_CODE = "HDFC0000307"
    const val BANK_ACCOUNT_TYPE = "Current Account"

    // Tax & Regulatory Details
    const val GST_NUMBER = "24AAHCN6833G1ZF"
    const val MSME_NUMBER = "UDYAM-GJ-32-0031160"

    // Official Documents & Google Drive Links
    const val CATALOGUE_PDF_NAME = "Catalogue NCPL.pdf"
    const val CATALOGUE_DRIVE_URL = "https://drive.google.com/uc?id=1xQ1EVjfEKvhkGcK9N72mVdWKfNuV06T5"
    const val CATALOGUE_DRIVE_VIEW_URL = "https://drive.google.com/file/d/1xQ1EVjfEKvhkGcK9N72mVdWKfNuV06T5/view?usp=sharing"

    const val GST_PDF_NAME = "Gst Certificate.pdf"
    const val GST_DRIVE_URL = "https://drive.google.com/uc?id=1eqr6al2DHQx8WmwTVIU8ffXTDvdh5uF8"
    const val GST_DRIVE_VIEW_URL = "https://drive.google.com/file/d/1eqr6al2DHQx8WmwTVIU8ffXTDvdh5uF8/view?usp=sharing"

    const val MSME_PDF_NAME = "Udyam Certificate (MSME).pdf"
    const val MSME_DRIVE_URL = "https://drive.google.com/uc?id=1pt4j4p70098X1jA__9SmHEGzn43aYTuh"
    const val MSME_DRIVE_VIEW_URL = "https://drive.google.com/file/d/1pt4j4p70098X1jA__9SmHEGzn43aYTuh/view?usp=sharing"

    const val BANK_PDF_NAME = "Bank Details.PDF"
    const val BANK_DRIVE_URL = "https://drive.google.com/uc?id=1lrPU7zp0KtvNn-tcKMDvOTErZL4ymFtu"
    const val BANK_DRIVE_VIEW_URL = "https://drive.google.com/file/d/1lrPU7zp0KtvNn-tcKMDvOTErZL4ymFtu/view?usp=sharing"

    // Address & Contact
    const val ADDRESS = "Panchasar Road, Morbi, Gujarat – 363641, India"
    const val OFFICE_MAP_URL = "https://maps.app.goo.gl/Sx54aiY4ezVeYyqY9"
    const val PHONE = "+91 82003 32632"
    const val PHONE_1 = "+91 98257 31735"
    const val PHONE_2 = "+91 98986 15543"
    const val EMAIL = "nirmaladevicarepvtltd@gmail.com"
    const val PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=com.nirmaladevicare"

    const val SHARE_APP_MESSAGE = """
🏢 *NIRMALADEVI CARE PVT. LTD.*
Trusted Industrial Chemical Trading & Solvents Supplier (Morbi, Gujarat)

Explore our verified business credentials, official catalogues, and direct support via our official mobile app:
🔹 *Products Catalogue:* Complete Industrial Chemicals & Solvents Range
🔹 *GST Certificate:* Verified Taxpayer Credentials (GSTIN: 24AABCN...)
🔹 *Udhyam (MSME) Certificate:* Govt. of India Registered Enterprise
🔹 *Official Bank Details:* Secure RTGS / NEFT / IMPS Banking Info
🔹 *Sales Depots & Hubs:* Morbi & Maliya Godowns

📱 *Download & Explore Official App:*
$PLAY_STORE_LINK
    """

    val CONTACT_PHONES = listOf(
        CompanyContactPhone(
            title = "Direct Helpline / Sales Desk",
            number = "+91 82003 32632",
            subtitle = "Central Morbi Desk · Chemical Supply & Orders",
            isPrimary = true
        ),
        CompanyContactPhone(
            title = "Contact Number 1",
            number = "+91 98257 31735",
            subtitle = "Industrial Sales & Commercial Desk",
            isPrimary = false
        ),
        CompanyContactPhone(
            title = "Contact Number 2",
            number = "+91 98986 15543",
            subtitle = "Customer Inquiries & Dispatch Logistics",
            isPrimary = false
        )
    )

    // Sales Depots / Godowns
    val DEPOT_1 = SalesDepot(
        id = "depot_1",
        depotNumber = 1,
        name = "Sales Depot & Godown 1 (Rajpar Road)",
        shortName = "Depot 1 · Rajpar Rd",
        area = "Rajpar, Morbi",
        address = "Survey No.21, Plot No 4P, Rajpar Rd, Morbi, Rajpar, Gujarat 363641",
        mapUrl = "https://maps.app.goo.gl/DTZ4sLbFBnnusc9u7",
        pincode = "363641",
        landmark = "Rajpar Road",
        description = "Prime ready-stock chemical storage & drum dispatch facility for Morbi ceramic & industrial cluster.",
        badge = "Morbi Dispatch Hub",
        storageTypes = listOf("Acids & Alkalis", "Carboys & Drums", "Same-Day Dispatch")
    )

    val DEPOT_2 = SalesDepot(
        id = "depot_2",
        depotNumber = 2,
        name = "Sales Depot & Godown 2 (Chanchavadarda, Maliya)",
        shortName = "Depot 2 · Maliya (Mi)",
        area = "Chanchavadarda, Ta-Maliya(mi)",
        address = "Survey No.283, Ta-Maliya(mi) Near Kodal Uniquoters Pvt. Ltd, Chanchavadarda, Gujarat 363660",
        mapUrl = "https://maps.app.goo.gl/EphZVUwfNYAbUrDq5",
        pincode = "363660",
        landmark = "Near Kodal Uniquoters Pvt. Ltd",
        description = "Bulk chemical godown & heavy vehicle loading facility located on the Maliya industrial corridor.",
        badge = "Bulk Logistics Hub",
        storageTypes = listOf("Bulk Tankers", "Heavy Vehicle Bay", "Direct Industrial Stock")
    )

    val SALES_DEPOTS: List<SalesDepot> = listOf(DEPOT_1, DEPOT_2)

    fun getFormattedHeadOfficeMessage(): String {
        return buildString {
            append("━━━━━━━━━━━━━━━━━━━━\n")
            append("🏢 *NIRMALADEVI CARE PVT. LTD.*\n")
            append("*REGISTERED / HEAD OFFICE*\n")
            append("Commercial & Administrative Desk\n")
            append("━━━━━━━━━━━━━━━━━━━━\n\n")
            append("📍 *Address:*\n$ADDRESS\n\n")
            append("🗺️ *Google Maps Link:*\n$OFFICE_MAP_URL\n\n")
            append("📞 *Direct Phone / Helpline:* $PHONE\n")
            append("📞 *Contact Number 1:* $PHONE_1\n")
            append("📞 *Contact Number 2:* $PHONE_2\n")
            append("✉️ *Email:* $EMAIL\n")
            append("📋 *GSTIN:* $GST_NUMBER\n")
            append("📋 *MSME (Udyam):* $MSME_NUMBER\n")
            append("━━━━━━━━━━━━━━━━━━━━")
        }
    }

    fun getFormattedDepotMessage(depot: SalesDepot): String {
        return buildString {
            append("━━━━━━━━━━━━━━━━━━━━\n")
            append("🏭 *NIRMALADEVI CARE PVT. LTD.*\n")
            append("*${depot.name}*\n")
            append("━━━━━━━━━━━━━━━━━━━━\n\n")
            append("📍 *Address:*\n${depot.address}\n\n")
            append("🗺️ *Google Maps Link:*\n${depot.mapUrl}\n\n")
            append("📋 *Details:*\n")
            append("• Area / Zone: ${depot.area}\n")
            append("• Landmark: ${depot.landmark}\n")
            append("• Pincode: ${depot.pincode}\n\n")
            append("📞 *Sales Desk:* $PHONE | $PHONE_1 | $PHONE_2\n")
            append("✉️ *Email:* $EMAIL\n")
            append("━━━━━━━━━━━━━━━━━━━━")
        }
    }

    fun getAllDepotsFormattedMessage(): String {
        return buildString {
            append("━━━━━━━━━━━━━━━━━━━━\n")
            append("🏭 *NIRMALADEVI CARE PVT. LTD.*\n")
            append("*Official Sales Depots & Godown Locations*\n")
            append("━━━━━━━━━━━━━━━━━━━━\n\n")
            append("🏢 *1. Head Office & Sales Desk:*\n")
            append("$ADDRESS\n\n")
            append("🏭 *2. Sales Depot & Godown 1 (Rajpar Road, Morbi):*\n")
            append("• Address: ${DEPOT_1.address}\n")
            append("• Maps: ${DEPOT_1.mapUrl}\n\n")
            append("🏭 *3. Sales Depot & Godown 2 (Chanchavadarda, Maliya):*\n")
            append("• Address: ${DEPOT_2.address}\n")
            append("• Landmark: ${DEPOT_2.landmark}\n")
            append("• Maps: ${DEPOT_2.mapUrl}\n\n")
            append("────────────────────\n")
            append("GSTIN: $GST_NUMBER · MSME: $MSME_NUMBER\n")
            append("📞 Contact Numbers:\n")
            append("• $PHONE (Helpline / Sales)\n")
            append("• $PHONE_1 (Contact 1)\n")
            append("• $PHONE_2 (Contact 2)\n")
            append("━━━━━━━━━━━━━━━━━━━━")
        }
    }

    fun getFormattedBankDetailsMessage(): String {
        return buildString {
            append("━━━━━━━━━━━━━━━━━━━━\n")
            append("🏦 *NIRMALADEVI CARE PRIVATE LIMITED*\n")
            append("*Official Banking & Payment Details*\n")
            append("━━━━━━━━━━━━━━━━━━━━\n\n")
            append("• Beneficiary / Account Name: *${BANK_BENEFICIARY}*\n")
            append("• Bank Name: *${BANK_NAME}*\n")
            append("• Branch: *${BANK_BRANCH}*\n")
            append("• Bank A/c No.: *${BANK_ACCOUNT_NUMBER}*\n")
            append("• RTGS / IFSC Code: *${BANK_IFSC_CODE}*\n")
            append("• Account Type: ${BANK_ACCOUNT_TYPE}\n\n")
            append("────────────────────\n")
            append("📋 *TAX & REGULATORY REGISTRATION*\n")
            append("• GSTIN: *${GST_NUMBER}*\n")
            append("• MSME (Udyam): *${MSME_NUMBER}*\n\n")
            append("────────────────────\n")
            append("For RTGS / NEFT / IMPS transfers, please share payment receipt/UTR on WhatsApp or Email for prompt order dispatch.\n\n")
            append("Thank you!\n")
            append("━━━━━━━━━━━━━━━━━━━━")
        }
    }
}

