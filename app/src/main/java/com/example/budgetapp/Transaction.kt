package com.example.budgetapp

import java.util.Date
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import kotlin.collections.ArrayList

data class Transaction(
    val id: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val date: Date = Date(),
    val notes: String = "",
    val type: String = ""
)


class TransactionsActivity : AppCompatActivity() {

    private lateinit var recyclerTransactions: RecyclerView
    private lateinit var btnAll: MaterialButton
    private lateinit var btnExpenses: MaterialButton
    private lateinit var btnIncome: MaterialButton
    private lateinit var btnDateFilter: MaterialButton
    private lateinit var fabAddTransaction: FloatingActionButton
    private lateinit var tvTotalIncome: TextView
    private lateinit var tvTotalExpenses: TextView
    private lateinit var tvBudgetWarning: TextView
    private lateinit var cardBudgetWarning: MaterialCardView

    private var transactions = ArrayList<Transaction>()
    private var filteredTransactions = ArrayList<Transaction>()
    private var currentFilter = "all"
    private var dateFilter = "month"
    private lateinit var transactionAdapter: TransactionFullAdapter
    private val monthlyBudgetLimit = 15000.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

        initializeViews()
        setupToolbar()
        loadSampleTransactions()
        setupFilters()
        updateUI()
        setupClickListeners()
    }

    private fun initializeViews() {
        recyclerTransactions = findViewById(R.id.recyclerTransactions)
        btnAll = findViewById(R.id.btnAll)
        btnExpenses = findViewById(R.id.btnExpenses)
        btnIncome = findViewById(R.id.btnIncome)
        btnDateFilter = findViewById(R.id.btnDateFilter)
        fabAddTransaction = findViewById(R.id.fabAddTransaction)
        tvTotalIncome = findViewById(R.id.tvTotalIncome)
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses)
        tvBudgetWarning = findViewById(R.id.tvBudgetWarning)
        cardBudgetWarning = findViewById(R.id.cardBudgetWarning)

        transactionAdapter = TransactionFullAdapter(filteredTransactions) { transaction ->
            showTransactionDetails(transaction)
        }
        recyclerTransactions.layoutManager = LinearLayoutManager(this)
        recyclerTransactions.adapter = transactionAdapter
    }

    private fun setupToolbar() {
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        toolbar.title = "Transactions"
    }

    private fun loadSampleTransactions() {
        transactions.clear()

        val calendar = Calendar.getInstance()

        val sampleTransactions = listOf(
            Transaction(UUID.randomUUID().toString(), 600.00, "Eating Out", calendar.time, "Restaurant dinner", "expense"),
            Transaction(UUID.randomUUID().toString(), 2000.00, "Groceries", calendar.time, "Monthly groceries", "expense"),
            Transaction(UUID.randomUUID().toString(), 3500.00, "Car Payment", calendar.time, "Car installment", "expense"),
            Transaction(UUID.randomUUID().toString(), 800.00, "Shopping", calendar.time, "Clothes and accessories", "expense"),
            Transaction(UUID.randomUUID().toString(), 5000.00, "Rent", calendar.time, "Monthly rent payment", "expense"),
            Transaction(UUID.randomUUID().toString(), 1500.00, "Vacation", calendar.time, "Vacation savings/booking", "expense"),
            Transaction(UUID.randomUUID().toString(), 25000.00, "Salary", calendar.time, "Monthly salary", "income"),
            Transaction(UUID.randomUUID().toString(), 1200.00, "Utilities", calendar.time, "Electricity and water", "expense"),
            Transaction(UUID.randomUUID().toString(), 450.00, "Entertainment", calendar.time, "Movie and dining", "expense")
        )
        transactions.addAll(sampleTransactions)
    }

    private fun setupFilters() {
        btnAll.setOnClickListener {
            currentFilter = "all"
            updateFilterButtons()
            applyFilters()
        }

        btnExpenses.setOnClickListener {
            currentFilter = "expense"
            updateFilterButtons()
            applyFilters()
        }

        btnIncome.setOnClickListener {
            currentFilter = "income"
            updateFilterButtons()
            applyFilters()
        }

        btnDateFilter.setOnClickListener {
            showDateFilterDialog()
        }
    }

    private fun updateFilterButtons() {
        btnAll.isEnabled = currentFilter != "all"
        btnExpenses.isEnabled = currentFilter != "expense"
        btnIncome.isEnabled = currentFilter != "income"
    }

    private fun applyFilters() {
        filteredTransactions.clear()

        val filteredByType = when (currentFilter) {
            "expense" -> transactions.filter { it.type == "expense" }
            "income" -> transactions.filter { it.type == "income" }
            else -> transactions
        }

        val filteredByDate = when (dateFilter) {
            "week" -> filteredByType.filter { isWithinLastWeek(it.date) }
            "month" -> filteredByType.filter { isWithinCurrentMonth(it.date) }
            else -> filteredByType
        }

        filteredTransactions.addAll(filteredByDate)
        transactionAdapter.notifyDataSetChanged()
        updateSummary()
        checkBudgetWarning()
    }

    private fun updateSummary() {
        val totalIncome = filteredTransactions.filter { it.type == "income" }.sumOf { it.amount }
        val totalExpenses = filteredTransactions.filter { it.type == "expense" }.sumOf { it.amount }

        tvTotalIncome.text = formatCurrency(totalIncome)
        tvTotalExpenses.text = formatCurrency(totalExpenses)
    }

    private fun checkBudgetWarning() {
        val monthlyExpenses = getCurrentMonthExpenses()
        val percentage = (monthlyExpenses / monthlyBudgetLimit) * 100

        when {
            percentage >= 100 -> {
                cardBudgetWarning.visibility = View.VISIBLE
                tvBudgetWarning.text = "🔴 OVER BUDGET! You have exceeded your R${String.format("%,.2f", monthlyBudgetLimit)} budget!"
            }
            percentage >= 80 -> {
                cardBudgetWarning.visibility = View.VISIBLE
                val remaining = monthlyBudgetLimit - monthlyExpenses
                tvBudgetWarning.text = "🟡 WARNING: Close to budget limit! Only R${String.format("%,.2f", remaining)} remaining"
            }
            else -> {
                cardBudgetWarning.visibility = View.GONE
            }
        }
    }

    private fun getCurrentMonthExpenses(): Double {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        return transactions.filter { transaction ->
            val transactionCalendar = Calendar.getInstance().apply { time = transaction.date }
            transaction.type == "expense" &&
                    transactionCalendar.get(Calendar.MONTH) == currentMonth &&
                    transactionCalendar.get(Calendar.YEAR) == currentYear
        }.sumOf { it.amount }
    }

    private fun updateUI() {
        applyFilters()
    }

    private fun isWithinCurrentMonth(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        val dateCalendar = Calendar.getInstance().apply { time = date }
        return dateCalendar.get(Calendar.MONTH) == currentMonth &&
                dateCalendar.get(Calendar.YEAR) == currentYear
    }

    private fun isWithinLastWeek(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)

        val dateCalendar = Calendar.getInstance().apply { time = date }
        return dateCalendar.get(Calendar.WEEK_OF_YEAR) == currentWeek &&
                dateCalendar.get(Calendar.YEAR) == currentYear
    }

    private fun showDateFilterDialog() {
        val options = arrayOf("All Time", "This Month", "This Week")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Date Range")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> {
                    dateFilter = "all"
                    btnDateFilter.text = "All Time"
                }
                1 -> {
                    dateFilter = "month"
                    btnDateFilter.text = "This Month"
                }
                2 -> {
                    dateFilter = "week"
                    btnDateFilter.text = "This Week"
                }
            }
            applyFilters()
        }
        builder.show()
    }

    private fun setupClickListeners() {
        fabAddTransaction.setOnClickListener {
            showAddTransactionDialog()
        }
    }

    private fun showAddTransactionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_transaction, null)
        val etAmount = dialogView.findViewById<EditText>(R.id.et_amount)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinner_category)
        val etDate = dialogView.findViewById<EditText>(R.id.et_date)
        val etNotes = dialogView.findViewById<EditText>(R.id.et_notes)

        val categories = resources.getStringArray(R.array.categories)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerCategory.adapter = adapter

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        etDate.setText(dateFormat.format(calendar.time))

        etDate.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                calendar.set(year, month, day)
                etDate.setText(dateFormat.format(calendar.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Add Transaction")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
                val category = spinnerCategory.selectedItem.toString()
                val dateStr = etDate.text.toString()
                val date = try { dateFormat.parse(dateStr) ?: Date() } catch (e: Exception) { Date() }
                val notes = etNotes.text.toString()

                if (amount > 0) {
                    val transaction = Transaction(
                        id = UUID.randomUUID().toString(),
                        amount = amount,
                        category = category,
                        date = date,
                        notes = notes,
                        type = "expense"
                    )
                    transactions.add(0, transaction)
                    applyFilters()
                    Toast.makeText(this, "Transaction added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showTransactionDetails(transaction: Transaction) {
        val details = """
            Category: ${transaction.category}
            Amount: ${formatCurrency(transaction.amount)}
            Date: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(transaction.date)}
            Type: ${transaction.type.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}
            Notes: ${transaction.notes.ifEmpty { "No notes" }}
        """.trimIndent()

        MaterialAlertDialogBuilder(this)
            .setTitle("Transaction Details")
            .setMessage(details)
            .setPositiveButton("OK", null)
            .setNeutralButton("Delete") { _, _ ->
                transactions.remove(transaction)
                applyFilters()
                Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
        return format.format(amount).replace("ZAR", "R")
    }

    inner class TransactionFullAdapter(
        private val transactions: List<Transaction>,
        private val onItemClick: (Transaction) -> Unit
    ) : RecyclerView.Adapter<TransactionFullAdapter.TransactionViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
            val view = layoutInflater.inflate(R.layout.item_transaction_full, parent, false)
            return TransactionViewHolder(view)
        }

        override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
            val transaction = transactions[position]
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

            holder.tvCategory.text = transaction.category
            holder.tvDate.text = dateFormat.format(transaction.date)
            holder.tvAmount.text = formatCurrency(transaction.amount)
            holder.tvAmount.setTextColor(ContextCompat.getColor(this@TransactionsActivity,
                if (transaction.type == "expense") android.R.color.holo_red_dark else android.R.color.holo_green_dark))

            holder.tvCategoryIcon.text = getCategoryIcon(transaction.category)

            if (transaction.notes.isNotEmpty()) {
                holder.tvNotes.text = transaction.notes
                holder.tvNotes.visibility = View.VISIBLE
            } else {
                holder.tvNotes.visibility = View.GONE
            }

            holder.itemView.setOnClickListener { onItemClick(transaction) }
        }

        override fun getItemCount() = transactions.size

        inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvCategoryIcon: TextView = itemView.findViewById(R.id.tvCategoryIcon)
            val tvCategory: TextView = itemView.findViewById(R.id.tvTransactionCategory)
            val tvNotes: TextView = itemView.findViewById(R.id.tvTransactionNotes)
            val tvDate: TextView = itemView.findViewById(R.id.tvTransactionDate)
            val tvAmount: TextView = itemView.findViewById(R.id.tvTransactionAmount)
        }

        private fun getCategoryIcon(category: String): String {
            return when (category.lowercase()) {
                "eating out" -> "🍽️"
                "groceries" -> "🛒"
                "car payment" -> "🚗"
                "shopping" -> "🛍️"
                "rent" -> "🏠"
                "vacation" -> "✈️"
                "utilities" -> "💡"
                "entertainment" -> "🎬"
                "salary" -> "💰"
                else -> "💵"
            }
        }
    }
}