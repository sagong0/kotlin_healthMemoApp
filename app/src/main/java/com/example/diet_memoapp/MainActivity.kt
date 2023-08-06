package com.example.diet_memoapp

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.icu.util.GregorianCalendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    val dataModelList = mutableListOf<DataModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = Firebase.database
        val myRef = database.getReference("myMemo")

        val listview = findViewById<ListView>(R.id.mainLv)

        val listViewAdapter = ListViewAdapter(dataModelList)
        listview.adapter = listViewAdapter

        Log.d("Beforsync----------", dataModelList.toString())

        // Read from the database
        myRef.child(Firebase.auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataModelList.clear()

                for(dataModel in snapshot.children){
                    Log.d("DataDataDA",dataModel.toString())
                    dataModelList.add(dataModel.getValue(DataModel::class.java)!!)
                }
                listViewAdapter.notifyDataSetChanged()
                Log.d("DataModelListIs", dataModelList.toString())
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        val writeBtn = findViewById<ImageView>(R.id.writeButton)
        writeBtn.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog,null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("운동 메모 다이얼로그")

            val mAlertDialog = mBuilder.show()

            val dateSelectBtn = mAlertDialog.findViewById<Button>(R.id.dateSelectBtn)
            var dateText =""

            dateSelectBtn?.setOnClickListener {

                val today = GregorianCalendar()

                val year : Int = today.get(Calendar.YEAR)
                val month : Int = today.get(Calendar.MONTH)
                val date : Int = today.get(Calendar.DATE)

                val dlg = DatePickerDialog(this, object :DatePickerDialog.OnDateSetListener{
                    override fun onDateSet(
                        view: DatePicker?,
                        year: Int,
                        month: Int,
                        dayOfMonth: Int
                    ) {
                        Log.d("MAIN", "year : ${year}, month : ${month + 1}, day : $date")
                        dateSelectBtn.setText("${year},${month + 1},$date")
                        dateText = "${year},${month + 1},$date"
                    }
                }, year,month,date)

                dlg.show()
            }

            val saveBtn = mAlertDialog.findViewById<Button>(R.id.saveBtn)
            saveBtn!!.setOnClickListener {
                // Write a message to the database
                val database = Firebase.database
                val myRef = database.getReference("myMemo").child(Firebase.auth.currentUser!!.uid)


                val healthMemo = mAlertDialog.findViewById<EditText>(R.id.healthMemoArea)?.text.toString()

                val model = DataModel(dateText, healthMemo)

                myRef.push().setValue(model)
                mAlertDialog.dismiss()
            }
        }
    }
}