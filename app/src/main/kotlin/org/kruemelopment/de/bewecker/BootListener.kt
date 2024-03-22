package org.kruemelopment.de.bewecker

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService

class BootListener : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action!! == "android.intent.action.BOOT_COMPLETED") {
            NotificationListenerService.requestRebind(
                ComponentName(
                    context,
                    NotificationListener::class.java
                )
            )
        }
    }
}