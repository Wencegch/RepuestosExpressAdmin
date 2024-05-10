package com.repuestosexpressadmin.controllers

import RecyclerAdapterProductos
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iamageo.library.BeautifulDialog
import com.iamageo.library.description
import com.iamageo.library.onNegative
import com.iamageo.library.onPositive
import com.iamageo.library.position
import com.iamageo.library.title
import com.iamageo.library.type
import com.repuestosexpressadmin.R
import com.repuestosexpressadmin.models.Producto
import com.repuestosexpressadmin.utils.Firebase
import com.repuestosexpressadmin.utils.Utils

class ProductosActivity : AppCompatActivity() {

    private lateinit var productosAdapter: RecyclerAdapterProductos
    private lateinit var recyclerView: RecyclerView
    private lateinit var productosArray: ArrayList<Producto>
    private lateinit var idFamilia: String
    private var mActionMode: ActionMode? = null
    private var posicionPulsada: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        idFamilia = intent.getStringExtra("Idfamilia").toString()
        val nombreFamilia = intent.getStringExtra("Nombre")
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = nombreFamilia
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.green))
        }

        productosArray = ArrayList()
        recyclerView = findViewById(R.id.recyclerViewProductos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        productosAdapter = RecyclerAdapterProductos(productosArray)
        recyclerView.adapter = productosAdapter

        Firebase().obtenerProductosFamilia(idFamilia) { listaProductos ->
            productosArray.clear()
            productosArray.addAll(listaProductos)
            productosAdapter.notifyDataSetChanged()
        }

        productosAdapter.setOnItemLongClickListener(object : RecyclerAdapterProductos.OnItemLongClickListener {
            override fun onItemLongClick(position: Int) {
                val productoSeleccionado = productosArray[position]
                Log.d("setOnItemLongClickListener",productoSeleccionado.id)

                // Si el producto está seleccionado, deselecciónalo
                if (productoSeleccionado.selected) {
                    productoSeleccionado.selected = false
                } else {
                    // Si el producto no está seleccionado, selecciona este y deselecciona los demás
                    productosAdapter.deseleccionarTodos()
                    productoSeleccionado.selected = true
                }
                // Notificar al adaptador sobre los cambios
                productosAdapter.notifyDataSetChanged()

                // Actualizar la posición pulsada
                posicionPulsada = position

                // Iniciar el modo de acción si es necesario
                if (productoSeleccionado.selected && mActionMode == null) {
                    mActionMode = startActionMode(mActionCallback)!!
                } else if (!productoSeleccionado.selected && mActionMode != null) {
                    mActionMode!!.finish()
                }
            }
        })

    }

    //Menu simple añadir
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private val subirProductoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val idProductoNuevo = data?.getStringExtra("idProducto")
            if (!idProductoNuevo.isNullOrEmpty()) {
                // Obtener el nuevo producto utilizando el ID y agregarlo a la lista
                Firebase().obtenerProductoPorId(idProductoNuevo) { nuevoProducto ->
                    if (nuevoProducto != null && !productosArray.contains(nuevoProducto)) {
                        productosArray.add(nuevoProducto)
                        productosAdapter.notifyItemInserted(productosArray.size)
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            R.id.btn_Add -> {
                val i = Intent(this, SubirProductoActivity::class.java)
                i.putExtra("idFamilia", idFamilia)
                Log.d("Id Familia", idFamilia)
                subirProductoLauncher.launch(i)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val mActionCallback: ActionMode.Callback = object : ActionMode.Callback {

        override fun onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean {
            val inflater = actionMode.menuInflater
            inflater.inflate(R.menu.delete_menu, menu)
            return true
        }

        override fun onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(actionMode: ActionMode, menuItem: MenuItem): Boolean {
            val itemId = menuItem.itemId
            if (itemId == R.id.btn_Borrar) {

                BeautifulDialog.build(this@ProductosActivity)
                    .title(getString(R.string.borrar_producto), titleColor = R.color.black)
                    .description(getString(R.string.perder_informacion_producto))
                    .type(type = BeautifulDialog.TYPE.ALERT)
                    .position(BeautifulDialog.POSITIONS.CENTER)
                    .onPositive(text = getString(R.string.aceptar), shouldIDismissOnClick = true) {
                        val producto: Producto = productosAdapter.getProducto(posicionPulsada)
                        val idProducto = producto.id
                        val urlImagen = producto.imgUrl

                        Firebase().borrarProducto(idProducto, urlImagen) {
                            Utils.Toast(this@ProductosActivity, getString(R.string.producto_eliminado))
                            productosArray.removeAt(posicionPulsada)
                            productosAdapter.notifyItemRemoved(posicionPulsada)
                        }
                    }
                    .onNegative(text = getString(R.string.cancelar)) {}

                actionMode.finish()
            }
            return true
        }

        override fun onDestroyActionMode(actionMode: ActionMode) {
            productosAdapter.deseleccionarTodos()
            mActionMode = null
        }
    }
}