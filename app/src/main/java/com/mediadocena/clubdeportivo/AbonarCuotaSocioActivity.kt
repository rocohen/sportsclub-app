package com.mediadocena.clubdeportivo

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.material.snackbar.Snackbar
import com.mediadocena.clubdeportivo.database.ClubDatabaseHelper
import com.mediadocena.clubdeportivo.entities.Cuota
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AbonarCuotaSocioActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_abonar_cuota_socio)

        // LOGICA CAPTURA DE ID ENVIADO EN EL INTENT
        val idClienteVar = intent.getStringExtra("ID_CLIENTE")?.toIntOrNull() // CONSTANTE QUE CONTIENE ID CLIENTE DE LA ACTIVITY ANTERIOR
        var idCliente = 0
        if (idClienteVar != null) { idCliente = idClienteVar }


        // LOGICA BOTON VOLVER
        val iBtnVolver = findViewById<ImageButton>(R.id.iBtnVolver)
        iBtnVolver.setOnClickListener{
            val intent = Intent(this, AbonarCuotaActivity::class.java)
            startActivity(intent)
        }


        // LOGICA BOTON GENERAR CARNET
        val btnGenCarnet = findViewById<Button>(R.id.btnGenCarnet)
        btnGenCarnet.setOnClickListener{
            val intent = Intent(this, CarnetSocio::class.java)
            intent.putExtra("ID_CLIENTE", idCliente)
            startActivity(intent)
        }


        // LOGICA SPINNER (CBO)
        val cboFormaPago = findViewById<Spinner>(R.id.cboFormaPago)
        val adapter = ArrayAdapter.createFromResource( // SE CREA ADAPTADOR, SE LE ASIGNAN LOS VALORES DESDE XML
            this, R.array.combo_opciones_pago,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // SE DEFINE EL DESING DEL DROPDOWN DEL SPINNER
        cboFormaPago.adapter = adapter // SE VINCULA EL ARRAY ADAPTER CON EL SPINNER


        // LOGICA BOTON REGISTRAR PAGO
        val btnRegistrarPago = findViewById<Button>(R.id.btnRegPago)
        val errorAbonoSocio = findViewById<LinearLayoutCompat>(R.id.errorAbonoSocio)
        btnRegistrarPago.setOnClickListener{
            val cuota: Cuota // INSTANCIA CLASE CUOTA
            val datos = ClubDatabaseHelper(this) // INSTANCIA DE DB
            var cantidadCuotas = 0 // Se asigna al valuar la forma de pago

            // SE VALIDA SI EL SOCIO TIENE CUOTA VIGENTE CON LA FECHA OBTENIDA
            var bandera : Int? // Creamos un variable para usarlo de bandera
            var queryVencimiento = datos.ObtenerUltimoVencimientoSocio(idCliente)
            // Validamos resultado y asignamos valor a la bandera
            if (queryVencimiento == "0") {
                bandera = 1
            }
            else {
                var conversion = LocalDate.parse(queryVencimiento, DateTimeFormatter.ISO_DATE)
                if (conversion < LocalDate.now()) {
                    bandera = 1
                }
                else {
                    bandera = 0
                }
            }
            // Segun el valor de la bandera se proce o no al pago.
            if (bandera == 1) {
                // Variables para instanciar un objeto CUOTA
                val fechaPago: LocalDate = LocalDate.now()
                var montoPago: Double = 18000.00 // Variable que puede cambiar dependiendo la promocion
                val formaPago: String = cboFormaPago.selectedItem.toString()
                val fechaVence: LocalDate = LocalDate.now().plusDays(30)
                var tienePromo: Boolean = false // Variable que puede cambiar dependiendo la forma de pago
                val detallePago: String = "Abono de cuota Societaria"

                // Validamos la forma de pago
                if (formaPago == "Forma de Pago") {
                    Snackbar.make(errorAbonoSocio, "Error: Debe seleccionar una forma de pago valida.", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.RED)
                        .setTextColor(Color.WHITE)
                        .show()
                }
                else {
                    when (formaPago) {
                        "Tarjeta 3 Cuotas (5% OFF, sin interes)" -> {
                            tienePromo = true
                            cantidadCuotas = 3
                        }

                        "Tarjeta 6 Cuotas (10% OFF, sin interes)" -> {
                            tienePromo = true
                            cantidadCuotas = 6
                        }
                    }
                    // Instanciamos un objeto de la clase cuota
                    cuota = Cuota(
                        idCliente,
                        fechaPago,
                        montoPago,
                        formaPago,
                        fechaVence,
                        tienePromo,
                        detallePago
                    )
                    cuota.aplicarPromocion(cantidadCuotas) // Aplicamos el metodo de la clase cuotas para modificar el monto
                    // Ejecutamos el registro de pago en la base de datos y obtenemos un resultado
                    val resultado = datos.registrarPago(
                        cuota.idCliente,
                        cuota.fecha.toString(),
                        cuota.monto,
                        cuota.formaPago,
                        cuota.fechaVencimiento.toString(),
                        cuota.tienePromo,
                        cuota.detalle
                    )
                    // Evaluamos si el insert fue exitoso
                    if (resultado != -1L) {
                        val intent = Intent(this, AbonoExitoso::class.java)
                        intent.putExtra("ID_CLIENTE", cuota.idCliente) // ENVIO ID CLIENTE
                        startActivity(intent)
                    } else {
                        Snackbar.make(
                            errorAbonoSocio,
                            "Error: No se pudo registrar el pago correctamente.",
                            Snackbar.LENGTH_SHORT
                        )
                            .setBackgroundTint(Color.RED)
                            .setTextColor(Color.WHITE)
                            .show()
                    }
                }
            }
            else {
                Snackbar.make(errorAbonoSocio,"AVISO: El abono mensual sigue vigente. " +
                        "No se puede proceder a un nuevo pago.", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.RED)
                    .setTextColor(Color.WHITE)
                    .show()
            }
        }
    }
}