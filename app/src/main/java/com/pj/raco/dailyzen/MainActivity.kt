package com.pj.raco.dailyzen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get notification permission from user
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (!notificationManager.isNotificationPolicyAccessGranted) {
//            val snackbar = Snackbar.make(findViewById(R.id.main), "Please grant notification permission.", Snackbar.LENGTH_INDEFINITE)
//            snackbar.setAction("Grant") {
//                val intent = Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS)
//                startActivity(intent)
//            }
//            snackbar.show()
//        }

        // get current hour
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        // based on current hour, set greet as morning, afternoon or evening
        val greet = when (currentHour) {
            in 0..11 -> "morning."
            in 12..15 -> "afternoon."
            in 16..23 -> "evening."
            else -> "morning."
        }

        // get greet text view and set its value to greet
        val greetTextView = findViewById<TextView>(R.id.greet)
        greetTextView.text = greet

        val quoteTextView = findViewById<TextView>(R.id.quote)
        val authorTextView = findViewById<TextView>(R.id.author)

        // get json array of quotes from assets/data.json and select random quote from array
        fun genQuote(): JSONObject {
            val quotes = JSONObject(assets.open("data.json").bufferedReader().use { it.readText() })
                .getJSONArray("quotes")
            return quotes.getJSONObject((0 until quotes.length()).random())
        }

        fun summonQuote() {
            val quote = genQuote()
            quoteTextView.text = quote.getString("quote")
            authorTextView.text = "— " + quote.getString("author")
        }

        summonQuote()

        // get share and refresh buttons
        val shareButton = findViewById<ImageButton>(R.id.share)
        val refreshButton = findViewById<ImageButton>(R.id.refresh)

        // set share button to share quote
        shareButton.setOnClickListener {
            val intent= Intent()
            intent.action=Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, quoteTextView.text.toString() + " — " + authorTextView.text.toString())
            intent.type="text/plain"
            startActivity(Intent.createChooser(intent,"Share To:"))
        }

        // set refresh button to summon new quote
        refreshButton.setOnClickListener {
            summonQuote()
        }

        // generate channel id
        val CHANNEL_ID = "dailyzen"
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // get notification title and content
        fun genNotification(): NotificationCompat.Builder {
            val notifQuote = genQuote()
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Take some time to reflect on your day.")
                .setContentText(notifQuote.getString("quote") + " — " + notifQuote.getString("author"))
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(notifQuote.getString("quote") + " — " + notifQuote.getString("author")))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            return builder
        }

        fun createNotificationChannel() {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "dailyzen."
                val descriptionText = "live your life the stoic way."
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun sendNotification() {
            createNotificationChannel()
            val builder = genNotification()
            // check if notification permission is granted
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(0, builder.build())
            }
        }

        // send notification at 8pm
//        refreshButton.setOnClickListener {
//            sendNotification()
//        }
        // create a background process that sends notification at 8pm
//        val timer = Timer()
//        timer.scheduleAtFixedRate(object : TimerTask() {
//            override fun run() {
//                sendNotification()
//            }
//        }, 0, 1000 * 60 * 60 * 24)

    }
}