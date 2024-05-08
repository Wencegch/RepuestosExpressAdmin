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

    val id: String
        get() = _id

    val nombre: String
        get() = _nombre

    val info: String
        get() = _info

    val imgUrl: String
        get() = _imgUrl

    val selected: Boolean
        get() = _selected

}