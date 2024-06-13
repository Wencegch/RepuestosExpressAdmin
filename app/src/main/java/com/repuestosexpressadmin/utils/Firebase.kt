package com.repuestosexpressadmin.utils

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.repuestosexpressadmin.models.Familia
import com.repuestosexpressadmin.models.LineasPedido
import com.repuestosexpressadmin.models.Pedido
import com.repuestosexpressadmin.models.Producto
import java.util.Calendar

/**
 * Clase que gestiona las operaciones con Firebase Firestore y Firebase Storage.
 */
class Firebase {
    private var referenceFamilias = FirebaseFirestore.getInstance().collection("Familias")
    private var referenceProductos = FirebaseFirestore.getInstance().collection("Productos")
    private var referencePedidos = FirebaseFirestore.getInstance().collection("Pedidos")
    private var referenceUsuarios = FirebaseFirestore.getInstance().collection("Usuarios")

    private var storage = FirebaseStorage.getInstance()

    // Interfaz para escuchar eventos de subida de producto
    interface OnSubirProductoListener {
        fun onProductoSubido(idProducto: String?)
        fun onImageSubida(idProducto: String?)
    }

    // Interfaz para escuchar eventos de subida de familia
    interface OnSubirFamiliaListener {
        fun onFamiliaSubida(idFamilia: String?)
        fun onImageSubida(idFamilia: String?)
    }

    //FamiliasActivity

