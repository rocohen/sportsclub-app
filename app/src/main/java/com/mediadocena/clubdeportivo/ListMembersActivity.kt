package com.mediadocena.clubdeportivo

import android.os.Bundle
import android.widget.ExpandableListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ListMembersActivity : AppCompatActivity() {
//    Date picker variables
    private lateinit var currentDate: Date
    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var dateEditText: TextInputEditText
    private lateinit var locale: Locale
    // Variables to list club members
    private lateinit var expandableListView: ExpandableListView
    private lateinit var listAdapter: CustomExpandableListAdapter
    private lateinit var listDataHeader: List<String>
    private lateinit var listDataChild: HashMap<String, List<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_members)
        // Initialize all components
        initComponents()
        // This is to config the current date in the EditText
        configCurrentDate()
        // Initialize all listeners
        initListeners()

        // Prepare data to list
        prepareListData()

        // Create adapter to show list in activity layout
        listAdapter = CustomExpandableListAdapter(this, listDataHeader, listDataChild)
        expandableListView.setAdapter(listAdapter)

    }

    private fun initComponents(){
        dateEditText = findViewById(R.id.dateEditText)
        expandableListView = findViewById(R.id.expandableListView)
    }

    private fun initListeners(){
        // When user clicks on date field DatePicker dialog shows
        dateEditText.setOnClickListener {
            showDatePicker { selectedDate ->
                dateEditText.setText(selectedDate)
            }
        }
    }

    private fun configCurrentDate(){
        // Sets location to spanish Argentina
        locale = Locale("es", "AR")
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)

        currentDate = Calendar.getInstance().time
        dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateEditText.setText(dateFormat.format(currentDate))
    }

    // Activates date picker functionality
    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Seleccionar Fecha")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selection
            val selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
            onDateSelected(selectedDate)
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    // Functions to list club members
    private fun prepareListData() {
        listDataHeader = listOf("Hernán Navarro", "Santiago Nuñez", "Alejandra Dominguez")

        val hernanDetails = listOf(
            "Id: 500",
            "Teléfono: 9512145487",
            "Correo: hernannavarro@example@gmail.com",
            "Abonó: 18000",
            "F.Últ.Pago: 05/06/2024",
            "F.Vencimiento: 05/07/2024"
        )

        val santiagoDetails = listOf(
            "Id: 501",
            "Teléfono: 9512145488",
            "Correo: santiagonn@example@gmail.com",
            "Abonó: 20000",
            "F.Últ.Pago: 05/06/2024",
            "F.Vencimiento: 05/07/2024"
        )

        val alejandraDetails = listOf(
            "Id: 502",
            "Teléfono: 1162145488",
            "Correo: aledominguez@example@gmail.com",
            "Abonó: 18000",
            "F.Últ.Pago: 05/06/2024",
            "F.Vencimiento: 05/07/2024"
        )

        listDataChild = hashMapOf(
            "Hernán Navarro" to hernanDetails,
            "Santiago Nuñez" to santiagoDetails,
            "Alejandra Dominguez" to alejandraDetails
        )
    }
}