package com.mediadocena.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputLayout
import com.mediadocena.clubdeportivo.database.ClubDatabaseHelper
import com.mediadocena.clubdeportivo.utils.SnackbarUtils

class RegisterClientActivity : AppCompatActivity() {
    private lateinit var iBtnBack: ImageButton
    private lateinit var txtName: TextInputLayout
    private lateinit var txtSurname: TextInputLayout
    private lateinit var txtEmail: TextInputLayout
    private lateinit var clientType: AutoCompleteTextView
    private lateinit var txtDNI: TextInputLayout
    private lateinit var healthCheck: MaterialSwitch
    private lateinit var btnRegisterClient: Button
    private lateinit var dbHelper: ClubDatabaseHelper
    // Opciones válidas para el AutoCompleteTextView (ClientType)
    private val clientTypes = listOf("Socio", "No Socio")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_client)
        dbHelper = ClubDatabaseHelper(this)
        initComponents()
        initListeners()
    }

    private fun initComponents() {
        iBtnBack = findViewById(R.id.iBtnBack)
        txtName = findViewById(R.id.txtName)
        txtSurname = findViewById(R.id.txtSurname)
        txtEmail = findViewById(R.id.txtEmail)
        clientType = findViewById(R.id.autoCompTvClientType)
        txtDNI = findViewById(R.id.txtDNI)
        healthCheck = findViewById(R.id.healthCheck)
        btnRegisterClient = findViewById(R.id.btnRegisterClient)
    }

    private fun initListeners() {
        iBtnBack.setOnClickListener { returnToPrevView() }
        btnRegisterClient.setOnClickListener { registerClient() }
    }

    // Returns to the menu view by clicking the back arrow button
    private fun returnToPrevView(){
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish() // To close current activity
    }

    private fun areFieldsValid(): Boolean {
        // Verificamos que los TextInputLayout y el AutoCompleteTextView no estén vacíos
        val isNameValid = txtName.editText?.text?.isNotEmpty() == true
        val isSurnameValid = txtSurname.editText?.text?.isNotEmpty() == true
        val isEmailValid = txtEmail.editText?.text?.isNotEmpty() == true
        val isDNIValid = txtDNI.editText?.text?.isNotEmpty() == true
        val isClientTypeValid = clientTypes.contains(clientType.text.toString())

        return isNameValid && isSurnameValid && isEmailValid && isDNIValid && isClientTypeValid
    }

    private fun registerClient() {
        if (areFieldsValid()) {
            // TODO: eliminar la línea a continuación y registrar cliente en la base de datos
            SnackbarUtils
                .showCustomSnackbar(this, "Ya se puede registrar", "success")
                .show()
        } else {
            SnackbarUtils
                .showCustomSnackbar(this, "Los campos no deben estar vacíos", "error")
                .show()
        }
    }
}