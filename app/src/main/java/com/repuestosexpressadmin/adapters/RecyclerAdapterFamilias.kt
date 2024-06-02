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

class RecyclerAdapterFamilias(private var listFamilias: ArrayList<Familia>): RecyclerView.Adapter<RecyclerAdapterFamilias.ViewHolder>() {

    private lateinit var progressDrawable: CircularProgressDrawable
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: RecyclerAdapterProductos.OnItemLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_familia, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val familia: Familia = listFamilias[position]

        holder.nombre.text = familia.nombre
        holder.info.text = familia.info

        if(familia.selected){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
        }else{
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        }

        //Configuración del CircularProgressDrawable
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

        holder.itemView.setOnClickListener{
            onItemClickListener?.onItemClick(position)
        }

        // Agregar OnLongClickListener para borrar la familia
        holder.itemView.setOnLongClickListener {
            onItemLongClickListener?.onItemLongClick(position)
            true
        }
    }

    override fun getItemCount(): Int {
        return listFamilias.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.txtNombreFamilia)
        val info: TextView = itemView.findViewById(R.id.txtInformacion)
        val imagenFamilia: ImageView = itemView.findViewById(R.id.imageViewFamilia)

    }

    fun getFamilia(pos: Int): Familia {
        return this.listFamilias.get(pos)
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

    fun deseleccionarTodos() {
        for (familia in listFamilias) {
            familia.selected = false
        }
        notifyDataSetChanged()
    }

    fun updateFamilias(newFamilias: ArrayList<Familia>) {
        this.listFamilias = newFamilias
        notifyDataSetChanged()
    }
}