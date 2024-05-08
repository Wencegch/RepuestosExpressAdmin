package com.repuestosexpressadmin.controllers

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.repuestosexpressadmin.R
import com.repuestosexpressadmin.models.Producto
import com.repuestosexpressadmin.utils.Firebase
import com.repuestosexpressadmin.utils.Utils
import java.io.IOException

class SubirProductoActivity : AppCompatActivity(), Firebase.OnSubirProductoListener{

    private lateinit var txtNombreProducto: EditText
    private lateinit var txtPrecioProducto: EditText
    private lateinit var btnSubirImagen: Button
    private lateinit var btnAddProducto: Button
    private lateinit var imgSubirProducto: ImageView
    private lateinit var firebase: Firebase
    private var imagenUri: Uri? = null
    private var bitmap: Bitmap? = null

    companion object {
        const val CODE_PICTURE = 1
    }

    private val seleccionarImagenLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                imagenUri = data?.data!!
                // Cargar la imagen en el ImageView si la URI de la imagen no es nula
                if (imagenUri != null) {
                    imgSubirProducto.setImageURI(imagenUri)
                    Log.i("Imagen Uri", "$imagenUri")
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subir_producto)

        txtNombreProducto = findViewById(R.id.txtNombreSubirProducto)
        txtPrecioProducto = findViewById(R.id.txtPrecioSubirProducto)
        btnSubirImagen = findViewById(R.id.btn_SeleccionImagen)
        btnAddProducto = findViewById(R.id.btn_SubirProducto)
        imgSubirProducto = findViewById(R.id.imageViewSubirProducto)

        firebase = Firebase()

        btnSubirImagen.setOnClickListener {
            pedirPermisos()
        }

        btnAddProducto.setOnClickListener {
            val idFamilia = intent.getStringExtra("idFamilia")
            Log.i("idFamilia", "$idFamilia")

            // Verificar que el campo de nombre del producto no esté vacío
            if (txtNombreProducto.text.isNotEmpty()) {
                // Verificar que el campo de precio del producto no esté vacío
                if (txtPrecioProducto.text.isNotEmpty()) {
                    // Convertir el texto del campo de precio a un valor numérico
                    val precio = txtPrecioProducto.text.toString().toDoubleOrNull()

                    // Verificar que se pudo convertir el precio correctamente y que no es null
                    if (precio != null) {
                        // Llamar al método crearProducto de Firebase y pasar el nuevo producto
                        val producto = Producto(txtNombreProducto.text.toString(), precio, "", idFamilia!!)
                        firebase.crearProducto(producto, this)
                    } else {
                        // Notificar al usuario que el precio ingresado no es válido
                        Utils.Toast(this, getString(R.string.ingresar_precio_valido))
                    }
                } else {
                    // Notificar al usuario que el campo de precio es obligatorio
                    Utils.Toast(this, getString(R.string.ingresar_precio))
                    txtPrecioProducto.error = getString(R.string.requerido)
                }
            } else {
                txtNombreProducto.error = getString(R.string.requerido)
                // Notificar al usuario que el campo de nombre es obligatorio
                Utils.Toast(this, getString(R.string.ingresar_nombre))
            }

        }

    }

    // Método para cuando se obtiene una imagen de la galería
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CODE_PICTURE -> {
                if (resultCode == RESULT_OK && data != null) {
                    // Obtiene la URI de la imagen seleccionada
                    imagenUri = data.data
                    try {
                        // Utiliza ImageDecoder para decodificar la imagen URI a un Bitmap
                        val source: ImageDecoder.Source = ImageDecoder.createSource(contentResolver, imagenUri!!)
                        bitmap = ImageDecoder.decodeBitmap(source)

                        // Muestra el Bitmap en el ImageView
                        imgSubirProducto.setImageBitmap(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
            } else {
                Log.i("Permission: ", "Denied")
            }
        }

    private fun pedirPermisos() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                abrirGaleria()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        seleccionarImagenLauncher.launch(intent)
    }

    override fun onProductoSubido(idProducto: String?) {
        if (idProducto != null) {
            if (imagenUri != null) {
                firebase.subirImagenProducto(idProducto, imagenUri, this)
            } else {
                firebase.subirImagenProducto(idProducto, null, this)
            }
        } else {
            Log.e("onProductoSubido", "El id del producto es nulo")
        }
    }


    override fun onImageSubida(idProducto: String?) {
        val returnIntent = Intent().apply {
            putExtra("idProducto", idProducto)
        }
        setResult(RESULT_OK, returnIntent)
        finish()
        Utils.Toast(applicationContext, getString(R.string.registro_insertado))
    }
}
