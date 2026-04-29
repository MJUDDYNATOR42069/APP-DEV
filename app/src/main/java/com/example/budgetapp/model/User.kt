package com.example.budgetapp.model

data class User(
    val name: String = "",
    val email: String = "",
    val balance: Double = 0.0,
    val date: String = "",
    val goal: String = ""
)