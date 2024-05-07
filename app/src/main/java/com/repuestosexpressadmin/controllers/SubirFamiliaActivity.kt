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

class SubirFamiliaActivity : AppCompatActivity(), Firebase.OnSubirFamiliaListener {

    private lateinit var txtNombreFamilia: EditText
    private lateinit var txtInfoFamilia: EditText
    private lateinit var btnSubirImagenFamilia: Button
    private lateinit var btnAddFamilia: Button
    private lateinit var imgSubirFamilia: ImageView
    private lateinit var firebase: Firebase
    private var imagenUri: Uri? = null
    private var bitmap: Bitmap? = null

    private val seleccionarImagenLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                imagenUri = data?.data!!
                // Cargar la imagen en el ImageView si la URI de la imagen no es nula
                if (imagenUri != null) {
                    imgSubirFamilia.setImageURI(imagenUri)
                    Log.i("Imagen Uri", "$imagenUri")
                }
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subir_familia)

        txtNombreFamilia = findViewById(R.id.txtNombreSubirFamilia)
        txtInfoFamilia = findViewById(R.id.txtInfoSubirFamilia)
        btnSubirImagenFamilia = findViewById(R.id.btn_SeleccionImagenFamilia)
        btnAddFamilia = findViewById(R.id.btn_SubirFamilia)
        imgSubirFamilia = findViewById(R.id.imageViewSubirFamilia)

        firebase = Firebase()

        btnSubirImagenFamilia.setOnClickListener {
            pedirPermisos()
        }

        btnAddFamilia.setOnClickListener {
            if (txtNombreFamilia.text.isNotEmpty()) {
                if(txtInfoFamilia.text.isNotEmpty()) {
                    val familia = Familia(txtNombreFamilia.text.toString(), txtInfoFamilia.text.toString(), "")
                    firebase.crearFamilia(familia, this)
                }
            }
        }
    }

    // Método para cuando se obtiene una imagen de la galería
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SubirProductoActivity.CODE_PICTURE -> {
                if (resultCode == RESULT_OK && data != null) {
                    // Obtiene la URI de la imagen seleccionada
                    imagenUri = data.data
                    try {
                        // Utiliza ImageDecoder para decodificar la imagen URI a un Bitmap
                        val source: ImageDecoder.Source = ImageDecoder.createSource(contentResolver, imagenUri!!)
                        bitmap = ImageDecoder.decodeBitmap(source)

                        // Muestra el Bitmap en el ImageView
                        imgSubirFamilia.setImageBitmap(bitmap)
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

    override fun onFamiliaSubida(idFamilia: String?) {
        if (idFamilia != null) {
            if (imagenUri != null) {
                firebase.subirImagenFamilia(idFamilia, imagenUri, this)
            }else {
                firebase.subirImagenFamilia(idFamilia, null, this)
            }
        } else {
            Log.e("onProductoSubido", "El id del producto es nulo")

        }
    }

    override fun onImageSubida(idFamilia: String?) {
        val returnIntent = Intent()
        returnIntent.putExtra("id", idFamilia)
        setResult(RESULT_OK, returnIntent)
        finish()
        Utils.Toast(applicationContext, getString(R.string.registro_insertado))    }
}