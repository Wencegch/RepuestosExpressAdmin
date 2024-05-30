package com.repuestosexpressadmin.models

import java.io.Serializable
import java.util.Date

class Pedido: Serializable {
    private var _id: String = ""
    private var _usuario: String = ""
    private var _estado: String = ""
    private var _fecha: Date

    constructor(id: String, usuario: String, estado: String, fecha: Date) {
        _id = id
        _usuario = usuario
        _estado = estado
        _fecha = fecha
    }

    constructor(usuario: String, estado: String, fecha: Date) {
        _usuario = usuario
        _estado = estado
        _fecha = fecha
    }

    var id: String
        get() = _id
        set(value) {
            _id = value
        }

    var usuario: String
        get() = _usuario
        set(value) {
            _usuario = value
        }

    var estado: String
        get() = _estado
        set(value) {
            _estado = value
        }

    var fecha: Date
        get() = _fecha
        set(value) {
            _fecha = value
        }

}