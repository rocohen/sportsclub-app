package com.mediadocena.clubdeportivo

import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.mediadocena.clubdeportivo.database.ClubDatabaseHelper
import com.mediadocena.clubdeportivo.dataclasses.PaymentDetails
import com.mediadocena.clubdeportivo.utils.SnackbarUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class PaymentReceipt : AppCompatActivity() {
    private lateinit var paymentReceipt: View
    private lateinit var sendButton: Button
    private lateinit var dataClientID: TextView
    private lateinit var dataReceiptDate: TextView
    private lateinit var dataExpirationDate: TextView
    private lateinit var dataPaymentMethod: TextView
    private lateinit var receiveData: TextView
    private lateinit var sumData: TextView
    private lateinit var conceptData: TextView
    private lateinit var amount: TextView
    private lateinit var dbHelper: ClubDatabaseHelper
    private var  paymentDetails: PaymentDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment_receipt)
        dbHelper = ClubDatabaseHelper(this)
        initComponents()

        // LOGICA PARA CAPTURAR EL ID CLIENTE ENVIADO EN EL INTENT
        val idCliente = intent.getIntExtra("ID_CLIENTE", 0)
        if(idCliente != 0) {
            paymentDetails = dbHelper.obtenerDetallesUltPago(idCliente)

            if(paymentDetails != null) {
                val fechaFormatted = formatInputDate(paymentDetails!!.fecPago, "yyyy-MM-dd","dd/MM/yyyy")
                val fechaVencFormatted = formatInputDate(paymentDetails!!.fecVencPago, "yyyy-MM-dd","dd/MM/yyyy")
                dataClientID.text = paymentDetails?.id.toString()
                dataReceiptDate.text = fechaFormatted
                dataExpirationDate.text = fechaVencFormatted
                dataPaymentMethod.text = paymentDetails?.formaPago
                receiveData.text = paymentDetails?.nombreCompleto
                sumData.text = paymentDetails?.montoAbono.toString()
                conceptData.text = paymentDetails?.detalle
                amount.text = paymentDetails?.montoAbono.toString()
            }
        }

        // LOGICA BOTON VOLVER
        val btnVolver = findViewById<Button>(R.id.backButton)
        btnVolver.setOnClickListener{
            val intent = Intent(this, AbonarCuotaActivity::class.java)
            startActivity(intent)
            finish()
        }

        // LOGICA BOTON MENU
        val homeButton = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.homeButton)
        homeButton.setOnClickListener{
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        // LOGICA DE DESCARGA DEL COMPROBANTE DE PAGO
        sendButton.setOnClickListener {
            createPdfFromView(paymentReceipt, "comprobante_pago")
            sendButton.isEnabled = false
        }
    /* ---------------------------------------------------------------------------------------
     *   Para probar mandar un email comentar la línea anterior y descomentar la que sigue
     *   solo funciona si estamos loguedos con una cuenta de correo en el emulador
     *    al hacer click en el botón enviar vamos al proveedor de correo, por ejemplo gmail y
     *    los datos son cargados junto al pdf adjunto para enviar manualmente
     * ---------------------------------------------------------------------------------------*/
       // sendButton.setOnClickListener {  sendPaymentReceipt(paymentDetails!!.correo) sendButton.isEnabled = false }
    }

    private fun initComponents() {
        paymentReceipt = findViewById(R.id.paymentReceipt)
        sendButton = findViewById(R.id.sendButton)
        dataClientID = findViewById(R.id.dataClientID)
        dataReceiptDate = findViewById(R.id.dataReceiptDate)
        dataExpirationDate = findViewById(R.id.dataExpirationDate)
        dataPaymentMethod = findViewById(R.id.dataPaymentMethod)
        receiveData = findViewById(R.id.receive_data)
        sumData = findViewById(R.id.sum_data)
        conceptData = findViewById(R.id.concept_data)
        amount = findViewById(R.id.amount)
    }

    private fun formatInputDate(fecha:String, originalF: String = "dd/MM/yyyy", targetF: String = "yyyy-MM-dd"):String {
        val originalFormat = SimpleDateFormat(originalF, Locale.getDefault())
        val targetFormat = SimpleDateFormat(targetF, Locale.getDefault())
        return try {
            val date = originalFormat.parse(fecha)
            if (date != null) {
                targetFormat.format(date) // Format date to new format
            } else {
                throw IllegalArgumentException("Fecha inválida: $fecha")
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("Error al parsear la fecha: ${e.message}")
        }
    }

    private fun createPdfFromView(view: View, fileName: String): File? {
        // Creamos un objeto PdfDocument y obtenemos las dimensiones de la vista
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(view.width, view.height, 1).create()
        // Creamos una página para el documento PDF
        val page = document.startPage(pageInfo)
        // Dibujamos la vista en la página del PDF
        view.draw(page.canvas)
        document.finishPage(page)

        //Una vez convertido a pdf lo nombramos y pasamos al directorio de documentos del dispositivo
        val pdfDirPath = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.path
        val pdfFile = File(pdfDirPath, "${fileName}_${System.currentTimeMillis()}.pdf")
        try {
            FileOutputStream(pdfFile).use { out ->
                document.writeTo(out)
            }
            if (pdfFile.exists()) {
                SnackbarUtils
                    .showCustomSnackbar(this, "COMPROBANTE guardado exitosamente", "success")
                    .setAnchorView(sendButton)
                    .show()
            } else {
                SnackbarUtils
                    .showCustomSnackbar(this,"Error al crear el archivo COMPROBANTE", "error")
                    .show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            document.close()
        }
        return pdfFile
    }

    private fun sendEmailWithPdf(pdfFile: File, email: String) {
        val pdfUri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.provider",
            pdfFile
        )

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, "Comprobante de Pago")
            putExtra(Intent.EXTRA_TEXT, "Adjunto el comprobante de pago.")
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(emailIntent, "Enviar correo..."))
    }

    private fun sendPaymentReceipt(clientEmail: String) {
        val pdfFile = createPdfFromView(paymentReceipt, "comprobante_pago")

        if(pdfFile != null) {
            sendEmailWithPdf(pdfFile, clientEmail)
        }
        else {
            AlertDialog.Builder(this)
                .setTitle("Error al generar PDF")
                .setMessage("Hubo un problema al crear el comprobante PDF. ¿Desea intentarlo nuevamente?")
                .setPositiveButton("Reintentar") { _, _ ->
                    // Llamamos nuevamente a la función de creación de PDF
                    val pdfFile = createPdfFromView(paymentReceipt, "comprobante_pago")
                    if(pdfFile != null) {
                        sendEmailWithPdf(pdfFile, clientEmail)
                    }
                    else
                    {
                        SnackbarUtils
                            .showCustomSnackbar(this,"Error: no se pudo generar el PDF.", "error")
                            .show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}