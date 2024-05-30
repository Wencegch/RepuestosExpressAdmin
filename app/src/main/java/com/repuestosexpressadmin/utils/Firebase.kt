package com.repuestosexpressadmin.utils

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.repuestosexpressadmin.models.Familia
import com.repuestosexpressadmin.models.LineasPedido
import com.repuestosexpressadmin.models.Pedido
import com.repuestosexpressadmin.models.Producto
import java.util.Calendar

class Firebase {
    private var referenceFamilias = FirebaseFirestore.getInstance().collection("Familias")
    private var referenceProductos = FirebaseFirestore.getInstance().collection("Productos")
    private var referencePedidos = FirebaseFirestore.getInstance().collection("Pedidos")
    private var storage = FirebaseStorage.getInstance()

    interface OnSubirProductoListener {
        fun onProductoSubido(idProducto: String?)
        fun onImageSubida(idProducto: String?)
    }

    interface OnSubirFamiliaListener {
        fun onFamiliaSubida(idFamilia: String?)
        fun onImageSubida(idFamilia: String?)
    }

    //FamiliasActivity
    fun obtenerFamilias(onComplete: (List<Familia>) -> Unit) {
        val listaFamilias = mutableListOf<Familia>()
        referenceFamilias.get().addOnSuccessListener { querySnapshot ->
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

    fun crearFamilia(familia: Familia, listenerSubirFamiliaActivity: OnSubirFamiliaListener){
        val datosFamilia: MutableMap<String, Any> = HashMap()
        datosFamilia["nombre"] = familia.nombre
        datosFamilia["info"] = familia.info
        datosFamilia["imgUrl"] = ""

        referenceFamilias.add(datosFamilia).addOnSuccessListener { documentReference->
            val id: String = documentReference.id
            Log.i("Crear familia", "Exitoso. ID del documento: $id")
            // Aquí puedes realizar cualquier acción adicional con el ID del documento, si es necesario
            listenerSubirFamiliaActivity.onFamiliaSubida(id)
        }.addOnFailureListener { exception ->
                Log.e("Error", "$exception")
        }
    }

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
                            .addOnSuccessListener(OnSuccessListener<Void?> {
                                listenerSubirFamiliaActivity.onImageSubida(id)
                            })
                            .addOnFailureListener(OnFailureListener { exception ->
                                Log.e("Error", "$exception")
                            })
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Error", "$exception")
                }
        } else {
            referenceFamilias.document(id)
                .update("imgUrl", "no tiene")
                .addOnSuccessListener(OnSuccessListener<Void?> {
                    listenerSubirFamiliaActivity.onImageSubida(id)
                })
                .addOnFailureListener(OnFailureListener { exception ->
                    Log.e("Error al subir la imagen", "$exception")
                })
        }
    }

    /**
     * Elimina una familia y todos los productos asociados a esa familia de Firestore y sus imágenes asociadas de Firebase Storage.
     * @param idFamilia El ID de la familia que se va a eliminar.
     */
    fun borrarFamiliaYProductos(idFamilia: String, onComplete: () -> Unit) {
        // Eliminar la familia de la colección "Familias" en Firestore y su imagen asociada desde Firebase Storage
        referenceFamilias.document(idFamilia)
            .delete()
            .addOnSuccessListener {
                // Eliminación exitosa de la familia en Firestore
                Log.d("Familia borrada", "Sí")
                // Obtener la referencia del almacenamiento para la imagen asociada a la familia
                val storageRef = storage.reference.child("familias/$idFamilia")
                // Eliminar la imagen asociada a la familia desde Firebase Storage
                storageRef.delete()
                    .addOnFailureListener { exception ->
                        // Manejar error al eliminar la imagen asociada a la familia
                        Log.e("Error al eliminar la imagen asociada a la familia", "$exception")
                    }

                // Eliminar los productos asociados a la familia de la colección "Productos" en Firestore y sus imágenes asociadas desde Firebase Storage
                referenceProductos
                    .whereEqualTo("idFamilia", idFamilia)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot.documents) {
                            // Eliminar cada producto asociado a la familia en Firestore
                            val productId = document.id
                            document.reference.delete()
                                .addOnSuccessListener {
                                    // Eliminación exitosa del producto en Firestore
                                    Log.d("Producto borrado", "Sí")
                                    // Obtener la referencia del almacenamiento para la imagen asociada al producto
                                    val productStorageRef = storage.reference.child("productos/$productId")
                                    // Eliminar la imagen asociada al producto desde Firebase Storage
                                    productStorageRef.delete()
                                        .addOnFailureListener { exception ->
                                            // Manejar error al eliminar la imagen asociada al producto
                                            Log.e("Error", "Error al eliminar la imagen asociada al producto: $exception")
                                        }
                                }
                                .addOnFailureListener { exception ->
                                    // Manejar error al eliminar el producto de Firestore
                                    Log.e("Error", "Error al eliminar el producto de Firestore: $exception")
                                }
                        }
                        onComplete() // Llamada a onComplete después de eliminar todos los productos asociados
                    }
                    .addOnFailureListener { exception ->
                        // Manejar error al obtener los productos asociados a la familia
                        Log.e("Error", "Error al obtener los productos asociados a la familia: $exception")
                        onComplete() // Llamada a onComplete en caso de error
                    }
            }
            .addOnFailureListener { exception ->
                // Manejar error al eliminar la familia de Firestore
                Log.e("Error al eliminar la familia de Firestore", "$exception")
                onComplete() // Llamada a onComplete en caso de error
            }
    }

    //ProductosActivity
    fun obtenerProductos(onComplete: (List<Producto>) -> Unit) {
        val listaProductos = mutableListOf<Producto>()
        referenceProductos.get().addOnSuccessListener { querySnapshot ->
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

    fun obtenerProductosFamilia(idFamilia: String, onComplete: (List<Producto>) -> Unit) {
        val listaProductos = mutableListOf<Producto>()
        referenceProductos.whereEqualTo("idFamilia", idFamilia).get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    Log.d("Id Firebase", document.id)
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
    fun crearProducto(producto: Producto, listenerSubirProductoActivity: OnSubirProductoListener) {
        // Crea un mapa de datos para el producto
        val datosProducto: MutableMap<String, Any> = HashMap()
        datosProducto["nombre"] = producto.nombre
        datosProducto["precio"] = producto.precio
        datosProducto["imgUrl"] = ""
        datosProducto["idFamilia"] = producto.idFamilia
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
                            .addOnSuccessListener(OnSuccessListener<Void?> {
                                listenerSubirProductoActivity.onImageSubida(id)
                            })
                            .addOnFailureListener(OnFailureListener { exception ->
                                Log.e("Error", "$exception")
                            })
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Error", "$exception")
                }
        } else {
            referenceProductos.document(id)
                .update("imgUrl", "no tiene")
                .addOnSuccessListener(OnSuccessListener<Void?> {
                    listenerSubirProductoActivity.onImageSubida(id)
                })
                .addOnFailureListener(OnFailureListener { exception ->
                    Log.e("Error al subir la imagen", "$exception")
                })
        }
    }

    fun borrarProducto(idProducto: String, imgUrl: String?, onComplete: () -> Unit) {
        referenceProductos.document(idProducto)
            .delete()
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
                            .addOnFailureListener { e ->
                                // Manejar error al actualizar sugerencias
                                Log.e("Error", "Error al actualizar sugerencias: $e")
                            }
                    }
                } else {
                    // Si la tabla estaba vacía, insertamos los nuevos directamente
                    insertarRegistrosProductos(idsProductosSeleccionados)
                    Log.d("Exito", "Exito al insertar sugerencias")
                }
            }
            .addOnFailureListener { e ->
                // Manejar error al obtener productos con sugerencias
                Log.e("Error", "Error al obtener productos con sugerencias: $e")
            }
    }

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
                .addOnFailureListener { e ->
                    // Manejar error al insertar sugerencias
                    Log.e("Error", "Error al insertar sugerencias: $e")
                }
        }
    }

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

                    if (usuario != null && estado != null && fecha != null) {
                        val pedido = Pedido(idPedido, usuario, estado, fecha)
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

                    if (usuario != null && estado != null && fecha != null) {
                        val pedido = Pedido(idPedido, usuario, estado, fecha)
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

    fun actualizarEstadoPedido(id: String, estado: String) {
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
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Error", "Error al obtener las líneas del pedido: $exception")
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("Error", "$exception")
            }
    }

}