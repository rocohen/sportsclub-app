package com.mediadocena.clubdeportivo

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.material.snackbar.Snackbar
import com.mediadocena.clubdeportivo.database.ClubDatabaseHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AbonarCuotaActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_abonar_cuota)

        //Logica Boton VOLVER
        val iBtnVolver = findViewById<ImageButton>(R.id.iBtnVolver)
        iBtnVolver.setOnClickListener{
            val intentVolver = Intent(this, MenuActivity::class.java)
            startActivity(intentVolver)
        }

        //Logica Boton CONFIRMAR
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        val txtIdCliente = findViewById<TextView>(R.id.editTxtIdCliente)
        val errorAbono = findViewById<LinearLayoutCompat>(R.id.errorAbono)

        btnConfirmar.setOnClickListener{
            val idCliente = txtIdCliente.text.toString().toInt()
            try {
                if (idCliente == null) {
                    Snackbar.make(errorAbono,"Por favor, ingrese un numero de ID para continuar.", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.RED)
                        .setTextColor(Color.WHITE)
                        .show()
                }
                else {
                    val datos = ClubDatabaseHelper(this)
                    val queryTipoCliente = datos.ObtenerTipoCliente(idCliente)

                    when (queryTipoCliente) {
                        "0" -> {
                                Snackbar.make(errorAbono, "Error: El cliente no existe.", Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(Color.RED)
                                    .setTextColor(Color.WHITE)
                                    .show()
                        }
                        "Socio" -> {
                            var queryVencimiento = datos.ObtenerUltimoVencimientoSocio(idCliente)
                            // VALIDAMOS SI LA QUERY ARROJO UNA FECHA O NUM. CERO
                            if (queryVencimiento == "0") {
                                val intent = Intent(this, AbonarCuotaSocioActivity::class.java)
                                intent.putExtra("ID_CLIENTE", idCliente.toString()) // ENVIO ID CLIENTE
                                startActivity(intent)
                            }
                            else {
                                // SE VALIDA SI EL SOCIO TIENE CUOTA VIGENTE CON LA FECHA OBTENIDA
                                var conversion = LocalDate.parse(queryVencimiento, DateTimeFormatter.ISO_DATE)
                                if (conversion < LocalDate.now()) {
                                    val intent = Intent(this, AbonarCuotaSocioActivity::class.java)
                                    intent.putExtra("ID_CLIENTE", idCliente.toString()) // ENVIO ID CLIENTE
                                    startActivity(intent)
                                }
                                else   {
                                    Snackbar.make(errorAbono,"AVISO: El abono mensual sigue vigente. " +
                                            "No se puede proceder a un nuevo pago.", Snackbar.LENGTH_SHORT)
                                        .setBackgroundTint(Color.RED)
                                        .setTextColor(Color.WHITE)
                                        .show()
                                }
                            }
                        }
                        "No Socio" -> {
                            val intent = Intent(this, NonAssociatePaymentActivity::class.java)
                            intent.putExtra("ID_CLIENTE", idCliente.toString()) // ENVIO ID CLIENTE
                            startActivity(intent)
                        }
                    }
                }
            }
            catch (e: Exception) {
                Log.e("Error", "Error en el boton confirmar: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}