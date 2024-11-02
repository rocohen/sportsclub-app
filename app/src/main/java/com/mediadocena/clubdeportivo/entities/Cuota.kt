package com.mediadocena.clubdeportivo.entities
import java.util.Date

class Cuota(
    var fecha: Date,
    var monto: Double,
    var idCliente: Int,
    var formaPago: String,
    var fechaVencimiento: Date,
    var tienePromo: Boolean,
    var detalle: String,
    var id: Int? = null
) {

    private fun calcDescuento3Cuotas() {
        // Calculamos el descuento de 5% por 3 cuotas
        val descuento = 0.95
        monto *= descuento
    }

    private fun calcDescuento6Cuotas() {
        // Calculamos el descuento de 10% por 6 cuotas
        val descuento = 0.90
        monto *= descuento
    }

    fun aplicarPromocion(cantCuotas: Int) {
        if (formaPago == "Tarjeta de crédito" && tienePromo) {
            when (cantCuotas) {
                3 -> calcDescuento3Cuotas()
                6 -> calcDescuento6Cuotas()
            }
        }
    }
}