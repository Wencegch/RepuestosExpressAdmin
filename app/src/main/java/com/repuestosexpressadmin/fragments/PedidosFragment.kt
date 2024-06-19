package com.repuestosexpressadmin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.repuestosexpressadmin.R
import com.repuestosexpressadmin.adapters.ViewPagerAdapter

/**
 * Fragmento que muestra una lista de pedidos organizada en pestañas.
 * Cada pestaña corresponde a un estado diferente del pedido, como "Pendiente", "En Proceso", "Completado", etc.
 * Los pedidos se muestran en un ViewPager2, permitiendo al usuario deslizarse entre las pestañas.
 */
class PedidosFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var viewAdapter: ViewPagerAdapter

    /**
     * Método llamado para inflar el diseño de este fragmento.
     * @param inflater El objeto LayoutInflater utilizado para inflar el diseño.
     * @param container El contenedor en el que se debe inflar el diseño.
     * @param savedInstanceState Estado previamente guardado de este fragmento.
     * @return La vista inflada.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflar el diseño del fragmento
        return inflater.inflate(R.layout.fragment_pedidos, container, false)
    }

    /**
     * Método llamado cuando la vista del fragmento ha sido creada.
     * Inicializa los componentes de la interfaz de usuario y configura el adaptador del ViewPager2.
     * También maneja los eventos de selección de pestañas en el TabLayout.
     * @param view La vista inflada.
     * @param savedInstanceState Estado previamente guardado de este fragmento.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.pedidos)
        }

        // Inicializar componentes de la interfaz de usuario
        tabLayout = view.findViewById(R.id.tabLayoutPedidos)
        viewPager2 = view.findViewById(R.id.viewPagerPedidos)

        // Configurar el adaptador para el ViewPager2
        viewAdapter = ViewPagerAdapter(parentFragmentManager, viewLifecycleOwner.lifecycle)
        viewPager2.adapter = viewAdapter

        // Escuchar los eventos de selección de pestañas en el TabLayout
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(selectedTab: TabLayout.Tab?) {
                if (selectedTab != null) {
                    // Cambiar la página del ViewPager2 al seleccionar una pestaña
                    viewPager2.currentItem = selectedTab.position
                }
            }

            override fun onTabUnselected(tab : TabLayout.Tab?) {
                // No se realiza ninguna acción cuando se deselecciona una pestaña
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // No se realiza ninguna acción cuando se vuelve a seleccionar una pestaña
            }
        })

        // Actualizar la selección de pestañas al cambiar de página en el ViewPager2
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }
}

