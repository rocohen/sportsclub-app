package com.mediadocena.clubdeportivo.dataclasses

data class MemberDetails(val id: Int,
                         val nombreCompleto: String,
                         val telefono: String,
                         val correo: String,
                         val montoAbono: Double,
                         val fecUltPago: String,
                         val fecVencPago: String)
