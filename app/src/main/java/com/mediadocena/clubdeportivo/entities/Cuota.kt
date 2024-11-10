package com.mediadocena.clubdeportivo.entities
import java.time.LocalDate

class Cuota(
    var idCliente: Int ,
    var fecha: LocalDate,
    var monto: Double,
    var formaPago: String,
    var fechaVencimiento: LocalDate,
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
        if ((formaPago == "Tarjeta 3 Cuotas (5% OFF, sin interes)" && tienePromo) or (formaPago == "Tarjeta 6 Cuotas (10% OFF, sin interes)" && tienePromo)) {
            when (cantCuotas) {
                3 -> calcDescuento3Cuotas()
                6 -> calcDescuento6Cuotas()
            }
        }
    }
}