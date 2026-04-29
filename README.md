BudgetWise ReadMe File 


Group Members: 
 * Owami  Ndlovu   ST10446013
 * Amanda Mthethwa   ST10456019
 * Iviwe Cabangana   ST10304224





# 💰 Budget App - Personal Finance Tracker

## 📱 Overview

The Budget App is a comprehensive personal finance management application built for Android. It helps users track their income, expenses, and monitor their monthly budget with visual indicators and real-time alerts.

## 🎯 Assignment Purpose

This application was developed as a practical demonstration of Android development skills, showcasing:
- Material Design principles
- Real-time budget tracking
- Transaction management system
- Visual data representation
- Multi-screen navigation

## ✨ Features

### Core Functionality
- ✅ **Budget Overview Dashboard** - Real-time view of total balance, monthly expenses, and income
- ✅ **Transaction Management** - Add, view, and delete expenses/income
- ✅ **Budget Tracking** - Set monthly budget limit (R15,000 default)
- ✅ **Color-Coded Alerts** - Visual warnings when approaching or exceeding budget
- ✅ **Recent Transactions List** - Quick view of latest transactions
- ✅ **Category Analysis** - Breakdown of spending by category
- ✅ **Filter System** - Filter transactions by type (All/Expenses/Income) and date range

### Visual Features
- 📊 **Progress Bars** - Visual representation of budget usage
- 🎨 **Color Indicators**:
  - 🟢 Green: Safe (below 80% of budget)
  - 🟡 Yellow/Orange: Warning (80-99% of budget)
  - 🔴 Red: Danger (100%+ of budget)
- 📱 **Material Design** - Modern, clean interface
- 🌓 **Dark/Light Mode** - Automatic theme switching

### Navigation
- 🏠 **Home** - Main budget dashboard
- 📊 **Dashboard** - Detailed financial overview
- 📝 **Transactions** - Complete transaction history
- 👤 **Profile** - User settings (coming soon)
- 📈 **Reports** - Financial reports (coming soon)

## 🛠️ Technical Stack

### Languages & Frameworks
- **Kotlin** - Primary programming language
- **XML** - Layout design
- **Material Design Components** - UI components

### Android Components Used
- Activities (MainActivity, DashboardActivity, TransactionsActivity)
- RecyclerView with custom adapters
- Material Cards and Buttons
- ProgressBars for visual feedback
- BottomNavigationView for navigation
- DatePickerDialog for date selection
- AlertDialog for user interactions

### Dependencies
```gradle
implementation 'androidx.core:core-ktx:1.12.0'
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.11.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'androidx.recyclerview:recyclerview:1.3.2'
