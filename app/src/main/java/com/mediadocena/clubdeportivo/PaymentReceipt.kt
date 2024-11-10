package com.mediadocena.clubdeportivo

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.text.TextPaint
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.mediadocena.clubdeportivo.database.ClubDatabaseHelper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaymentReceipt : AppCompatActivity() {

    // Declaración de variables
    var idCliente = 0
    var nombreCliente = "0"
    var formaPagoIntent = "0"
    var montoPagoIntent = "0"
    var fechaPagoIntent = "0"
    var fechaVencimientoIntent = "0"
    var detallePagoIntent = "0"
    var mensajeError = "0"


    // Registrar un ActivityResultLauncher para crear el documento
    private val createPdfLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        uri?.let {
            try {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    generatePdf(outputStream, it)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "No se pudo guardar el archivo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment_receipt)

        // AGREGUE ESTO PORQUE ME SALIA ERROR AL CARGAR LOS DATOS DESDE BBDD

        // Lógica para capturar los datos del intent en caso de ERROR
        formaPagoIntent = intent.getStringExtra("FORMA_PAGO") ?: "Sin especificar"
        montoPagoIntent = intent.getStringExtra("MONTO_PAGO") ?: "Sin especificar"
        fechaPagoIntent = intent.getStringExtra("FECHA_PAGO") ?: "Sin especificar"
        fechaVencimientoIntent = intent.getStringExtra("FECHA_VENCIMIENTO") ?: "Sin especificar"
        detallePagoIntent = intent.getStringExtra("DETALLE_PAGO") ?: "Sin especificar"


        // Instancia de la base de datos
        val dbHelper = ClubDatabaseHelper(this)

        // Cargar los datos del cliente
        idCliente = intent.getIntExtra("ID_CLIENTE", 0)
        val clientIDTextView = findViewById<TextView>(R.id.dataClientID)
        clientIDTextView.text = idCliente.toString()

        // Obtener el nombre del cliente
        nombreCliente = dbHelper.obtenerNombreCompletoPorIdCliente(idCliente).toString()
        val textViewNombre = findViewById<TextView>(R.id.receive_data)
        textViewNombre.text = nombreCliente


        //PROVOCA ERRORES (No hay datos para mostrar en los Intents del Activity)
        try {
            // Obtener la última cuota asociada al cliente
            val ultimaCuota = dbHelper.obtenerUltimaCuotaPorIdCliente(idCliente)

            // Mostrar los datos de la última cuota si existe
            if (ultimaCuota != null) {
                val fechaTextView = findViewById<TextView>(R.id.dataReceiptDate)
                val montoTextView = findViewById<TextView>(R.id.sum_data)
                val formaPagoTextView = findViewById<TextView>(R.id.dataPaymentMethod)
                val fechaVencimiento = findViewById<TextView>(R.id.dataExpirationDate)
                val concepto = findViewById<TextView>(R.id.concept_data)
                val total = findViewById<TextView>(R.id.amount)

                formaPagoIntent = ultimaCuota.formaPago
                montoPagoIntent = ultimaCuota.monto.toString()
                fechaPagoIntent = ultimaCuota.fecha.toString()
                fechaVencimientoIntent = ultimaCuota.fechaVencimiento.toString()
                detallePagoIntent = ultimaCuota.detalle

                // Actualizar los TextView con la información de la última cuota
                fechaTextView.text = ultimaCuota.fecha.toString()
                montoTextView.text = ultimaCuota.monto.toString()
                formaPagoTextView.text = ultimaCuota.formaPago
                fechaVencimiento.text = ultimaCuota.fechaVencimiento.toString()
                concepto.text = ultimaCuota.detalle
                total.text = ultimaCuota.monto.toString()

            } else {
                // Mostrar mensaje si no hay cuota para este cliente
                Toast.makeText(this, "No hay cuotas registradas para este cliente", Toast.LENGTH_SHORT).show()

                // Limpiar los TextView si no se encuentran datos
                val fechaTextView = findViewById<TextView>(R.id.dataReceiptDate)
                val montoTextView = findViewById<TextView>(R.id.sum_data)
                val formaPagoTextView = findViewById<TextView>(R.id.dataPaymentMethod)
                val fechaVencimiento = findViewById<TextView>(R.id.dataExpirationDate)
                val concepto = findViewById<TextView>(R.id.concept_data)
                val total = findViewById<TextView>(R.id.amount)

                fechaTextView.text = "No disponible"
                montoTextView.text = "No disponible"
                formaPagoTextView.text = "No disponible"
                fechaVencimiento.text = "No disponible"
                concepto.text = "No disponible"
                total.text = "No disponible"

            }
        } catch (e: Exception) {
            // En caso de error, mostrar un mensaje de error
            Toast.makeText(this, "Error al cargar los datos: ${e.message}", Toast.LENGTH_LONG).show()

            mensajeError = e.message.toString()

            // Limpiar los TextView en caso de error
            val fechaTextView = findViewById<TextView>(R.id.dataReceiptDate)
            val montoTextView = findViewById<TextView>(R.id.sum_data)
            val formaPagoTextView = findViewById<TextView>(R.id.dataPaymentMethod)
            val fechaVencimiento = findViewById<TextView>(R.id.dataExpirationDate)
            val concepto = findViewById<TextView>(R.id.concept_data)
            val total = findViewById<TextView>(R.id.amount)

            // Mostrar los datos del intent si están disponibles
            if(formaPagoIntent != null && montoPagoIntent != null && fechaPagoIntent != null && fechaVencimientoIntent != null && detallePagoIntent != null){
                fechaTextView.text = fechaPagoIntent
                montoTextView.text = montoPagoIntent
                formaPagoTextView.text = formaPagoIntent
                fechaVencimiento.text = fechaVencimientoIntent
                concepto.text = detallePagoIntent
                total.text = montoPagoIntent
            }else{
                fechaTextView.text = "Error"
                montoTextView.text = "Error"
                formaPagoTextView.text = "Error"
                fechaVencimiento.text = "Error"
                concepto.text = "Error"
                total.text = "Error"
            }

        }




        // LOGICA BOTON VOLVER
        val btnVolver = findViewById<Button>(R.id.backButton)
        btnVolver.setOnClickListener {
            val intent = Intent(this, AbonarCuotaActivity::class.java)
            startActivity(intent)
        }

        // LOGICA BOTON MENU
        val homeButton = findViewById<FloatingActionButton>(R.id.homeButton)
        homeButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }


        // LOGICA BOTON GENERAR PDF
        val generatePdfButton = findViewById<Button>(R.id.generatePdfButton)
        generatePdfButton.setOnClickListener {
            createFile()
        }


    }

    // CREAR EL PDF
    private fun generatePdf(outputStream: OutputStream, uri: Uri) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Colores y fuentes
        val titlePaint = TextPaint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 20f
        }
        val textPaint = TextPaint().apply {
            typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            textSize = 14f
        }
        val boldTextPaint = TextPaint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 14f
        }
        val redTextPaint = TextPaint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 14f
            color = android.graphics.Color.RED
        }

        // Logo (sustituye con tu imagen de logo)
        val logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.club_logo) // Cambia 'logo' por el nombre de tu recurso de imagen
        val logoScaled = Bitmap.createScaledBitmap(logoBitmap, 100, 100, false)
        canvas.drawBitmap(logoScaled, 20f, 50f, null)

        // Título
        canvas.drawText("COMPROBANTE DE PAGO", 220f, 80f, titlePaint)

        // Información del cliente
        canvas.drawText("ID Cliente", 140f, 150f, boldTextPaint)
        canvas.drawText( idCliente.toString(), 300f, 150f, textPaint)

        canvas.drawText("Fecha", 140f, 180f, boldTextPaint)
        canvas.drawText(fechaPagoIntent.toString(), 300f, 180f, textPaint)

        canvas.drawText("Fecha de Vencimiento", 140f, 210f, boldTextPaint)
        canvas.drawText(fechaVencimientoIntent.toString(), 300f, 210f, textPaint)

        canvas.drawText("Forma de Pago", 140f, 240f, boldTextPaint)
        canvas.drawText(formaPagoIntent.toString(), 300f, 240f, textPaint)

        // Contacto
        canvas.drawText("info@sportsclub.com", 140f, 270f, textPaint)
        canvas.drawText("011-256-5261", 140f, 290f, textPaint)

        // Información de pago
        canvas.drawText("RECIBI DE", 140f, 340f, boldTextPaint)
        canvas.drawText(nombreCliente.toString(), 300f, 340f, textPaint)

        canvas.drawText("LA SUMA DE", 140f, 370f, boldTextPaint)
        canvas.drawText(montoPagoIntent.toString(), 300f, 370f, textPaint)

        canvas.drawText("EN CONCEPTO DE", 140f, 400f, boldTextPaint)
        canvas.drawText(detallePagoIntent.toString(), 300f, 400f, textPaint)

        // Total
        canvas.drawText("TOTAL", 140f, 450f, boldTextPaint)
        canvas.drawText("${montoPagoIntent.toString()} pesos argentinos", 300f, 450f, redTextPaint)


        // ERRORES (Solo si hay un mensaje de error)
        if (!mensajeError.isNullOrEmpty() || mensajeError != "0") {
            canvas.drawText("ERRORES", 140f, 500f, boldTextPaint)
            canvas.drawText(mensajeError, 140f, 600f, textPaint)
        }

        pdfDocument.finishPage(page)

        try {
            pdfDocument.writeTo(outputStream)
            Toast.makeText(this, "PDF creado correctamente", Toast.LENGTH_SHORT).show()

            // Abrir el PDF después de guardarlo
            openPdf(uri)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al crear el PDF", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }

    // CREAR EL ARCHIVO
    private fun createFile() {

        // Obtener la fecha actual en el formato deseado
        val fechaActual = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

        // Crear el nombre del archivo con la fecha del día
        val nombreArchivo = "ClubDeportivo_ComprobantePago_" + idCliente + "_" + "_" + fechaActual + ".pdf"

        // Lanzar la creación del PDF con el nombre actualizado
        createPdfLauncher.launch(nombreArchivo)
    }

    // ABRIR EL PDF
    private fun openPdf(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
        }
        startActivity(Intent.createChooser(intent, "Abrir PDF con"))
    }

}
