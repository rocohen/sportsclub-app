package com.mediadocena.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.mediadocena.clubdeportivo.database.ClubDatabaseHelper
import com.mediadocena.clubdeportivo.dataclasses.MemberDetails
import com.mediadocena.clubdeportivo.utils.SnackbarUtils
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
    private var listDataHeader: List<String> = listOf()
    private var listDataChild: HashMap<String, List<String>> = hashMapOf()
    private lateinit var btnBack: ImageButton
    private var membersListFees: List<MemberDetails> = listOf()
    private lateinit var dbHelper: ClubDatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_members)
        dbHelper = ClubDatabaseHelper(this)
        // Initialize all components
        initComponents()
        // This is to config the current date in the EditText
        configCurrentDate()
        // Initialize all listeners
        initListeners()

        // Create adapter to show list in activity layout
        listAdapter = CustomExpandableListAdapter(this, listDataHeader, listDataChild)
        expandableListView.setAdapter(listAdapter)

    }

    private fun initComponents(){
        dateEditText = findViewById(R.id.dateEditText)
        expandableListView = findViewById(R.id.expandableListView)
        btnBack = findViewById(R.id.BtnBack)
    }

    private fun initListeners(){
        // When user clicks on date field DatePicker dialog shows
        dateEditText.setOnClickListener {
            showDatePicker { selectedDate ->
                dateEditText.setText(selectedDate)

                val formattedDate = formatInputDate(dateEditText.text.toString())
                // Bring club members list data
                membersListFees = dbHelper.listarSociosCuotasAVenc(formattedDate)

                if(membersListFees.isNotEmpty()) {
                    prepareListData(membersListFees)
                    // Reinitialized adapter with new list data
                    listAdapter = CustomExpandableListAdapter(this, listDataHeader, listDataChild)
                    expandableListView.setAdapter(listAdapter)
                } else {
                    // First we clean the adapter
                    listDataHeader = listOf()
                    listDataChild = hashMapOf()
                    listAdapter = CustomExpandableListAdapter(this, listDataHeader, listDataChild)
                    expandableListView.setAdapter(listAdapter)
                    SnackbarUtils
                        .showCustomSnackbar(this, "No existen registros para la fecha indicada", "warning")
                        .show()
                }
                listAdapter.notifyDataSetChanged()
            }
        }
        btnBack.setOnClickListener { returnToMenu() }
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
    private fun prepareListData(memblist: List<MemberDetails>) {
        listDataHeader = memblist.map { it.nombreCompleto }
        listDataChild = hashMapOf()

        for (member in memblist) {
            val memberDetails = listOf(
                "Id: ${member.id}",
                "Teléfono: ${member.telefono}",
                "Correo: ${member.correo}",
                "Abonó: ${member.montoAbono}",
                "F.Últ.Pago: ${member.fecUltPago}",
                "F.Vencimiento: ${member.fecVencPago}"
            )
            listDataChild[member.nombreCompleto] = memberDetails
        }
    }

    private fun returnToMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish() // To close current activity
    }

    private fun formatInputDate(fecha:String):String {
        val originalFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val targetFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
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
}