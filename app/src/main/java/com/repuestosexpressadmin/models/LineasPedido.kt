package com.repuestosexpressadmin.models

class LineasPedido {
    private var _idLineaPedido: String = ""
    private var _idProducto: String = ""
    private var _cantidad: Int = 0

    constructor(idLineaPedido: String, idProducto: String, cantidad: Int){
        _idLineaPedido = idLineaPedido
        _idProducto = idProducto
        _cantidad = cantidad
    }

    constructor(idProducto: String, cantidad: Int){
        _idProducto = idProducto
        _cantidad = cantidad
    }

    var idLineaPedido: String
        get() = _idLineaPedido
        set(value){
            _idLineaPedido = value
        }

    var idProducto: String
        get() = _idProducto
        set(value){
            _idProducto = value
        }

    var cantidad: Int
        get() = _cantidad
        set(value){
            _cantidad = value
        }
}