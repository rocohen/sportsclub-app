package com.mediadocena.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.mediadocena.clubdeportivo.database.ClubDatabaseHelper
import com.mediadocena.clubdeportivo.session.Session
import com.mediadocena.clubdeportivo.utils.SnackbarUtils

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: ClubDatabaseHelper
    private lateinit var usuLog : TextInputEditText
    private lateinit var usuPass : TextInputEditText
    private lateinit var btnLogin : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        dbHelper = ClubDatabaseHelper(this)
        initComponents()
        initListeners()
    }

    private fun initComponents() {
        usuLog = findViewById(R.id.etUser);
        usuPass = findViewById(R.id.etPass);
        btnLogin = findViewById(R.id.btnLogin)
    }

    private fun initListeners() {
        btnLogin.setOnClickListener{ login() }
    }

    private fun login() {
        val usuAlias = usuLog.text.toString();
        val usuClave = usuPass.text.toString();

        if(usuAlias.isNotEmpty() && usuClave.isNotEmpty()){
            val usuLogueado = dbHelper.obtenerUsuLogueado(usuAlias, usuClave)
            if(usuLogueado != null){
                Session.usuario = usuLogueado.nombre;
                Session.rol = usuLogueado.rol;
                navigateToMenu()
            } else {
                SnackbarUtils
                    .showCustomSnackbar(this, "Error: usuario y/o clave incorrecta. Intentar nuevamente", "error")
                    .show()
            }
        } else{
            SnackbarUtils
                .showCustomSnackbar(this, "Los campos no deben estar vacíos", "error")
                .show()
        }
    }

    // Navigate to new client form
    private fun navigateToMenu(){
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }
}