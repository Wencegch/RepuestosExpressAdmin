package com.repuestosexpressadmin.controllers

import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
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
import com.repuestosexpressadmin.adapters.RecyclerAdapterProductos
import com.repuestosexpressadmin.models.Producto
import com.repuestosexpressadmin.utils.Firebase
import com.repuestosexpressadmin.utils.Utils

/**
 * Actividad que muestra una lista de productos y permite su gestión.
 */
class ProductosActivity : AppCompatActivity() {

    private lateinit var productosAdapter: RecyclerAdapterProductos
    private lateinit var recyclerView: RecyclerView
    private lateinit var productos: ArrayList<Producto>
    private lateinit var productosFiltrados: ArrayList<Producto>
    private lateinit var idFamilia: String
    private var mActionMode: ActionMode? = null
    private var posicionPulsada: Int = -1
    private lateinit var txtFiltroProducto: EditText

    /**
     * Método llamado cuando la actividad es creada.
     * @param savedInstanceState Estado guardado de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        idFamilia = intent.getStringExtra("Idfamilia").toString()

        supportActionBar?.apply {
            title = intent.getStringExtra("Nombre")
        }

        recyclerView = findViewById(R.id.recyclerViewProductos)
        txtFiltroProducto = findViewById(R.id.txtFiltroProductos)

        productos = ArrayList()
        productosFiltrados = ArrayList()

        recyclerView.layoutManager = LinearLayoutManager(this)
        productosAdapter = RecyclerAdapterProductos(productosFiltrados)
        recyclerView.adapter = productosAdapter

        txtFiltroProducto.addTextChangedListener { userFilter ->
            productosFiltrados = productos.filter { producto ->
                producto.nombre.lowercase().contains(userFilter.toString().lowercase())
            }.toCollection(ArrayList())
            productosAdapter.updateProductos(productosFiltrados)
        }

        Firebase().obtenerProductosFamilia(idFamilia) { listaProductos ->
            productos.clear()
            productos.addAll(listaProductos)
            productosFiltrados.clear()
            productosFiltrados.addAll(listaProductos)
            productosAdapter.notifyDataSetChanged()
        }

        productosAdapter.setOnItemLongClickListener(object : RecyclerAdapterProductos.OnItemLongClickListener {
            override fun onItemLongClick(position: Int) {
                posicionPulsada = position
                val productoSeleccionado = productosFiltrados[posicionPulsada]

                if (productoSeleccionado.selected) {
                    productoSeleccionado.selected = false
                } else {
                    productosAdapter.deseleccionarTodos()
                    productoSeleccionado.selected = true
                }

                if (productoSeleccionado.selected && mActionMode == null) {
                    mActionMode = startActionMode(mActionCallback)!!
                } else {
                    if (!productoSeleccionado.selected && mActionMode != null) {
                    mActionMode!!.finish()
                    }
                }
            }
        })
    }

    /**
     * Método para crear el menú de opciones.
     * @param menu El menú de opciones.
     * @return true si el menú se creó correctamente.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Callback para manejar el resultado de la actividad de subir un producto.
     */
    private val subirProductoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val idProductoNuevo = data?.getStringExtra("idProducto")
            if (!idProductoNuevo.isNullOrEmpty()) {
                Firebase().obtenerProductoPorId(idProductoNuevo) { nuevoProducto ->
                    if (nuevoProducto != null && !productos.contains(nuevoProducto)) {
                        productos.add(nuevoProducto)
                        productosFiltrados.add(nuevoProducto)
                        productosAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    /**
     * Método para manejar la selección de opciones del menú.
     * @param item La opción del menú seleccionada.
     * @return true si la opción se manejó correctamente.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            R.id.btn_Add -> {
                val i = Intent(this, SubirProductoActivity::class.java)
                i.putExtra("idFamilia", idFamilia)
                subirProductoLauncher.launch(i)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Callback para manejar la acción de modo de acción contextual.
     */
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
                        val producto: Producto = productosFiltrados[posicionPulsada]
                        val idProducto = producto.id
                        val urlImagen = producto.imgUrl

                        Firebase().borrarProducto(idProducto, urlImagen) {
                            Utils.Toast(this@ProductosActivity, getString(R.string.producto_eliminado))
                            productos.remove(producto)
                            productosFiltrados.remove(producto)
                            productosAdapter.notifyDataSetChanged()
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
