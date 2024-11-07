package com.mediadocena.clubdeportivo.entities

import com.mediadocena.clubdeportivo.database.ClubDatabaseHelper

open class Cliente(
    var idCliente: Int? = null,
    var nombreC: String,
    var apellidoC: String,
    var dniC: String,
    var telC: String,
    var correoC: String,
    var tipoC: String,
    var aptoFisico: Boolean,
    var estadoC: Boolean
) {

    // El modificador open permite la sobreescritura del método
    open fun abonar(cuota: Cuota, numCuotas: Int) {
        cuota.aplicarPromocion(numCuotas)
    }

    // Metodo para validar tipo de cliente

}