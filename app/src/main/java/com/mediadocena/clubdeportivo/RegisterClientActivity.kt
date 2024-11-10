package com.mediadocena.clubdeportivo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.mediadocena.clubdeportivo.database.ClubDatabaseHelper
import com.mediadocena.clubdeportivo.utils.SnackbarUtils


class RegisterClientActivity : AppCompatActivity() {
    private lateinit var iBtnBack: ImageButton
    private lateinit var txtName: TextInputLayout
    private lateinit var txtSurname: TextInputLayout
    private lateinit var txtEmail: TextInputLayout
    private lateinit var txtPhone: TextInputLayout
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
        txtPhone = findViewById(R.id.txtPhone)
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
        /* Verificamos que los TextInputLayout y el AutoCompleteTextView no estén vacíos*/
        val isNameValid = txtName.editText?.text?.trim()?.isNotEmpty() == true
        val isSurnameValid = txtSurname.editText?.text?.trim()?.isNotEmpty() == true
        val isEmailValid = txtEmail.editText?.text?.trim()?.isNotEmpty() == true
        val isDNIValid = txtDNI.editText?.text?.trim()?.isNotEmpty() == true
        val isPhoneValid = txtPhone.editText?.text?.trim()?.isNotEmpty() == true
        val isClientTypeValid = clientTypes.contains(clientType.text.toString())

        return isNameValid && isSurnameValid && isEmailValid && isDNIValid && isClientTypeValid && isPhoneValid
    }

    private fun registerClient() {
        if (areFieldsValid()) {

            val name = txtName.editText?.text.toString()
            val surname = txtSurname.editText?.text.toString()
            val dni = txtDNI.editText?.text.toString()
            val mail = txtEmail.editText?.text.toString()
            val phone = txtPhone.editText?.text.toString()
            val clientTypeText = clientType.text.toString()
            val healthCheckStatus = if (healthCheck.isChecked) 1 else 0

            val resultado = dbHelper.registrarCliente(
                    nombreC = name,
                    apellidoC = surname,
                    dniC = dni,
                    telC = phone,
                    correoC = mail,
                    tipoC = clientTypeText,
                    aptoFisico = healthCheckStatus
            )

            if (resultado == -1L){
                SnackbarUtils
                    .showCustomSnackbar(this, "Error: No se pudo realizar el registro", "error")
                    .setAnchorView(btnRegisterClient)
                    .show()
            }else if (resultado == -2L){
                SnackbarUtils
                    .showCustomSnackbar(this, "El cliente ya está registrado", "error")
                    .setAnchorView(btnRegisterClient)
                    .show()
            }else{
                SnackbarUtils
                    .showCustomSnackbar(this, "Cliente registrado exitosamente con ID $resultado ", "success", Snackbar.LENGTH_INDEFINITE)
                    .setAction("ABONAR"){
                        val intent = Intent(this, AbonarCuotaActivity::class.java)
                        intent.putExtra("idCliente", resultado.toString())
                        startActivity(intent)
                        //sino eliminar setAction y llamar a la funcion returnToPrevView()
                    }
                    .setActionTextColor(Color.WHITE)
                    .setAnchorView(btnRegisterClient)
                    .show()
            }

        } else {
            SnackbarUtils
                .showCustomSnackbar(this, "Error: los campos no pueden estar vacíos", "error")
                .setAnchorView(btnRegisterClient)
                .show()
          }
    }


     private fun clearFields() {
        txtName.editText?.text?.clear()
        txtSurname.editText?.text?.clear()
        txtEmail.editText?.text?.clear()
        txtDNI.editText?.text?.clear()
        clientType.setText("")
        healthCheck.isChecked = false
    }



}
