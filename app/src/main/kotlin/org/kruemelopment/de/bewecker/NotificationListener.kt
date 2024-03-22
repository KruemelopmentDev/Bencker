package org.kruemelopment.de.bewecker

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager
import java.util.Calendar
import java.util.Locale

@SuppressLint("OverrideAbstract")
class NotificationListener : NotificationListenerService() {

    var context: Context? = null
    private var alarme:ArrayList<Alarmliste> = ArrayList()
    var myDB: DataBaseHelper? = null
    private var sensitive = false
    private var sbDB: SchlafendeBaseHelper? = null
    private var isActive=true

    override fun onCreate() {
        super.onCreate()
        myDB = DataBaseHelper(this)
        sbDB = SchlafendeBaseHelper(this)
        context = this
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateAlarme()
        updateActive()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateActive(){
        val prefs = applicationContext.getSharedPreferences("global", MODE_PRIVATE)
        isActive=prefs.getBoolean("isactive", true)
    }
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (alarme.isNotEmpty()&&isActive) {
            var title = sbn.notification.extras.getString("android.title")
            var text = sbn.notification.extras.getString("android.text")
            if(title==null||text==null) return
            val packageName = sbn.packageName
            if (!sensitive) {
                title = title.lowercase(Locale.getDefault())
                text = text.lowercase(Locale.getDefault())
            }
            checkalarm(
                title,
                text,
                packageName
            )
        }
    }

    private fun updateAlarme(){
        val sp2 = PreferenceManager.getDefaultSharedPreferences(this)
        sensitive = sp2.getBoolean("sensitive", false)
        val res = myDB!!.allData
        alarme.clear()
        if (res.count > 0) {
            while (res.moveToNext()) {
                if (res.getInt(4) == 1) alarme.add(
                    Alarmliste(
                        res.getString(0),
                        res.getString(1),
                        res.getString(2),
                        res.getString(3),
                        res.getInt(4),
                        res.getString(5),
                        res.getString(6),
                        res.getString(7),
                        res.getInt(8),
                        res.getInt(9),
                        res.getInt(10),
                        res.getString(11),
                        res.getString(12),
                        res.getInt(13),
                        res.getString(14),
                        res.getInt(15),
                        res.getInt(16),
                        res.getInt(17),
                        res.getString(18)
                    )
                )
            }
        }
        res.close()
    }

    private fun checkalarm(title: String, text: String, packageName: String) {
        for (alarmliste in alarme) {
            var nachricht = alarmliste.nachricht
            var absender = alarmliste.absender
            var gruppe = alarmliste.gruppe
            var app = alarmliste.packageName
            if (nachricht!!.contains("3deak8jfns}e4³[") || nachricht == "") nachricht = null
            if (absender!!.contains("3deak8jfns}e4³[") || absender == "") absender = null
            if (gruppe!!.contains("3deak8jfns}e4³[") || gruppe == "") gruppe = null
            if (app!!.contains("3deak8jfns}e4³[") || app == "") app = null
            if (!sensitive && nachricht != null) {
                nachricht = nachricht.lowercase(Locale.getDefault())
            }
            if (!sensitive && absender != null) {
                absender = absender.lowercase(Locale.getDefault())
            }
            if (!sensitive && gruppe != null) {
                gruppe = gruppe.lowercase(Locale.getDefault())
            }
            if (app == null && gruppe == null && absender == null && nachricht == null) return
            if (app != null && packageName != app) continue
            if (gruppe != null && !title.contains(gruppe)) continue
            if (absender != null && !title.contains(absender)) continue
            if (nachricht != null && !text.contains(nachricht)) continue
            startalarm(
                alarmliste.volume,
                alarmliste.vibrate,
                alarmliste.increase,
                alarmliste.name,
                alarmliste.schlummer,
                alarmliste.nwv,
                alarmliste.nwn,
                alarmliste.id,
                alarmliste.wdh,
                alarmliste.wartezeit,
                alarmliste.exitsleep == 1,
                text,
                alarmliste.absender,
                alarmliste.gruppe,
                alarmliste.packageName,
                alarmliste.nachricht,
                alarmliste.songuri
            )
        }
    }

