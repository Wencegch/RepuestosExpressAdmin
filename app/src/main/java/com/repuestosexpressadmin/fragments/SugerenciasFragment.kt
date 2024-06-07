package com.repuestosexpressadmin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.repuestosexpressadmin.R
import com.repuestosexpressadmin.adapters.RecyclerAdapterProductos
import com.repuestosexpressadmin.models.Producto
import com.repuestosexpressadmin.utils.Firebase
import com.repuestosexpressadmin.utils.Utils

/**
 * Fragmento que muestra una lista de productos disponibles para sugerencias.
 * Permite al usuario seleccionar productos y enviar sugerencias.
 */
class SugerenciasFragment : Fragment() {
    private lateinit var productosAdapter: RecyclerAdapterProductos
    private lateinit var recyclerView: RecyclerView
    private lateinit var productos: ArrayList<Producto>
    private var posicionPulsada: Int = 0

    /**
     * Método llamado para inflar el diseño de este fragmento y configurar la barra de acciones.
     * @param inflater El objeto LayoutInflater utilizado para inflar el diseño.
     * @param container El contenedor en el que se debe inflar el diseño.
     * @param savedInstanceState Estado previamente guardado de este fragmento.
     * @return La vista inflada.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sugerencias, container, false)

        (activity as AppCompatActivity?)?.supportActionBar?.apply {
            title = getString(R.string.sugerencias)
            setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.color.green))
        }

        productos = ArrayList()
        recyclerView = view.findViewById(R.id.recyclerViewSugerencias)
        recyclerView.layoutManager = LinearLayoutManager(context)
        productosAdapter = RecyclerAdapterProductos(productos)
        recyclerView.adapter = productosAdapter

        Firebase().obtenerProductos { listaProductos ->
            productos.addAll(listaProductos)
            productosAdapter.notifyDataSetChanged()
        }

        productosAdapter.setOnItemClickListener(object : RecyclerAdapterProductos.OnItemClickListener {
            override fun onItemClick(position: Int) {
                posicionPulsada = position
                val productoSeleccionado = productos[posicionPulsada]
                if (productoSeleccionado.selected) {
                    productoSeleccionado.selected = false
                } else {
                    productoSeleccionado.selected = true
                }
                productosAdapter.notifyItemChanged(posicionPulsada)
            }
        })

        setHasOptionsMenu(true)
        return view
    }

    /**
     * Método llamado cuando se selecciona un elemento del menú de opciones.
     * Envia las sugerencias de productos seleccionados a Firebase.
     * @param item El elemento del menú que se seleccionó.
     * @return true si el evento fue manejado correctamente, false en caso contrario.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            R.id.btn_EnviarSugerencias -> {
                val idsProductosSeleccionados = mutableListOf<String>()
                for (producto in productos) {
                    if (producto.selected) {
                        idsProductosSeleccionados.add(producto.id)
                    }
                }
                Firebase().actualizarSugerencias(idsProductosSeleccionados)
                Utils.Toast(requireContext(), getString(R.string.sugerencias_enviadas))
                productosAdapter.deseleccionarTodos()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Método llamado para crear el menú de opciones en la barra de acciones.
     * @param menu El menú de opciones que se va a crear.
     * @param inflater El objeto MenuInflater utilizado para inflar el menú.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.suggest_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
