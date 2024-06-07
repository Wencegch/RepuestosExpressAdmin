package com.repuestosexpressadmin.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.repuestosexpressadmin.R
import com.repuestosexpressadmin.adapters.RecyclerAdapterPedidos
import com.repuestosexpressadmin.controllers.DetallePedidoActivity
import com.repuestosexpressadmin.models.Pedido
import com.repuestosexpressadmin.utils.Firebase

/**
 * Fragmento para mostrar una lista de pedidos finalizados.
 * Permite filtrar los pedidos por su ID y ver los detalles de cada pedido.
 */
class PedidosFinalizadosFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var pedidosAdapter: RecyclerAdapterPedidos
    private lateinit var pedidos: ArrayList<Pedido>
    private lateinit var pedidosFiltrados: ArrayList<Pedido>
    private lateinit var txtFiltroFinalizado: EditText

    /**
     * Método llamado para inflar el diseño de este fragmento.
     * @param inflater El objeto LayoutInflater utilizado para inflar el diseño.
     * @param container El contenedor en el que se debe inflar el diseño.
     * @param savedInstanceState Estado previamente guardado de este fragmento.
     * @return La vista inflada.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pedidos_finalizados, container, false)
    }

    /**
     * Método llamado cuando la vista del fragmento ha sido creada.
     * Inicializa los componentes de la interfaz de usuario y configura el adaptador de RecyclerView.
     * También maneja el filtrado de los pedidos y la navegación a los detalles del pedido.
     * @param view La vista inflada.
     * @param savedInstanceState Estado previamente guardado de este fragmento.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtFiltroFinalizado = view.findViewById(R.id.txtFiltroFinalizados)
        recyclerView = view.findViewById(R.id.recyclerViewPedidosFinalizados)

        pedidos = ArrayList()
        pedidosFiltrados = ArrayList()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        pedidosAdapter = RecyclerAdapterPedidos(requireContext(), pedidosFiltrados)
        recyclerView.adapter = pedidosAdapter

        // Escucha cambios en el texto del filtro y filtra los pedidos según el ID.
        txtFiltroFinalizado.addTextChangedListener { userFilter ->
            pedidosFiltrados = pedidos.filter { pedido ->
                pedido.id.lowercase().contains(userFilter.toString().lowercase())
            }.toCollection(ArrayList())
            pedidosAdapter.updatePedidos(pedidosFiltrados)
        }

        // Obtiene los pedidos finalizados desde Firebase y los muestra en el RecyclerView.
        Firebase().obtenerPedidosFinalizados() { listaPedidos ->
            pedidos.clear()
            pedidos.addAll(listaPedidos)
            pedidosFiltrados.clear()
            pedidosFiltrados.addAll(listaPedidos)
            pedidosAdapter.notifyDataSetChanged()
        }

        // Maneja el clic en un pedido para mostrar los detalles del mismo.
        pedidosAdapter.setOnItemClickListener(object : RecyclerAdapterPedidos.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val pedidoSeleccionado = pedidosFiltrados[position]
                val intent = Intent(requireContext(), DetallePedidoActivity::class.java).apply {
                    putExtra("pedido", pedidoSeleccionado)
                }
                startActivity(intent)
            }
        })
    }
}

