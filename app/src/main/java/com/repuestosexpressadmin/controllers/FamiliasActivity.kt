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
    private lateinit var familiasArray: ArrayList<Familia>
    private var mActionMode: ActionMode? = null
    private var posicionPulsada: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_familias)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = getString(R.string.familias)
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.green))
        }

        familiasArray = ArrayList()// Inicializa la lista de familias
        recyclerView = findViewById(R.id.recyclerViewFamilias)// Inicializa el RecyclerView y el Adapter

        recyclerView.layoutManager = LinearLayoutManager(this) // Agrega un LinearLayoutManager
        familiasAdapter = RecyclerAdapterFamilias(familiasArray)
        recyclerView.adapter = familiasAdapter

        // Obtiene las familias de Firebase y las agrega a la lista
        Firebase().obtenerFamilias { listaFamilias ->
            familiasArray.clear()
            // Agrega cada familia a la lista familias
            familiasArray.addAll(listaFamilias)
            // Notifica al adapter que los datos han cambiado
            familiasAdapter.notifyDataSetChanged()

        }

        // Establecer un Listener para manejar los clics en los elementos del RecyclerView
        familiasAdapter.setOnItemClickListener(object : RecyclerAdapterFamilias.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // Obtener el elemento seleccionado del array
                val familiaSeleccionada = familiasArray.get(position)
                val intent = Intent(this@FamiliasActivity, ProductosActivity::class.java).apply {
                    putExtra("Idfamilia", familiaSeleccionada.id)
                    putExtra("Nombre", familiaSeleccionada.nombre)
                }
                startActivity(intent)
            }
        })

        familiasAdapter.setOnItemLongClickListener(object : RecyclerAdapterFamilias.OnItemLongClickListener{
            override fun onItemLongClick(position: Int) {
                val familiaSeleccionada = familiasArray[position]

                // Si el producto está seleccionado, deselecciónalo
                if(familiaSeleccionada.selected){
                    familiaSeleccionada.selected = false
                }else{
                    // Si el producto no está seleccionado, selecciona este y deselecciona los demás
                    familiasAdapter.deseleccionarTodos()
                    familiaSeleccionada.selected = true
                }
                // Notificar al adaptador sobre los cambios
                familiasAdapter.notifyDataSetChanged()

                // Actualizar la posición pulsada
                posicionPulsada = position

                // Iniciar el modo de acción si es necesario
                if (familiaSeleccionada.selected && mActionMode == null) {
                    mActionMode = startActionMode(mActionCallback)!!
                } else if (!familiaSeleccionada.selected && mActionMode != null) {
                    mActionMode!!.finish()
                }
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
                    if (nuevaFamilia != null && !familiasArray.contains(nuevaFamilia)) {
                        familiasArray.add(nuevaFamilia)
                        familiasAdapter.notifyItemInserted(familiasArray.size)
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
                    .title(getString(R.string.borrar_familia), titleColor = R.color.black)
                    .description(getString(R.string.perder_informacion_familia))
                    .type(type = BeautifulDialog.TYPE.ALERT)
                    .position(BeautifulDialog.POSITIONS.CENTER)
                    .onPositive(text = getString(R.string.aceptar), shouldIDismissOnClick = true) {
                        val familia: Familia = familiasAdapter.getFamilia(posicionPulsada)
                        val idFamilia = familia.id

                        Firebase().borrarFamiliaYProductos(idFamilia) {
                            Utils.Toast(this@FamiliasActivity, getString(R.string.familia_eliminada))
                            familiasArray.removeAt(posicionPulsada)
                            familiasAdapter.notifyItemRemoved(posicionPulsada)
                        }
                    }
                    .onNegative(text = getString(R.string.cancelar)) {}

                actionMode.finish()
            }
            return true
        }

        override fun onDestroyActionMode(actionMode: ActionMode) {
            familiasAdapter.deseleccionarTodos()
            mActionMode = null
        }
    }
}