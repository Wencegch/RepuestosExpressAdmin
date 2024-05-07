package com.repuestosexpressadmin.controllers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
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
import com.repuestosexpressadmin.adapters.RecyclerAdapterProductos
import com.repuestosexpressadmin.models.Producto
import com.repuestosexpressadmin.utils.Firebase
import com.repuestosexpressadmin.utils.Utils

class ProductosActivity : AppCompatActivity() {

    private lateinit var productosAdapter: RecyclerAdapterProductos
    private lateinit var recyclerView: RecyclerView
    private lateinit var productos: ArrayList<Producto>
    private lateinit var idFamilia: String
    private lateinit var mActionMode: ActionMode
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

        productos = ArrayList()
        recyclerView = findViewById(R.id.recyclerViewProductos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        productosAdapter = RecyclerAdapterProductos(productos)
        recyclerView.adapter = productosAdapter

        Firebase().obtenerProductosFamilia(idFamilia) { listaProductos ->
            productos.clear()
            productos.addAll(listaProductos)
            productosAdapter.notifyDataSetChanged()
        }

        productosAdapter.setOnItemLongClickListener(object : RecyclerAdapterProductos.OnItemLongClickListener {

            override fun onItemLongClick(position: Int) {
                Utils.Toast(this@ProductosActivity, "Posición $position")

                Log.d("OnItemLongClickListener", "${productosAdapter.getProducto(position).id}")

                mActionMode = startActionMode(mActionCallback)!!
                posicionPulsada = position
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            R.id.btn_Add -> {
                val i = Intent(this, SubirProductoActivity::class.java)
                i.putExtra("idFamilia", idFamilia)
                Log.d("Id Familia", idFamilia)
                startActivity(i)
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
                            productos.removeAt(posicionPulsada)
                            productosAdapter.notifyItemRemoved(posicionPulsada)
                        }
                    }
                    .onNegative(text = getString(R.string.cancelar)) {}

                actionMode.finish()
            }
            return true
        }

        override fun onDestroyActionMode(actionMode: ActionMode) { }
    }
}