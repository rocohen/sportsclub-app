package com.mediadocena.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class CarnetSocio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carnet_socio)

        // LOGICA PARA CAPTURAR EL ID CLIENTE ENVIADO EN EL INTENT
        val idCliente = intent.getIntExtra("ID_CLIENTE", 0)

        // LOGICA BOTON VOLVER
        val btnVolver = findViewById<Button>(R.id.backButton)
        btnVolver.setOnClickListener{
            val intent = Intent(this, AbonarCuotaSocioActivity::class.java)
            intent.putExtra("ID_CLIENTE", idCliente.toString())
            startActivity(intent)
        }

        // LOGICA BOTON MENU
        val homeButton = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.homeButton)
        homeButton.setOnClickListener{
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }
}