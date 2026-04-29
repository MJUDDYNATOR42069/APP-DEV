package com.example.budgetapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AccountsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageRef: StorageReference

    private lateinit var backButton: ImageView
    private lateinit var logoutButton: ImageView
    private lateinit var profileImage: ImageView
    private lateinit var profileImageCard: CardView
    private lateinit var changePhotoText: TextView
    private lateinit var profileFullName: EditText
    private lateinit var profileEmail: EditText
    private lateinit var profilePhone: EditText
    private lateinit var saveProfileButton: Button
    private lateinit var totalIncomeValue: TextView
    private lateinit var totalExpensesValue: TextView
    private lateinit var remainingBudgetValue: TextView
    private lateinit var changePasswordLayout: LinearLayout
    private lateinit var notificationSettingsLayout: LinearLayout
    private lateinit var currencySettingsLayout: LinearLayout
    private lateinit var privacyPolicy: TextView
    private lateinit var appVersion: TextView

    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                selectedImageUri = result.data?.data

                selectedImageUri?.let {
                    profileImage.setImageURI(it)
                    uploadProfileImage(it)
                }
            }
        }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Assuming activity_accounts is the intended layout for this activity
        setContentView(R.layout.activity_accounts)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference

        initializeViews()
        setupClickListeners()
        loadUserData()
        loadBudgetStatistics()

        findViewById<android.view.View>(R.id.main)?.let { mainView ->
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, insets ->
                val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
                insets
            }
        }
    }

    private fun initializeViews() {
        // These IDs must exist in R.layout.activity_accounts or the app will crash at runtime.
        // If they don't exist, we'll need to update the layout or the code.
        backButton = findViewById(R.id.backButton) ?: ImageView(this)
        logoutButton = findViewById(R.id.logoutButton) ?: ImageView(this)
        profileImage = findViewById(R.id.profileImage) ?: ImageView(this)
        profileImageCard = findViewById(R.id.profileImageCard) ?: CardView(this)
        changePhotoText = findViewById(R.id.changePhotoText) ?: TextView(this)
        profileFullName = findViewById(R.id.profileFullName) ?: EditText(this)
        profileEmail = findViewById(R.id.profileEmail) ?: EditText(this)
        profilePhone = findViewById(R.id.profilePhone) ?: EditText(this)
        saveProfileButton = findViewById(R.id.saveProfileButton) ?: Button(this)
        totalIncomeValue = findViewById(R.id.totalIncomeValue) ?: TextView(this)
        totalExpensesValue = findViewById(R.id.totalExpensesValue) ?: TextView(this)
        remainingBudgetValue = findViewById(R.id.remainingBudgetValue) ?: TextView(this)
        changePasswordLayout = findViewById(R.id.changePasswordLayout) ?: LinearLayout(this)
        notificationSettingsLayout = findViewById(R.id.notificationSettingsLayout) ?: LinearLayout(this)
        currencySettingsLayout = findViewById(R.id.currencySettingsLayout) ?: LinearLayout(this)
        privacyPolicy = findViewById(R.id.privacyPolicy) ?: TextView(this)
        appVersion = findViewById(R.id.appVersion) ?: TextView(this)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener { finish() }
        logoutButton.setOnClickListener { showLogoutDialog() }
        profileImageCard.setOnClickListener { chooseImage() }
        changePhotoText.setOnClickListener { chooseImage() }
        saveProfileButton.setOnClickListener { saveUserData() }
        changePasswordLayout.setOnClickListener { changePassword() }
        notificationSettingsLayout.setOnClickListener { showNotificationSettings() }
        currencySettingsLayout.setOnClickListener { showCurrencySettings() }
        privacyPolicy.setOnClickListener { showPrivacyPolicy() }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    profileFullName.setText(document.getString("fullName") ?: "")
                    profilePhone.setText(document.getString("phone") ?: "")
                    profileEmail.setText(auth.currentUser?.email ?: "")
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserData() {
        val userId = auth.currentUser?.uid ?: return
        val fullName = profileFullName.text.toString().trim()
        val phone = profilePhone.text.toString().trim()

        if (fullName.isEmpty()) {
            profileFullName.error = "Full name required"
            profileFullName.requestFocus()
            return
        }

        saveProfileButton.isEnabled = false
        saveProfileButton.text = getString(R.string.saving)

        val userData = hashMapOf(
            "fullName" to fullName,
            "phone" to phone
        )

        firestore.collection("users")
            .document(userId)
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                saveProfileButton.isEnabled = true
                saveProfileButton.text = getString(R.string.save_changes)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
                saveProfileButton.isEnabled = true
                saveProfileButton.text = getString(R.string.save_changes)
            }
    }

    private fun loadBudgetStatistics() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("transactions")
            .whereEqualTo("type", "income")
            .get()
            .addOnSuccessListener { documents ->
                var total = 0.0
                for (doc in documents) {
                    total += doc.getDouble("amount") ?: 0.0
                }
                totalIncomeValue.text = "R%.2f".format(total)
                updateRemainingBudget()
            }

        firestore.collection("users")
            .document(userId)
            .collection("transactions")
            .whereEqualTo("type", "expense")
            .get()
            .addOnSuccessListener { documents ->
                var total = 0.0
                for (doc in documents) {
                    total += doc.getDouble("amount") ?: 0.0
                }
                totalExpensesValue.text = "R%.2f".format(total)
                updateRemainingBudget()
            }
    }

    private fun updateRemainingBudget() {
        val income = totalIncomeValue.text.toString().replace("R", "").toDoubleOrNull() ?: 0.0
        val expense = totalExpensesValue.text.toString().replace("R", "").toDoubleOrNull() ?: 0.0
        val remaining = income - expense

        remainingBudgetValue.text = "R%.2f".format(remaining)

        if (remaining < 0) {
            remainingBudgetValue.setTextColor(
                ContextCompat.getColor(this, android.R.color.holo_red_dark)
            )
        } else {
            remainingBudgetValue.setTextColor(
                ContextCompat.getColor(this, android.R.color.holo_green_dark)
            )
        }
    }

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun uploadProfileImage(imageUri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        val imageRef = storageRef.child("profile_images/$userId.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    firestore.collection("users")
                        .document(userId)
                        .update("profileImage", uri.toString())
                        .addOnSuccessListener {
                            Toast.makeText(this, "Profile image updated", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun changePassword() {
        val email = auth.currentUser?.email ?: return
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(this, "Password reset email sent", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to send email", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showNotificationSettings() {
        Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun showCurrencySettings() {
        Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun showPrivacyPolicy() {
        Toast.makeText(this, "Privacy Policy coming soon", Toast.LENGTH_SHORT).show()
    }
}
