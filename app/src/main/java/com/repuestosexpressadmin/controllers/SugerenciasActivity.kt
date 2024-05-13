package com.repuestosexpressadmin.controllers

import RecyclerAdapterProductos
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.repuestosexpressadmin.R
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

        supportActionBar?.apply {
            title = getString(R.string.sugerencias)
            setBackgroundDrawable(ContextCompat.getDrawable(this@SugerenciasActivity, R.color.green))
        }

        productos = ArrayList()// Inicializa la lista de familias
        recyclerView = findViewById(R.id.recyclerViewSugerencias)
        recyclerView.layoutManager = LinearLayoutManager(this)
        productosAdapter = RecyclerAdapterProductos(productos)
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
                // Actualizar la posición pulsada
                posicionPulsada = position
                val productoSeleccionado = productos[posicionPulsada]

                // Si el producto está seleccionado, deselecciónalo
                if (productoSeleccionado.selected) {
                    productoSeleccionado.selected = false
                } else {
                    // Si el producto no está seleccionado, lo selecciona
                    productoSeleccionado.selected = true
                }
                // Notificar al adaptador sobre los cambios
                productosAdapter.notifyItemChanged(posicionPulsada)
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
                // Crear una lista para almacenar los IDs de los productos seleccionados
                val idsProductosSeleccionados = mutableListOf<String>()
                // Iterar sobre los productos en el adaptador para encontrar los seleccionados
                for (producto in productos) {
                    if (producto.selected) {
                        // Agregar el ID del producto seleccionado a la lista
                        idsProductosSeleccionados.add(producto.id)
                    }
                }
                // Llamar al método para actualizar las sugerencias de la clase Firebase
                Firebase().actualizarSugerencias(idsProductosSeleccionados)
                Utils.Toast(this, getString(R.string.sugerencias_enviadas))
                finish()
                return true // Devolver true para indicar que el evento ha sido manejado
            }
        }
        return super.onOptionsItemSelected(item)
    }

}