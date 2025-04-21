package com.example.vibematch
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import android.widget.Button


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            when {
                username.isEmpty() -> etUsername.error = "Please enter username"
                email.isEmpty() -> etEmail.error = "Please enter email"
                password.isEmpty() -> etPassword.error = "Please enter password"
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                    etEmail.error = "Invalid email format"
                else -> {
                    // Perform login logic here
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, CompleteFormActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}