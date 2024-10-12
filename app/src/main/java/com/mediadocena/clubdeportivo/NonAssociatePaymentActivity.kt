package com.mediadocena.clubdeportivo

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.mediadocena.clubdeportivo.com.mediadocena.clubdeportivo.Paginator

class NonAssociatePaymentActivity : AppCompatActivity() {
    private lateinit var card: MaterialCardView
    private lateinit var parentLayout: LinearLayout //Container we use to add activities dynamically
    private lateinit var activityNameTextView: TextView
    private lateinit var activityIdTextView: TextView
    private lateinit var activityPriceTextView: TextView
    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var paginator: Paginator<ActivityModel> // Paginator instance
    private val activities = mutableListOf<ActivityModel>() //  Sport activities data model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_non_associate_payment)
        //Initialize all components
        initComponents()

        // Examples to add cards dynamically from database
        activities.add(ActivityModel("Yoga", "101", "$5000"))
        activities.add(ActivityModel("Pilates", "102", "$7500"))
        activities.add(ActivityModel("Spinning", "103", "$6000"))
        activities.add(ActivityModel("Zumba", "104", "$6700"))
        activities.add(ActivityModel("Natación", "105", "$6200"))
        activities.add(ActivityModel("Ciclismo", "106", "$5500"))

        // Initialize the pagination with the list of sport activities and page size
        paginator = Paginator(activities, 4)

        // Shows the first page of activities
        showPage()

        //Initialize all listeners
        initListeners()

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
                    view as MaterialCardView
                    view.isChecked = !view.isChecked
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