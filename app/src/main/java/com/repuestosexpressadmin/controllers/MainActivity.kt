package com.repuestosexpressadmin.controllers

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.repuestosexpressadmin.R
import com.repuestosexpressadmin.databinding.ActivityMainBinding
import com.repuestosexpressadmin.fragments.FamiliasFragment
import com.repuestosexpressadmin.fragments.PedidosFragment
import com.repuestosexpressadmin.fragments.SugerenciasFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(SugerenciasFragment())

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_sugerencias -> replaceFragment(SugerenciasFragment())
                R.id.navigation_pedidos -> replaceFragment(PedidosFragment())
                R.id.navigation_productos -> replaceFragment(FamiliasFragment())
                else -> {}
            }
            true
        }
    }

    /**
     * Método llamado cuando se presiona el botón de retroceso.
     */
    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)

        // Si el fragmento actual es BookingsFragment o ProfileFragment, cambia al fragmento HomeFragment
        if (currentFragment is PedidosFragment || currentFragment is FamiliasFragment) {
            replaceFragment(SugerenciasFragment())
            // Cada vez que se presiona el botón de retroceso, se cambia el elemento seleccionado en el BottomNavigationView al elemento de inicio
            binding.bottomNavigation.selectedItemId = R.id.navigation_sugerencias
        } else { onBackPressedDispatcher.onBackPressed() }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}