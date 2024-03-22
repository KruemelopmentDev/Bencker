package org.kruemelopment.de.bewecker

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ActionReceiverAktive : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val mNotificationManager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        mNotificationManager.cancel(103)
        val myDB = DataBaseHelper(context)
        val res = myDB.allData
        if (res.count > 0) {
            while (res.moveToNext()) {
                myDB.updateAktiv(res.getString(0), 0)
            }
        }
        res.close()
    }
}
