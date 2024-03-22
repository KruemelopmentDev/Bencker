package org.kruemelopment.de.bewecker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

open class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (SchlafendeBaseHelper(context).checkactiv(intent.getStringExtra("schlummerid"))) {
            val intent1 = Intent(context, AlertActivity::class.java)
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent1.setAction("start")
            intent1.putExtra("vol", intent.getIntExtra("vol", 0))
            intent1.putExtra("schlummerid", intent.getStringExtra("schlummerid"))
            intent1.putExtra("vol", intent.getIntExtra("vol", 0))
            intent1.putExtra("vib", intent.getIntExtra("vib", 0))
            intent1.putExtra("inc", intent.getIntExtra("inc", 0))
            intent1.putExtra("tit", intent.getStringExtra("tit"))
            intent1.putExtra("klin", intent.getStringExtra("klin"))
            intent1.putExtra("schlummerzeit", intent.getIntExtra("schlummerzeit", 0))
            intent1.putExtra("wartezeit", intent.getIntExtra("wartezeit", -1))
            intent1.putExtra("exitsleep", intent.getBooleanExtra("exitsleep", true))
            context.startActivity(intent1)
        }
    }
}
