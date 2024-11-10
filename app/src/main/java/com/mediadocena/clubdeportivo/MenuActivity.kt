package com.mediadocena.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.mediadocena.clubdeportivo.session.Session
import kotlin.system.exitProcess

class MenuActivity : AppCompatActivity() {
    private lateinit var cvRegisterClient: com.google.android.material.card.MaterialCardView
    private lateinit var cvPayMembership: MaterialCardView
    private lateinit var cvListClubMembers: MaterialCardView
    private lateinit var cvLogout: MaterialCardView
    private lateinit var userName: TextView
    private lateinit var userRole: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        initComponents() // Initializes all components
        initListeners()

        // Asignamos el usuarioy su rol
        userName.text = Session.usuario
        userRole.text = Session.rol
    }

    private fun initComponents(){
        cvRegisterClient = findViewById(R.id.cvRegisterClient)
        cvPayMembership = findViewById(R.id.cvPayMembership)
        cvListClubMembers = findViewById(R.id.cvListClubMembers)
        cvLogout = findViewById(R.id.cvLogout)
        userName = findViewById(R.id.tvUserName)
        userRole = findViewById(R.id.tvUserRole)
    }

    private fun initListeners() {
        //  Navigation options listeners
        cvRegisterClient.setOnClickListener { navigateToRegisterClient() }
        cvPayMembership.setOnClickListener{ navigateToPayMembershipFee() }
        cvListClubMembers.setOnClickListener { navigateToListMembers() }
        cvLogout.setOnClickListener { logout() }
    }

    // Menu navigation options
    private fun navigateToRegisterClient(){
        val intent = Intent(this, RegisterClientActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToPayMembershipFee() {
        val intent = Intent(this, AbonarCuotaActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToListMembers() {
        val intent = Intent(this, ListMembersActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        finishAffinity() // Cierra todas las actividades y sale de la aplicación
        exitProcess(0) // Termina el proceso de la aplicación
    }
}