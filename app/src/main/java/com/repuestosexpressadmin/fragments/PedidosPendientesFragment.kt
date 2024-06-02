package com.repuestosexpressadmin.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.repuestosexpressadmin.R
import com.repuestosexpressadmin.adapters.RecyclerAdapterPedidos
import com.repuestosexpressadmin.controllers.DetallePedidoActivity
import com.repuestosexpressadmin.models.Pedido
import com.repuestosexpressadmin.utils.Firebase
import com.repuestosexpressadmin.utils.Utils

class PedidosPendientesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var pedidosAdapter: RecyclerAdapterPedidos
    private lateinit var pedidos: ArrayList<Pedido>
    private lateinit var pedidosFiltrados: ArrayList<Pedido>
    private lateinit var detallePedidoLauncher: ActivityResultLauncher<Intent>
    private lateinit var txtFiltroPendiente: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pedidos_pendientes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtFiltroPendiente = view.findViewById(R.id.txtFiltroPendientes)
        recyclerView = view.findViewById(R.id.recyclerViewPedidosPendientes)

        pedidos = ArrayList()
        pedidosFiltrados = ArrayList()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        pedidosAdapter = RecyclerAdapterPedidos(requireContext(), pedidosFiltrados)
        recyclerView.adapter = pedidosAdapter

        txtFiltroPendiente.addTextChangedListener { userFilter ->
            pedidosFiltrados = pedidos.filter { pedido ->
                pedido.id.lowercase().contains(userFilter.toString().lowercase())
            }.toCollection(ArrayList())
            pedidosAdapter.updatePedidos(pedidosFiltrados)
        }

        detallePedidoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    actualizarListaPedidos()
                }
            }

        actualizarListaPedidos()

        pedidosAdapter.setOnItemClickListener(object : RecyclerAdapterPedidos.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val pedidoSeleccionado = pedidosFiltrados[position]
                val intent = Intent(requireContext(), DetallePedidoActivity::class.java).apply {
                    putExtra("pedido", pedidoSeleccionado)
                }
                detallePedidoLauncher.launch(intent)
            }
        })
    }

    private fun actualizarListaPedidos() {
        Firebase().obtenerPedidosPendientes() { listaPedidos ->
            pedidos.clear()
            pedidos.addAll(listaPedidos)
            pedidosFiltrados.clear()
            pedidosFiltrados.addAll(listaPedidos)
            pedidosAdapter.notifyDataSetChanged()
        }
    }
}
