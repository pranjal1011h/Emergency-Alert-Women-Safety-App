package com.example.safetyapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        val etName =
            findViewById<EditText>(R.id.etName)

        val etPhone =
            findViewById<EditText>(R.id.etPhone)

        val etPin =
            findViewById<EditText>(R.id.etPin)
        val etConfirmPin =
            findViewById<EditText>(R.id.etConfirmPin)
        val btnRegister =
            findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {

            val name =
                etName.text.toString()

            val phone =
                etPhone.text.toString()

            val pin =
                etPin.text.toString()
            val confirmPin =
                etConfirmPin.text.toString()

            if (name.isEmpty()
                || phone.length != 10
                || pin.length != 4
            ) {

                Toast.makeText(
                    this,
                    "Enter valid details",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }
            if (pin != confirmPin) {

                Toast.makeText(
                    this,
                    "PIN does not match",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val prefs =
                getSharedPreferences(
                    "user_data",
                    MODE_PRIVATE
                )

            prefs.edit()
                .putString("name", name)
                .putString("phone", phone)
                .putString("pin", pin)
                .apply()

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
        }
    }
}