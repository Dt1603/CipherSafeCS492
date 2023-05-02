package com.example.computersecurity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the button in the layout
        val button = findViewById<Button>(R.id.login_button)

        // Set a click listener on the button
        button.setOnClickListener {
            // Create an Intent to start the second activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
