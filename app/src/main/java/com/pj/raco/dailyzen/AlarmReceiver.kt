package com.pj.raco.dailyzen

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import org.json.JSONObject

class AlarmReceiver: BroadcastReceiver() {
    /**
     * sends notification when receives alarm
     * and then reschedule the reminder again
     **/
    override fun onReceive(context: Context, intent: Intent) {
        val contentIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId: String = "raco_dailyzen"
        val quotes = JSONObject(context.assets.open("data.json").bufferedReader().use { it.readText() })
            .getJSONArray("quotes")
        val notifQuote = quotes.getJSONObject((0 until quotes.length()).random())

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Take some time to reflect on your day.")
            .setContentText(notifQuote.getString("quote") + " — " + notifQuote.getString("author"))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(notifQuote.getString("quote") + " — " + notifQuote.getString("author")))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
             // notificationId is a unique int for each notification that you must define
            notify(0, builder.build())
        }
    }
}
