package com.repuestosexpressadmin.utils

import android.content.Context
import android.widget.Toast

class Utils {

    companion object {

        fun Toast(cont: Context, sms: String) {
            Toast.makeText(cont, sms, Toast.LENGTH_SHORT).show()
        }
    }
}
