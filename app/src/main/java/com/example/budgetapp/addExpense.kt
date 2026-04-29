package com.example.budgetapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class addExpense : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_expense)

        findViewById<android.view.View>(R.id.main)?.let { mainView ->
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // Initialize Views
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val etDate = findViewById<EditText>(R.id.etDate)
        val etStartTime = findViewById<EditText>(R.id.etStartTime)
        val etEndTime = findViewById<EditText>(R.id.etEndTime)
        val etCategory = findViewById<EditText>(R.id.etCategory)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // Bottom Navigation
        val btnHome = findViewById<LinearLayout>(R.id.btnHome)
        val btnExpenses = findViewById<LinearLayout>(R.id.btnExpenses)
        val btnGoals = findViewById<LinearLayout>(R.id.btnGoals)

        btnBack.setOnClickListener {
            finish()
        }

        etDate.setOnClickListener {
            showDatePicker(etDate)
        }

        etStartTime.setOnClickListener {
            showTimePicker(etStartTime)
        }

        etEndTime.setOnClickListener {
            showTimePicker(etEndTime)
        }

        btnSave.setOnClickListener {
            saveExpense()
        }

        // Navigation
        btnHome.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        btnExpenses.setOnClickListener {
            startActivity(Intent(this, expenseList::class.java))
            finish()
        }

        btnGoals.setOnClickListener {
            startActivity(Intent(this, GoalActivity::class.java))
            finish()
        }
    }

    private fun showDatePicker(editText: EditText) {
        val c = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            val date = String.format("%02d/%02d/%d", day, month + 1, year)
            editText.setText(date)
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePicker(editText: EditText) {
        val c = Calendar.getInstance()
        TimePickerDialog(this, { _, hour, minute ->
            val amPm = if (hour < 12) "AM" else "PM"
            val displayHour = if (hour % 12 == 0) 12 else hour % 12
            val time = String.format("%02d:%02d %s", displayHour, minute, amPm)
            editText.setText(time)
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show()
    }

    private fun saveExpense() {
        // Here you would normally save to Firebase or a local DB
        Toast.makeText(this, "Expense Saved Successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }
}