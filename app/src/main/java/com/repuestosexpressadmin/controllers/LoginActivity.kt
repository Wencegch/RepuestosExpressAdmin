package com.repuestosexpressadmin.controllers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.repuestosexpressadmin.R
import com.repuestosexpressadmin.utils.Firebase
import com.repuestosexpressadmin.utils.Utils

class LoginActivity : AppCompatActivity() {
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var saveEmail: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txtEmail = findViewById(R.id.txtEmail)
        txtPassword = findViewById(R.id.txtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        saveEmail = findViewById(R.id.checkBoxEmail)

        loadPreferences()

        btnLogin.setOnClickListener {
            if (txtEmail.text.toString().trim().isNotEmpty()) {
                if (txtPassword.text.toString().trim().isNotEmpty()) {
                    Firebase().iniciarSesion(txtEmail.text.toString().trim()) { success ->
                        if (success) {
                            saveUserPreferences()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Utils.Toast(this, getString(R.string.introducir_cuenta_administrador))
                        }
                    }
                } else {
                    txtPassword.error = getString(R.string.requerido)
                }
            } else {
                txtEmail.error = getString(R.string.requerido)
            }
        }
    }

    /**
     * Guarda la información del usuario en la memoria interna del dispositivo
     */
    private fun saveUserPreferences() {
        val activado: Boolean
        // se crea un objeto SharedPreferences que se utiliza para almacenar las preferencias
        val sharedPreferences = getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)

        if (saveEmail.isChecked) {
            activado = true
            // se almacena el email (obtenido del objeto txtEmail)
            sharedPreferences.edit().putString("EMAIL", txtEmail.text.toString()).apply()
            val editor = sharedPreferences.edit()
            editor.putBoolean("SAVE_EMAIL", activado)
            editor.apply()
        } else {
            activado = false
            sharedPreferences.edit().remove("EMAIL").apply()
            val editor = sharedPreferences.edit()
            editor.putBoolean("SAVE_EMAIL", activado)
            editor.apply()
        }
    }


    /**
     * Carga las preferencias de usuario.
     */
    private fun loadPreferences() {
        // se obtiene el objeto SharedPreferences que se utilizó para almacenar las preferencias en el método saveUserPreferences
        val sharedPreferences = getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        // se obtiene el valor booleano save asociado con la clave "SAVE_EMAIL" utilizando el método getBoolean()
        val save = sharedPreferences.getBoolean("SAVE_EMAIL", false)

        if (save) {
            val user = sharedPreferences.getString("EMAIL", "")
            txtEmail.setText(user)
            saveEmail.isChecked = true
        }
    }

}
