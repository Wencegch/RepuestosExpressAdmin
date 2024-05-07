package com.repuestosexpressadmin.controllers

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.repuestosexpressadmin.R

class MainActivity : AppCompatActivity() {
    private lateinit var btnSugerencias: Button
    private lateinit var btnPedidos: Button
    private lateinit var btnProductos: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnSugerencias = findViewById(R.id.btn_Sugerencias)
        btnPedidos = findViewById(R.id.btn_Pedidos)
        btnProductos = findViewById(R.id.btn_Productos)

        btnSugerencias.setOnClickListener {
            val intent = Intent(this, SugerenciasActivity::class.java)
            startActivity(intent)
        }

        btnPedidos.setOnClickListener {

        }

        btnProductos.setOnClickListener {
            val intent = Intent(this, FamiliasActivity::class.java)
            startActivity(intent)
        }

    }
}