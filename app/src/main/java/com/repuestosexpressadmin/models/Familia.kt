package com.repuestosexpressadmin.models

class Familia() {
    var id: String = ""
    var nombre: String = ""
    var info: String = ""
    var imgUrl: String = ""

    constructor( nombre: String, info: String, imgUrl: String) : this(){
        this.nombre = nombre
        this.info = info
        this.imgUrl = imgUrl
    }

    constructor(id: String, nombre: String, info: String, imgUrl: String) : this(){
        this.id = id
        this.nombre = nombre
        this.info = info
        this.imgUrl = imgUrl
    }
}