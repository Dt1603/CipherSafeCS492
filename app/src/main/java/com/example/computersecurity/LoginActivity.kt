package com.example.computersecurity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    private lateinit var dbHelper: UserDbHelper
    private lateinit var usernameText: EditText
    private lateinit var passwordText: EditText
    private lateinit var confirmPassText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = UserDbHelper(this)

        usernameText = findViewById(R.id.edit_text_email)
        passwordText = findViewById(R.id.edit_text_password)
        confirmPassText = findViewById(R.id.edit_text_confirm_password)

        findViewById<Button>(R.id.login_button).setOnClickListener { loginButton() }
        findViewById<Button>(R.id.signup_button).setOnClickListener { signupButton() }
    }

    private fun signupButton() {
        if (usernameText.text.isEmpty() || passwordText.text.isEmpty() || confirmPassText.text.isEmpty()) {
            showToast("Please enter username, password, and confirm password")
            return
        }

        if (passwordText.text.toString() != confirmPassText.text.toString()) {
            showToast("Passwords do not match. Please try again.")
            return
        }

        try {
            dbHelper.createUser(usernameText.text.toString(), passwordText.text.toString(),confirmPassText.text.toString())
            clearEditTexts()
            showToast("Successfully signed up!")
        } catch (e: Exception) {
            Log.e(TAG, "error: $e")
        }
    }

    private fun loginButton() {
        if (usernameText.text.isEmpty() || passwordText.text.isEmpty()) {
            showToast("Please enter username and password")
            return
        }

        try {
            val isUserValid = dbHelper.checkUserCredentials(
                usernameText.text.toString(),
                passwordText.text.toString()
            )

            if (isUserValid) {
                val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("isLoggedIn", true)
                editor.apply()

                showToast("Successfully logged in!")
                clearEditTexts()

                val intent = Intent(this, EncryptionActivity::class.java)
                startActivity(intent)
            } else {
                showToast("Invalid credentials. Please try again.")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error logging in: $e")
            showToast("Error logging in.")
        }
    }
    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun clearEditTexts() {
        usernameText.text.clear()
        passwordText.text.clear()
        confirmPassText.text.clear()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

    companion object {
        const val TAG = "LoginActivity"
    }
}
