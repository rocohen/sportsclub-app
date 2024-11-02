package com.mediadocena.clubdeportivo.entities

class NoSocio(
    nombreC: String,
    apellidoC: String,
    dniC: String,
    telC: String,
    correoC: String,
    tipoC: String,
    aptoFisico: Boolean,
    estadoC: Boolean,
    idCliente: Int? = null
) : Cliente(
    nombreC = nombreC,
    apellidoC = apellidoC,
    dniC = dniC,
    telC = telC,
    correoC = correoC,
    tipoC = tipoC,
    aptoFisico = aptoFisico,
    estadoC = estadoC,
    idCliente = idCliente
)