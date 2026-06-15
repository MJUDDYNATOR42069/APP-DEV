BudgetWise ReadMe File 


Group Members: 
 * Owami  Ndlovu   ST10446013
 * Amanda Mthethwa   ST10456019
 * Iviwe Cabangana   ST10304224





# 💰 Budget App - Personal Finance Tracker

## 📱 Overview

BudgetWise is a comprehensive personal finance management application built natively for Android. It empowers users to track income and expenses, set and monitor monthly budgets, analyze spending patterns, and receive real-time visual alerts. The app follows Material Design guidelines and focuses on simplicity, clarity, and actionable financial insights.

## 🎯 Assignment Purpose

This application was developed as a practical demonstration of advanced Android development skills, including:
•	Implementation of Material Design principles
•	Real-time budget tracking and transaction management
•	Visual data representation (progress bars, color-coded alerts)
•	Multi-screen navigation with consistent UI/UX patterns
•	Persistent local data storage and state management


## ✨ Features

### Core Functionality
•	✅ Real-time budget overview dashboard
•	✅ Add, view, and delete income/expense transactions
•	✅ Set and edit monthly budget limits (default: R15,000)
•	✅ Color-coded budget alerts (green, yellow, red)
•	✅ Recent transactions list with fast access
•	✅ Category-based spending breakdown
•	✅ Filter transactions by type (All, Expenses, Income) and 

Detailed Page Breakdown
 1. Home Page (Budget Overview Dashboard)
The Home Page serves as the primary landing screen. It provides a high-level snapshot of the user’s financial health.
Components:
•	Total Balance – Calculated as (Total Income – Total Expenses)
•	Total Expenses – Sum of all expense transactions
•	Total Income – Sum of all income transactions
•	Monthly Budget Limit – User-defined or default (R15,000)
•	Budget Usage Progress Bar – Visual representation of expenses vs. budget
•	Color-Coded Alert System:
o	🟢 Green: Expenses < 80% of budget
o	🟡 Yellow/Orange: Expenses between 80% – 99% of budget
o	🔴 Red: Expenses ≥ 100% of budget
•	Recent Transactions List – Last 5 transactions for quick review
•	Floating Action Button (FAB) – Quick-add new transaction
User Actions:
•	Add new income or expense
•	View full transaction history
•	Navigate to Stats, Expenses, Goals, or Profile pages


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

 2. Stats Page (Category Analysis & Spending Insights)
The Stats Page provides detailed analytics on spending patterns, helping users understand where their money goes.
Components:
•	Spending by Category – Pie chart or bar chart showing category-wise expense distribution (e.g., Food, Transport, Entertainment, Bills)
•	Top Spending Category – Highlights the category with the highest expenses
•	Monthly Spending Trend – Line graph comparing expenses over recent months (optional extended feature)
•	Budget Efficiency Indicator – Percentage of budget used and remaining

# User Actions:
•	Switch between current month and custom date range
•	Tap on a category to view all related transactions
•	Export or share spending summary (optional)
________________________________________
 3. Expenses Page (Transaction Management)
The Expenses Page is a dedicated screen for managing all expense-related transactions. It supports full CRUD (Create, Read, Update, Delete) operations.
Components:
•	Transaction List (RecyclerView) – Displays all expenses with:
o	Title / Description
o	Amount (formatted in ZAR)
o	Category (with icon)
o	Date
•	Filter & Sort Options:
o	Filter by: All, Expenses only, Income only
o	Sort by: Date (newest/oldest), Amount (highest/lowest)
o	Date range selector (DatePickerDialog)
•	Add / Edit Transaction Dialog – Material AlertDialog with fields for:
o	Title
o	Amount
o	Category (dropdown)
o	Date
o	Type (Income or Expense)
•	Delete Action – Swipe-to-delete or long-press + confirmation dialog

# User Actions:
•	Add a new expense or income
•	Edit existing transactions
•	Delete transactions with undo option
•	Search transactions by title or category
________________________________________

 4. Goals Page (Financial Goals & Savings Targets)
The Goals Page allows users to set, track, and achieve short-term or long-term financial goals.
Components:
•	Goal Creation Form – Dialog or dedicated section with:
o	Goal name (e.g., “New Laptop”, “Emergency Fund”)
o	Target amount
o	Deadline (optional)
o	Current progress (user or system-updated)
•	Active Goals List – Each goal shows:
o	Goal name
o	Target amount
o	Current saved amount
o	Progress bar (% completed)
o	Days remaining (if deadline set)
•	Goal Completion Status – Mark as completed or archive

# User Actions:
•	Create, edit, or delete a savings goal
•	Add contributions toward a goal (linked to income/expense entries if integrated)
•	View motivational messages when a goal is reached
 Integration note: Goals can be manually updated or automatically linked to specific transaction categories (e.g., “Savings” category contributions).
________________________________________

 5. Profile Page (User Settings & Preferences)
The Profile Page manages user preferences, app settings, and account-related information.
Components:
•	User Info Section – Name, email, profile picture (optional, future implementation)
•	Budget Settings:
o	Set / edit monthly budget limit (default R15,000)
o	Reset budget period (monthly rolling)
•	App Preferences:
o	Dark / Light mode toggle (or follow system)
o	Currency format (ZAR default)
o	Notification preferences (budget exceeded, weekly summaries – optional)
•	Data Management:
o	Export transactions (CSV or PDF)
o	Clear all data (with confirmation)
•	About Section – App version, group member credits, contact support

# User Actions:
•	Update monthly budget limit
•	Switch theme (Dark / Light / System default)
•	Export financial data for external use
•	Reset app data (caution: irreversible)


## 🛠️ Technical Stack

### Languages & Frameworks
- **Kotlin** - Primary programming language
- **XML** - Layout design
- **Material Design Components** - UI components

Android Components Used
•	Activities: MainActivity, DashboardActivity, TransactionsActivity, GoalsActivity, ProfileActivity
•	Fragments (optional for bottom nav screens)
•	RecyclerView with custom Adapter and ViewHolder
•	Material Cards, Buttons, and ProgressBars
•	BottomNavigationView for primary navigation
•	DatePickerDialog for date selection
•	AlertDialog for user confirmations and input forms
•	SharedPreferences or Room Database for local data persistence
Dependencies (Gradle)
gradle
implementation 'androidx.core:core-ktx:1.12.0'
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.11.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'androidx.recyclerview:recyclerview:1.3.2'
implementation 'androidx.room:room-runtime:2.6.0'
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'

Installation & Setup
1.	Clone the repository or download the project ZIP.
2.	Open the project in Android Studio (latest stable version).
3.	Sync Gradle files and install dependencies.
4.	Run the app on an emulator (API level 24+) or a physical Android device.
________________________________________
 Testing & Known Limitations
•	Goals page currently supports manual progress updates (auto-linking to transactions planned for v2).
•	Reports page is labeled as “Coming Soon” in the current build.
•	Data persistence is implemented via SharedPreferences for budget and Room Database for transactions.
•	Dark/Light mode follows system setting by default, with user override in Profile.
________________________________________
 License & Academic Integrity
This project was developed for academic purposes to demonstrate Android development proficiency. All code and design are original work by the named group members.

