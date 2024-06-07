package com.repuestosexpressadmin.models

/**
 * Clase que representa una línea de pedido.
 * @property idLineaPedido El ID de la línea de pedido.
 * @property idProducto El ID del producto asociado a la línea de pedido.
 * @property cantidad La cantidad del producto asociado a la línea de pedido.
 */
class LineasPedido {
    private var _idLineaPedido: String = ""
    private var _idProducto: String = ""
    private var _cantidad: Int = 0

    /**
     * Constructor primario de la clase.
     * @param idLineaPedido El ID de la línea de pedido.
     * @param idProducto El ID del producto asociado a la línea de pedido.
     * @param cantidad La cantidad del producto asociado a la línea de pedido.
     */
    constructor(idLineaPedido: String, idProducto: String, cantidad: Int){
        _idLineaPedido = idLineaPedido
        _idProducto = idProducto
        _cantidad = cantidad
    }

    /**
     * Constructor secundario de la clase.
     * @param idProducto El ID del producto asociado a la línea de pedido.
     * @param cantidad La cantidad del producto asociado a la línea de pedido.
     */
    constructor(idProducto: String, cantidad: Int){
        _idProducto = idProducto
        _cantidad = cantidad
    }

    /**
     * Getter y Setter para el ID de la línea de pedido.
     */
    var idLineaPedido: String
        get() = _idLineaPedido
        set(value){
            _idLineaPedido = value
        }

    /**
     * Getter y Setter para el ID del producto asociado a la línea de pedido.
     */
    var idProducto: String
        get() = _idProducto
        set(value){
            _idProducto = value
        }

    /**
     * Getter y Setter para la cantidad del producto asociado a la línea de pedido.
     */
    var cantidad: Int
        get() = _cantidad
        set(value){
            _cantidad = value
        }
}
