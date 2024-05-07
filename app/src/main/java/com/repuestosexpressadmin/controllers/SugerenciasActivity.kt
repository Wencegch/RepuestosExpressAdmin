package com.repuestosexpressadmin.controllers

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.repuestosexpressadmin.R
import com.repuestosexpressadmin.adapters.RecyclerAdapterProductos
import com.repuestosexpressadmin.models.Producto
import com.repuestosexpressadmin.utils.Firebase
import com.repuestosexpressadmin.utils.Utils

class SugerenciasActivity : AppCompatActivity() {
    private lateinit var productosAdapter: RecyclerAdapterProductos
    private lateinit var recyclerView: RecyclerView
    private lateinit var productos: ArrayList<Producto>
    private var posicionPulsada: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sugerencias)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = getString(R.string.sugerencias)
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.green))
        }

        productos = ArrayList()// Inicializa la lista de familias
        recyclerView = findViewById(R.id.recyclerViewSugerencias)// Inicializa el RecyclerView y el Adapter

        recyclerView.layoutManager = LinearLayoutManager(this) // Agrega un LinearLayoutManager
        productosAdapter = RecyclerAdapterProductos(productos, enableLongClick = false)
        recyclerView.adapter = productosAdapter

        // Obtiene las familias de Firebase y las agrega a la lista
        Firebase().obtenerProductos { listaProductos ->
            // Agrega cada familia a la lista familias
            productos.addAll(listaProductos)
            // Notifica al adapter que los datos han cambiado
            productosAdapter.notifyDataSetChanged()
        }

        productosAdapter.setOnItemClickListener(object :RecyclerAdapterProductos.OnItemClickListener {
            override fun onItemClick(position: Int) {
                Utils.Toast(this@SugerenciasActivity, "Posición $position")
                Log.d("OnItemClickListener", productosAdapter.getProducto(position).id)
                Log.d("OnItemClickListener", productosAdapter.getProducto(position).nombre)
                posicionPulsada = position
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.suggest_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            R.id.btn_EnviarSugerencias -> {
                Utils.Toast(this, "Hola")
            }
        }
        return super.onOptionsItemSelected(item)
    }
}