package com.mediadocena.clubdeportivo

import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.mediadocena.clubdeportivo.database.ClubDatabaseHelper
import com.mediadocena.clubdeportivo.dataclasses.ClubMemberIdCard
import com.mediadocena.clubdeportivo.utils.SnackbarUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CarnetSocio : AppCompatActivity() {
    private lateinit var dbHelper : ClubDatabaseHelper
    private lateinit var sendBtnIdCard: Button
    private lateinit var clubMemberIdCardView : View
    private lateinit var dataNumSoc: TextView
    private lateinit var dataAnioSoc: TextView
    private lateinit var dataNombreSocio : TextView
    private lateinit var dataApellidoSocio: TextView
    private var clubMemberIdCard: ClubMemberIdCard? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carnet_socio)
        dbHelper = ClubDatabaseHelper(this)
        initComponents()
        // LOGICA PARA CAPTURAR EL ID CLIENTE ENVIADO EN EL INTENT
        val idCliente = intent.getIntExtra("ID_CLIENTE", 0)
        if(idCliente != 0) {
            clubMemberIdCard = dbHelper.obtenerCarnetSocioData(idCliente)

            if(clubMemberIdCard != null) {
                dataNumSoc.text = clubMemberIdCard?.numSocio.toString()
                dataAnioSoc.text = clubMemberIdCard?.anio
                dataNombreSocio.text = clubMemberIdCard?.nombre
                dataApellidoSocio.text = clubMemberIdCard?.apelllido
            }
        }


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

        // LOGICA DE DESCARGA DEL CARNET DE SOCIO
        sendBtnIdCard.setOnClickListener{
            createPdfFromView(clubMemberIdCardView, "carnet_socio")
            sendBtnIdCard.isEnabled = false
        }
        /* ---------------------------------------------------------------------------------------
         *   Para probar mandar un email comentar la línea anterior y descomentar la que sigue
         *   solo funciona si estamos loguedos con una cuenta de correo en el emulador
         *    al hacer click en el botón enviar vamos al proveedor de correo, por ejemplo gmail y
         *    los datos son cargados junto al pdf adjunto para enviar manualmente
         * ---------------------------------------------------------------------------------------*/
//         sendBtnIdCard.setOnClickListener{
//                    sendCarnetSocio(clubMemberIdCard!!.correo)
//                    sendBtnIdCard.isEnabled = false
//                }
    }

    private fun initComponents() {
        clubMemberIdCardView = findViewById(R.id.txtClubMemberIdCard)
        sendBtnIdCard = findViewById(R.id.sendBtnIdCard)
        dataNumSoc = findViewById(R.id.dataNumSoc)
        dataAnioSoc = findViewById(R.id.dataAnioSoc)
        dataNombreSocio = findViewById(R.id.dataNombreSocio)
        dataApellidoSocio = findViewById(R.id.dataApellidoSocio)
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
                    .showCustomSnackbar(this, "CARNET guardado exitosamente", "success")
                    .setAnchorView(sendBtnIdCard)
                    .show()
            } else {
                SnackbarUtils
                    .showCustomSnackbar(this,"Error al crear el documento CARNET", "error")
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
            putExtra(Intent.EXTRA_SUBJECT, "Carnet de Socio")
            putExtra(Intent.EXTRA_TEXT, "Adjunto el carnet de socio.")
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(emailIntent, "Enviar correo..."))
    }

    private fun sendCarnetSocio(clientEmail: String) {
        val pdfFile = createPdfFromView(clubMemberIdCardView, "carnet_socio")

        if(pdfFile != null) {
            sendEmailWithPdf(pdfFile, clientEmail)
        }
        else {
            AlertDialog.Builder(this)
                .setTitle("Error al generar PDF")
                .setMessage("Hubo un problema al crear el carnet. ¿Desea intentarlo nuevamente?")
                .setPositiveButton("Reintentar") { _, _ ->
                    // Llamamos nuevamente a la función de creación de PDF
                    val pdfFile = createPdfFromView(clubMemberIdCardView, "carnet_socio")
                    if(pdfFile != null) {
                        sendEmailWithPdf(pdfFile, clientEmail)
                    }
                    else
                    {
                        SnackbarUtils
                            .showCustomSnackbar(this,"Error: no se pudo generar el documento.", "error")
                            .show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}