    private fun startalarm(
        volume: Int,
        vibration: Int,
        increase: Int,
        title: String?,
        schlummerzeit: String?,
        nwv: String?,
        nwn: String?,
        id: String?,
        aktiviert: Int,
        wartezeit: Int,
        exitsleep: Boolean,
        text: String?,
        absender: String?,
        gruppe: String?,
        app: String?,
        nachricht: String?,
        songuri:String
    ) {
        if (text != null) if (!checkprio(id, text)) return
        val a: Int = if (nwv!!.contains("3deak8jfns}e4")) {
            -1
        } else if (nwv == "") {
            val sp2 = getSharedPreferences("Settings", 0)
            val help = sp2.getString("nwv", "")
            if (help.isNullOrEmpty()) -1 else help.split(":".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[0].toInt() * 60 + help.split(":".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[1].toInt()
        } else nwv.split(":".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[0].toInt() * 60 + nwv.split(":".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()[1].toInt()
        val b: Int = if (nwn!!.contains("3deak8jfns}e4")) {
            -1
        } else if (nwn == "") {
            val sp2 = getSharedPreferences("Settings", 0)
            val help = sp2.getString("nwn", "")
            if (help.isNullOrEmpty()) -1 else help.split(":".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[0].toInt() * 60 + help.split(":".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[1].toInt()
        } else nwn.split(":".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[0].toInt() * 60 + nwn.split(":".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()[1].toInt()
        val schlummern: Int = if (schlummerzeit!!.isEmpty()) {
            val sp2 = PreferenceManager.getDefaultSharedPreferences(
                applicationContext
            )
            val help = sp2.getString("schlummertime", "")
            if (help.isNullOrEmpty()) 300 else help.split(":".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[0].toInt() * 60 + help.split(":".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[1].toInt()
        } else schlummerzeit.split(":".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[0].toInt() * 60 + schlummerzeit.split(":".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()[1].toInt()
        val rightNow = Calendar.getInstance()
        val current = rightNow[Calendar.HOUR_OF_DAY] * 60 + rightNow[Calendar.MINUTE]
        val future =
            (schlummern + rightNow[Calendar.HOUR_OF_DAY] * 60 * 60 + rightNow[Calendar.MINUTE] * 60 + rightNow[Calendar.SECOND]).toLong()
        var stunde = (future / 60 / 60).toString()
        if (stunde.length == 1) stunde = "0$stunde"
        var minute = (future / 60 % 60).toString()
        if (minute.length == 1) minute = "0$minute"
        var sekunde = (future % 60).toString()
        if (sekunde.length == 1) sekunde = "0$sekunde"
        val bis = "$stunde:$minute:$sekunde"
        if(b!=-1&&current>b) return
        if(a!=-1&&current<a) return
        if (aktiviert == 0) {
            myDB!!.updateAktiv(id, 0)
            updateNotifications()
        }
        sbDB!!.insertData(title, absender, gruppe, app, nachricht, bis).toString()
        val i = Intent(this, AlertActivity::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        i.setAction("start")
        i.putExtra("schlummerid", sbDB!!.getlastid())
        i.putExtra("vol", volume)
        i.putExtra("vib", vibration)
        i.putExtra("inc", increase)
        i.putExtra("tit", title)
        i.putExtra("klin", songuri)
        i.putExtra("schlummerzeit", schlummern)
        i.putExtra("exitsleep", exitsleep)
        i.putExtra("wartezeit", wartezeit)
        startActivity(i)
    }

    private fun updateNotifications() {
        val sp2 = PreferenceManager.getDefaultSharedPreferences(this)
        if (!sp2.getBoolean("statusbaricon", false)) return
        var b = 0
        for (a in alarme) if (a.wdh == 0) b++
        if (b == 0) {
            val notificationManager =
                context!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(103)
        } else {
            val text: String = if (b > 1) "Es sind aktuell $b Bencker aktiv" else "Es ist aktuell ein Bencker aktiv"
            val logo = R.drawable.logow
            val notificationIntent = Intent(this, MainActivity::class.java)
            notificationIntent.setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        or Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
            val intent = PendingIntent.getActivity(
                this, 0,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE
            )
            val action = Intent(context, ActionReceiverAktive::class.java)
            val actionIntent =
                PendingIntent.getBroadcast(this, 0, action, PendingIntent.FLAG_UPDATE_CURRENT)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("1", "Bencker", importance)
            mChannel.setSound(null, null)
            val mNotificationManager =
                context!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.createNotificationChannel(mChannel)
            val naction = NotificationCompat.Action(0, getString(R.string.finishall), actionIntent)
            val notification = NotificationCompat.Builder(context!!, "1")
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentText(text)
                .setSmallIcon(logo)
                .addAction(naction)
                .setColor(ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, theme))
                .setContentTitle("Bencker")
                .setContentIntent(intent)
                .build()
            mNotificationManager.notify(103, notification)
        }
    }

    private fun checkprio(id: String?, text: String): Boolean {
        val sp2 = PreferenceManager.getDefaultSharedPreferences(this)
        sensitive = sp2.getBoolean("sensitive", false)
        if (myDB!!.getPrio(id) <= 0) return true
        if (myDB!!.getPrio(id) > PrioDataBaseHelper(applicationContext).lastlevel()) return true
        val cur = PrioDataBaseHelper(applicationContext).getPriosbelow((myDB!!.getPrio(id) - 1).toString())
        if (!cur.moveToFirst()) {
            return true
        }
        val help = ArrayList<String>()
        if (cur.count > 0) {
            if (cur.getString(2).contains(";")) {
                help.addAll(
                    listOf(
                        *cur.getString(2).split(";".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()))
            } else help.add(cur.getString(2))
            while (cur.moveToNext()) {
                if (cur.getString(2).contains(";")) {
                    help.addAll(
                        listOf(
                            *cur.getString(2).split(";".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()))
                } else help.add(cur.getString(2))
            }
        } else return true
        if (!sensitive) {
            for (b in help) {
                if (text.lowercase(Locale.getDefault())
                        .contains(b.lowercase(Locale.getDefault()))
                ) {
                    return true
                }
            }
        } else {
            for (b in help) {
                if (text.contains(b)) {
                    return true
                }
            }
        }
        return false
    }
}