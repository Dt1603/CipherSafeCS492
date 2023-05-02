package com.example.computersecurity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class EncryptionActivity : AppCompatActivity() {
    private lateinit var key: String
    private lateinit var inputText: EditText
    private lateinit var keyText: EditText
    private lateinit var encryptedDataTextView: TextView
    private lateinit var decryptedDataTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encryption)

        inputText = findViewById(R.id.input_text)
        keyText = findViewById(R.id.key_text)
        val encryptButton = findViewById<Button>(R.id.encrypt_button)
        val decryptButton = findViewById<Button>(R.id.decrypt_button)
        encryptedDataTextView = findViewById(R.id.encrypted_data_label_textview)
        decryptedDataTextView = findViewById(R.id.decrypted_data_label_textview)

        encryptButton.setOnClickListener {
            val inputData = inputText.text.toString()
            key = keyText.text.toString()

            if (inputData.isEmpty() || key.isEmpty()) {
                Toast.makeText(this, "Please enter input and key!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val keyBytes = key.toByteArray()
                val tea = TEA(keyBytes)

                var testData = inputData.toByteArray()

                if (testData.size % 8 != 0) {
                    val padding = 8 - (testData.size % 8)
                    testData = testData.copyOf(testData.size + padding)
                }

                val encryptedData = tea.encrypt(testData.toString())

                //encryptedDataTextView.text = encryptedData.contentToString()
                val encryptedText = String(encryptedData, Charsets.UTF_8)
                encryptedDataTextView.text = encryptedText
                Log.d("EncryptionActivity", "Encrypted text: $encryptedText")
            } catch (e: Exception) {
                Toast.makeText(this, "Encryption failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        decryptButton.setOnClickListener {
            val encryptedData = encryptedDataTextView.text.toString()

            if (encryptedData.isEmpty() || key.isEmpty()) {
                Toast.makeText(this, "Please encrypt some data first and enter key!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val keyBytes = key.toByteArray()
                val tea = TEA(keyBytes)

                val decryptedData = tea.decrypt(encryptedData)

                decryptedDataTextView.text = decryptedData.contentToString()
            } catch (e: Exception) {
                Toast.makeText(this, "Decryption failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        val nextActivityButton = findViewById<Button>(R.id.shamir_button)
        nextActivityButton.setOnClickListener {
            val url = "https://www.google.com/search?q=shamir%27s+secret+sharing"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }


}
