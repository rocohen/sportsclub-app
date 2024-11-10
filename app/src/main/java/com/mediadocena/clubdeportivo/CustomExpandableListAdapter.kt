package com.mediadocena.clubdeportivo
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView

class CustomExpandableListAdapter(
    private val context: Context,
    private val listDataHeader: List<String>,
    private val listDataChild: HashMap<String, List<String>>
) : BaseExpandableListAdapter() {

    override fun getChild(listPosition: Int, expandableListPosition: Int): Any {
        return listDataChild[listDataHeader[listPosition]]!![expandableListPosition]
    }

    override fun getChildId(listPosition: Int, expandableListPosition: Int): Long {
        return expandableListPosition.toLong()
    }

    override fun getGroup(listPosition: Int): Any {
        return listDataHeader[listPosition]
    }

    override fun getGroupCount(): Int {
        return listDataHeader.size
    }

    override fun getChildrenCount(listPosition: Int): Int {
        // We need to return only one block of details in each list item
        return 1
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandableListPosition: Int): Boolean {
        return true
    }

    //    Gets header name
    override fun getGroupView(listPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val headerTitle = getGroup(listPosition) as String
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_group,null)
        val lblListHeader = view.findViewById<TextView>(R.id.tvListTitle)
        lblListHeader.text = headerTitle
        return view
    }

    override fun getChildView(listPosition: Int, expandableListPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, null)
        // Add values to each textView
        val memberDetails = listDataChild[listDataHeader[listPosition]]


        val idTextView = view.findViewById<TextView>(R.id.tvId)
        val phoneTextView = view.findViewById<TextView>(R.id.tvPhone)
        val emailTextView = view.findViewById<TextView>(R.id.tvEmail)
        val amountTextView = view.findViewById<TextView>(R.id.tvAmountPayment)
        val lastPaymentTextView = view.findViewById<TextView>(R.id.tvLastPayment)
        val dueDateTextView = view.findViewById<TextView>(R.id.tvDueDate)

        // If details are not null we assign each value.
        memberDetails?.let {
            idTextView.text = it[0]  // "Id: 500"
            phoneTextView.text = it[1]  // "Teléfono: 9512145487"
            emailTextView.text = it[2]  // "Correo: hernannavarro@ex..."
            amountTextView.text = it[3]  // "Abonó: 18000"
            lastPaymentTextView.text = it[4]  // "F.Últ.Pago: 05/06/2024"
            dueDateTextView.text = it[5]  // "F.Vencimiento: 05/07/2024"
        }

        return view
    }
}