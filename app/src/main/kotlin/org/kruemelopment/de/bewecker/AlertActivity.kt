package org.kruemelopment.de.bewecker

import android.app.Activity
import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View.OnClickListener
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AlertActivity : Activity(), OnAudioFocusChangeListener {

    private var vibrator: Vibrator? = null
    var mediaPlayer: MediaPlayer? = null
    private var oldvolume = 0
    var endvolume = 0
    var currentvolume = 0
    var volumeincrease = 0
    private var wartezeit = 0
    private var exitsleep = false
    private val mHandler = Handler(Looper.getMainLooper())
    private var schlummerid: String? = null
    private var sDB: SchlafendeBaseHelper? = null
    private var leise = false
    private var mLostAudioFocus = false
    private var audioFocusRequest:AudioFocusRequest?=null
    private var alreadyfinished=false


    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        }
        else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        if (intent.getIntExtra("vol", 0) > -1) createsound(
            intent.getIntExtra("vol", 0), intent.getIntExtra("inc", 0), intent.getStringExtra("klin")
        )
        if (intent.getIntExtra("vib", 0) > 0) createvibration(intent.getIntExtra("vib", 30))
        exitsleep = intent.getBooleanExtra("exitsleep", true)
        wartezeit = intent.getIntExtra("wartezeit", -1)
        schlummerid = intent.getStringExtra("schlummerid")
        setContentView(R.layout.activity_alertscreen)
        sDB = SchlafendeBaseHelper(this)
        val schlummer = findViewById<RelativeLayout>(R.id.layout2)
        val beenden = findViewById<RelativeLayout>(R.id.layout1)
        val alarm = findViewById<TextView>(R.id.textView13)
        val uhrzeit = findViewById<TextView>(R.id.textView14)
        alarm.text = intent.getStringExtra("tit")
        val sdf = SimpleDateFormat("HH:mm", Locale.GERMANY)
        uhrzeit.text = sdf.format(Calendar.getInstance().time)
        schlummer.setOnClickListener {
            schlummern(
                intent.getIntExtra("vol", 0),
                intent.getIntExtra("vib", 0),
                intent.getIntExtra("inc", 0),
                intent.getStringExtra("tit"),
                intent.getStringExtra("klin"),
                intent.getIntExtra("schlummerzeit", 0)
            )
        }
        beenden.setOnClickListener { stopAlarm() }
        if (wartezeit > 0) {
            mHandler.postDelayed(warten, wartezeit * 1000L)
        }
        val layout=findViewById<ConstraintLayout>(R.id.alertbackground)
        val am = getSystemService(ALARM_SERVICE) as AlarmManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S&&!am.canScheduleExactAlarms()) {
            layout.setBackgroundResource(R.drawable.alertbackground2)
            schlummer.setOnClickListener { OnClickListener {

            } }
        }
    }

    private val warten = Runnable {
        if (exitsleep) {
            schlummern(
                intent.getIntExtra("vol", 0),
                intent.getIntExtra("vib", 0),
                intent.getIntExtra("inc", 0),
                intent.getStringExtra("tit"),
                intent.getStringExtra("klin"),
                intent.getIntExtra("schlummerzeit", 0)
            )
        } else {
            if (mediaPlayer != null) if (mediaPlayer!!.isPlaying) mediaPlayer!!.stop()
            if (vibrator != null) vibrator!!.cancel()
            finish()
        }
    }

    override fun onUserLeaveHint() {
        if(!alreadyfinished)stopAlarm()
        super.onUserLeaveHint()
    }

    public override fun onPause() {
        if(!alreadyfinished)stopAlarm()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!alreadyfinished)stopAlarm()
    }

    private fun stopAlarm() {
        if (sDB!!.checkactiv(schlummerid)) sDB!!.deleteData(schlummerid)
        val sp2 = PreferenceManager.getDefaultSharedPreferences(this)
        if (sp2.getBoolean("schlummerbaricon", false)) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(schlummerid!!.toInt())
        }
        mHandler.removeCallbacks(mVolumeRunnable)
        mHandler.removeCallbacks(warten)
        val am = (getSystemService(AUDIO_SERVICE) as AudioManager)
        if(audioFocusRequest!=null) am.abandonAudioFocusRequest(audioFocusRequest!!)
        am.setStreamVolume(AudioManager.STREAM_MUSIC, oldvolume, 0)
        try {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
        } catch (ignored: Exception) {
        }
        try {
            vibrator!!.cancel()
        } catch (ignored: Exception) {
        }
        finish()
    }

    private fun schlummern(
        volume: Int,
        vibration: Int,
        increase: Int,
        title: String?,
        songid: String?,
        schlummern: Int
    ) {
        alreadyfinished=true
        var schlummern2=schlummern
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("vol", volume)
        intent.putExtra("vib", vibration)
        intent.putExtra("inc", increase)
        intent.putExtra("tit", title)
        intent.putExtra("klin", songid.toString())
        intent.putExtra("schlummerid", schlummerid)
        intent.putExtra("wartezeit", intent.getIntExtra("wartezeit", -1))
        intent.putExtra("exitsleep", intent.getBooleanExtra("exitsleep", true))
        if (schlummern2 <= 0) {
            val sp2 = PreferenceManager.getDefaultSharedPreferences(this)
            val help = sp2.getString("schlummertime", "")
            schlummern2 = if (!help.isNullOrEmpty()) help.split(":".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[0].toInt() * 60 + help.split(":".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[1].toInt() else 300
        }
        intent.putExtra("schlummerzeit", schlummern2)
        makenotification(title, schlummerid, schlummern2)
        val sender = PendingIntent.getBroadcast(
            this,
            schlummerid!!.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val am = getSystemService(ALARM_SERVICE) as AlarmManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S&&am.canScheduleExactAlarms()) {
            am.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + schlummern2 * 1000L,
                sender
            )
        }
        else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            am.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + schlummern2 * 1000L,
                sender
            )
        }
        mHandler.removeCallbacks(mVolumeRunnable)
        mHandler.removeCallbacks(warten)
        val ama = (getSystemService(AUDIO_SERVICE) as AudioManager)
        if(audioFocusRequest!=null) ama.abandonAudioFocusRequest(audioFocusRequest!!)
        ama.setStreamVolume(AudioManager.STREAM_MUSIC, oldvolume, 0)
        try {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
        } catch (ignored: Exception) {
        }
        try {
            vibrator!!.cancel()
        } catch (ignored: Exception) {
        }
        finish()
    }

    private val mVolumeRunnable: Runnable = object : Runnable {
        override fun run() {
            if (mediaPlayer != null && currentvolume + volumeincrease <= endvolume) {
                currentvolume += volumeincrease
                mediaPlayer!!.setVolume(currentvolume / 100f, currentvolume / 100f)
                mHandler.postDelayed(this, 1000)
            } else mHandler.removeCallbacks(this)
        }
    }

    private fun createsound(volume: Int, increase: Int, songuri: String?) {
        var songuri2 = songuri
        if (songuri2.isNullOrEmpty() || songuri2 == "-") {
            val sp8 = getSharedPreferences("Settings", 0)
            songuri2 = sp8.getString("ringtonedefault", "-")
        }
        if (songuri2!!.startsWith("deaktiviert:") || songuri2 == "-") return
        if (volume < 0) return
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        mediaPlayer!!.isLooping = true
        try {
            val parcelFd = contentResolver.openFileDescriptor(Uri.parse(songuri2), "r")!!
            mediaPlayer!!.setDataSource(parcelFd.fileDescriptor)
            mediaPlayer!!.prepare()
            parcelFd.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val am = getSystemService(AUDIO_SERVICE) as AudioManager
        oldvolume = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        am.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
            0
        )
        if (increase <= 0) {
            mediaPlayer!!.setVolume(volume.toFloat(), volume.toFloat())
            mediaPlayer!!.start()
        }
        if (increase > 0) {
            volumeincrease = volume / increase
            mediaPlayer!!.setVolume(volumeincrease / 100f, volumeincrease / 100f)
            endvolume = volume
            currentvolume = volumeincrease
            mediaPlayer!!.start()
            mHandler.postDelayed(mVolumeRunnable, 1000)
        }
        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setOnAudioFocusChangeListener(this)
            .build()
        if(audioFocusRequest!=null) am.requestAudioFocus(audioFocusRequest!!)
    }

    private fun createvibration(vibration: Int) {
        if (vibration < 0) return
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(0, vibration.toLong())
        assert(vibrator != null)
        if (vibrator!!.hasAmplitudeControl()) {
            val effect =
                VibrationEffect.createOneShot(vibration.toLong(), VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator!!.vibrate(effect)
        } else {
            vibrator!!.vibrate(pattern, 0)
        }
    }

    private fun makenotification(title: String?, id: String?, schlummern: Int) {
        val sp2 = PreferenceManager.getDefaultSharedPreferences(this)
        val future =
            (schlummern + Calendar.getInstance()[Calendar.HOUR_OF_DAY] * 60 * 60 + Calendar.getInstance()[Calendar.MINUTE] * 60 + Calendar.getInstance()[Calendar.SECOND]).toLong()
        var stunde = (future / 60 / 60).toString()
        if (stunde.length == 1) stunde = "0$stunde"
        var minute = (future / 60 % 60).toString()
        if (minute.length == 1) minute = "0$minute"
        var sekunde = (future % 60).toString()
        if (sekunde.length == 1) sekunde = "0$sekunde"
        val bis = "$stunde:$minute:$sekunde"
        val text = "Bencker schlummert bis $bis"
        sDB!!.updatezeit(bis, id)
        if (!sp2.getBoolean("schlummerbaricon", false)) return
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
        val action = Intent(this, ActionReceiver::class.java)
        action.putExtra("schlummerid", id)
        val actionIntent = PendingIntent.getBroadcast(
            this,
            schlummerid!!.toInt(),
            action,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val importance = NotificationManager.IMPORTANCE_LOW
        val mChannel = NotificationChannel(id, "Bencker", importance)
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.createNotificationChannel(mChannel)
        val naction: NotificationCompat.Action =
            NotificationCompat.Action.Builder(0, getString(R.string.finish), actionIntent)
                .build()
        val notification = NotificationCompat.Builder(this, id!!)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentText(text)
            .setSmallIcon(logo)
            .setColor(ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, theme))
            .addAction(naction)
            .setContentTitle("$title schlummert")
            .setContentIntent(intent)
            .build()
        mNotificationManager.notify(id.toInt(), notification)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> mediaPlayer!!.pause()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.setVolume(0.3f, 0.3f)
                leise = true
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
                mLostAudioFocus = true
            }

            AudioManager.AUDIOFOCUS_GAIN -> if (leise) {
                mediaPlayer!!.setVolume(1.0f, 1.0f)
                leise = false
            } else if (mLostAudioFocus) {
                if (!mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.start()
                }
                mLostAudioFocus = false
            }
        }
    }
}
