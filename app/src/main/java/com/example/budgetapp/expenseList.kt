package com.example.budgetapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class expenseList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_expense_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Views
        val cardFromDate = findViewById<CardView>(R.id.cardFromDate)
        val tvFromDate = findViewById<TextView>(R.id.tvFromDate)
        val cardToDate = findViewById<CardView>(R.id.cardToDate)
        val tvToDate = findViewById<TextView>(R.id.tvToDate)
        val etSearch = findViewById<EditText>(R.id.etSearch)

        val btnHome = findViewById<LinearLayout>(R.id.btnHome)
        val btnExpenses = findViewById<LinearLayout>(R.id.btnExpenses)
        val btnAdd = findViewById<LinearLayout>(R.id.btnAdd)
        val btnStats = findViewById<LinearLayout>(R.id.btnStats)
        val btnGoals = findViewById<LinearLayout>(R.id.btnGoals)

        // Date Picker for "From" Date
        cardFromDate.setOnClickListener {
            showDatePicker(tvFromDate)
        }

        // Date Picker for "To" Date
        cardToDate.setOnClickListener {
            showDatePicker(tvToDate)
        }

        // Navigation
        btnHome.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        // btnExpenses is current page, no action needed or just refresh
        btnExpenses.setOnClickListener {
            // Already on expenses page
        }

        btnAdd.setOnClickListener {
            startActivity(Intent(this, addExpense::class.java))
        }

        btnStats.setOnClickListener {
            // startActivity(Intent(this, StatsActivity::class.java))
        }

        btnGoals.setOnClickListener {
            startActivity(Intent(this, GoalActivity::class.java))
            finish()
        }
    }

    private fun showDatePicker(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedYear-${String.format("%02d", selectedMonth + 1)}-${String.format("%02d", selectedDay)}"
                textView.text = date
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}