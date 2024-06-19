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

/**
 * Actividad para subir un nuevo producto.
 */
class SubirProductoActivity : AppCompatActivity(), Firebase.OnSubirProductoListener {

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

    /**
     * Lanzador para seleccionar una imagen.
     */
    private val seleccionarImagenLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                imagenUri = data?.data!!
                if (imagenUri != null) {
                    imgSubirProducto.setImageURI(imagenUri)
                    Log.i("Imagen Uri", "$imagenUri")
                }
            }
        }

    /**
     * Método llamado cuando la actividad es creada.
     * @param savedInstanceState Estado guardado de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subir_producto)

        txtNombreProducto = findViewById(R.id.txtNombreSubirProducto)
        txtPrecioProducto = findViewById(R.id.txtPrecioSubirProducto)
        btnSubirImagen = findViewById(R.id.btn_SeleccionImagen)
        btnAddProducto = findViewById(R.id.btn_SubirProducto)
        imgSubirProducto = findViewById(R.id.imageViewSubirProducto)

        firebase = Firebase()

        supportActionBar?.apply {
            title = getString(R.string.nuevo_producto)
        }

        btnSubirImagen.setOnClickListener {
            pedirPermisos()
        }

        btnAddProducto.setOnClickListener {
            val idFamilia = intent.getStringExtra("idFamilia")

            if (txtNombreProducto.text.trim().isNotEmpty()) {
                if (txtPrecioProducto.text.isNotEmpty()) {
                    val precio = txtPrecioProducto.text.toString().toDoubleOrNull()

                    if (precio != null && precio > 0)  {
                        val producto = Producto(txtNombreProducto.text.toString(), precio, "", idFamilia!!)
                        firebase.crearProducto(producto, this)
                        btnAddProducto.isEnabled = false
                    } else {
                        Utils.Toast(this, getString(R.string.ingresar_precio_valido))
                    }
                } else {
                    Utils.Toast(this, getString(R.string.ingresar_precio))
                    txtPrecioProducto.error = getString(R.string.requerido)
                }
            } else {
                txtNombreProducto.error = getString(R.string.requerido)
                Utils.Toast(this, getString(R.string.ingresar_nombre))
            }
        }
    }

    /**
     * Método para manejar el resultado de la selección de una imagen de la galería.
     * @param requestCode Código de solicitud.
     * @param resultCode Código de resultado.
     * @param data Intent que contiene la URI de la imagen seleccionada.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CODE_PICTURE -> {
                if (resultCode == RESULT_OK && data != null) {
                    imagenUri = data.data
                    try {
                        val source: ImageDecoder.Source = ImageDecoder.createSource(contentResolver, imagenUri!!)
                        bitmap = ImageDecoder.decodeBitmap(source)
                        imgSubirProducto.setImageBitmap(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    /**
     * Lanzador de solicitud de permiso.
     */
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
            } else {
                Log.i("Permission: ", "Denied")
            }
        }

    /**
     * Método para pedir permisos de lectura de almacenamiento externo.
     */
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

    /**
     * Método para abrir la galería y seleccionar una imagen.
     */
    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        seleccionarImagenLauncher.launch(intent)
    }

    /**
     * Método llamado cuando se sube un producto.
     * @param idProducto ID del producto subido.
     */
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

    /**
     * Método llamado cuando se sube la imagen de un producto.
     * @param idProducto ID del producto cuya imagen fue subida.
     */
    override fun onImageSubida(idProducto: String?) {
        val returnIntent = Intent().apply {
            putExtra("idProducto", idProducto)
        }
        setResult(RESULT_OK, returnIntent)
        finish()
        Utils.Toast(applicationContext, getString(R.string.registro_insertado))
    }
}
