package com.repuestosexpressadmin.models

import java.io.Serializable

/**
 * Clase que representa un producto.
 * @property _id El identificador único del producto.
 * @property _nombre El nombre del producto.
 * @property _precio El precio del producto.
 * @property _imgUrl La URL de la imagen del producto.
 * @property _idFamilia El identificador de la familia a la que pertenece el producto.
 * @constructor Crea una nueva instancia de la clase Producto.
 */
class Producto: Serializable {
    private var _id: String = ""
    private var _nombre: String = ""
    private var _precio: Double = 0.0
    private var _imgUrl: String = ""
    private var _idFamilia: String = ""
    private var _selected: Boolean = false

    /**
     * Constructor primario para crear una instancia de Producto sin el identificador único.
     * @param nombre El nombre del producto.
     * @param precio El precio del producto.
     * @param imgUrl La URL de la imagen del producto.
     * @param idFamilia El identificador de la familia a la que pertenece el producto.
     */
    constructor(nombre: String, precio: Double, imgUrl: String, idFamilia: String) {
        _nombre = nombre
        _precio = precio
        _imgUrl = imgUrl
        _idFamilia = idFamilia
    }

    /**
     * Constructor secundario para crear una instancia de Producto con el identificador único.
     * @param id El identificador único del producto.
     * @param nombre El nombre del producto.
     * @param precio El precio del producto.
     * @param imgUrl La URL de la imagen del producto.
     * @param idFamilia El identificador de la familia a la que pertenece el producto.
     */
    constructor(id: String, nombre: String, precio: Double, imgUrl: String, idFamilia: String) {
        _id = id
        _nombre = nombre
        _precio = precio
        _imgUrl = imgUrl
        _idFamilia = idFamilia
    }

    /**
     * Propiedad para acceder y modificar el identificador único del producto.
     */
    var id: String
        get() = _id
        set(value) {
            _id = value
        }

    /**
     * Propiedad para acceder y modificar el nombre del producto.
     */
    var nombre: String
        get() = _nombre
        set(value) {
            _nombre = value
        }

    /**
     * Propiedad para acceder y modificar el precio del producto.
     */
    var precio: Double
        get() = _precio
        set(value) {
            _precio = value
        }

    /**
     * Propiedad para acceder y modificar la URL de la imagen del producto.
     */
    var imgUrl: String
        get() = _imgUrl
        set(value) {
            _imgUrl = value
        }

    /**
     * Propiedad para acceder y modificar el identificador de la familia a la que pertenece el producto.
     */
    var idFamilia: String
        get() = _idFamilia
        set(value) {
            _idFamilia = value
        }

    var selected: Boolean
        get() = _selected
        set(value) {
            _selected = value
        }
}
