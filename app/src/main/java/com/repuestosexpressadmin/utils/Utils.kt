package com.repuestosexpressadmin.utils

import android.content.Context
import android.widget.Toast

/**
 * Clase utilitaria para proporcionar métodos de ayuda comunes.
 */
class Utils {
    /**
     * Compañero de objeto que contiene métodos estáticos para ayudar con tareas comunes.
     */
    companion object {

        /**
         * Muestra un mensaje de toast corto.
         * @param cont Contexto en el que se mostrará el Toast.
         * @param sms El mensaje que se mostrará en el Toast.
         */
        fun Toast(cont: Context, sms: String) {
            Toast.makeText(cont, sms, Toast.LENGTH_SHORT).show()
        }
    }
}
