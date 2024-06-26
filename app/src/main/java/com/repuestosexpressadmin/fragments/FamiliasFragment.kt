package com.repuestosexpressadmin.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
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
import com.repuestosexpressadmin.adapters.RecyclerAdapterFamilias
import com.repuestosexpressadmin.controllers.ProductosActivity
import com.repuestosexpressadmin.controllers.SubirFamiliaActivity
import com.repuestosexpressadmin.models.Familia
import com.repuestosexpressadmin.utils.Firebase
import com.repuestosexpressadmin.utils.Utils

/**
 * Fragmento para mostrar la lista de familias y sus productos asociados.
 */
class FamiliasFragment : Fragment() {

    private lateinit var familiasAdapter: RecyclerAdapterFamilias
    private lateinit var recyclerView: RecyclerView
    private lateinit var familias: ArrayList<Familia>
    private lateinit var familiasFiltradas: ArrayList<Familia>
    private var mActionMode: ActionMode? = null
    private var posicionPulsada: Int = -1
    private lateinit var txtFiltroFamilia: EditText

    /**
     * Método llamado para crear la vista del fragmento.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_familias, container, false)
    }

    /**
     * Método llamado cuando la vista del fragmento ha sido creada.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.familias)
        }

        txtFiltroFamilia = view.findViewById(R.id.txtFiltroFamilia)
        recyclerView = view.findViewById(R.id.recyclerViewFamilias)

        familias = ArrayList()
        familiasFiltradas = ArrayList()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        familiasAdapter = RecyclerAdapterFamilias(familiasFiltradas)
        recyclerView.adapter = familiasAdapter

        txtFiltroFamilia.addTextChangedListener { userFilter ->
            familiasFiltradas = familias.filter { familia ->
                familia.nombre.lowercase().contains(userFilter.toString().lowercase())
            }.toCollection(ArrayList())
            familiasAdapter.updateFamilias(familiasFiltradas)
        }

        Firebase().obtenerFamilias { listaFamilias ->
            familias.clear()
            familias.addAll(listaFamilias)
            familiasFiltradas.clear()
            familiasFiltradas.addAll(listaFamilias)
            familiasAdapter.notifyDataSetChanged()
        }

        familiasAdapter.setOnItemClickListener(object : RecyclerAdapterFamilias.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val haySeleccionadas = familias.any { it.selected }
                if (haySeleccionadas) {
                    mActionMode?.finish()
                } else {
                    val familiaSeleccionada = familiasFiltradas[position]
                    val intent = Intent(requireContext(), ProductosActivity::class.java).apply {
                        putExtra("Idfamilia", familiaSeleccionada.id)
                        putExtra("Nombre", familiaSeleccionada.nombre)
                    }
                    startActivity(intent)
                }
            }
        })

        familiasAdapter.setOnItemLongClickListener(object : RecyclerAdapterFamilias.OnItemLongClickListener {
            override fun onItemLongClick(position: Int) {
                posicionPulsada = position
                val familiaSeleccionada = familiasFiltradas[posicionPulsada]

                if (familiaSeleccionada.selected) {
                    familiaSeleccionada.selected = false
                } else {
                    familiasAdapter.deseleccionarTodos()
                    familiaSeleccionada.selected = true
                }

                if (familiaSeleccionada.selected && mActionMode == null) {
                    mActionMode = requireActivity().startActionMode(mActionCallback)
                } else {
                    if (!familiaSeleccionada.selected && mActionMode != null) {
                        mActionMode!!.finish()
                    }
                }
            }
        })
    }

    /**
     * Método para crear el menú de opciones del fragmento.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
    }

    /**
     * Método llamado cuando se selecciona un elemento del menú de opciones.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btn_Add -> {
                val i = Intent(requireContext(), SubirFamiliaActivity::class.java)
                subirFamiliaLauncher.launch(i)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * Lanzador de actividad para el resultado de la subida de una nueva familia.
     */
    private val subirFamiliaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val data: Intent? = result.data
            val idFamiliaNueva = data?.getStringExtra("idFamilia")
            if (!idFamiliaNueva.isNullOrEmpty()) {
                Firebase().obtenerFamiliaPorId(idFamiliaNueva) { nuevaFamilia ->
                    if (nuevaFamilia != null && !familias.contains(nuevaFamilia)) {
                        familias.add(nuevaFamilia)
                        familiasFiltradas.add(nuevaFamilia)
                        familiasAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    /**
     * Callback para las acciones del modo de acción contextual.
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
                BeautifulDialog.build(requireContext() as Activity)
                    .title(getString(R.string.borrar_familia), titleColor = R.color.black)
                    .description(getString(R.string.perder_informacion_familia))
                    .type(type = BeautifulDialog.TYPE.ALERT)
                    .position(BeautifulDialog.POSITIONS.CENTER)
                    .onPositive(text = getString(R.string.aceptar), shouldIDismissOnClick = true) {
                        val familia: Familia = familiasAdapter.getFamilia(posicionPulsada)
                        val idFamilia = familia.id

                        Firebase().borrarFamiliaYProductos(idFamilia) {
                            Utils.Toast(requireContext(), getString(R.string.familia_eliminada))
                            familias.remove(familia)
                            familiasFiltradas.remove(familia)
                            familiasAdapter.notifyDataSetChanged()
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
