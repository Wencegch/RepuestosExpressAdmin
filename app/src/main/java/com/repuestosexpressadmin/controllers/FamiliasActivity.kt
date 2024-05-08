package com.repuestosexpressadmin.controllers

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
import com.repuestosexpressadmin.adapters.RecyclerAdapterFamilias
import com.repuestosexpressadmin.models.Familia
import com.repuestosexpressadmin.R
import com.repuestosexpressadmin.utils.Firebase
import com.repuestosexpressadmin.utils.Utils

class FamiliasActivity : AppCompatActivity() {

    private lateinit var familiasAdapter: RecyclerAdapterFamilias
    private lateinit var recyclerView: RecyclerView
    private lateinit var familias: ArrayList<Familia>
    private lateinit var mActionMode: ActionMode
    private var posicionPulsada: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_familias)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = getString(R.string.familias)
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.green))
        }

        familias = ArrayList()// Inicializa la lista de familias
        recyclerView = findViewById(R.id.recyclerViewFamilias)// Inicializa el RecyclerView y el Adapter

        recyclerView.layoutManager = LinearLayoutManager(this) // Agrega un LinearLayoutManager
        familiasAdapter = RecyclerAdapterFamilias(familias)
        recyclerView.adapter = familiasAdapter

        // Obtiene las familias de Firebase y las agrega a la lista
        Firebase().obtenerFamilias { listaFamilias ->
            familias.clear()
            // Agrega cada familia a la lista familias
            familias.addAll(listaFamilias)
            // Notifica al adapter que los datos han cambiado
            familiasAdapter.notifyDataSetChanged()

        }

        // Establecer un Listener para manejar los clics en los elementos del RecyclerView
        familiasAdapter.setOnItemClickListener(object : RecyclerAdapterFamilias.OnItemClickListener {
            /**
             * Método que se llama cuando se hace clic en un elemento del RecyclerView.
             * @param position La posición del elemento seleccionado en la lista.
             */
            override fun onItemClick(position: Int) {
                // Obtener el elemento seleccionado del array
                val familiaSeleccionada = familias.get(position)

                val intent = Intent(this@FamiliasActivity, ProductosActivity::class.java).apply {
                    putExtra("Idfamilia", familiaSeleccionada.id)
                    putExtra("Nombre", familiaSeleccionada.nombre)
                }
                Log.d("Id Familia", familiaSeleccionada.id)
                Log.d("Nombre", familiaSeleccionada.nombre)
                Log.d("Información", familiaSeleccionada.info)
                Log.d("Imagen URL", familiaSeleccionada.imgUrl)

                startActivity(intent)
            }
        })

        familiasAdapter.setOnItemLongClickListener(object : RecyclerAdapterFamilias.OnItemLongClickListener{
            override fun onItemLongClick(position: Int) {
                Utils.Toast(this@FamiliasActivity, "Posición $position")
                mActionMode = startActionMode(mActionCallback)!!
                posicionPulsada = position
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private val subirFamiliaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val idFamiliaNueva = data?.getStringExtra("idFamilia")
            if (!idFamiliaNueva.isNullOrEmpty()) {
                // Obtener la nueva familia utilizando el ID y agregarla a la lista
                Firebase().obtenerFamiliaPorId(idFamiliaNueva) { nuevaFamilia ->
                    if (nuevaFamilia != null && !familias.contains(nuevaFamilia)) {
                        familias.add(nuevaFamilia)
                        familiasAdapter.notifyItemInserted(familias.size)
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btn_Add -> {
                val i = Intent(this, SubirFamiliaActivity::class.java)
                subirFamiliaLauncher.launch(i)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
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

                BeautifulDialog.build(this@FamiliasActivity)
                    .title(getString(R.string.borrar_producto), titleColor = R.color.black)
                    .description(getString(R.string.perder_informacion_familia))
                    .type(type = BeautifulDialog.TYPE.ALERT)
                    .position(BeautifulDialog.POSITIONS.CENTER)
                    .onPositive(text = getString(R.string.aceptar), shouldIDismissOnClick = true) {
                        val familia: Familia = familiasAdapter.getFamilia(posicionPulsada)
                        val idFamilia = familia.id

                        Firebase().borrarFamiliaYProductos(idFamilia) {
                            Utils.Toast(this@FamiliasActivity, getString(R.string.familia_eliminada))
                            familias.removeAt(posicionPulsada)
                            familiasAdapter.notifyItemRemoved(posicionPulsada)
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