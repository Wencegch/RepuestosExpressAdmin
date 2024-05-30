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

class RecyclerAdapterDetallePedidos(private var lineasPedidos: ArrayList<LineasPedido>) : RecyclerView.Adapter<RecyclerAdapterDetallePedidos.ViewHolder>() {

    private lateinit var progressDrawable: CircularProgressDrawable
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detalle_pedido, parent, false)
        return ViewHolder(view)
    }

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

    override fun getItemCount(): Int {
        return lineasPedidos.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreDetallePedido: TextView = itemView.findViewById(R.id.txtNombreProductoHome)
        val precioDetallePedido: TextView = itemView.findViewById(R.id.txtPrecioProductoHome)
        val cantidadDetallePedido: TextView = itemView.findViewById(R.id.txtCantidadDetallePedido)
        val imagenDetallePedido: ImageView = itemView.findViewById(R.id.imageViewProductoHome)
    }

    fun getLineasPedido(pos: Int): LineasPedido {
        return lineasPedidos[pos]
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int)
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        onItemLongClickListener = listener
    }
}
