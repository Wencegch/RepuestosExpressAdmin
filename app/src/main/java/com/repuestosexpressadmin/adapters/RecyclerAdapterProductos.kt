package com.repuestosexpressadmin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.repuestosexpressadmin.models.Producto
import com.repuestosexpressadmin.R

class RecyclerAdapterProductos(private var listProductos:ArrayList<Producto>, private val enableLongClick: Boolean = true): RecyclerView.Adapter<RecyclerAdapterProductos.ViewHolder>() {

    private lateinit var progressDrawable: CircularProgressDrawable
    private lateinit var onItemClickListener: RecyclerAdapterFamilias.OnItemClickListener
    private lateinit var onItemLongClickListener: OnItemLongClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto: Producto = listProductos[position]

        holder.nombreProducto.text = producto.nombre
        holder.precioProducto.text = producto.precio.toString() + "€"

        //Configuración del CircularProgressDrawable
        progressDrawable = CircularProgressDrawable(holder.itemView.context)
        progressDrawable.setStrokeWidth(10f)
        progressDrawable.setStyle(CircularProgressDrawable.LARGE)
        progressDrawable.setCenterRadius(30f)
        progressDrawable.start()

        Glide.with(holder.itemView.context)
            .load(producto.imgUrl)
            .placeholder(progressDrawable)
            .error(R.drawable.imagennoencontrada)
            .into(holder.imagenProducto)

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(position)
        }

        // Agregar OnLongClickListener para borrar el producto
        if (enableLongClick) {
            holder.itemView.setOnLongClickListener {
                onItemLongClickListener.onItemLongClick(position)
                true
            }
        }
    }

    override fun getItemCount(): Int {
        if (listProductos == null){
            return 0
        }
        return listProductos.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreProducto: TextView = itemView.findViewById(R.id.txtNombreProducto)
        val precioProducto : TextView = itemView.findViewById(R.id.txtPrecioProducto)
        val imagenProducto: ImageView = itemView.findViewById(R.id.imageViewProducto)

    }

    fun getProducto(pos: Int): Producto {
        return this.listProductos.get(pos)
    }

    //OnItemClickListener
    /**
     * Interfaz para manejar los clics en los elementos del RecyclerView.
     */
    interface OnItemClickListener : RecyclerAdapterFamilias.OnItemClickListener {
        /**
         * Método llamado cuando se hace clic en un elemento del RecyclerView.
         * @param position La posición del elemento en la lista.
         */
        override fun onItemClick(position: Int)
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
    interface OnItemLongClickListener {
        /**
         * Método llamado cuando se hace clic en un elemento del RecyclerView.
         * @param position La posición del elemento en la lista.
         */
        fun onItemLongClick(position: Int)
    }

    /**
     * Establece el listener para manejar los clics en los elementos del RecyclerView.
     * @param listener El listener para manejar los clics en los elementos.
     */
    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        onItemLongClickListener = listener
    }

}