    /**
     * Obtiene todas las familias disponibles.
     * @param onComplete La acción a realizar cuando se obtienen las familias.
     */
    fun obtenerFamilias(onComplete: (List<Familia>) -> Unit) {
        val listaFamilias = mutableListOf<Familia>()
        referenceFamilias.whereEqualTo("eliminado", false)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val id = document.id
                    val nombre = document.getString("nombre")
                    val info = document.getString("info")
                    val imgUrl = document.getString("imgUrl")
                    if (nombre != null && info != null && imgUrl != null) {
                        val familia = Familia(id, nombre, info, imgUrl)
                        listaFamilias.add(familia)
                    }
                }
                onComplete(listaFamilias)
        }.addOnFailureListener { exception ->
            Log.d("Error", "$exception")
            onComplete(emptyList()) // Devolver una lista vacía en caso de error
        }
    }

    /**
     * Obtiene una familia por su ID.
     * @param idFamilia El ID de la familia que se desea obtener.
     * @param onComplete La acción a realizar cuando se obtiene la familia.
     */
    fun obtenerFamiliaPorId(idFamilia: String, onComplete: (Familia?) -> Unit) {
        referenceFamilias.document(idFamilia).get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val id = document.id
                val nombre = document.getString("nombre")
                val info = document.getString("info")
                val imgUrl = document.getString("imgUrl")
                if (nombre != null && info != null && imgUrl != null) {
                    val familia = Familia(id, nombre, info, imgUrl)
                    onComplete(familia)
                } else {
                    onComplete(null) // Devolver null si falta algún campo
                }
            } else {
                onComplete(null) // Devolver null si no se encuentra ningún documento
            }
        }.addOnFailureListener { exception ->
            Log.d("Error", "$exception")
            onComplete(null) // Devolver null en caso de error
        }
    }

    /**
     * Crea una nueva familia en Firestore.
     * @param familia La familia que se va a crear.
     * @param listenerSubirFamiliaActivity El listener para manejar eventos relacionados con la subida de la familia.
     */
    fun crearFamilia(familia: Familia, listenerSubirFamiliaActivity: OnSubirFamiliaListener){
        val datosFamilia: MutableMap<String, Any> = HashMap()
        datosFamilia["nombre"] = familia.nombre
        datosFamilia["info"] = familia.info
        datosFamilia["imgUrl"] = ""
        datosFamilia["eliminado"] = false

        referenceFamilias.add(datosFamilia).addOnSuccessListener { documentReference->
            val id: String = documentReference.id
            Log.i("Crear familia", "Exitoso. ID del documento: $id")
            // Aquí puedes realizar cualquier acción adicional con el ID del documento, si es necesario
            listenerSubirFamiliaActivity.onFamiliaSubida(id)
        }.addOnFailureListener { exception ->
                Log.e("Error", "$exception")
        }
    }

    /**
     * Sube la imagen de una familia a Firebase Storage y actualiza su URL en Firestore.
     * @param id El ID de la familia a la que pertenece la imagen.
     * @param imagenUri La URI de la imagen que se va a subir.
     * @param listenerSubirFamiliaActivity El listener para manejar eventos relacionados con la subida de la imagen.
     */
    fun subirImagenFamilia(id: String, imagenUri: Uri?, listenerSubirFamiliaActivity: OnSubirFamiliaListener) {
        if (imagenUri != null) {
            val storageRef = storage.reference.child("familias/$id")
            storageRef.putFile(imagenUri)
                .addOnSuccessListener {
                    // Obtener la URL de descarga de la imagen subida
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->//cuando tenemos la url, modificamos ese elemento
                        val url = downloadUrl.toString()
                        //modificamos el producto, teniendo ahora la url de descarga
                        referenceFamilias.document(id)
                            .update("imgUrl", url)
                            .addOnSuccessListener {
                                listenerSubirFamiliaActivity.onImageSubida(id)
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Error", "$exception")
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Error", "$exception")
                }
        } else {
            referenceFamilias.document(id)
                .update("imgUrl", "no tiene")
                .addOnSuccessListener{
                    listenerSubirFamiliaActivity.onImageSubida(id)
                }
                .addOnFailureListener { exception ->
                    Log.e("Error al subir la imagen", "$exception")
                }
        }
    }

    /**
     * Elimina una familia y todos los productos asociados a esa familia de Firestore y sus imágenes asociadas de Firebase Storage.
     * @param idFamilia El ID de la familia que se va a eliminar.
     */
    fun borrarFamiliaYProductos(idFamilia: String, onComplete: () -> Unit) {
        val actualizacionesFamilia = mapOf(
            "eliminado" to true,
            "imgUrl" to "borrada"
        )

        referenceFamilias.document(idFamilia)
            .update(actualizacionesFamilia)
            .addOnSuccessListener {
                // Éxito al actualizar la familia
                Log.d("Familia actualizada", "Campo 'eliminado' actualizado a true")
                // Obtener la referencia del almacenamiento para la imagen asociada a la familia
                val storageRefFamilia = storage.reference.child("familias/$idFamilia")
                // Eliminar la imagen asociada a la familia desde Firebase Storage
                storageRefFamilia.delete()
                    .addOnSuccessListener {
                        // Éxito al borrar la imagen del almacenamiento
                        Log.d("Imagen de familia borrada", "Sí")
                    }.addOnFailureListener { exception ->
                        // Manejar error al borrar la imagen del almacenamiento
                        Log.e("Error", "Error al eliminar la imagen asociada a la familia: $exception")
                    }

                // Obtener todos los productos asociados a la familia
                referenceProductos
                    .whereEqualTo("idFamilia", idFamilia)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot.documents) {
                            val productId = document.id
                            val imgUrl = document.getString("imgUrl")

                            // Crear el mapa con los campos a actualizar para cada producto
                            val actualizacionesProducto = mapOf(
                                "eliminado" to true,
                                "sugerencias" to false,
                                "imgUrl" to "borrada"
                            )

                            document.reference.update(actualizacionesProducto)
                                .addOnSuccessListener {
                                    // Éxito al actualizar el producto
                                    Log.d("Producto actualizado", "Campo 'eliminado' actualizado a true para el producto con ID: $productId")

                                    // Si la imagen tiene una URL asociada, también la borramos del almacenamiento
                                    if (!imgUrl.isNullOrEmpty()) {
                                        val imagenRef = storage.getReferenceFromUrl(imgUrl)
                                        imagenRef.delete()
                                            .addOnSuccessListener {
                                                // Eliminación exitosa de la imagen del almacenamiento
                                                Log.d("Imagen de producto borrada", "Sí")
                                            }
                                            .addOnFailureListener { exception ->
                                                Log.e("Error", "Error al borrar la imagen del almacenamiento: $exception")
                                            }
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("Error", "Error al actualizar el campo 'eliminado' del producto en Firestore: $exception")
                                }
                        }
                        onComplete() // Llamada a onComplete después de actualizar todos los productos asociados
                    }
                    .addOnFailureListener { exception ->
                        // Manejar error al obtener los productos asociados a la familia
                        Log.e("Error", "Error al obtener los productos asociados a la familia: $exception")
                        onComplete() // Llamada a onComplete en caso de error
                    }
            }
            .addOnFailureListener { exception ->
                // Manejar error al actualizar la familia en Firestore
                Log.e("Error", "Error al actualizar el campo 'eliminado' de la familia en Firestore: $exception")
                onComplete() // Llamada a onComplete en caso de error
            }
    }

    //ProductosActivity
    /**
     * Obtiene todos los productos disponibles.
     * @param onComplete La acción a realizar cuando se obtienen los productos.
     */
    fun obtenerProductos(onComplete: (List<Producto>) -> Unit) {
        val listaProductos = mutableListOf<Producto>()
        referenceProductos
            .whereEqualTo("eliminado", false)
            .get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                val idProducto = document.id
                val nombre = document.getString("nombre")
                val precio = document.getDouble("precio")
                val imgUrl = document.getString("imgUrl")
                val idFamilia = document.getString("idFamilia")

                if (nombre != null && precio != null && imgUrl != null && idFamilia != null) {
                    val producto = Producto(idProducto, nombre, precio.toDouble(), imgUrl, idFamilia)
                    listaProductos.add(producto)
                }
            }
            onComplete(listaProductos)
        }.addOnFailureListener { exception ->
            Log.d("Error", "$exception")
            onComplete(emptyList()) // Devolver una lista vacía en caso de error
        }
    }

    /**
     * Obtiene un producto por su ID.
     * @param idProducto El ID del producto que se desea obtener.
     * @param onComplete La acción a realizar cuando se obtiene el producto.
     */
    fun obtenerProductoPorId(idProducto: String, onComplete: (Producto?) -> Unit) {
        referenceProductos.document(idProducto).get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val idProducto = document.id
                val nombre = document.getString("nombre")
                val precio = document.getDouble("precio")
                val imgUrl = document.getString("imgUrl")
                val idFamilia = document.getString("idFamilia")

                if (nombre != null && precio != null && imgUrl != null && idFamilia != null) {
                    val producto = Producto(idProducto, nombre, precio.toDouble(), imgUrl, idFamilia)
                    onComplete(producto)
                } else {
                    onComplete(null) // Devolver null si falta algún campo
                }
            } else {
                onComplete(null) // Devolver null si no se encuentra ningún documento
            }
        }.addOnFailureListener { exception ->
            Log.d("Error", "$exception")
            onComplete(null) // Devolver null en caso de error
        }
    }

    /**
     * Obtiene todos los productos de una familia específica.
     * @param idFamilia El ID de la familia de la que se quieren obtener los productos.
     * @param onComplete La acción a realizar cuando se obtienen los productos.
     */
    fun obtenerProductosFamilia(idFamilia: String, onComplete: (List<Producto>) -> Unit) {
        val listaProductos = mutableListOf<Producto>()
        referenceProductos.whereEqualTo("idFamilia", idFamilia)
            .whereEqualTo("eliminado", false)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val idFirebase = document.id
                    val nombre = document.getString("nombre")
                    val precio = document.getDouble("precio")
                    val imgUrl = document.getString("imgUrl")
                    val idFamilia = document.getString("idFamilia")

                    if (nombre != null && precio != null && imgUrl != null && idFamilia != null) {
                        val producto = Producto(idFirebase, nombre, precio.toDouble(), imgUrl, idFamilia)
                        listaProductos.add(producto)
                    }
                }
                onComplete(listaProductos)
            }.addOnFailureListener { exception ->
                Log.d("Error", "$exception")
                onComplete(emptyList()) // Devolver una lista vacía en caso de error
            }
    }

    //SubirProductoActivity
    /**
     * Crea un nuevo producto en la base de datos.
     * @param producto El producto que se va a crear.
     * @param listenerSubirProductoActivity El listener para manejar eventos relacionados con la subida del producto.
     */
    fun crearProducto(producto: Producto, listenerSubirProductoActivity: OnSubirProductoListener) {
        // Crea un mapa de datos para el producto
        val datosProducto: MutableMap<String, Any> = HashMap()
        datosProducto["nombre"] = producto.nombre
        datosProducto["precio"] = producto.precio
        datosProducto["imgUrl"] = ""
        datosProducto["idFamilia"] = producto.idFamilia
        datosProducto["eliminado"] = false
        datosProducto["sugerencias"] = false
        datosProducto["veces_pedido"] = 0

        val fechaActual = Calendar.getInstance().time
        datosProducto["fecha"] = fechaActual

        referenceProductos.add(datosProducto)
            .addOnSuccessListener { documentReference ->
                val id: String = documentReference.id
                Log.i("Crear producto", "Exitoso. ID del documento: $id")
                // Aquí puedes realizar cualquier acción adicional con el ID del documento, si es necesario
                listenerSubirProductoActivity.onProductoSubido(id)
            }
            .addOnFailureListener { exception ->
                Log.e("Error", "$exception")
            }
    }

    /**
     * Sube una imagen para un producto a Firebase Storage y actualiza la URL de la imagen en Firestore.
     * @param id El ID del producto al que se subirá la imagen.
     * @param imagenUri La URI de la imagen que se va a subir.
     * @param listenerSubirProductoActivity El listener para manejar eventos relacionados con la subida de la imagen del producto.
     */
    fun subirImagenProducto(id: String, imagenUri: Uri?, listenerSubirProductoActivity: OnSubirProductoListener) {
        if (imagenUri != null) {
            val storageRef = storage.reference.child("productos/$id")
            storageRef.putFile(imagenUri)
                .addOnSuccessListener {
                    // Obtener la URL de descarga de la imagen subida
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl -> //3 -> cuando tenemos la url, modificamos ese elemento
                        val url = downloadUrl.toString()
                        //modificamos el producto, teniendo ahora la url de descarga
                        referenceProductos.document(id)
                            .update("imgUrl", url)
                            .addOnSuccessListener{
                                listenerSubirProductoActivity.onImageSubida(id)
                            }
                            .addOnFailureListener{ exception ->
                                Log.e("Error", "$exception")
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Error", "$exception")
                }
        } else {
            referenceProductos.document(id)
                .update("imgUrl", "no tiene")
                .addOnSuccessListener{
                    listenerSubirProductoActivity.onImageSubida(id)
                }
                .addOnFailureListener{ exception ->
                    Log.e("Error al subir la imagen", "$exception")
                }
        }
    }

    /**
     * Borra un producto de Firestore y su imagen asociada de Firebase Storage.
     * @param idProducto El ID del producto que se va a borrar.
     * @param imgUrl La URL de la imagen asociada al producto, si existe.
     * @param onComplete Callback para manejar la finalización del proceso de borrado.
     */
    fun borrarProducto(idProducto: String, imgUrl: String?, onComplete: () -> Unit) {
        // Crear el mapa con los campos a actualizar
        val actualizaciones = mapOf(
            "eliminado" to true,
            "sugerencias" to false,
            "imgUrl" to "borrada"
        )

        referenceProductos.document(idProducto)
            .update(actualizaciones)
            .addOnSuccessListener {
                // Eliminación exitosa del documento de producto en Firestore
                Log.d("Producto borrado", "Sí")
                // Si la imagen tiene una URL asociada, también la borramos del almacenamiento
                if (!imgUrl.isNullOrEmpty()) {
                    val imagenRef = storage.getReferenceFromUrl(imgUrl)

                    imagenRef.delete()
                        .addOnSuccessListener {
                            // Eliminación exitosa de la imagen del almacenamiento
                            Log.d("Imagen borrada", "Sí")
                            onComplete()
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Error", "Error al borrar la imagen del almacenamiento: $exception")
                            onComplete()
                        }
                } else {
                    // No hay URL de imagen asociada al producto, así que solo llamamos a onComplete
                    onComplete()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Error", "Error al borrar el producto de Firestore: $exception")
                onComplete()
            }
    }


    //Sugerencias
    /**
     * Actualiza las sugerencias de productos en Firestore.
     * @param idsProductosSeleccionados Lista de IDs de productos seleccionados como sugerencias.
     */
    fun actualizarSugerencias(idsProductosSeleccionados: MutableList<String>) {
        // Cambiamos las sugerencias que había a false
        referenceProductos
            .whereEqualTo("sugerencias", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    querySnapshot.documents.forEach { document ->
                        document.reference.update("sugerencias", false)
                            .addOnSuccessListener {
                                // Cuando haya cambiado el último, añadimos los nuevos
                                if (document == querySnapshot.documents.last()) {
                                    insertarRegistrosProductos(idsProductosSeleccionados)
                                }
                            }
                            .addOnFailureListener { exception ->
                                // Manejar error al actualizar sugerencias
                                Log.e("Error", "Error al actualizar sugerencias: $exception")
                            }
                    }
                } else {
                    // Si la tabla estaba vacía, insertamos los nuevos directamente
                    insertarRegistrosProductos(idsProductosSeleccionados)
                    Log.d("Exito", "Exito al insertar sugerencias")
                }
            }
            .addOnFailureListener { exception ->
                // Manejar error al obtener productos con sugerencias
                Log.e("Error", "Error al obtener productos con sugerencias: $exception")
            }
    }

    /**
     * Inserta registros de productos como sugerencias en Firestore.
     * @param idsProductosSeleccionados Lista de IDs de productos seleccionados como sugerencias.
     */
    private fun insertarRegistrosProductos(idsProductosSeleccionados: MutableList<String>) {
        // Añadimos los nuevos
        idsProductosSeleccionados.forEach { productId ->
            referenceProductos
                .document(productId)
                .update("sugerencias", true)
                .addOnSuccessListener {
                    // Manejar éxito al insertar sugerencias
                    Log.d("Exito", "Exito al insertar sugerencias")
                }
                .addOnFailureListener { exception ->
                    // Manejar error al insertar sugerencias
                    Log.e("Error", "Error al insertar sugerencias: $exception")
                }
        }
    }

    /**
     * Obtiene los pedidos pendientes de Firestore.
     * @param onComplete Callback que se llama cuando se completó la obtención de los pedidos.
     */
    fun obtenerPedidosPendientes(onComplete: (List<Pedido>) -> Unit) {
        val listaPedidos = mutableListOf<Pedido>()
        referencePedidos
            .whereEqualTo("estado", "Pendiente")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val idPedido = document.id
                    val usuario = document.getString("usuario")
                    val estado = document.getString("estado")
                    val fecha = document.getDate("fecha")
                    val direccion = document.getString("direccion")
                    val metodoPago = document.getString("metodoPago")

                    if (usuario != null && estado != null && fecha != null && direccion != null && metodoPago != null) {
                        val pedido = Pedido(idPedido, usuario, estado, fecha, direccion, metodoPago)
                        listaPedidos.add(pedido)
                    }
                }
                onComplete(listaPedidos)
            }
            .addOnFailureListener { exception ->
                Log.d("Error", "$exception")
                onComplete(emptyList()) // Devolver una lista vacía en caso de error
            }
    }

    /**
     * Obtiene los pedidos finalizados de Firestore.
     * @param onComplete Callback que se llama cuando se completó la obtención de los pedidos.
     */
    fun obtenerPedidosFinalizados(onComplete: (List<Pedido>) -> Unit) {
        val listaPedidos = mutableListOf<Pedido>()
        referencePedidos
            .whereEqualTo("estado", "Finalizado")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val idPedido = document.id
                    val usuario = document.getString("usuario")
                    val estado = document.getString("estado")
                    val fecha = document.getDate("fecha")
                    val direccion = document.getString("direccion")
                    val metodoPago = document.getString("metodoPago")

                    if (usuario != null && estado != null && fecha != null && direccion != null && metodoPago != null) {
                        val pedido = Pedido(idPedido, usuario, estado, fecha, direccion, metodoPago)
                        listaPedidos.add(pedido)
                    }
                }
                onComplete(listaPedidos)
            }
            .addOnFailureListener { exception ->
                Log.d("Error", "$exception")
                onComplete(emptyList()) // Devolver una lista vacía en caso de error
            }
    }

    /**
     * Borra un pedido y todas sus líneas de pedido asociadas por su ID.
     * @param pedidoId ID del pedido a borrar.
     * @param onComplete Callback que se llama cuando se completa la operación. Devuelve true si la operación fue exitosa, de lo contrario false.
     */
    fun borrarPedidoPorId(pedidoId: String, onComplete: (Boolean) -> Unit) {
        // Primero obtener y eliminar todas las líneas de pedido en la subcolección "lineas"
        referencePedidos.document(pedidoId).collection("lineas")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val batch = FirebaseFirestore.getInstance().batch()
                for (document in querySnapshot.documents) {
                    batch.delete(document.reference)
                }

                // Una vez que las líneas de pedido están eliminadas, eliminar el documento del pedido
                batch.commit()
                    .addOnSuccessListener {
                        referencePedidos.document(pedidoId).delete()
                            .addOnSuccessListener {
                                Log.d("Eliminar Pedido", "Pedido y subcolecciones eliminados con éxito con ID: $pedidoId")
                                onComplete(true)
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Error", "Error al eliminar el pedido con ID: $pedidoId, excepción: $exception")
                                onComplete(false)
                            }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Error", "Error al eliminar subcolecciones del pedido con ID: $pedidoId, excepción: $exception")
                        onComplete(false)
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("Error", "Error al obtener subcolecciones del pedido con ID: $pedidoId, excepción: $exception")
                onComplete(false)
            }
    }

    /**
     * Obtiene todas las líneas de pedido asociadas a un pedido por su ID.
     * @param pedidoId ID del pedido del cual se obtendrán las líneas de pedido.
     * @param onComplete Callback que se llama cuando se completa la operación. Devuelve una lista de objetos LineasPedido.
     */
    fun obtenerLineasDePedido(pedidoId: String, onComplete: (List<LineasPedido>) -> Unit) {
        val lineasPedido = mutableListOf<LineasPedido>()

        referencePedidos.document(pedidoId)
            .collection("lineas")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val idProducto = document.getString("idProducto")
                    val cantidad = document.getLong("cantidad")?.toInt()
                    val lineaPedido = LineasPedido(idProducto!!, cantidad!!)
                    lineasPedido.add(lineaPedido)
                }
                onComplete(lineasPedido)
            }
            .addOnFailureListener { exception ->
                Log.e("Error", "Error al obtener las líneas del pedido: $exception")
                onComplete(emptyList()) // Devolver una lista vacía en caso de error
            }
    }

    /**
     * Actualiza el estado de un pedido y realiza operaciones adicionales según el estado.
     * @param id ID del pedido que se actualizará.
     * @param estado Nuevo estado del pedido.
     * @param onComplete Callback que se llama cuando se completa la operación. Devuelve un booleano que indica si la operación se realizó con éxito.
     */
    fun actualizarEstadoPedido(id: String, estado: String, onComplete: (Boolean) -> Unit) {
        referencePedidos.document(id)
            .update("estado", estado)
            .addOnSuccessListener {
                Log.d("Pedido actualizado", "Sí")

                // Obtener las líneas del pedido
                referencePedidos.document(id).collection("lineas")
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot.documents) {
                            val idProducto = document.getString("idProducto")
                            val cantidad = document.getLong("cantidad")?.toInt()

                            // Actualizar las veces que se pidió un producto
                            referenceProductos.document(idProducto!!)
                                .update("veces_pedido", FieldValue.increment(cantidad!!.toLong()))
                                .addOnSuccessListener {
                                    Log.d("Pedido", "Producto actualizado con éxito para el producto: $idProducto")
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("Error", "Error al actualizar el producto $idProducto: $exception")
                                }
                        }
                        onComplete(true)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Error", "Error al obtener las líneas del pedido: $exception")
                        onComplete(false)
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("Error", "$exception")
                onComplete(false)
            }
    }

    fun iniciarSesion(email: String, onComplete: (Boolean) -> Unit) {
        referenceUsuarios
            .whereEqualTo("email", email)
            .whereEqualTo("administrador", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // El correo electrónico existe y el usuario es administrador
                    onComplete(true)
                } else {
                    // El correo electrónico no existe o el usuario no es administrador
                    onComplete(false)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Error", "Error al verificar el usuario: $exception")
                onComplete(false)
            }
    }
}