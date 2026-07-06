package com.example.safetyapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (SessionManager.isLoggedIn(this)) {

            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )

            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val prefs =
            getSharedPreferences(
                "user_data",
                MODE_PRIVATE
            )

        if (prefs.getString("pin", null) == null) {

            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )

            finish()
            return
        }

        val btnLogin =
            findViewById<Button>(R.id.btnLogin)

        val etPassword =
            findViewById<EditText>(R.id.etPassword)

        btnLogin.setOnClickListener {

            val enteredPin =
                etPassword.text.toString()

            val savedPin =
                prefs.getString(
                    "pin",
                    ""
                )

            if (enteredPin == savedPin) {

                SessionManager.setLoggedIn(
                    this,
                    true
                )

                startActivity(
                    Intent(
                        this,
                        MainActivity::class.java
                    )
                )

                finish()

            } else {

                Toast.makeText(
                    this,
                    "Invalid PIN",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}