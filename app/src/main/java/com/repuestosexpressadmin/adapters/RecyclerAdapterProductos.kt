package com.repuestosexpressadmin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.repuestosexpressadmin.R
import com.repuestosexpressadmin.models.Producto

/**
 * Adaptador para mostrar los productos en un RecyclerView.
 * @param listProductos Lista de productos a mostrar.
 */
class RecyclerAdapterProductos(private var listProductos: ArrayList<Producto>) : RecyclerView.Adapter<RecyclerAdapterProductos.ViewHolder>() {

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)
        return ViewHolder(view)
    }

    /**
     * Vincula los datos de un producto a una vista.
     * @param holder El ViewHolder que contiene la vista.
     * @param position La posición del ítem en la lista.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto: Producto = listProductos[position]

        holder.nombreProducto.text = producto.nombre
        holder.precioProducto.text = holder.itemView.context.getString(R.string.precio_formato, producto.precio)

        if (producto.selected) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        }

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
            .into(holder.imagenProducto)

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position)
        }

        // Agregar OnLongClickListener para borrar el producto
        holder.itemView.setOnLongClickListener {
            onItemLongClickListener?.onItemLongClick(position)
            true
        }
    }

    /**
     * Devuelve el número de ítems en la lista de productos.
     * @return El número de ítems.
     */
    override fun getItemCount(): Int {
        return listProductos.size
    }

    /**
     * ViewHolder para representar un ítem de producto.
     * @param itemView La vista para el ítem.
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreProducto: TextView = itemView.findViewById(R.id.txtNombreProducto)
        val precioProducto: TextView = itemView.findViewById(R.id.txtPrecioProducto)
        val imagenProducto: ImageView = itemView.findViewById(R.id.imageViewProducto)
    }

    /**
     * Obtiene un producto en una posición específica.
     * @param pos La posición del producto.
     * @return El producto en la posición especificada.
     */
    fun getProducto(pos: Int): Producto {
        return listProductos[pos]
    }

    // OnItemClickListener
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

    // OnItemLongClickListener
    /**
     * Interfaz para manejar los clics largos en los elementos del RecyclerView.
     */
    interface OnItemLongClickListener {
        /**
         * Método llamado cuando se hace un clic largo en un elemento del RecyclerView.
         * @param position La posición del elemento en la lista.
         */
        fun onItemLongClick(position: Int)
    }

    /**
     * Establece el listener para manejar los clics largos en los elementos del RecyclerView.
     * @param listener El listener para manejar los clics largos en los elementos.
     */
    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        onItemLongClickListener = listener
    }

    /**
     * Deselecciona todos los productos de la lista.
     */
    fun deseleccionarTodos() {
        for (producto in listProductos) {
            producto.selected = false
        }
        notifyDataSetChanged()
    }

    /**
     * Actualiza la lista de productos y notifica al adaptador para actualizar la vista.
     * @param newProductos La nueva lista de productos.
     */
    fun updateProductos(newProductos: ArrayList<Producto>) {
        this.listProductos = newProductos
        notifyDataSetChanged()
    }
}
