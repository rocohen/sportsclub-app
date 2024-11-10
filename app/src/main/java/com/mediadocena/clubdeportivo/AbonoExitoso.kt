package com.mediadocena.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class AbonoExitoso : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_abono_exitoso)

        // Lógica para capturar el ID cliente enviado en el intent
        val idCliente = intent.getIntExtra("ID_CLIENTE", 0)

        // Obtener datos con valores predeterminados en caso de que sean nulos
        val formaPago = intent.getStringExtra("FORMA_PAGO") ?: "Sin especificar"
        val montoPago = intent.getDoubleExtra("MONTO_PAGO", 0.0)
        val fechaPago = intent.getStringExtra("FECHA_PAGO") ?: "Sin especificar"
        val fechaVencimiento = intent.getStringExtra("FECHA_VENCIMIENTO") ?: "Sin especificar"
        val detallePago = intent.getStringExtra("DETALLE_PAGO") ?: "Sin especificar"

        // Depuración: ver los valores que se van a enviar a PaymentReceipt
        Log.d("AbonoExitoso", "FORMA_PAGO: $formaPago, MONTO_PAGO: $montoPago, FECHA_PAGO: $fechaPago, FECHA_VENCIMIENTO: $fechaVencimiento, DETALLE_PAGO: $detallePago")

        // Lógica botón Generar Comprobante
        val btnGenerarComprobante = findViewById<Button>(R.id.btnGenerarComprobante)
        btnGenerarComprobante.setOnClickListener {
            val intent = Intent(this, PaymentReceipt::class.java)
            intent.putExtra("ID_CLIENTE", idCliente) // Enviar ID cliente

            // Enviar datos solo si son válidos
            intent.putExtra("FORMA_PAGO", formaPago)
            intent.putExtra("MONTO_PAGO", montoPago)
            intent.putExtra("FECHA_PAGO", fechaPago)
            intent.putExtra("FECHA_VENCIMIENTO", fechaVencimiento)
            intent.putExtra("DETALLE_PAGO", detallePago)

            startActivity(intent)
        }
    }
}