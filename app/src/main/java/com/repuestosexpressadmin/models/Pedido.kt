package com.repuestosexpressadmin.models

import java.io.Serializable
import java.util.Date

/**
 * Clase que representa un pedido en la aplicación.
 * @property id El ID del pedido.
 * @property usuario El usuario asociado al pedido.
 * @property estado El estado del pedido.
 * @property fecha La fecha en que se realizó el pedido.
 */
class Pedido: Serializable {
    private var _id: String = ""
    private var _usuario: String = ""
    private var _estado: String = ""
    private var _fecha: Date

    /**
     * Constructor primario de la clase.
     * @param id El ID del pedido.
     * @param usuario El usuario asociado al pedido.
     * @param estado El estado del pedido.
     * @param fecha La fecha en que se realizó el pedido.
     */
    constructor(id: String, usuario: String, estado: String, fecha: Date) {
        _id = id
        _usuario = usuario
        _estado = estado
        _fecha = fecha
    }

    /**
     * Constructor secundario de la clase.
     * @param usuario El usuario asociado al pedido.
     * @param estado El estado del pedido.
     * @param fecha La fecha en que se realizó el pedido.
     */
    constructor(usuario: String, estado: String, fecha: Date) {
        _usuario = usuario
        _estado = estado
        _fecha = fecha
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
}
