package com.example.budgetapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GoalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_goal)
        
        findViewById<android.view.View>(R.id.main)?.let { mainView ->
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        setupNavigation()
    }

    private fun setupNavigation() {
        val btnHome = findViewById<LinearLayout>(R.id.btnHome)
        val btnExpenses = findViewById<LinearLayout>(R.id.btnExpenses)
        val btnAdd = findViewById<LinearLayout>(R.id.btnAdd)
        val btnStats = findViewById<LinearLayout>(R.id.btnStats)
        val btnGoals = findViewById<LinearLayout>(R.id.btnGoals)

        btnHome.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        btnExpenses.setOnClickListener {
            startActivity(Intent(this, expenseList::class.java))
            finish()
        }

        btnAdd.setOnClickListener {
            startActivity(Intent(this, addExpense::class.java))
        }

        btnGoals.setOnClickListener {
            // Already on Goals page
        }

        btnStats.setOnClickListener {
            // Add Stats navigation if available
        }
    }
}