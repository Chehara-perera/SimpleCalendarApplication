package com.example.calendarapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.google.firebase.database.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class CalendarActivity : AppCompatActivity() {
    private lateinit var btn_new: Button
    private lateinit var events:ArrayList<EventDay>
    private lateinit var calendar: Calendar
    private lateinit var calendar_view: com.applandeo.materialcalendarview.CalendarView
    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var event_spinner:Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        btn_new=findViewById(R.id.btn_new)
        btn_new.setOnClickListener{
            var myintent=Intent(this,EventActivity::class.java)
            startActivity(myintent)
        }

        //adding event titles to a spinner

        event_spinner=findViewById<Spinner>(R.id.event_spinner)
        var names= arrayListOf<String>()
        ref=FirebaseDatabase.getInstance().getReference()
        ref.child("events").addValueEventListener(object:
            ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {


                for (spinnerSnapshot in snapshot.children) {
                    var spinnerName = spinnerSnapshot.child("event_title").getValue().toString()+" - "+spinnerSnapshot.child("event_date").getValue().toString()+" - At:  "+spinnerSnapshot.child("event_start").getValue().toString()
                    names.add(spinnerName.toString())
                    adapter = ArrayAdapter(
                        applicationContext,
                        android.R.layout.simple_spinner_dropdown_item,
                        names
                    )
                    event_spinner.adapter = adapter
                }

                events = ArrayList<EventDay>()
                calendar_view = findViewById(R.id.calendar_view)


                //get today date to calendar
                calendar = Calendar.getInstance()


                //set date onto calendar
                calendar.timeInMillis = getMilliFromDate("25/01/2023")

                //add events onto ArrayList
                events.add(EventDay(calendar, R.drawable.baseline_event_available_24))


                //adding extra event
                calendar.timeInMillis = getMilliFromDate("31/01/2023")
                events.add(EventDay(calendar, R.drawable.baseline_event_available_24))


                //set events into calendar view
                calendar_view.setEvents(events)



            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            //convert date into long
            private fun getMilliFromDate(dateFormat: String?): Long {
                var date = Date()
                val formatter = SimpleDateFormat("dd/MM/yyyy")
                try {
                    date = formatter.parse(dateFormat)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                println("Today is $date")
                return date.time
            }


        })
    }
}