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
import com.repuestosexpressadmin.models.Familia
import com.repuestosexpressadmin.utils.Firebase
import com.repuestosexpressadmin.utils.Utils
import java.io.IOException

/**
 * Actividad para subir una nueva familia de productos.
 */
class SubirFamiliaActivity : AppCompatActivity(), Firebase.OnSubirFamiliaListener {

    private lateinit var txtNombreFamilia: EditText
    private lateinit var txtInfoFamilia: EditText
    private lateinit var btnSubirImagenFamilia: Button
    private lateinit var btnAddFamilia: Button
    private lateinit var imgSubirFamilia: ImageView
    private lateinit var firebase: Firebase
    private var imagenUri: Uri? = null
    private var bitmap: Bitmap? = null

    /**
     * Método llamado cuando la actividad es creada.
     * @param savedInstanceState Estado guardado de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subir_familia)

        txtNombreFamilia = findViewById(R.id.txtNombreSubirFamilia)
        txtInfoFamilia = findViewById(R.id.txtInfoSubirFamilia)
        btnSubirImagenFamilia = findViewById(R.id.btn_SeleccionImagenFamilia)
        btnAddFamilia = findViewById(R.id.btn_SubirFamilia)
        imgSubirFamilia = findViewById(R.id.imageViewSubirFamilia)

        firebase = Firebase()

        supportActionBar?.apply {
            title = getString(R.string.nueva_familia)
        }

        btnSubirImagenFamilia.setOnClickListener {
            pedirPermisos()
        }

        btnAddFamilia.setOnClickListener {
            if (txtNombreFamilia.text.trim().isNotEmpty()) {
                if(txtInfoFamilia.text.trim().isNotEmpty()) {
                    val familia = Familia(txtNombreFamilia.text.toString(), txtInfoFamilia.text.toString(), "")
                    firebase.crearFamilia(familia, this)
                    btnAddFamilia.isEnabled = false
                } else {
                    txtInfoFamilia.error = getString(R.string.requerido)
                }
            } else {
                txtNombreFamilia.error = getString(R.string.requerido)
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
            SubirProductoActivity.CODE_PICTURE -> {
                if (resultCode == RESULT_OK && data != null) {
                    imagenUri = data.data
                    try {
                        val source: ImageDecoder.Source = ImageDecoder.createSource(contentResolver, imagenUri!!)
                        bitmap = ImageDecoder.decodeBitmap(source)
                        imgSubirFamilia.setImageBitmap(bitmap)
                    } catch (ioException: IOException) {
                        Log.e("SubirFamiliaActivity", "Error al cargar la imagen: ${ioException.message}")
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
                abrirGaleria()
            } else {
                Log.i("Permission: ", "Denied")
            }
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
                    imgSubirFamilia.setImageURI(imagenUri)
                    Log.i("Imagen Uri", "$imagenUri")
                }
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
     * Método llamado cuando se sube una familia.
     * @param idFamilia ID de la familia subida.
     */
    override fun onFamiliaSubida(idFamilia: String?) {
        if (idFamilia != null) {
            if (imagenUri != null) {
                firebase.subirImagenFamilia(idFamilia, imagenUri, this)
            } else {
                firebase.subirImagenFamilia(idFamilia, null, this)
            }
        } else {
            Log.e("onProductoSubido", "El Id del producto es nulo")
        }
    }

    /**
     * Método llamado cuando se sube la imagen de una familia.
     * @param idFamilia ID de la familia cuya imagen fue subida.
     */
    override fun onImageSubida(idFamilia: String?) {
        val returnIntent = Intent().apply {
            putExtra("idFamilia", idFamilia)
        }
        setResult(RESULT_OK, returnIntent)
        finish()
        Utils.Toast(applicationContext, getString(R.string.registro_insertado))
    }
}
