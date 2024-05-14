package com.repuestosexpressadmin.controllers

import SugerenciasFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.repuestosexpressadmin.R
import com.repuestosexpressadmin.databinding.ActivityMainBinding
import com.repuestosexpressadmin.fragments.FamiliasFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(SugerenciasFragment())

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_sugerencias -> replaceFragment(SugerenciasFragment())
                //R.id.navigation_pedidos -> replaceFragment(PedidosFragment())
                R.id.navigation_productos -> replaceFragment(FamiliasFragment())
                else -> {}
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}