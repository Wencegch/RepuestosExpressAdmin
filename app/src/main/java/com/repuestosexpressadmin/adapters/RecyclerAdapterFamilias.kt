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
import com.repuestosexpressadmin.models.Familia

/**
 * Adaptador para mostrar las familias en un RecyclerView.
 * @param listFamilias Lista de familias a mostrar.
 */
class RecyclerAdapterFamilias(private var listFamilias: ArrayList<Familia>): RecyclerView.Adapter<RecyclerAdapterFamilias.ViewHolder>() {

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_familia, parent, false)
        return ViewHolder(view)
    }

    /**
     * Vincula los datos de una familia a una vista.
     * @param holder El ViewHolder que contiene la vista.
     * @param position La posición del ítem en la lista.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val familia: Familia = listFamilias[position]

        holder.nombre.text = familia.nombre
        holder.info.text = familia.info

        if (familia.selected) {
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
            .load(familia.imgUrl)
            .placeholder(progressDrawable)
            .error(R.drawable.imagennoencontrada)
            .into(holder.imagenFamilia)

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClickListener?.onItemLongClick(position)
            true
        }
    }

    /**
     * Devuelve el número de ítems en la lista de familias.
     * @return El número de ítems.
     */
    override fun getItemCount(): Int {
        return listFamilias.size
    }

    /**
     * ViewHolder para representar un ítem de familia.
     * @param itemView La vista para el ítem.
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.txtNombreFamilia)
        val info: TextView = itemView.findViewById(R.id.txtInformacion)
        val imagenFamilia: ImageView = itemView.findViewById(R.id.imageViewFamilia)
    }

    /**
     * Obtiene una familia en una posición específica.
     * @param pos La posición de la familia.
     * @return La familia en la posición especificada.
     */
    fun getFamilia(pos: Int): Familia {
        return this.listFamilias[pos]
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
     * Deselecciona todas las familias en la lista.
     */
    fun deseleccionarTodos() {
        for (familia in listFamilias) {
            familia.selected = false
        }
        notifyDataSetChanged()
    }

    /**
     * Actualiza la lista de familias y notifica al adaptador para actualizar la vista.
     * @param newFamilias La nueva lista de familias.
     */
    fun updateFamilias(newFamilias: ArrayList<Familia>) {
        this.listFamilias = newFamilias
        notifyDataSetChanged()
    }
}
