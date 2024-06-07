package com.repuestosexpressadmin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.repuestosexpressadmin.R
import com.repuestosexpressadmin.models.LineasPedido
import com.repuestosexpressadmin.utils.Firebase

/**
 * Adaptador para mostrar los detalles de los pedidos en un RecyclerView.
 * @param lineasPedidos Lista de líneas de pedido a mostrar.
 */
class RecyclerAdapterDetallePedidos(private var lineasPedidos: ArrayList<LineasPedido>) : RecyclerView.Adapter<RecyclerAdapterDetallePedidos.ViewHolder>() {

    private lateinit var progressDrawable: CircularProgressDrawable
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null

    /**
     * Crea una nueva vista para representar un ítem en el RecyclerView.
     * @param parent El ViewGroup al cual se añadirá la nueva vista.
     * @param viewType El tipo de vista del nuevo ítem.
     * @return Un nuevo ViewHolder que contiene la vista para el ítem.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detalle_pedido, parent, false)
        return ViewHolder(view)
    }

    /**
     * Vincula los datos de una línea de pedido a una vista.
     * @param holder El ViewHolder que contiene la vista.
     * @param position La posición del ítem en la lista.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lineasPedido: LineasPedido = lineasPedidos[position]

        Firebase().obtenerProductoPorId(lineasPedido.idProducto) { producto ->
            if (producto != null) {
                holder.nombreDetallePedido.text = producto.nombre
                val precio = producto.precio * lineasPedido.cantidad
                holder.precioDetallePedido.text = holder.itemView.context.getString(R.string.precio_formato2, precio)

                // Configuración del CircularProgressDrawable
                progressDrawable = CircularProgressDrawable(holder.itemView.context)
                progressDrawable.setStrokeWidth(10f)
                progressDrawable.setStyle(CircularProgressDrawable.LARGE)
                progressDrawable.setCenterRadius(30f)
                progressDrawable.start()

                Glide.with(holder.itemView.context)
                    .load(producto.imgUrl)
                    .placeholder(progressDrawable)
                    .error(R.drawable.imagennoencontrada)
                    .into(holder.imagenDetallePedido)
            }
        }

        holder.cantidadDetallePedido.text = holder.itemView.context.getString(R.string.cantidad_formato, lineasPedido.cantidad)

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClickListener?.onItemLongClick(position)
            true
        }
    }

    /**
     * Devuelve el número de ítems en la lista de líneas de pedido.
     * @return El número de ítems.
     */
    override fun getItemCount(): Int {
        return lineasPedidos.size
    }

    /**
     * ViewHolder para representar un ítem de línea de pedido.
     * @param itemView La vista para el ítem.
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreDetallePedido: TextView = itemView.findViewById(R.id.txtNombreProductoHome)
        val precioDetallePedido: TextView = itemView.findViewById(R.id.txtPrecioProductoHome)
        val cantidadDetallePedido: TextView = itemView.findViewById(R.id.txtCantidadDetallePedido)
        val imagenDetallePedido: ImageView = itemView.findViewById(R.id.imageViewProductoHome)
    }

    /**
     * Obtiene una línea de pedido en una posición específica.
     * @param pos La posición de la línea de pedido.
     * @return La línea de pedido en la posición especificada.
     */
    fun getLineasPedido(pos: Int): LineasPedido {
        return lineasPedidos[pos]
    }

    /**
     * Interfaz para manejar clics en los ítems.
     */
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    /**
     * Establece un listener para los clics en los ítems.
     * @param listener El listener a establecer.
     */
    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    /**
     * Interfaz para manejar clics largos en los ítems.
     */
    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int)
    }

    /**
     * Establece un listener para los clics largos en los ítems.
     * @param listener El listener a establecer.
     */
    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        onItemLongClickListener = listener
    }
}
