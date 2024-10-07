package com.mediadocena.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class RegisterClientActivity : AppCompatActivity() {
    private lateinit var iBtnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_client)
        initComponents()
        initListeners()
    }

    private fun initComponents() {
        iBtnBack = findViewById(R.id.iBtnBack)
    }

    private fun initListeners() {
        iBtnBack.setOnClickListener { returnToPrevView() }
    }

    // Returns to the menu view by clicking the back arrow button
    private fun returnToPrevView(){
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish() // To close current activity
    }
}