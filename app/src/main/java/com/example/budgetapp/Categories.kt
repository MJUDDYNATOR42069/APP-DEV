package com.example.budgetapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat

data class Category(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val monthlyLimit: Double = 0.0,
    val spent: Double = 0.0,
    val icon: String = "other",
    val isArchived: Boolean = false,
    val isCustom: Boolean = false,
    val userId: String = "",
    val color: String = "#3B84F1",
    val createdAt: Long = System.currentTimeMillis()
)

class CategoryActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // Views
    private lateinit var backButton: ImageView
    private lateinit var addCategoryButton: ImageView
    private lateinit var incomeTab: TextView
    private lateinit var essentialTab: TextView
    private lateinit var wantsTab: TextView
    private lateinit var savingsTab: TextView
    private lateinit var debtTab: TextView
    private lateinit var sectionTitle: TextView
    private lateinit var totalBudgetAmount: TextView
    private lateinit var totalSpent: TextView
    private lateinit var remainingBudget: TextView
    private lateinit var budgetProgressBar: ProgressBar
    private lateinit var categoriesContainer: LinearLayout

    private val categoriesList = mutableListOf<Category>()
    private val decimalFormat = DecimalFormat("$#,##0.00")
    private var currentType = "essential"
    private var totalBudget = 0.0
    private var totalSpentAmount = 0.0
    private var selectedIcon = "food"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        initializeViews()
        setupClickListeners()
        setupPredefinedCategories()
        loadCategories()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        addCategoryButton = findViewById(R.id.addCategoryButton)
        incomeTab = findViewById(R.id.incomeTab)
        essentialTab = findViewById(R.id.essentialTab)
        wantsTab = findViewById(R.id.wantsTab)
        savingsTab = findViewById(R.id.savingsTab)
        debtTab = findViewById(R.id.debtTab)
        sectionTitle = findViewById(R.id.sectionTitle)
        totalBudgetAmount = findViewById(R.id.totalBudgetAmount)
        totalSpent = findViewById(R.id.totalSpent)
        remainingBudget = findViewById(R.id.remainingBudget)
        budgetProgressBar = findViewById(R.id.budgetProgressBar)
        categoriesContainer = findViewById(R.id.categoriesContainer)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener { finish() }
        addCategoryButton.setOnClickListener { showAddEditCategoryDialog(null) }

        incomeTab.setOnClickListener {
            currentType = "income"
            updateTabStyles()
            loadCategories()
        }

        essentialTab.setOnClickListener {
            currentType = "essential"
            updateTabStyles()
            loadCategories()
        }

        wantsTab.setOnClickListener {
            currentType = "wants"
            updateTabStyles()
            loadCategories()
        }

        savingsTab.setOnClickListener {
            currentType = "savings"
            updateTabStyles()
            loadCategories()
        }

        debtTab.setOnClickListener {
            currentType = "debt"
            updateTabStyles()
            loadCategories()
        }
    }

    private fun updateTabStyles() {
        val tabs = listOf(incomeTab, essentialTab, wantsTab, savingsTab, debtTab)
        tabs.forEach { tab ->
            tab.setBackgroundResource(R.drawable.tab_unselected)
            tab.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        }

        val activeTab = when (currentType) {
            "income" -> incomeTab
            "essential" -> essentialTab
            "wants" -> wantsTab
            "savings" -> savingsTab
            "debt" -> debtTab
            else -> essentialTab
        }

        activeTab.setBackgroundResource(R.drawable.tab_selected)
        activeTab.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))

        sectionTitle.text = when (currentType) {
            "income" -> "Monthly Income"
            "essential" -> "Essential Expenses"
            "wants" -> "Non-Essential Expenses"
            "savings" -> "Savings & Goals"
            "debt" -> "Debt Payments"
            else -> "Budget"
        }
    }

    private fun setupPredefinedCategories() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId)
            .collection("categories")
            .whereEqualTo("isCustom", false)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    createPredefinedCategories()
                }
            }
    }

    private fun createPredefinedCategories() {
        val userId = auth.currentUser?.uid ?: return

        val allCategories = mutableListOf<HashMap<String, Any>>()

        // Income Categories
        val incomeCategories = listOf("Salary/Wages", "Freelance/Side Hustle", "Gifts/Refunds", "Transfers")
        incomeCategories.forEach { category ->
            allCategories.add(hashMapOf(
                "name" to category,
                "type" to "income",
                "monthlyLimit" to 0.0,
                "spent" to 0.0,
                "icon" to "income",
                "isArchived" to false,
                "isCustom" to false,
                "userId" to userId,
                "color" to "#4CAF50",
                "createdAt" to System.currentTimeMillis()
            ))
        }

        // Essential Categories
        val essentialCategories = listOf(
            "Housing (Rent/Mortgage)", "Utilities", "Groceries", "Transportation",
            "Insurance", "Healthcare", "Childcare/School Fees", "Minimum Debt Payments"
        )
        essentialCategories.forEach { category ->
            val limit = when {
                category.contains("Housing") -> 2000.0
                category.contains("Groceries") -> 500.0
                category.contains("Transportation") -> 300.0
                else -> 100.0
            }
            allCategories.add(hashMapOf(
                "name" to category,
                "type" to "essential",
                "monthlyLimit" to limit,
                "spent" to 0.0,
                "icon" to getIconForCategory(category),
                "isArchived" to false,
                "isCustom" to false,
                "userId" to userId,
                "color" to "#F44336",
                "createdAt" to System.currentTimeMillis()
            ))
        }

        // Wants Categories
        val wantsCategories = listOf(
            "Dining Out", "Entertainment", "Shopping", "Subscriptions", "Travel/Vacation", "Gifts & Celebrations"
        )
        wantsCategories.forEach { category ->
            val limit = when {
                category.contains("Dining") -> 200.0
                category.contains("Entertainment") -> 100.0
                category.contains("Shopping") -> 150.0
                else -> 50.0
            }
            allCategories.add(hashMapOf(
                "name" to category,
                "type" to "wants",
                "monthlyLimit" to limit,
                "spent" to 0.0,
                "icon" to getIconForCategory(category),
                "isArchived" to false,
                "isCustom" to false,
                "userId" to userId,
                "color" to "#FF9800",
                "createdAt" to System.currentTimeMillis()
            ))
        }

        // Savings Categories
        val savingsCategories = listOf("Emergency Fund", "Retirement Savings", "Down Payment", "Education Fund", "Custom Goal")
        savingsCategories.forEach { category ->
            allCategories.add(hashMapOf(
                "name" to category,
                "type" to "savings",
                "monthlyLimit" to 500.0,
                "spent" to 0.0,
                "icon" to "savings",
                "isArchived" to false,
                "isCustom" to false,
                "userId" to userId,
                "color" to "#2196F3",
                "createdAt" to System.currentTimeMillis()
            ))
        }

        // Debt Categories
        val debtCategories = listOf("Credit Card Payoff", "Student Loan", "Personal Loan")
        debtCategories.forEach { category ->
            allCategories.add(hashMapOf(
                "name" to category,
                "type" to "debt",
                "monthlyLimit" to 300.0,
                "spent" to 0.0,
                "icon" to "debt",
                "isArchived" to false,
                "isCustom" to false,
                "userId" to userId,
                "color" to "#9C27B0",
                "createdAt" to System.currentTimeMillis()
            ))
        }

        // Special Categories
        val specialCategories = listOf("Uncategorized", "Adjustments")
        specialCategories.forEach { category ->
            allCategories.add(hashMapOf(
                "name" to category,
                "type" to "essential",
                "monthlyLimit" to 0.0,
                "spent" to 0.0,
                "icon" to "other",
                "isArchived" to false,
                "isCustom" to false,
                "userId" to userId,
                "color" to "#9E9E9E",
                "createdAt" to System.currentTimeMillis()
            ))
        }

        val batch = firestore.batch()
        allCategories.forEach { category ->
            val docRef = firestore.collection("users").document(userId)
                .collection("categories").document()
            batch.set(docRef, category)
        }

        batch.commit().addOnSuccessListener {
            loadCategories()
        }
    }

    private fun getIconForCategory(categoryName: String): String {
        return when {
            categoryName.contains("Food") || categoryName.contains("Grocery") || categoryName.contains("Dining") -> "food"
            categoryName.contains("Transport") || categoryName.contains("Car") -> "transport"
            categoryName.contains("Shopping") -> "shopping"
            categoryName.contains("Entertainment") -> "entertainment"
            categoryName.contains("Housing") || categoryName.contains("Rent") -> "housing"
            categoryName.contains("Utilities") -> "utilities"
            categoryName.contains("Healthcare") || categoryName.contains("Insurance") -> "health"
            categoryName.contains("Savings") || categoryName.contains("Emergency") -> "savings"
            categoryName.contains("Debt") || categoryName.contains("Loan") || categoryName.contains("Credit") -> "debt"
            categoryName.contains("Salary") || categoryName.contains("Income") -> "income"
            else -> "other"
        }
    }

    private fun loadCategories() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users").document(userId)
            .collection("categories")
            .whereEqualTo("type", currentType)
            .whereEqualTo("isArchived", false)
            .get()
            .addOnSuccessListener { documents ->
                categoriesList.clear()
                categoriesContainer.removeAllViews()

                totalBudget = 0.0
                totalSpentAmount = 0.0

                for (document in documents) {
                    val category = Category(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        type = document.getString("type") ?: "",
                        monthlyLimit = document.getDouble("monthlyLimit") ?: 0.0,
                        spent = document.getDouble("spent") ?: 0.0,
                        icon = document.getString("icon") ?: "other",
                        isArchived = document.getBoolean("isArchived") ?: false,
                        isCustom = document.getBoolean("isCustom") ?: false,
                        userId = userId,
                        color = document.getString("color") ?: "#3B84F1",
                        createdAt = document.getLong("createdAt") ?: System.currentTimeMillis()
                    )

                    if (currentType != "income") {
                        totalBudget += category.monthlyLimit
                        totalSpentAmount += category.spent
                    }

                    categoriesList.add(category)
                    addCategoryView(category)
                }

                updateBudgetSummary()

                if (categoriesList.isEmpty()) {
                    showEmptyState()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load categories: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addCategoryView(category: Category) {
        val itemView = LayoutInflater.from(this).inflate(R.layout.activity_item_category, categoriesContainer, false)

        val categoryIcon = itemView.findViewById<ImageView>(R.id.categoryIcon)
        val categoryName = itemView.findViewById<TextView>(R.id.categoryName)
        val categoryBudgetText = itemView.findViewById<TextView>(R.id.categoryBudget)
        val categorySpent = itemView.findViewById<TextView>(R.id.categorySpent)
        val categoryProgress = itemView.findViewById<ProgressBar>(R.id.categoryProgress)
        val menuButton = itemView.findViewById<ImageView>(R.id.menuButton)
        val menuOptions = itemView.findViewById<LinearLayout>(R.id.menuOptions)

        categoryName.text = category.name
        setCategoryIcon(categoryIcon, category.icon)

        if (currentType == "income") {
            categoryBudgetText.visibility = View.GONE
            categorySpent.text = decimalFormat.format(category.spent)
            categorySpent.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            categoryProgress.visibility = View.GONE
        } else {
            categoryBudgetText.text = "Limit: ${decimalFormat.format(category.monthlyLimit)}"
            categorySpent.text = decimalFormat.format(category.spent)

            val progress = if (category.monthlyLimit > 0) {
                ((category.spent / category.monthlyLimit) * 100).toInt()
            } else 0
            categoryProgress.progress = progress.coerceAtMost(100)

            if (category.spent > category.monthlyLimit && category.monthlyLimit > 0) {
                categorySpent.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
                categoryProgress.progressTintList = ContextCompat.getColorStateList(this, android.R.color.holo_red_dark)
            } else {
                categorySpent.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
                categoryProgress.progressTintList = ContextCompat.getColorStateList(this, android.R.color.holo_blue_dark)
            }
        }

        menuButton.setOnClickListener {
            val isVisible = menuOptions.visibility == View.VISIBLE
            menuOptions.visibility = if (isVisible) View.GONE else View.VISIBLE
        }

        itemView.findViewById<TextView>(R.id.editOption).setOnClickListener {
            menuOptions.visibility = View.GONE
            showAddEditCategoryDialog(category)
        }

        itemView.findViewById<TextView>(R.id.hideOption).setOnClickListener {
            menuOptions.visibility = View.GONE
            archiveCategory(category)
        }

        itemView.findViewById<TextView>(R.id.deleteOption).setOnClickListener {
            menuOptions.visibility = View.GONE
            deleteCategory(category)
        }

        categoriesContainer.addView(itemView)
    }

    private fun setCategoryIcon(imageView: ImageView, iconName: String) {
        val iconRes = when (iconName) {
            "food" -> R.drawable.cash
            "transport" -> R.drawable.sheet
            "shopping" -> R.drawable.tabler_wallet
            "entertainment" -> R.drawable.gridicons_stats_alt
            "savings" -> R.drawable.mage_goals
            "income" -> R.drawable.cash
            else -> R.drawable.round_menu_24
        }
        imageView.setImageResource(iconRes)
    }

    private fun showAddEditCategoryDialog(category: Category?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_category, null)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val nameInput = dialogView.findViewById<TextInputEditText>(R.id.categoryNameInput)
        val limitInput = dialogView.findViewById<TextInputEditText>(R.id.monthlyLimitInput)
        val archivedCheckBox = dialogView.findViewById<MaterialCheckBox>(R.id.archivedCheckBox)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)

        val iconFood = dialogView.findViewById<ImageView>(R.id.iconFood)
        val iconTransport = dialogView.findViewById<ImageView>(R.id.iconTransport)
        val iconShopping = dialogView.findViewById<ImageView>(R.id.iconShopping)
        val iconEntertainment = dialogView.findViewById<ImageView>(R.id.iconEntertainment)
        val iconSavings = dialogView.findViewById<ImageView>(R.id.iconSavings)

        val icons = listOf(iconFood, iconTransport, iconShopping, iconEntertainment, iconSavings)

        if (category != null) {
            dialogTitle.text = "Edit Category"
            nameInput.setText(category.name)
            if (currentType != "income") {
                limitInput.setText(category.monthlyLimit.toString())
            } else {
                limitInput.visibility = View.GONE
            }
            archivedCheckBox.isChecked = category.isArchived
            selectedIcon = category.icon
        } else {
            dialogTitle.text = "Add Custom Category"
            if (currentType == "income") {
                limitInput.visibility = View.GONE
            }
        }

        icons.forEach { icon ->
            icon.setOnClickListener {
                icons.forEach { it.isSelected = false }
                icon.isSelected = true
                selectedIcon = when (icon.id) {
                    R.id.iconFood -> "food"
                    R.id.iconTransport -> "transport"
                    R.id.iconShopping -> "shopping"
                    R.id.iconEntertainment -> "entertainment"
                    R.id.iconSavings -> "savings"
                    else -> "other"
                }
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        saveButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val limitText = limitInput.text.toString().trim()

            if (name.isEmpty()) {
                nameInput.error = "Category name is required"
                return@setOnClickListener
            }

            val limit = if (limitText.isNotEmpty()) limitText.toDoubleOrNull() ?: 0.0 else 0.0

            if (category != null) {
                updateCategory(category, name, limit, archivedCheckBox.isChecked)
            } else {
                addCategory(name, limit, selectedIcon)
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun addCategory(name: String, limit: Double, icon: String) {
        val userId = auth.currentUser?.uid ?: return

        val categoryData = hashMapOf(
            "name" to name,
            "type" to currentType,
            "monthlyLimit" to limit,
            "spent" to 0.0,
            "icon" to icon,
            "isArchived" to false,
            "isCustom" to true,
            "userId" to userId,
            "color" to "#3B84F1",
            "createdAt" to System.currentTimeMillis()
        )

        firestore.collection("users").document(userId)
            .collection("categories")
            .add(categoryData)
            .addOnSuccessListener {
                Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show()
                loadCategories()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to add category: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateCategory(category: Category, name: String, limit: Double, isArchived: Boolean) {
        val userId = auth.currentUser?.uid ?: return

        val updates = hashMapOf<String, Any>(
            "name" to name,
            "monthlyLimit" to limit,
            "icon" to selectedIcon,
            "isArchived" to isArchived
        )

        firestore.collection("users").document(userId)
            .collection("categories")
            .document(category.id)
            .update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Category updated", Toast.LENGTH_SHORT).show()
                loadCategories()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to update: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun archiveCategory(category: Category) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId)
            .collection("categories")
            .document(category.id)
            .update("isArchived", true)
            .addOnSuccessListener {
                Toast.makeText(this, "Category archived", Toast.LENGTH_SHORT).show()
                loadCategories()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to archive: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteCategory(category: Category) {
        AlertDialog.Builder(this)
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete ${category.name}?")
            .setPositiveButton("Delete") { _, _ ->
                val userId = auth.currentUser?.uid ?: return@setPositiveButton

                firestore.collection("users").document(userId)
                    .collection("categories")
                    .document(category.id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Category deleted", Toast.LENGTH_SHORT).show()
                        loadCategories()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Failed to delete: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateBudgetSummary() {
        if (currentType != "income") {
            totalBudgetAmount.text = decimalFormat.format(totalBudget)
            val remaining = totalBudget - totalSpentAmount
            remainingBudget.text = decimalFormat.format(remaining)

            if (remaining < 0) {
                remainingBudget.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            } else {
                remainingBudget.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            }

            totalSpent.text = decimalFormat.format(totalSpentAmount)

            val progress = if (totalBudget > 0) {
                ((totalSpentAmount / totalBudget) * 100).toInt()
            } else 0
            budgetProgressBar.progress = progress.coerceAtMost(100)
            budgetProgressBar.visibility = View.VISIBLE
        } else {
            totalBudgetAmount.text = "Income Tracking"
            remainingBudget.text = ""
            budgetProgressBar.visibility = View.GONE
        }
    }

    private fun showEmptyState() {
        val emptyView = LayoutInflater.from(this).inflate(R.layout.empty_category_state, categoriesContainer, false)
        categoriesContainer.addView(emptyView)
    }
}