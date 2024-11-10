package com.mediadocena.clubdeportivo.dataclasses

data class PaymentDetails(val id: Int,
                          val nombreCompleto: String,
                          val fecPago: String,
                          val fecVencPago: String,
                          val montoAbono: Double,
                          val formaPago: String,
                          val detalle: String,
                          val correo: String)
