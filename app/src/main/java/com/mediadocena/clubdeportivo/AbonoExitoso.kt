package com.mediadocena.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class AbonoExitoso : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_abono_exitoso)

        // LOGICA PARA CAPTURAR EL ID CLIENTE ENVIADO EN EL INTENT
        val idCliente = intent.getIntExtra("ID_CLIENTE", 0)

        // LOGICA BOTON GENERAR COMPROBANTE
        val btnGenerarComprobante = findViewById<Button>(R.id.btnGenerarComprobante)
        btnGenerarComprobante.setOnClickListener{
            val intent = Intent(this, PaymentReceipt::class.java)
            intent.putExtra("ID_CLIENTE", idCliente) // ENVIO ID CLIENTE
            startActivity(intent)
            finish()
        }


    }
}