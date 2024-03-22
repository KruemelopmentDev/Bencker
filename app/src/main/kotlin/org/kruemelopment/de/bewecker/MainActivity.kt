package org.kruemelopment.de.bewecker

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Dialog
import android.content.ComponentName
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.service.notification.NotificationListenerService
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.rey.material.widget.SnackBar

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    NewAlarm.IProcessFilter {

    private var drawer: DrawerLayout? = null
    private var transaction: FragmentTransaction? = null
    private var oldid = R.id.allbewecker
    private var lastitem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val lol = PreferenceManager.getDefaultSharedPreferences(this)
            val nacht = lol.getBoolean("darkmode", false)
            if (nacht) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) else AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
        }
        super.onCreate(savedInstanceState)
        callback = this
        setContentView(R.layout.activity_main2)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer!!.addDrawerListener(toggle)
        toggle.syncState()
        navigationView!!.setNavigationItemSelectedListener(this)
        lastitem = navigationView!!.checkedItem
        transaction = supportFragmentManager.beginTransaction()
        transaction!!.replace(R.id.frame_layout, AllAlarms())
        transaction!!.commit()
        supportActionBar!!.title = "Alle Bencker"
        enableNotificationListenerService()
        val sp = getSharedPreferences("Settings", 0)
        val start = sp.getBoolean("firststart", true)
        var second = sp.getBoolean("secondstart", true)
        if (start) {
            second = false
            val ede = sp.edit()
            ede.putBoolean("secondstart", false)
            ede.apply()
            val dialog = Dialog(this, R.style.Dialog)
            dialog.setContentView(R.layout.webdialog)
            val ja = dialog.findViewById<TextView>(R.id.textView5)
            val nein = dialog.findViewById<TextView>(R.id.textView8)
            ja.setOnClickListener {
                dialog.dismiss()
                startActivityForResult(
                    Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"),
                    2001
                )
                val ed = sp.edit()
                ed.putBoolean("firststart", false)
                ed.apply()
            }
            nein.setOnClickListener {
                finishAndRemoveTask()
            }
            val textView = dialog.findViewById<TextView>(R.id.textView4)
            textView.text = Html.fromHtml(
                "Mit der Nutzung dieser App aktzeptiere ich die " +
                        "<a href=\"https://www.kruemelopment-dev.de/datenschutzerklaerung\">Datenschutzerklärung</a>" + " und die " + "<a href=\"https://www.kruemelopment-dev.de/nutzungsbedingungen\">Nutzungsbedingungen</a>" + " von Krümelopment Dev",Html.FROM_HTML_MODE_LEGACY
            )
            textView.movementMethod = LinkMovementMethod.getInstance()
            dialog.setCancelable(false)
            dialog.show()
            val sp2 = PreferenceManager.getDefaultSharedPreferences(this)
            showtipp = sp2.getBoolean("reminder", true)
            notificationsmake = sp2.getBoolean("statusbaricon", false)
        }
        if (second) {
            deleteDatabase("Alarms.db")
            val ede = sp.edit()
            ede.putBoolean("secondstart", false)
            ede.apply()
        }
        val v = layoutInflater.inflate(R.layout.nav_header_main, navigationView, false)
        val imageView = v.findViewById<ImageView>(R.id.imageView)
        val params = imageView.layoutParams as RelativeLayout.LayoutParams
        params.setMargins(0, statusBarHeight, 0, 0)
        imageView.layoutParams = params
        navigationView!!.addHeaderView(v)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val view = window.decorView.findViewById<View>(android.R.id.content)
            view.addOnLayoutChangeListener(object : OnLayoutChangeListener {
                override fun onLayoutChange(
                    v: View,
                    left: Int,
                    top: Int,
                    right: Int,
                    bottom: Int,
                    oldLeft: Int,
                    oldTop: Int,
                    oldRight: Int,
                    oldBottom: Int
                ) {
                    val rects: MutableList<Rect> = ArrayList()
                    val rect = Rect(0, 0, right / 2, bottom)
                    rects.add(rect)
                    v.systemGestureExclusionRects = rects
                    v.removeOnLayoutChangeListener(this)
                }
            })
        }
        val intent = Intent()
        val packageName = packageName
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            val snackBar = SnackBar(this)
            snackBar.padding(15, 15)
            snackBar.backgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.colorPrimary,
                    theme
                )
            )
            snackBar.textColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.white,
                    theme
                )
            )
            snackBar.actionText("Einstellungen öffnen")
            snackBar.actionClickListener { sb, _ ->
                sb.dismiss()
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.setData(Uri.parse("package:$packageName"))
                startActivity(intent)
            }
            snackBar.text("Es ist ratsam, die Akkuoptimierung für die App auszuschalten.")
            snackBar.singleLine(false)
            snackBar.duration(8000)
            if (showtipp) {
                snackBar.show(this)
            }
        }
    }

    override fun onBackPressed() {
        if (drawer!!.isDrawerOpen(GravityCompat.START)) {
            drawer!!.closeDrawer(GravityCompat.START)
        } else {
            if (oldid == R.id.newbewecker) {
                AlertDialog.Builder(this)
                    .setPositiveButton(getString(R.string.leave)) { dialog, _ ->
                        dialog.dismiss()
                        oldid = R.id.allbewecker
                        transaction = supportFragmentManager.beginTransaction()
                        transaction!!.replace(R.id.frame_layout, AllAlarms())
                        transaction!!.commit()
                        navigationView!!.setCheckedItem(R.id.allbewecker)
                        supportActionBar!!.title = "Alle Bencker"
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                    .setTitle(getString(R.string.delete_alarm))
                    .setMessage(getString(R.string.go_back_new))
                    .setCancelable(true)
                    .show()
            } else if (oldid != R.id.allbewecker) {
                oldid = R.id.allbewecker
                transaction = supportFragmentManager.beginTransaction()
                transaction!!.replace(R.id.frame_layout, AllAlarms())
                transaction!!.commit()
                navigationView!!.setCheckedItem(R.id.allbewecker)
                supportActionBar!!.title = "Alle Bencker"
            } else super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.newbewecker && oldid != R.id.newbewecker) {
            transaction = supportFragmentManager.beginTransaction()
            transaction!!.replace(R.id.frame_layout, NewAlarm())
            transaction!!.commit()
            lastitem!!.setChecked(false)
            item.setChecked(true)
            supportActionBar!!.title = "Neuer Bencker"
        } else if (id == R.id.allbewecker && oldid != R.id.allbewecker) {
            transaction = supportFragmentManager.beginTransaction()
            transaction!!.replace(R.id.frame_layout, AllAlarms())
            transaction!!.commit()
            lastitem!!.setChecked(false)
            item.setChecked(true)
            supportActionBar!!.title =
                "Alle Bencker"
        } else if (id == R.id.nutz) {
            val uri = Uri.parse("https://www.kruemelopment-dev.de/nutzungsbedingungen")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } else if (id == R.id.daten) {
            val uri = Uri.parse("https://www.kruemelopment-dev.de/datenschutzerklaerung")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } else if (id == R.id.settings && oldid != R.id.settings) {
            transaction = supportFragmentManager.beginTransaction()
            transaction!!.replace(R.id.frame_layout, SettingsFragment())
            transaction!!.commit()
            lastitem!!.setChecked(false)
            item.setChecked(true)
            supportActionBar!!.title =
                "Einstellungen"
        } else if (id == R.id.feed) {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.setData(Uri.parse("mailto:kontakt@kruemelopment-dev.de"))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else if (id == R.id.sleeping && oldid != R.id.sleeping) {
            transaction = supportFragmentManager.beginTransaction()
            transaction!!.replace(R.id.frame_layout, Schlafende())
            transaction!!.commit()
            lastitem!!.setChecked(false)
            item.setChecked(true)
            supportActionBar!!.title = "Schlummernde Bencker"
        } else if (id == R.id.priorities && oldid != R.id.priorities) {
            transaction = supportFragmentManager.beginTransaction()
            transaction!!.replace(R.id.frame_layout, Importance())
            transaction!!.commit()
            lastitem!!.setChecked(false)
            item.setChecked(true)
            supportActionBar!!.title = "Prioritäten"
        } else if (id == R.id.sources && oldid != R.id.sources) {
            transaction = supportFragmentManager.beginTransaction()
            transaction!!.replace(R.id.frame_layout, Libraries())
            transaction!!.commit()
            lastitem!!.setChecked(false)
            item.setChecked(true)
            supportActionBar!!.title =
                "Open-Source-Bibliotheken"
        }
        oldid = id
        drawer!!.closeDrawer(GravityCompat.START)
        return true
    }

    private fun enableNotificationListenerService() {
        val intent=Intent(this,NotificationListener::class.java)
        startService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        enableNotificationListenerService()
    }

    private val statusBarHeight: Int
        get() {
            var result = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

    override fun safedtransfer() {
        transaction = supportFragmentManager.beginTransaction()
        transaction!!.replace(R.id.frame_layout, AllAlarms())
        transaction!!.commit()
        lastitem!!.setChecked(false)
        oldid = R.id.allbewecker
        navigationView!!.setCheckedItem(R.id.allbewecker)
        supportActionBar!!.title = "Alle Bencker"
        val snackBar = SnackBar(this)
        snackBar.padding(15, 15)
        snackBar.backgroundColor(ResourcesCompat.getColor(resources,R.color.colorPrimary,theme))
        snackBar.textColor(ResourcesCompat.getColor(resources,R.color.white,theme))
        snackBar.actionText("Einstellungen öffnen")
        snackBar.actionClickListener { sb, _ ->
            sb.dismiss()
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }
        snackBar.text(getString(R.string.read_notifications))
        snackBar.singleLine(false)
        snackBar.duration(8000)
        if (showtipp) {
            snackBar.show(this)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2001) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse(
                        "package:$packageName"
                    )
                )
                startActivityForResult(intent,2002)
            }
            else {
                val am = getSystemService(ALARM_SERVICE) as AlarmManager
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S&&!am.canScheduleExactAlarms()){
                    val intent = Intent(
                        ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse(
                            "package:$packageName"
                        )
                    )
                    startActivity(intent)
                }
            }
        }
        else if (requestCode==2002){
            val am = getSystemService(ALARM_SERVICE) as AlarmManager
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S&&!am.canScheduleExactAlarms()){
                val intent = Intent(
                    ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse(
                        "package:$packageName"
                    )
                )
                startActivity(intent)
            }

        }
        if (resultCode != RESULT_OK) return
        if (requestCode == 1 || requestCode == 2) {
            val fragment = supportFragmentManager.fragments[0]
            if (fragment is AllAlarms) {
                fragment.update(requestCode)
            }
        }
    }

    companion object {
        var notificationsmake = false
        var showtipp = false
        var navigationView: NavigationView? = null
        var callback: NewAlarm.IProcessFilter? = null
    }
}
