package com.mediadocena.clubdeportivo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    private lateinit var cvRegisterClient: com.google.android.material.card.MaterialCardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        initComponents() // Initializes all components
        initListeners()
    }

    private fun initComponents(){
        cvRegisterClient = findViewById(R.id.cvRegisterClient)
    }

    private fun initListeners() {
        //  Navigate to new client form
        cvRegisterClient.setOnClickListener { navigateToRegisterClient() }
    }

    // Navigate to new client form
    private fun navigateToRegisterClient(){
        val intent = Intent(this, RegisterClientActivity::class.java)
        startActivity(intent)
    }
}