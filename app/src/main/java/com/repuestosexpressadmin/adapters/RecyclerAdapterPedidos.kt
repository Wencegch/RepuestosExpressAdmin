package com.repuestosexpressadmin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.repuestosexpressadmin.R
import com.repuestosexpressadmin.models.Pedido
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adaptador para mostrar los pedidos en un RecyclerView.
 * @param context Contexto de la aplicación.
 * @param listPedidos Lista de pedidos a mostrar.
 */
class RecyclerAdapterPedidos(private val context: Context, private var listPedidos: ArrayList<Pedido>) : RecyclerView.Adapter<RecyclerAdapterPedidos.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: RecyclerAdapterProductos.OnItemLongClickListener? = null

    /**
     * Crea una nueva vista para representar un ítem en el RecyclerView.
     * @param parent El ViewGroup al cual se añadirá la nueva vista.
     * @param viewType El tipo de vista del nuevo ítem.
     * @return Un nuevo ViewHolder que contiene la vista para el ítem.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido, parent, false)
        return ViewHolder(view)
    }

    /**
     * Vincula los datos de un pedido a una vista.
     * @param holder El ViewHolder que contiene la vista.
     * @param position La posición del ítem en la lista.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pedido: Pedido = listPedidos[position]

        // Formatear la fecha y la hora desde el objeto Date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val fechaFormatted = dateFormat.format(pedido.fecha)
        val horaFormatted = timeFormat.format(pedido.fecha)

        holder.idPedido.text = holder.itemView.context.getString(R.string.id, pedido.id)
        holder.estadoPedido.text = pedido.estado
        holder.pedidoFecha.text = fechaFormatted
        holder.pedidoHora.text = horaFormatted

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClickListener?.onItemLongClick(position)
            true
        }
    }

    /**
     * Devuelve el número de ítems en la lista de pedidos.
     * @return El número de ítems.
     */
    override fun getItemCount(): Int {
        return listPedidos.size
    }

    /**
     * ViewHolder para representar un ítem de pedido.
     * @param itemView La vista para el ítem.
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pedidoFecha: TextView = itemView.findViewById(R.id.txtPedidoFecha)
        val pedidoHora: TextView = itemView.findViewById(R.id.txtPedidoHora)
        val idPedido: TextView = itemView.findViewById(R.id.txtPedidoId)
        val estadoPedido: TextView = itemView.findViewById(R.id.txtPedidoEstado)
    }

    /**
     * Obtiene un pedido en una posición específica.
     * @param pos La posición del pedido.
     * @return El pedido en la posición especificada.
     */
    fun getPedidos(pos: Int): Pedido {
        return this.listPedidos[pos]
    }

    /**
     * Interfaz para manejar los clics en los elementos del RecyclerView.
     */
    interface OnItemClickListener {
        /**
         * Método llamado cuando se hace clic en un elemento del RecyclerView.
         * @param position La posición del elemento en la lista.
         */
        fun onItemClick(position: Int)
    }

    /**
     * Establece el listener para manejar los clics en los elementos del RecyclerView.
     * @param listener El listener para manejar los clics en los elementos.
     */
    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    /**
     * Interfaz para manejar los clics largos en los elementos del RecyclerView.
     */
    interface OnItemLongClickListener : RecyclerAdapterProductos.OnItemLongClickListener {
        /**
         * Método llamado cuando se hace un clic largo en un elemento del RecyclerView.
         * @param position La posición del elemento en la lista.
         */
        override fun onItemLongClick(position: Int)
    }

    /**
     * Establece el listener para manejar los clics largos en los elementos del RecyclerView.
     * @param listener El listener para manejar los clics largos en los elementos.
     */
    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        onItemLongClickListener = listener
    }

    /**
     * Actualiza la lista de pedidos y notifica al adaptador para actualizar la vista.
     * @param newPedidos La nueva lista de pedidos.
     */
    fun updatePedidos(newPedidos: ArrayList<Pedido>) {
        this.listPedidos = newPedidos
        notifyDataSetChanged()
    }
}
