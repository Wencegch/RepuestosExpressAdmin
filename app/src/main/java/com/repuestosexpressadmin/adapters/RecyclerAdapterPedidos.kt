package com.repuestosexpressadmin.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iamageo.library.BeautifulDialog
import com.iamageo.library.description
import com.iamageo.library.onNegative
import com.iamageo.library.onPositive
import com.iamageo.library.position
import com.iamageo.library.title
import com.iamageo.library.type
import com.repuestosexpressadmin.utils.Firebase
import com.repuestosexpressadmin.utils.Utils
import com.repuestosexpressadmin.R
import com.repuestosexpressadmin.models.Pedido
import java.text.SimpleDateFormat
import java.util.Locale

class RecyclerAdapterPedidos(private val context: Context, private var listPedidos: ArrayList<Pedido>
) : RecyclerView.Adapter<RecyclerAdapterPedidos.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: RecyclerAdapterProductos.OnItemLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pedido: Pedido = listPedidos[position]

        // Formatear la fecha y la hora desde el objeto Date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val fechaFormatted = dateFormat.format(pedido.fecha)
        val horaFormatted = timeFormat.format(pedido.fecha)

        holder.idPedido.text = "ID: ${pedido.id}"
        holder.estadoPedido.text = pedido.estado
        holder.pedidoFecha.text = fechaFormatted
        holder.pedidoHora.text = horaFormatted

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position)
        }

        holder.itemView.setOnLongClickListener {
            val popup = PopupMenu(holder.itemView.context, holder.itemView)
            popup.inflate(R.menu.popup_menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.btn_Delete -> {
                        BeautifulDialog.build(context as Activity)
                            .title(context.getString(R.string.cancelar_pedido), titleColor = R.color.black)
                            .description(context.getString(R.string.informacion_eliminar_pedido), color = R.color.black)
                            .type(type = BeautifulDialog.TYPE.ALERT)
                            .position(BeautifulDialog.POSITIONS.CENTER)
                            .onPositive(text = context.getString(android.R.string.ok), shouldIDismissOnClick = true) {
                                val pedido = listPedidos[position]
                                Firebase().borrarPedidoPorId(pedido.id){ success ->
                                    if (success){
                                        listPedidos.removeAt(position)
                                        notifyItemRemoved(position)
                                        notifyItemRangeChanged(position, listPedidos.size)
                                    }else{
                                        Utils.Toast(context, context.getString(R.string.error_eliminar_pedido))
                                    }
                                }
                            }
                            .onNegative(text = context.getString(android.R.string.cancel)) {}
                        true
                    }
                    else -> false
                }
            }
            popup.show()
            true
        }
    }

    override fun getItemCount(): Int {
        return listPedidos.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pedidoFecha: TextView = itemView.findViewById(R.id.txtPedidoFecha)
        val pedidoHora: TextView = itemView.findViewById(R.id.txtPedidoHora)
        val idPedido: TextView = itemView.findViewById(R.id.txtPedidoId)
        val estadoPedido: TextView = itemView.findViewById(R.id.txtPedidoEstado)
    }

    fun getPedidos(pos: Int): Pedido {
        return this.listPedidos[pos]
    }

    //OnItemClickListener
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

    //OnItemLongClickListener
    /**
     * Interfaz para manejar los clics en los elementos del RecyclerView.
     */
    interface OnItemLongClickListener : RecyclerAdapterProductos.OnItemLongClickListener {
        /**
         * Método llamado cuando se hace clic en un elemento del RecyclerView.
         * @param position La posición del elemento en la lista.
         */
        override fun onItemLongClick(position: Int)
    }

    /**
     * Establece el listener para manejar los clics en los elementos del RecyclerView.
     * @param listener El listener para manejar los clics en los elementos.
     */
    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        onItemLongClickListener = listener
    }

}