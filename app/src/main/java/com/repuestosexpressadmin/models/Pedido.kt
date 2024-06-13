package com.repuestosexpressadmin.models

import java.io.Serializable
import java.util.Date

/**
 * Clase que representa un pedido en la aplicación.
 * @property id El ID del pedido.
 * @property usuario El usuario asociado al pedido.
 * @property estado El estado del pedido.
 * @property fecha La fecha en que se realizó el pedido.
 * @property direccion La dirección de envío del pedido.
 * @property metodoPago El método de pago utilizado para realizar el pedido.
 */
class Pedido: Serializable {
    private var _id: String = ""
    private var _usuario: String = ""
    private var _estado: String = ""
    private var _fecha: Date
    private var _direccion: String = ""
    private var _metodoPago: String = ""

    /**
     * Constructor primario de la clase.
     * @param id El ID del pedido.
     * @param usuario El usuario asociado al pedido.
     * @param estado El estado del pedido.
     * @param fecha La fecha en que se realizó el pedido.
     * @param direccion La dirección de envío del pedido.
     * @param metodoPago El método de pago del pedido.
     */
    constructor(id: String, usuario: String, estado: String, fecha: Date,direccion: String, metodoPago: String) {
        _id = id
        _usuario = usuario
        _estado = estado
        _fecha = fecha
        _direccion = direccion
        _metodoPago = metodoPago
    }

    /**
     * Constructor secundario de la clase.
     * @param usuario El usuario asociado al pedido.
     * @param estado El estado del pedido.
     * @param fecha La fecha en que se realizó el pedido.
     * @param direccion La dirección de envío del pedido.
     * @param metodoPago El método de pago del pedido.
     */
    constructor(usuario: String, estado: String, fecha: Date, direccion: String, metodoPago: String) {
        _usuario = usuario
        _estado = estado
        _fecha = fecha
        _direccion = direccion
        _metodoPago = metodoPago
    }

    /**
     * Getter y Setter para el ID del pedido.
     */
    var id: String
        get() = _id
        set(value) {
            _id = value
        }

    /**
     * Getter y Setter para el usuario asociado al pedido.
     */
    var usuario: String
        get() = _usuario
        set(value) {
            _usuario = value
        }

    /**
     * Getter y Setter para el estado del pedido.
     */
    var estado: String
        get() = _estado
        set(value) {
            _estado = value
        }

    /**
     * Getter y Setter para la fecha en que se realizó el pedido.
     */
    var fecha: Date
        get() = _fecha
        set(value) {
            _fecha = value
        }

    /**
     * Getter y Setter para la dirección del pedido.
     */
    var direccion: String
        get() = _direccion
        set(value) {
            _direccion = value
        }

    /**
     * Getter y Setter para el método de pago del pedido.
     */
    var metodoPago: String
        get() = _metodoPago
        set(value) {
            _metodoPago = value
        }
}
