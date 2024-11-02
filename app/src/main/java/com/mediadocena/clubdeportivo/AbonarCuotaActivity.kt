package com.mediadocena.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class AbonarCuotaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_abonar_cuota)

        val iBtnVolver = findViewById<ImageButton>(R.id.iBtnVolver)
        iBtnVolver.setOnClickListener{
            val intentVolver = Intent(this, MenuActivity::class.java)
            startActivity(intentVolver)
        }

        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        btnConfirmar.setOnClickListener{
            val intentSiguiente = Intent(this, AbonarCuotaSocioActivity::class.java)
            startActivity(intentSiguiente)
        }
    }
}