package com.repuestosexpressadmin.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.repuestosexpressadmin.fragments.PedidosHistorialFragment
import com.repuestosexpressadmin.fragments.PedidosPendientesFragment

/**
 * Adaptador para manejar la navegación entre fragmentos en un ViewPager2.
 * @property fragmentManager El FragmentManager utilizado para gestionar los fragmentos.
 * @property lifecycle El ciclo de vida asociado al adaptador.
 */
class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    // Lista de fragmentos gestionados por el adaptador
    private val fragmentList = listOf(
        PedidosPendientesFragment(), // Fragmento de pedidos pendientes
        PedidosHistorialFragment()   // Fragmento de historial de pedidos
    )

    /**
     * Obtiene el número total de fragmentos en el ViewPager2.
     * @return El número total de fragmentos.
     */
    override fun getItemCount(): Int = fragmentList.size

    /**
     * Crea un nuevo fragmento en la posición especificada.
     * @param position La posición del fragmento en el ViewPager2.
     * @return El fragmento creado.
     */
    override fun createFragment(position: Int): Fragment {
        // Devuelve el fragmento correspondiente a la posición
        return fragmentList[position]
    }
}
