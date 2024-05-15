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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

class FamiliasFragment : Fragment() {

    private lateinit var familiasAdapter: RecyclerAdapterFamilias
    private lateinit var recyclerView: RecyclerView
    private lateinit var familias: ArrayList<Familia>
    private var mActionMode: ActionMode? = null
    private var posicionPulsada: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_familias, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.familias)
            setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.color.green))
        }

        familias = ArrayList()
        recyclerView = view.findViewById(R.id.recyclerViewFamilias)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        familiasAdapter = RecyclerAdapterFamilias(familias)
        recyclerView.adapter = familiasAdapter

        Firebase().obtenerFamilias { listaFamilias ->
            familias.clear()
            familias.addAll(listaFamilias)
            familiasAdapter.notifyDataSetChanged()
        }

        familiasAdapter.setOnItemClickListener(object : RecyclerAdapterFamilias.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // Verificar si hay alguna familia seleccionada
                val haySeleccionadas = familias.any { it.selected }
                if (haySeleccionadas) {
                    mActionMode?.finish()
                }else{
                    val familiaSeleccionada = familias[position]
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
                val familiaSeleccionada = familias[posicionPulsada]

                if (familiaSeleccionada.selected) {
                    familiaSeleccionada.selected = false
                } else {
                    familiasAdapter.deseleccionarTodos()
                    familiaSeleccionada.selected = true
                }

                if (familiaSeleccionada.selected && mActionMode == null) {
                    mActionMode = requireActivity().startActionMode(mActionCallback)
                } else if (!familiaSeleccionada.selected && mActionMode != null) {
                    mActionMode?.finish()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
    }

    private val subirFamiliaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val data: Intent? = result.data
            val idFamiliaNueva = data?.getStringExtra("idFamilia")
            if (!idFamiliaNueva.isNullOrEmpty()) {
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
                val i = Intent(requireContext(), SubirFamiliaActivity::class.java)
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
                            familias.removeAt(posicionPulsada)
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
