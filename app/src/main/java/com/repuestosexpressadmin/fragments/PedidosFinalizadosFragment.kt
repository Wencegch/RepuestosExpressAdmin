package com.repuestosexpressadmin.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.repuestosexpressadmin.R
import com.repuestosexpressadmin.adapters.RecyclerAdapterPedidos
import com.repuestosexpressadmin.controllers.DetallePedidoActivity
import com.repuestosexpressadmin.models.Pedido
import com.repuestosexpressadmin.utils.Firebase

class PedidosFinalizadosFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var pedidosAdapter: RecyclerAdapterPedidos
    private lateinit var pedidos: ArrayList<Pedido>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pedidos_finalizados, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pedidos = ArrayList()
        recyclerView = view.findViewById(R.id.recyclerViewPedidosFinalizados)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        pedidosAdapter = RecyclerAdapterPedidos(requireContext(), pedidos)
        recyclerView.adapter = pedidosAdapter

        Firebase().obtenerPedidosFinalizados(){ listaPedidos ->
            pedidos.clear()
            pedidos.addAll(listaPedidos)
            pedidosAdapter.notifyDataSetChanged()
        }

        pedidosAdapter.setOnItemClickListener(object : RecyclerAdapterPedidos.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val pedidoSeleccionado = pedidos[position]
                val intent = Intent(requireContext(), DetallePedidoActivity::class.java).apply {
                    putExtra("pedido", pedidoSeleccionado)
                }
                startActivity(intent)
            }
        })
    }
}