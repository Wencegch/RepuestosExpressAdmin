package com.repuestosexpressadmin.models

class Producto() {
    var id: String = ""
    var nombre: String = ""
    var precio: Double = 0.0
    var imgUrl: String = ""
    var idFamilia: String = ""

    // Constructor sin el ID
    constructor(nombre: String, precio: Double, imgUrl: String, idFamilia: String) : this() {
        this.nombre = nombre
        this.precio = precio
        this.imgUrl = imgUrl
        this.idFamilia = idFamilia
    }

    // Constructor con el ID
    constructor(id: String, nombre: String, precio: Double, imgUrl: String, idFamilia: String) : this() {
        this.id = id
        this.nombre = nombre
        this.precio = precio
        this.imgUrl = imgUrl
        this.idFamilia = idFamilia
    }
}
