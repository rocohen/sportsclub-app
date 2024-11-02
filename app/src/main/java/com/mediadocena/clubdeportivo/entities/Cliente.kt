package com.mediadocena.clubdeportivo.entities

open class Cliente(
    var nombreC: String,
    var apellidoC: String,
    var dniC: String,
    var telC: String,
    var correoC: String,
    var tipoC: String,
    var aptoFisico: Boolean,
    var estadoC: Boolean,
    var idCliente: Int? = null
) {

    // El modificador open permite la sobreescritura del método
    open fun abonar(cuota: Cuota, numCuotas: Int) {
        cuota.aplicarPromocion(numCuotas)
    }
}