package com.example.calendarapplication

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import com.applandeo.materialcalendarview.utils.calendar
import com.example.calendarapplication.databinding.ActivityEventBinding
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.ArrayList

class EventActivity : AppCompatActivity() {
    private lateinit var txt_title:EditText
    private lateinit var date_picker:DatePicker
    private lateinit var time_picker_start:TimePicker
    private lateinit var time_picker_end:TimePicker
    private lateinit var txt_date:TextView
    private lateinit var txt_start:TextView
    private lateinit var txt_end:TextView
    private lateinit var txt_note:EditText
    private lateinit var btn_create:Button
    private lateinit var binding:ActivityEventBinding
    private lateinit var reminder_spinner:Spinner
    private lateinit var reminder_array:ArrayList<String>
    private lateinit var adapter:Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()
        binding.btnCreate.setOnClickListener {
            scheduleNotification() }

        reminder_spinner = findViewById(R.id.event_spinner)
        reminder_array= arrayListOf<String>()
        reminder_array.add("15 mins before")
        reminder_array.add("1 hour before")
        reminder_array.add("1 day before")
        reminder_array.add("Select date and time")

        var reminderAdapter=ArrayAdapter(applicationContext,android.R.layout.simple_spinner_dropdown_item,reminder_array)
        reminder_spinner.adapter=reminderAdapter
        txt_title = findViewById(R.id.txt_title)
        date_picker = findViewById(R.id.date_picker)
        time_picker_start = findViewById(R.id.time_picker_start)
        time_picker_end = findViewById(R.id.time_picker_end)
        txt_note = findViewById(R.id.txt_note)
        btn_create = findViewById(R.id.btn_create)

    }

    private fun scheduleNotification() {

        //Storing events in the firebase realtime database

        var event_title = txt_title.text.toString()
        var event_date= date_picker.dayOfMonth.toString()+"/"+date_picker.month.toString()+"/"+date_picker.year.toString()
        var event_start=time_picker_start.hour.toString()+":"+time_picker_start.minute.toString()
        var event_end=time_picker_end.hour.toString()+":"+time_picker_end.minute.toString()
        var note = txt_note.text.toString()

        val ref = FirebaseDatabase.getInstance().getReference("events")
        val eventId = ref.push().key
        val event = Event(eventId!!, event_title, event_date,event_start,event_end, note)
        ref.child(eventId).setValue(event).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Event saved successfully!!!", Toast.LENGTH_SHORT).show()
                var myintent = Intent(this, CalendarActivity::class.java)
                startActivity(myintent)
            } else {
                Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        val intent=Intent(applicationContext,Notification::class.java)
        val title=binding.txtTitle.text.toString()
        val message=binding.txtNote.text.toString()
        intent.putExtra(titleExtra,title)
        intent.putExtra(messageExtra,message)

        val pendingIntent=PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        )
        val alarmManager=getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        showAlert(time,title,message)
    }

    private fun showAlert(time: Long, title: String, message: String) {

        val date=Date(time)
        val dateFormat=android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat= android.text.format.DateFormat.getLongDateFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notification Scheduled")
            .setMessage(
                "Title:"+ title +
                "\nMessage: "+message+
                "\nAt"+dateFormat.format(date)+" "+timeFormat.format(date))
            .setPositiveButton("Okay"){_,_ ->}
            .show()



    }

    private fun getTime(): Long {

        //setting notification reminders

        if (reminder_spinner.selectedItem == 0) {
            val minute = binding.timePickerStart.minute.toString().toInt()-15
            val hour = binding.timePickerStart.hour
            val day = binding.datePicker.dayOfMonth
            val month = binding.datePicker.month
            val year = binding.datePicker.year
            val calendar = Calendar.getInstance()
            calendar.set(year, month, day, hour, minute)


        }
       if (reminder_spinner.selectedItem == 1) {
            val minute = binding.timePickerStart.minute
            val hour = binding.timePickerStart.hour.toString().toInt()-1
            val day = binding.datePicker.dayOfMonth
            val month = binding.datePicker.month
            val year = binding.datePicker.year
            val calendar = Calendar.getInstance()
            calendar.set(year, month, day, hour, minute)


        }
        if (reminder_spinner.selectedItem == 2) {
            val minute = binding.timePickerStart.minute
            val hour = binding.timePickerStart.hour
            val day = binding.datePicker.dayOfMonth.toString().toInt()-1
            val month = binding.datePicker.month
            val year = binding.datePicker.year
            val calendar = Calendar.getInstance()
            calendar.set(year, month, day, hour, minute)


        }
        if (reminder_spinner.selectedItem == 3) {
            Toast.makeText(this, "Notitifcation set for start time!!!", Toast.LENGTH_SHORT).show()
            var myintent = Intent(this, CalendarActivity::class.java)
            startActivity(myintent)
        }

        return calendar.timeInMillis
    }

    private fun createNotificationChannel() {

        val name = "Notification Channel"
        val desc ="A Description of the channel"
        val importance=NotificationManager.IMPORTANCE_DEFAULT
        val channel=NotificationChannel(channelID,name,importance)
        channel.description=desc
        val notificationManager=getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


}

