package com.mediadocena.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class AbonarCuotaSocioActivity : AppCompatActivity() {

    // VARIABLES PRIVADAS PARA LA SELECCION DE PAGO
    private lateinit var cboFormaPago: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_abonar_cuota_socio)

        // LOGICA BOTON VOLVER
        val iBtnVolver = findViewById<ImageButton>(R.id.iBtnVolver)
        iBtnVolver.setOnClickListener{
            val intentVolver = Intent(this, AbonarCuotaActivity::class.java)
            startActivity(intentVolver)
        }

        // SE GUARDA ID DE SPINNER (COMBOBOX) Y EDITTEXT (TEXTBOX), LUEGO ASOCIO EL ADAPTADOR CON LOS VALORES XML
        cboFormaPago = findViewById<Spinner>(R.id.cboFormaPago)
        val adapter = ArrayAdapter.createFromResource(
            this, R.array.combo_opciones_pago,
            android.R.layout.simple_spinner_item
        )

        // SE DEFINE EL DESING DEL DROPDOWN DEL SPINNER
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // SE VINCULA EL ARRAY ADAPTER CON EL SPINNER
        cboFormaPago.adapter = adapter

        // SE CONFIGURA EL LISTENER PARA DETECTAR CUANDO UN ITEM ES SELECCIONADO
        cboFormaPago.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // METODO OBLIGATORIO CUANDO SE SELECCIONA UN ELEMENTO
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View?,
                position: Int,
                id: Long
            ){}
            // METODO OBLIGATORIO CUANDO NO SE ELECCIONA UN ELEMENTO
            override fun onNothingSelected(parentView: AdapterView<*>) {
                // SIN ACCIONES
            }
        }
    }
}