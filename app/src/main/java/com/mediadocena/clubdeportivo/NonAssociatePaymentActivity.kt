package com.mediadocena.clubdeportivo

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.mediadocena.clubdeportivo.com.mediadocena.clubdeportivo.Paginator
import com.mediadocena.clubdeportivo.database.ClubDatabaseHelper
import com.mediadocena.clubdeportivo.entities.Cuota
import java.time.LocalDate

class NonAssociatePaymentActivity : AppCompatActivity() {
    private lateinit var card: MaterialCardView
    private lateinit var parentLayout: LinearLayout //Container we use to add activities dynamically
    private lateinit var activityNameTextView: TextView
    private lateinit var activityIdTextView: TextView
    private lateinit var activityPriceTextView: TextView
    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var paginator: Paginator<ActivityModel> // Paginator instance
    private var activities = mutableListOf<ActivityModel>() //  Sport activities data model

    // Para almacenar los datos de la cardview seleccionada
    private var nomAct: String = ""
    private var idAct: Int = 0
    private var priceAct: Double = 00.0


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_non_associate_payment)
        //Initialize all components
        initComponents()

        // LOGICA CAPTURA DE ID ENVIADO EN EL INTENT
        val idClienteVar = intent.getStringExtra("ID_CLIENTE")?.toIntOrNull()
        var idCliente = 0
        if (idClienteVar != null) { idCliente = idClienteVar }

        // Se obtiene la lista de actividades de la BD
        val datos = ClubDatabaseHelper(this)
        var lista = datos.ObtenerActividadesDisponibles(idCliente)

        // Asignamos la lista de actividades a Activities
        activities = lista

        // Initialize the pagination with the list of sport activities and page size
        paginator = Paginator(activities, 4)

        // Shows the first page of activities
        showPage()

        //Initialize all listeners
        initListeners()


        // LOGICA BOTON VOLVER
        val imgBtnBack = findViewById<ImageButton>(R.id.imgBtnBack)
        imgBtnBack.setOnClickListener{
            val intentAtras = Intent(this,AbonarCuotaActivity::class.java)
            startActivity(intentAtras)
        }


        val cboFormaPago = findViewById<AutoCompleteTextView>(R.id.acTvPaymentMethod)
        val errorAbonoNoSocio = findViewById<LinearLayoutCompat>(R.id.errorAbonoNoSocio)

        // LOGICA BOTON REGISTRAR PAGO
        val botonRegistrarPago = findViewById<Button>(R.id.btnRegistrarPagoNoSocio)
        botonRegistrarPago.setOnClickListener{
            val nombre = nomAct
            val id = idAct
            val price = priceAct
            if (nombre != "" && price != 00.0) {
                // Validamos is el cliente tiene registrado el pago en la actividad elegida
                val resultado = datos.ValidarPagoActividad(id.toString(), idCliente.toString())
                if (resultado == "0") {
                    val cuota: Cuota // INSTANCIA CLASE CUOTA
                    var cantidadCuotas = 0 // Se asigna al valuar la forma de pago

                    // Variables para instanciar un objeto CUOTA
                    val fechaPago: LocalDate = LocalDate.now()
                    var monto: Double = priceAct
                    val formaPago: String = cboFormaPago.text.toString()
                    val fechaVence: LocalDate = LocalDate.now()
                    var tienePromo: Boolean = false
                    val detallePago: String = "Abono de actividad ${nomAct}"

                    if (formaPago == "Forma de pago") {
                        Snackbar.make(errorAbonoNoSocio,"Error: Debe seleccionar una forma de pago valida.", Snackbar.LENGTH_SHORT)
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
                        cuota = Cuota(
                            idCliente,
                            fechaPago,
                            monto,
                            formaPago,
                            fechaVence,
                            tienePromo,
                            detallePago
                        )
                        cuota.aplicarPromocion(cantidadCuotas) // Luego de instanciar aplicamos la promocion
                        val resultado = datos.registrarPago(
                            cuota.idCliente,
                            cuota.fecha.toString(),
                            cuota.monto,
                            cuota.formaPago,
                            cuota.fechaVencimiento.toString(),
                            cuota.tienePromo,
                            cuota.detalle
                        )
                        // Evaluamos si el pago se registro correctamente
                        if (resultado != -1L) {
                            // Procedemos a inscribir al cliente en la actividad y a modificar el cupo disponible
                            var cupoDisp = datos.ObtenerCupoDisp(id)
                            if (cupoDisp > 0) {
                                cupoDisp --
                                val modificacion = datos.ModificarCupoDisponible(id, cupoDisp)
                                if (modificacion == "1") {
                                    val insercion = datos.InscripcionNoSocio(cuota.idCliente, id, cuota.fecha)
                                    if (insercion != -1L) {
                                        val intent = Intent(this, AbonoExitoso::class.java)
                                        intent.putExtra("ID_CLIENTE", cuota.idCliente) // ENVIO ID CLIENTE
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                        else {
                            Snackbar.make(errorAbonoNoSocio,"Error: No se pudo registrar el pago " +
                                    "correctamente.", Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(Color.RED)
                                .setTextColor(Color.WHITE)
                                .show()
                        }
                    }
                }
                else {
                    Snackbar.make(errorAbonoNoSocio, "ERROR: El cliente ya abono la actividad seleccionada en el dia de la fecha.", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.RED)
                        .setTextColor(Color.WHITE)
                        .show()
                }
            }
            else {
                Snackbar.make(errorAbonoNoSocio, "ERROR: Por favor, seleccione una actividad para continuar.", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.RED)
                    .setTextColor(Color.WHITE)
                    .show()
            }
        }
    }


    private fun initComponents() {
        card = findViewById(R.id.cardActivity01)
        parentLayout = findViewById(R.id.layoutActivityList)

        //Pagination buttons
        btnNext = findViewById(R.id.btnNext)
        btnPrevious = findViewById(R.id.btnPrevious)
    }


    private fun initListeners() {
        setPaginationListeners()
    }


    private fun setListListeners() {
        for (i in 0 until parentLayout.childCount) {
            val child = parentLayout.getChildAt(i)
            if (child is MaterialCardView) {
                child.setOnLongClickListener { view ->
                    // Uncheck all cards first
                    uncheckAllCards()
                    // 'view' is the MaterialCardView clicked
                    val cardView = view as MaterialCardView
                    view.isChecked = !view.isChecked
                    nomAct = cardView.findViewById<TextView>(R.id.activity_name).text.toString()
                    idAct = cardView.findViewById<TextView>(R.id.activity_id).text.toString().toInt()
                    priceAct = cardView.findViewById<TextView>(R.id.activity_price).text.toString().toDouble()
                    true
                }
            }
        }
    }


    private fun uncheckAllCards(){
        // Filters all MaterialCardView components in a list
        val cardViews = (0 until parentLayout.childCount)
            .map { parentLayout.getChildAt(it) }
            .filterIsInstance<MaterialCardView>()

        for (cardView in cardViews) {
            cardView.isChecked = false  // Removes all checked MaterialCardView components
        }
    }


    // Function to show activities of a specific page
    private fun showPage() {
        // Removes all the activities from current page
        parentLayout.removeAllViews()

        // Gets all the elements of current page and shows them dynamically
        val pageItems = paginator.getCurrentPageItems()
        for (activity in pageItems) {
            addActivityCard(activity)
        }

        // Updates the state of pagination buttons
        btnPrevious.isEnabled = paginator.hasPreviousPage()
        btnNext.isEnabled = paginator.hasNextPage()

        // Set list listeners to wait for possible checks on activity cards
        setListListeners()
    }


    private fun setPaginationListeners(){
        btnNext.setOnClickListener {
            if (paginator.goToNextPage()) {
                showPage()
            }
        }

        btnPrevious.setOnClickListener {
            if (paginator.goToPreviousPage()) {
                showPage()
            }
        }
    }


    private fun addActivityCard(activity: ActivityModel) {
        // Inflating the card layout from the XML
        val inflater = LayoutInflater.from(this)
        val cardView = inflater.inflate(R.layout.activity_card_item, parentLayout, false) as MaterialCardView

        // We call all textViews ids to config them dynamically
        activityNameTextView = cardView.findViewById(R.id.activity_name)
        activityIdTextView = cardView.findViewById(R.id.activity_id)
        activityPriceTextView = cardView.findViewById(R.id.activity_price)

        activityNameTextView.text = activity.name
        activityIdTextView.text = activity.id
        activityPriceTextView.text = activity.price

        // Adding the inflated card to the LinearLayout
        parentLayout.addView(cardView)
    }
}

// Data model that represents each sport activity
data class ActivityModel(val name: String, val id: String, val price: String)