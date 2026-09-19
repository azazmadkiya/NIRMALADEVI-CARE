package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun customerInquiry_formatsNameAndCompanyCorrectly() {
    val inquiry = com.example.data.model.CustomerInquiry(
      contactName = "Rajesh Patel",
      companyName = "Morbi Ceramics Ltd.",
      phone = "+91 98765 43210",
      location = "Morbi, Gujarat"
    )
    assertEquals("Rajesh Patel (Morbi Ceramics Ltd.)", inquiry.displayName)
    assertTrue(inquiry.toWhatsAppMessage().contains("Morbi Ceramics Ltd."))
    assertTrue(inquiry.toWhatsAppMessage().contains("Rajesh Patel"))
  }

  @Test
  fun customerInquiry_calculatesTotalUnits() {
    val acid = com.example.data.model.ChemicalCatalog.products.first()
    val item1 = com.example.data.model.QuoteItem(
      product = acid,
      selectedGrade = "Glacial 99.8%",
      quantity = 25,
      unit = "30 kg Carboy",
      isWholesale = true
    )
    val item2 = com.example.data.model.QuoteItem(
      product = acid,
      selectedGrade = "Glacial 99.8%",
      quantity = 15,
      unit = "30 kg Carboy",
      isWholesale = false
    )
    val inquiry = com.example.data.model.CustomerInquiry(
      contactName = "Rajesh Patel",
      items = listOf(item1, item2)
    )
    assertEquals(40, inquiry.totalUnits)
    assertEquals(2, inquiry.items.size)
  }

  @Test
  fun customerInquiry_validatesRequiredFields() {
    val acid = com.example.data.model.ChemicalCatalog.products.first()
    val validItem = com.example.data.model.QuoteItem(
      product = acid,
      selectedGrade = "Glacial 99.8%",
      quantity = 10,
      unit = "30 kg Carboy",
      isWholesale = false
    )

    // Case 1: Empty inquiry fails validation
    val emptyInquiry = com.example.data.model.CustomerInquiry()
    val emptyValidation = emptyInquiry.validate()
    assertFalse(emptyValidation.isValid)
    assertNotNull(emptyValidation.errorFor("items"))
    assertNotNull(emptyValidation.errorFor("contactName"))
    assertNotNull(emptyValidation.errorFor("companyName"))
    assertNotNull(emptyValidation.errorFor("phone"))

    // Case 2: Short phone number fails
    val invalidPhoneInquiry = com.example.data.model.CustomerInquiry(
      contactName = "Rajesh",
      companyName = "Morbi Ceramics",
      phone = "12345",
      location = "Morbi",
      items = listOf(validItem)
    )
    val phoneValidation = invalidPhoneInquiry.validate()
    assertFalse(phoneValidation.isValid)
    assertEquals("Please enter a valid 10-digit mobile or WhatsApp number.", phoneValidation.errorFor("phone"))

    // Case 3: Complete inquiry succeeds
    val validInquiry = com.example.data.model.CustomerInquiry(
      contactName = "Rajesh Patel",
      companyName = "Morbi Ceramics Ltd.",
      phone = "+91 98765 43210",
      location = "GIDC Phase 2, Morbi, Gujarat",
      items = listOf(validItem)
    )
    val validResult = validInquiry.validate()
    assertTrue(validResult.isValid)
    assertTrue(validResult.fieldErrors.isEmpty())
  }

  @Test
  fun searchFilter_filtersByNameAndCategoryCorrectly() {
    val viewModel = com.example.ui.viewmodel.ChemicalViewModel()

    // Test search by chemical name
    viewModel.updateSearchQuery("Nitric")
    assertTrue(viewModel.uiState.value.filteredProducts.isNotEmpty())
    assertTrue(viewModel.uiState.value.filteredProducts.all { it.name.contains("Nitric", ignoreCase = true) })

    // Test search by chemical category title
    viewModel.updateSearchQuery("Alkalis")
    assertTrue(viewModel.uiState.value.filteredProducts.isNotEmpty())
    assertTrue(viewModel.uiState.value.filteredProducts.all {
      it.category == com.example.data.model.ChemicalCategory.ALKALIS ||
      it.name.contains("Alkalis", ignoreCase = true)
    })

    // Test category selection filter
    viewModel.updateSearchQuery("")
    viewModel.selectCategory(com.example.data.model.ChemicalCategory.ACIDS)
    assertTrue(viewModel.uiState.value.filteredProducts.isNotEmpty())
    assertTrue(viewModel.uiState.value.filteredProducts.all { it.category == com.example.data.model.ChemicalCategory.ACIDS })
  }

  @Test
  fun pendingInquiry_modelAndViewModelManagement() {
    val viewModel = com.example.ui.viewmodel.ChemicalViewModel()
    val product = com.example.data.model.ChemicalCatalog.products.first()

    // Initially pending inquiries empty
    assertTrue(viewModel.uiState.value.pendingInquiries.isEmpty())

    // Add pending inquiry
    viewModel.addPendingInquiry(
      userName = "Rajesh Patel",
      companyName = "Morbi Ceramics Ltd.",
      quantity = 25,
      product = product,
      unit = "Carboys (35 kg)",
      notes = "Priority delivery"
    )

    assertEquals(1, viewModel.uiState.value.pendingInquiries.size)
    val item = viewModel.uiState.value.pendingInquiries.first()
    assertEquals("Rajesh Patel", item.userName)
    assertEquals("Morbi Ceramics Ltd.", item.companyName)
    assertEquals(25, item.quantity)
    assertEquals(product.name, item.productName)

    // Update quantity
    viewModel.updatePendingQuantity(item.id, 50)
    assertEquals(50, viewModel.uiState.value.pendingInquiries.first().quantity)

    // Delete item
    viewModel.deletePendingInquiry(item.id)
    assertTrue(viewModel.uiState.value.pendingInquiries.isEmpty())

    // Test clear all
    viewModel.addPendingInquiry("User A", "Company A", 10, product)
    viewModel.addPendingInquiry("User B", "Company B", 20, product)
    assertEquals(2, viewModel.uiState.value.pendingInquiries.size)
    viewModel.clearAllPendingInquiries()
    assertTrue(viewModel.uiState.value.pendingInquiries.isEmpty())
  }

  @Test
  fun categorySelection_togglesCategoryAndProducts() {
    val viewModel = com.example.ui.viewmodel.ChemicalViewModel()

    // Initially none selected
    assertEquals(0, viewModel.uiState.value.selectedCategoryCount)
    assertEquals(0, viewModel.uiState.value.selectedProductCount)

    // Select "acid" category
    viewModel.toggleCategorySelectionInDb("acid", true)

    val acidCategory = viewModel.uiState.value.categorySelections.find { it.categoryId == "acid" }
    assertNotNull(acidCategory)
    assertTrue(acidCategory!!.isSelected)
    assertEquals(1, viewModel.uiState.value.selectedCategoryCount)

    val acidProducts = viewModel.uiState.value.selectedProductsInDb.filter { it.categoryId == "acid" }
    assertTrue(acidProducts.isNotEmpty())
    assertTrue(acidProducts.all { it.isSelected })
    assertEquals(acidProducts.size, viewModel.uiState.value.selectedProductCount)

    // Deselect "acid" category
    viewModel.toggleCategorySelectionInDb("acid", false)
    val acidCategoryDeselected = viewModel.uiState.value.categorySelections.find { it.categoryId == "acid" }
    assertNotNull(acidCategoryDeselected)
    assertFalse(acidCategoryDeselected!!.isSelected)
    assertEquals(0, viewModel.uiState.value.selectedCategoryCount)
    assertEquals(0, viewModel.uiState.value.selectedProductCount)
  }

  @Test
  fun productSelection_togglesIndividualProduct() {
    val viewModel = com.example.ui.viewmodel.ChemicalViewModel()
    val firstProduct = viewModel.uiState.value.selectedProductsInDb.first()

    // Select first product
    viewModel.toggleProductSelectionInDb(firstProduct.productId, true)
    val updatedProduct = viewModel.uiState.value.selectedProductsInDb.find { it.productId == firstProduct.productId }
    assertNotNull(updatedProduct)
    assertTrue(updatedProduct!!.isSelected)
    assertEquals(1, viewModel.uiState.value.selectedProductCount)

    // Parent category count should be 1
    val parentCat = viewModel.uiState.value.categorySelections.find { it.categoryId == firstProduct.categoryId }
    assertNotNull(parentCat)
    assertEquals(1, parentCat!!.selectedProductCount)

    // Deselect product
    viewModel.toggleProductSelectionInDb(firstProduct.productId, false)
    val deselectedProduct = viewModel.uiState.value.selectedProductsInDb.find { it.productId == firstProduct.productId }
    assertNotNull(deselectedProduct)
    assertFalse(deselectedProduct!!.isSelected)
    assertEquals(0, viewModel.uiState.value.selectedProductCount)
  }

  @Test
  fun selectAll_bulkUpdatesAllCategoriesAndProducts() {
    val viewModel = com.example.ui.viewmodel.ChemicalViewModel()

    // Bulk select all
    viewModel.selectAllInDb(true)
    assertTrue(viewModel.uiState.value.categorySelections.all { it.isSelected })
    assertTrue(viewModel.uiState.value.selectedProductsInDb.all { it.isSelected })
    assertEquals(viewModel.uiState.value.categorySelections.size, viewModel.uiState.value.selectedCategoryCount)
    assertEquals(viewModel.uiState.value.selectedProductsInDb.size, viewModel.uiState.value.selectedProductCount)

    // Bulk clear all
    viewModel.selectAllInDb(false)
    assertTrue(viewModel.uiState.value.categorySelections.all { !it.isSelected })
    assertTrue(viewModel.uiState.value.selectedProductsInDb.all { !it.isSelected })
    assertEquals(0, viewModel.uiState.value.selectedCategoryCount)
    assertEquals(0, viewModel.uiState.value.selectedProductCount)
  }

  @Test
  fun addSelectedProductsToPendingInquiry_createsRoomInquiries() {
    val viewModel = com.example.ui.viewmodel.ChemicalViewModel()

    // Select acid category
    viewModel.toggleCategorySelectionInDb("acid", true)
    val acidProductsCount = viewModel.uiState.value.selectedProductsInDb.count { it.categoryId == "acid" }

    // Convert selected to pending inquiries
    viewModel.addSelectedProductsToPendingInquiry(
      userName = "Jignesh Shah",
      companyName = "Apex Ceramic Morbi",
      quantity = 20
    )

    assertEquals(acidProductsCount, viewModel.uiState.value.pendingInquiries.size)
    val firstInquiry = viewModel.uiState.value.pendingInquiries.first()
    assertEquals("Jignesh Shah", firstInquiry.userName)
    assertEquals("Apex Ceramic Morbi", firstInquiry.companyName)
    assertEquals(20, firstInquiry.quantity)
  }

  @Test
  fun companyInfo_containsCorrectBankGstAndMsmeDetails() {
    val info = com.example.data.model.CompanyInfo
    assertEquals("HDFC BANK", info.BANK_NAME)
    assertEquals("MORVI-GUJARAT", info.BANK_BRANCH)
    assertEquals("NIRMALADEVI CARE PRIVATE LIMITED", info.BANK_BENEFICIARY)
    assertEquals("50200097827378", info.BANK_ACCOUNT_NUMBER)
    assertEquals("HDFC0000307", info.BANK_IFSC_CODE)
    assertEquals("24AAHCN6833G1ZF", info.GST_NUMBER)
    assertEquals("UDYAM-GJ-32-0031160", info.MSME_NUMBER)

    val bankMessage = info.getFormattedBankDetailsMessage()
    assertTrue(bankMessage.contains("50200097827378"))
    assertTrue(bankMessage.contains("HDFC0000307"))
    assertTrue(bankMessage.contains("HDFC BANK"))
    assertTrue(bankMessage.contains("MORVI-GUJARAT"))
    assertTrue(bankMessage.contains("24AAHCN6833G1ZF"))
    assertTrue(bankMessage.contains("UDYAM-GJ-32-0031160"))
  }

  @Test
  fun companyInfo_containsTwoSalesDepotsWithCorrectAddressesAndLinks() {
    val depots = com.example.data.model.CompanyInfo.SALES_DEPOTS
    assertEquals(2, depots.size)

    val depot1 = depots[0]
    assertEquals(1, depot1.depotNumber)
    assertEquals("Survey No.21, Plot No 4P, Rajpar Rd, Morbi, Rajpar, Gujarat 363641", depot1.address)
    assertEquals("https://maps.app.goo.gl/DTZ4sLbFBnnusc9u7", depot1.mapUrl)
    assertEquals("363641", depot1.pincode)

    val depot2 = depots[1]
    assertEquals(2, depot2.depotNumber)
    assertEquals("Survey No.283, Ta-Maliya(mi) Near Kodal Uniquoters Pvt. Ltd, Chanchavadarda, Gujarat 363660", depot2.address)
    assertEquals("https://maps.app.goo.gl/EphZVUwfNYAbUrDq5", depot2.mapUrl)
    assertEquals("363660", depot2.pincode)

    val allDepotsMsg = com.example.data.model.CompanyInfo.getAllDepotsFormattedMessage()
    assertTrue(allDepotsMsg.contains("https://maps.app.goo.gl/DTZ4sLbFBnnusc9u7"))
    assertTrue(allDepotsMsg.contains("https://maps.app.goo.gl/EphZVUwfNYAbUrDq5"))
    assertTrue(allDepotsMsg.contains("Survey No.21"))
    assertTrue(allDepotsMsg.contains("Survey No.283"))
    assertTrue(allDepotsMsg.contains("363641"))
    assertTrue(allDepotsMsg.contains("363660"))
  }

  @Test
  fun productCatalogueData_hasCompleteCatalogueAndGodownDetails() {
    val items = com.example.data.model.ProductCatalogueData.ALL_PRODUCTS
    assertEquals(24, items.size)
    assertEquals(16, com.example.data.model.ProductCatalogueData.LEFT_COLUMN.size)
    assertEquals(8, com.example.data.model.ProductCatalogueData.RIGHT_COLUMN.size)

    // Verify key products exist
    val names = items.map { it.name }
    assertTrue(names.any { it.contains("SULPHURIC ACID", ignoreCase = true) })
    assertTrue(names.any { it.contains("HYDROCLORIC", ignoreCase = true) })
    assertTrue(names.any { it.contains("NITRIC ACID", ignoreCase = true) })
    assertTrue(names.any { it.contains("CAUSTIC SODA", ignoreCase = true) })
    assertTrue(names.any { it.contains("HYDROGIN PEROXSIDE", ignoreCase = true) })

    // Verify divisions & addresses
    val div1 = com.example.data.model.ProductCatalogueData.DIVISION_1_ADDRESS
    val div2 = com.example.data.model.ProductCatalogueData.DIVISION_2_ADDRESS
    assertTrue(div1.contains("Chanchavadarda") && div1.contains("363660"))
    assertTrue(div2.contains("Rajpar Road") && div2.contains("363641"))

    // Verify formatted share text
    val shareText = com.example.data.model.ProductCatalogueData.getCatalogueShareMessage()
    assertTrue(shareText.contains("NIRMALADEVI CARE"))
    assertTrue(shareText.contains("ISO 9001:2015"))
    assertTrue(shareText.contains("Pipaliya Char Rasta"))
    assertTrue(shareText.contains("Rajpar Road"))
  }

  @Test
  fun companyInfo_headOfficeMessage_hasRegisteredOfficeAndAdministrativeDesk() {
    val msg = com.example.data.model.CompanyInfo.getFormattedHeadOfficeMessage()
    assertTrue(msg.contains("REGISTERED / HEAD OFFICE"))
    assertTrue(msg.contains("Commercial & Administrative Desk"))
    assertTrue(msg.contains(com.example.data.model.CompanyInfo.ADDRESS))
    assertTrue(msg.contains(com.example.data.model.CompanyInfo.OFFICE_MAP_URL))
    assertTrue(msg.contains(com.example.data.model.CompanyInfo.PHONE))
    assertTrue(msg.contains(com.example.data.model.CompanyInfo.GST_NUMBER))
  }
}

