package org.kruemelopment.de.bewecker

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val mNotificationManager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        mNotificationManager.cancel(intent.getStringExtra("schlummerid")!!.toInt())
        SchlafendeBaseHelper(context).deleteData(intent.getStringExtra("schlummerid"))
    }
}
