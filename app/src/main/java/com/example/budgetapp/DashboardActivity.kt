package com.example.budgetapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    // =========================
    // VIEWS
    // =========================

    private lateinit var tvWelcome: TextView
    private lateinit var tvTotalSpent: TextView

    private lateinit var tvDashboardTotalBalance: TextView
    private lateinit var tvMonthlyExpenses: TextView
    private lateinit var tvMonthlyIncome: TextView

    private lateinit var tvMinGoal: TextView
    private lateinit var tvMaxGoal: TextView

    private lateinit var tvBudgetLimit: TextView
    private lateinit var tvBudgetStatus: TextView
    private lateinit var progressBudget: ProgressBar

    private lateinit var recyclerTransactions: RecyclerView

    private lateinit var btnHome: LinearLayout
    private lateinit var btnExpenses: LinearLayout
    private lateinit var btnAdd: LinearLayout
    private lateinit var btnStats: LinearLayout
    private lateinit var btnGoals: LinearLayout

    private lateinit var ivProfile: ImageView

    // =========================
    // DATA
    // =========================

    private var transactions = ArrayList<Transaction>()

    private val monthlyBudgetLimit = 15000.00

    // =========================
    // ON CREATE
    // =========================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_dashboard2)

        // EDGE TO EDGE
        findViewById<android.view.View>(R.id.main)?.let { mainView ->
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
                )

                insets
            }
        }

        initializeViews()

        loadSampleTransactions()

        updateDashboard()

        setupNavigation()

        setupClickListeners()
    }

    // =========================
    // INITIALIZE VIEWS
    // =========================

    private fun initializeViews() {

        tvWelcome = findViewById(R.id.tvWelcome)
        tvTotalSpent = findViewById(R.id.tvTotalSpent)

        tvDashboardTotalBalance =
            findViewById(R.id.tvDashboardTotalBalance)

        tvMonthlyExpenses =
            findViewById(R.id.tvMonthlyExpenses)

        tvMonthlyIncome =
            findViewById(R.id.tvMonthlyIncome)

        tvMinGoal =
            findViewById(R.id.tvMinGoal)

        tvMaxGoal =
            findViewById(R.id.tvMaxGoal)

        tvBudgetLimit =
            findViewById(R.id.tvBudgetLimit)

        tvBudgetStatus =
            findViewById(R.id.tvBudgetStatus)

        progressBudget =
            findViewById(R.id.progressBudget)

        recyclerTransactions =
            findViewById(R.id.recyclerDashboardTransactions)

        ivProfile =
            findViewById(R.id.ivProfile)

        // NAVIGATION

        btnHome = findViewById(R.id.btnHome)
        btnExpenses = findViewById(R.id.btnExpenses)
        btnAdd = findViewById(R.id.btnAdd)
        btnStats = findViewById(R.id.btnStats)
        btnGoals = findViewById(R.id.btnGoals)
    }

    // =========================
    // SAMPLE DATA
    // =========================

    private fun loadSampleTransactions() {

        transactions.clear()

        val sampleTransactions = listOf(

            Transaction(
                "1",
                600.00,
                "Eating Out",
                Date(),
                "Restaurant dinner",
                "expense"
            ),

            Transaction(
                "2",
                2000.00,
                "Groceries",
                Date(),
                "Monthly groceries",
                "expense"
            ),

            Transaction(
                "3",
                3500.00,
                "Car Payment",
                Date(),
                "Car installment",
                "expense"
            ),

            Transaction(
                "4",
                800.00,
                "Shopping",
                Date(),
                "Clothes and accessories",
                "expense"
            ),

            Transaction(
                "5",
                5000.00,
                "Rent",
                Date(),
                "Monthly rent payment",
                "expense"
            ),

            Transaction(
                "6",
                1500.00,
                "Vacation",
                Date(),
                "Vacation savings",
                "expense"
            ),

            Transaction(
                "7",
                25000.00,
                "Salary",
                Date(),
                "Monthly salary",
                "income"
            )
        )

        transactions.addAll(sampleTransactions)
    }

    // =========================
    // UPDATE DASHBOARD
    // =========================

    private fun updateDashboard() {

        val monthlyExpenses = getMonthlyExpenses()

        val monthlyIncome = getMonthlyIncome()

        val totalBalance = getTotalBalance()

        val budgetPercentage =
            (monthlyExpenses / monthlyBudgetLimit) * 100

        // HEADER

        tvWelcome.text = "Welcome back, User"

        // MAIN CARD

        tvTotalSpent.text = formatCurrency(monthlyExpenses)

        // STATS

        tvDashboardTotalBalance.text =
            formatCurrency(totalBalance)

        tvMonthlyExpenses.text =
            formatCurrency(monthlyExpenses)

        tvMonthlyIncome.text =
            formatCurrency(monthlyIncome)

        // GOALS

        tvMinGoal.text = "R 1,500"
        tvMaxGoal.text = "R 3,500"

        // BUDGET

        tvBudgetLimit.text =
            "Budget Limit: ${formatCurrency(monthlyBudgetLimit)}"

        progressBudget.progress =
            minOf(budgetPercentage.toInt(), 100)

        when {

            budgetPercentage >= 100 -> {

                progressBudget.progressTintList =
                    ContextCompat.getColorStateList(
                        this,
                        android.R.color.holo_red_dark
                    )

                tvBudgetStatus.text =
                    "❌ Over Budget"
            }

            budgetPercentage >= 80 -> {

                progressBudget.progressTintList =
                    ContextCompat.getColorStateList(
                        this,
                        android.R.color.holo_orange_dark
                    )

                tvBudgetStatus.text =
                    "⚠️ Close To Budget"
            }

            else -> {

                progressBudget.progressTintList =
                    ContextCompat.getColorStateList(
                        this,
                        android.R.color.holo_green_dark
                    )

                tvBudgetStatus.text =
                    "✅ Budget Safe"
            }
        }

        // TRANSACTIONS

        setupTransactions()
    }

    // =========================
    // TRANSACTIONS
    // =========================

    private fun setupTransactions() {

        val recentTransactions =
            transactions.filter {
                it.type == "expense"
            }.take(5)

        recyclerTransactions.layoutManager =
            LinearLayoutManager(this)

        recyclerTransactions.adapter =
            DashboardTransactionAdapter(recentTransactions)
    }

    // =========================
    // NAVIGATION
    // =========================

    private fun setupNavigation() {

        btnHome.setOnClickListener {

            // Current Page
        }

        btnExpenses.setOnClickListener {

            startActivity(
                Intent(this, expenseList::class.java)
            )
        }

        btnAdd.setOnClickListener {

            startActivity(
                Intent(this, addExpense::class.java)
            )
        }

        btnGoals.setOnClickListener {

            startActivity(
                Intent(this, GoalActivity::class.java)
            )
        }

        btnStats.setOnClickListener {

            Toast.makeText(
                this,
                "Stats page coming soon",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =========================
    // CLICK LISTENERS
    // =========================

    private fun setupClickListeners() {

        ivProfile.setOnClickListener {

            Toast.makeText(
                this,
                "Profile clicked",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =========================
    // CALCULATIONS
    // =========================

    private fun getMonthlyExpenses(): Double {

        return transactions
            .filter { it.type == "expense" }
            .sumOf { it.amount }
    }

    private fun getMonthlyIncome(): Double {

        return transactions
            .filter { it.type == "income" }
            .sumOf { it.amount }
    }

    private fun getTotalBalance(): Double {

        return getMonthlyIncome() - getMonthlyExpenses()
    }

    // =========================
    // FORMATTERS
    // =========================

    private fun formatCurrency(amount: Double): String {

        val format =
            NumberFormat.getCurrencyInstance(
                Locale("en", "ZA")
            )

        return format.format(amount)
            .replace("ZAR", "R")
    }

    // =========================
    // RECYCLER ADAPTER
    // =========================

    inner class DashboardTransactionAdapter(
        private val transactions: List<Transaction>
    ) :
        RecyclerView.Adapter<DashboardTransactionAdapter.TransactionViewHolder>() {

        override fun onCreateViewHolder(
            parent: android.view.ViewGroup,
            viewType: Int
        ): TransactionViewHolder {

            val view = layoutInflater.inflate(
                R.layout.item_dashboard_transaction,
                parent,
                false
            )

            return TransactionViewHolder(view)
        }

        override fun onBindViewHolder(
            holder: TransactionViewHolder,
            position: Int
        ) {

            val transaction = transactions[position]

            val dateFormat =
                SimpleDateFormat(
                    "MMM dd",
                    Locale.getDefault()
                )

            holder.tvTransactionName.text =
                transaction.category

            holder.tvTransactionDate.text =
                dateFormat.format(transaction.date)

            holder.tvTransactionAmount.text =
                formatCurrency(transaction.amount)

            holder.tvTransactionAmount.setTextColor(
                ContextCompat.getColor(
                    this@DashboardActivity,
                    android.R.color.holo_red_dark
                )
            )
        }

        override fun getItemCount(): Int {

            return transactions.size
        }

        inner class TransactionViewHolder(
            itemView: android.view.View
        ) :
            RecyclerView.ViewHolder(itemView) {

            val tvTransactionName: TextView =
                itemView.findViewById(R.id.tvTransactionName)

            val tvTransactionDate: TextView =
                itemView.findViewById(R.id.tvTransactionDate)

            val tvTransactionAmount: TextView =
                itemView.findViewById(R.id.tvTransactionAmount)
        }
    }
}