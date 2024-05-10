package com.repuestosexpressadmin.models

class Familia {
    private var _id: String = ""
    private var _nombre: String = ""
    private var _info: String = ""
    private var _imgUrl: String = ""
    private var _selected: Boolean = false

    constructor(nombre: String, info: String, imgUrl: String) {
        _nombre = nombre
        _info = info
        _imgUrl = imgUrl
    }

    constructor(id: String, nombre: String, info: String, imgUrl: String) {
        _id = id
        _nombre = nombre
        _info = info
        _imgUrl = imgUrl
    }

    var id: String
        get() = _id
        set(value) {
            _id = value
        }

    var nombre: String
        get() = _nombre
        set(value) {
            _nombre = value
        }

    var info: String
        get() = _info
        set(value) {
            _info = value
        }

    var imgUrl: String
        get() = _imgUrl
        set(value) {
            _imgUrl = value
        }

    var selected: Boolean
        get() = _selected
        set(value) {
            _selected = value
        }

}
