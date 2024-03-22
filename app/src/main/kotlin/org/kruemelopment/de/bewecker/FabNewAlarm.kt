package org.kruemelopment.de.bewecker

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rengwuxian.materialedittext.MaterialEditText
import com.rey.material.widget.CheckBox
import com.rey.material.widget.ImageView
import com.rey.material.widget.Slider
import com.rey.material.widget.Spinner
import es.dmoral.toasty.Toasty
import java.text.Collator
import java.util.Calendar
import java.util.Locale
import java.util.Objects

class FabNewAlarm : AppCompatActivity() {
    private var name1 = ""
    private var nachricht1 = ""
    private var absender1 = ""
    private var gruppe1 = ""
    private var app1: String? = ""
    private var vibration1 = -1
    private var volume1 = -1
    private var increase1 = -1
    private var songuri: String? = null
    private var nwv1 = ""
    private var nwn1 = ""
    private var aktiviert1 = 0
    private var schlummerzeit1 = ""
    private var wartezeit1 = 0
    private var importance1 = 0
    private var songtitel1: String? = ""
    private var klingeltontext: MaterialEditText? = null
    private var rese: MutableList<AppInfo> = ArrayList()
    var app: MaterialEditText? = null
    var name: MaterialEditText? = null
    var nachricht: MaterialEditText? = null
    var absender: MaterialEditText? = null
    var gruppe: MaterialEditText? = null
    private var vibration: Slider? = null
    private var volume: Slider? = null
    private var increase: Slider? = null
    private var wartezeit: Slider? = null
    var importance: Slider? = null
    private var nwv: MaterialEditText? = null
    private var nwn: MaterialEditText? = null
    private var aktiviert: Spinner? = null
    private var schlummerzeit: MaterialEditText? = null
    private var checkBox2: CheckBox? = null
    private var checkBox3: CheckBox? = null
    private var checkBox4: CheckBox? = null
    private var checkBox5: CheckBox? = null
    private var checkBox6: CheckBox? = null
    private var checkBox7: CheckBox? = null
    private var checkBox8: CheckBox? = null
    private var checkBox9: CheckBox? = null
    private var checkBox10: CheckBox? = null
    private var checkBox11: CheckBox? = null
    private var checkBox12: CheckBox? = null
    private var checkBox13: CheckBox? = null
    private var checkBox14: CheckBox? = null
    private var safe1 = true
    private var safe2 = true
    private var permission=Manifest.permission.READ_EXTERNAL_STORAGE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT>32) permission=Manifest.permission.READ_MEDIA_AUDIO
        setTheme(R.style.AppTheme2)
        setContentView(R.layout.activity_new_alarm)
        loadapps()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = getString(R.string.new_alarm)
        name = findViewById(R.id.editText1)
        nachricht = findViewById(R.id.editText2)
        absender = findViewById(R.id.editText3)
        gruppe = findViewById(R.id.editText4)
        app = findViewById(R.id.editText5)
        vibration = findViewById(R.id.slider1)
        volume = findViewById(R.id.slider2)
        increase = findViewById(R.id.slider3)
        nwv = findViewById(R.id.editText7)
        nwn = findViewById(R.id.editText8)
        aktiviert = findViewById(R.id.spinner)
        wartezeit = findViewById(R.id.slider4)
        importance = findViewById(R.id.slider5)
        checkBox14 = findViewById(R.id.checkbox14)
        val textView = findViewById<TextView>(R.id.textView6)
        val prios = PrioDataBaseHelper(this).lastlevel()
        if (prios > 0) {
            importance!!.setValueRange(0, prios, false)
        } else {
            importance!!.visibility = View.GONE
            textView.visibility = View.GONE
            checkBox14!!.visibility = View.GONE
        }
        val set = arrayOfNulls<String>(2)
        set[0] = getString(R.string.onetime)
        set[1] = getString(R.string.always)
        val spinnerArrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, set
        )
        spinnerArrayAdapter.setDropDownViewResource(R.layout.adapterbackground)
        aktiviert!!.adapter = spinnerArrayAdapter
        aktiviert!!.setSelection(1)
        schlummerzeit = findViewById(R.id.editText9)
        checkBox2 = findViewById(R.id.checkbox2)
        checkBox3 = findViewById(R.id.checkbox3)
        checkBox4 = findViewById(R.id.checkbox4)
        checkBox5 = findViewById(R.id.checkbox5)
        checkBox6 = findViewById(R.id.checkbox6)
        checkBox7 = findViewById(R.id.checkbox7)
        checkBox8 = findViewById(R.id.checkbox8)
        checkBox9 = findViewById(R.id.checkbox9)
        checkBox10 = findViewById(R.id.checkbox10)
        checkBox11 = findViewById(R.id.checkbox11)
        checkBox12 = findViewById(R.id.checkbox12)
        checkBox13 = findViewById(R.id.checkbox13)
        klingeltontext = findViewById(R.id.editText6)
        klingeltontext!!.setOnClickListener {
            val result = ContextCompat.checkSelfPermission(
                this@FabNewAlarm,
                permission
            )
            if (result == PackageManager.PERMISSION_GRANTED) {
                val data = Intent(Intent.ACTION_OPEN_DOCUMENT)
                data.addCategory(Intent.CATEGORY_OPENABLE)
                data.setType("audio/*")
                val intent = Intent.createChooser(data, "Klingelton auswählen")
                startActivityForResult(intent, 34)
            } else {
                ActivityCompat.requestPermissions(
                    this@FabNewAlarm,
                    arrayOf(permission),
                    101
                )
            }
        }
        app!!.isClickable = false
        app!!.setOnClickListener { pickapp() }
        checkBox2!!.setOnCheckedChangeListener { _, isChecked ->
            nachricht!!.isFocusableInTouchMode = isChecked
        }
        checkBox3!!.setOnCheckedChangeListener { _, isChecked ->
            absender!!.isFocusableInTouchMode = isChecked
        }
        checkBox4!!.setOnCheckedChangeListener { _, isChecked ->
            gruppe!!.isFocusableInTouchMode = isChecked
        }
        checkBox9!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                songuri = songuri!!.replace("deaktiviert:", "")
                volume!!.isFocusableInTouchMode = true
                increase!!.isFocusableInTouchMode = true
                checkBox7!!.isClickable = true
                checkBox8!!.isClickable = true
                checkBox8!!.isChecked = safe2
                checkBox7!!.isChecked = safe1
            } else {
                songuri = "deaktiviert:" + songuri!!.replace("deaktiviert:", "")
                volume!!.isFocusableInTouchMode = false
                increase!!.isFocusableInTouchMode = false
                safe1 = checkBox7!!.isChecked
                safe2 = checkBox8!!.isChecked
                checkBox7!!.isChecked = false
                checkBox8!!.isChecked = false
                checkBox7!!.isClickable = false
                checkBox8!!.isClickable = false
            }
        }
        checkBox6!!.setOnCheckedChangeListener { _, isChecked ->
            vibration!!.isFocusableInTouchMode = isChecked
        }
        checkBox7!!.setOnCheckedChangeListener { _, isChecked ->
            volume!!.isFocusableInTouchMode = isChecked
        }
        checkBox8!!.setOnCheckedChangeListener { _, isChecked ->
            increase!!.isFocusableInTouchMode = isChecked
        }
        nwv!!.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker =
                TimePickerDialog(this@FabNewAlarm, { _, selectedHour, selectedMinute ->
                    val a: String = if (selectedHour < 10 && selectedMinute < 10) {
                        "0$selectedHour:0$selectedMinute"
                    } else if (selectedHour < 10) {
                        "0$selectedHour:$selectedMinute"
                    } else if (selectedMinute < 10) {
                        "$selectedHour:0$selectedMinute"
                    } else "$selectedHour:$selectedMinute"
                    nwv!!.setText(a)
                }, hour, minute, true)
            mTimePicker.setTitle(getString(R.string.timepick))
            mTimePicker.show()
        }
        nwn!!.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker =
                TimePickerDialog(this@FabNewAlarm, { _, selectedHour, selectedMinute ->
                    val a: String = if (selectedHour < 10 && selectedMinute < 10) {
                        "0$selectedHour:0$selectedMinute"
                    } else if (selectedHour < 10) {
                        "0$selectedHour:$selectedMinute"
                    } else if (selectedMinute < 10) {
                        "$selectedHour:0$selectedMinute"
                    } else "$selectedHour:$selectedMinute"
                    nwn!!.setText(a)
                }, hour, minute, true)
            mTimePicker.setTitle(getString(R.string.timepick))
            mTimePicker.show()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 34 && resultCode == RESULT_OK) {
            if (data != null && data.data != null) {
                val songurid = data.data
                songuri = data.data.toString()
                val cursor = contentResolver.query(
                    songurid!!,
                    arrayOf(OpenableColumns.DISPLAY_NAME),
                    null,
                    null,
                    null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    val titleCol = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    songtitel1 = cursor.getString(titleCol)
                    cursor.close()
                }
                if (songtitel1 != null) {
                    klingeltontext!!.setText(songtitel1)
                }
                contentResolver.takePersistableUriPermission(
                    songurid,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val data = Intent(Intent.ACTION_OPEN_DOCUMENT)
                data.addCategory(Intent.CATEGORY_OPENABLE)
                data.setType("audio/*")
                val intent = Intent.createChooser(data, "Klingelton auswählen")
                startActivityForResult(intent, 34)
            } else {
                Toasty.error(this@FabNewAlarm, getString(R.string.permission_denied)).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) // Press Back Icon
        {
            this@FabNewAlarm.setResult(RESULT_CANCELED)
            finish()
        } else {
            speichern()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun pickapp() {
        val dialog = Dialog(this@FabNewAlarm, R.style.Dialog)
        dialog.setContentView(R.layout.appdialog)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.listeapps)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(100)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.recycleChildrenOnDetach = true
        recyclerView.layoutManager = layoutManager
        val appadapter = Appadapter(this@FabNewAlarm, rese, dialog)
        recyclerView.adapter = appadapter
        val searchtext = dialog.findViewById<MaterialEditText>(R.id.searchbox)
        searchtext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                appadapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable) {
                appadapter.filter(s.toString())
            }
        })
        dialog.setCancelable(true)
        dialog.setOnCancelListener { appadapter.filter("") }
        dialog.setOnDismissListener {
            app1 = rese[0].pname
            app!!.setText(rese[0].appname)
            appadapter.filter("")
        }
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            val suche = dialog.findViewById<ImageView>(R.id.suchbild)
            suche.setImageResource(R.drawable.search2)
        }
        dialog.show()
    }

    fun loadapps() {
        val th = Thread {
            val packs = packageManager.getInstalledPackages(0)
            for (i in packs.indices) {
                val p = packs[i]
                if (packageManager.getLaunchIntentForPackage(p.packageName) != null) {
                    val appName = p.applicationInfo.loadLabel(packageManager).toString()
                    val icon = p.applicationInfo.loadIcon(packageManager)
                    rese.add(AppInfo(appName, p.packageName, icon))
                }
            }
            rese.sortWith { o1, o2 ->
                val locale = Locale.getDefault()
                val collator = Collator.getInstance(locale)
                collator.strength = Collator.SECONDARY
                collator.compare(
                    o1.appname.lowercase(Locale.getDefault()), o2.appname.lowercase(
                        Locale.getDefault()
                    )
                )
            }
            app!!.isClickable = true
        }
        th.start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun speichern() {
        if (save()) {
            name1 = Objects.requireNonNull(name!!.text).toString()
            nachricht1 = if (checkBox2!!.isChecked) {
                Objects.requireNonNull(nachricht!!.text).toString()
            } else Objects.requireNonNull(nachricht!!.text).toString() + "3deak8jfns}e4³["
            absender1 = if (checkBox3!!.isChecked) {
                Objects.requireNonNull(absender!!.text).toString()
            } else Objects.requireNonNull(absender!!.text).toString() + "3deak8jfns}e4³["
            gruppe1 = if (checkBox4!!.isChecked) {
                Objects.requireNonNull(gruppe!!.text).toString()
            } else Objects.requireNonNull(gruppe!!.text).toString() + "3deak8jfns}e4³["
            if (!checkBox5!!.isChecked) {
                app1 += "3deak8jfns}e4³["
            }
            vibration1 = if (checkBox6!!.isChecked) {
                vibration!!.value
            } else vibration!!.value * -1
            volume1 = if (checkBox7!!.isChecked) {
                volume!!.value
            } else volume!!.value * -1
            increase1 = if (checkBox8!!.isChecked) {
                increase!!.value
            } else increase!!.value * -1
            nwv1 = if (checkBox10!!.isChecked) Objects.requireNonNull(nwv!!.text)
                .toString() else Objects.requireNonNull(
                nwv!!.text
            ).toString() + "3deak8jfns}e4³["
            nwn1 = if (checkBox11!!.isChecked) Objects.requireNonNull(nwn!!.text)
                .toString() else Objects.requireNonNull(
                nwn!!.text
            ).toString() + "3deak8jfns}e4³["
            schlummerzeit1 = Objects.requireNonNull(schlummerzeit!!.text).toString()
            if (!schlummerzeit1.contains(":") && !schlummerzeit!!.text.toString().isEmpty()) {
                schlummerzeit!!.error = getString(R.string.hint_schlummertime)
                return
            }
            if (checkBox9!!.isChecked) {
                if (songtitel1 == null) songtitel1 = ""
                if (songuri == null || songuri!!.isEmpty()) songuri = "-"
            }
            aktiviert1 =
                if (aktiviert!!.selectedItem.toString() == getString(R.string.onetime)) 0 else 1
            wartezeit1 = if (checkBox12!!.isChecked) wartezeit!!.value else wartezeit!!.value * -1
            var uebergabe = 0
            if (checkBox13!!.isChecked) uebergabe = 1
            importance1 = if (importance!!.visibility == View.GONE) {
                -100
            } else {
                if (checkBox14!!.isChecked) importance!!.value else importance!!.value * -1
            }
            val myDB = DataBaseHelper(this@FabNewAlarm)
            myDB.insertData(
                name1,
                absender1,
                gruppe1,
                1,
                app1,
                nachricht1,
                songuri,
                volume1,
                increase1,
                vibration1,
                nwv1,
                nwn1,
                aktiviert1,
                schlummerzeit1,
                wartezeit1,
                uebergabe,
                importance1,
                songtitel1
            )
            if (MainActivity.notificationsmake) makenotification()
            setResult(RESULT_OK)
            finish()
        } else {
            if (Objects.requireNonNull(name!!.text).toString().isEmpty()) name!!.error =
                getString(R.string.name_alarm) else if (absender!!.text.toString().isEmpty() &&
                gruppe!!.text.toString().isEmpty() &&
                nachricht!!.text.toString().isEmpty() &&
                app!!.text.toString().isEmpty()
            ) {
                Toasty.warning(this@FabNewAlarm, getString(R.string.parameter_hint)).show()
            } else if (!checkBox2!!.isChecked && !checkBox3!!.isChecked && !checkBox4!!.isChecked && !checkBox5!!.isChecked) {
                Toasty.warning(this@FabNewAlarm, getString(R.string.activate_parameter)).show()
            } else if (schlummerzeit!!.error == "") Toasty.warning(
                this@FabNewAlarm,
                getString(R.string.activate_right_one)
            ).show()
        }
    }

    private fun save(): Boolean {
        if (schlummerzeit!!.text.toString().isNotEmpty()) {
            if (!schlummerzeit!!.text.toString().contains(":")) {
                schlummerzeit!!.error = getString(R.string.sensesleeptime)
                return false
            }
            val a =
                schlummerzeit!!.text.toString().split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[0].toInt()
            val b =
                schlummerzeit!!.text.toString().split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[1].toInt()
            if (a < 0) {
                schlummerzeit!!.error = getString(R.string.sensesleeptime)
                return false
            }
            if (b < 0) {
                schlummerzeit!!.error = getString(R.string.sensesleeptime)
                return false
            }
            if (a == 0 && b == 0) {
                schlummerzeit!!.error = getString(R.string.sensesleeptime)
                return false
            }
            schlummerzeit!!.error = ""
        }
        return if (name!!.text.toString().isNotEmpty()) {
            if (absender!!.text.toString().isNotEmpty() && checkBox3!!.isChecked
            ) {
                true
            } else {
                if (gruppe!!.text.toString().isNotEmpty() && checkBox4!!.isChecked
                ) {
                    true
                } else {
                    if (nachricht!!.text.toString().isNotEmpty() && checkBox2!!.isChecked
                    ) {
                        true
                    } else {
                        app!!.text.toString().isNotEmpty() && checkBox5!!.isChecked
                    }
                }
            }
        } else false
    }

    private fun makenotification() {
        var b = 0
        val myDB = DataBaseHelper(this)
        val res = myDB.allData
        if (res.count > 0) {
            while (res.moveToNext()) {
                if (res.getInt(4) == 1) b++
            }
        }
        res.close()
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
        val action = Intent(this, ActionReceiverAktive::class.java)
        val actionIntent =
            PendingIntent.getBroadcast(this, 0, action, PendingIntent.FLAG_IMMUTABLE)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel("1", "Bencker", importance)
        mChannel.setSound(null, null)
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.createNotificationChannel(mChannel)
        val naction = Notification.Action(0, getString(R.string.finishall), actionIntent)
        val notification = Notification.Builder(this, "1")
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

    override fun onDestroy() {
        super.onDestroy()
        setTheme(R.style.AppTheme)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder(this)
                .setPositiveButton(getString(R.string.leave)) { dialog, _ ->
                    this@FabNewAlarm.setResult(RESULT_CANCELED)
                    dialog.dismiss()
                    finish()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                .setTitle(getString(R.string.delete_alarm))
                .setMessage(getString(R.string.go_back_new))
                .setCancelable(true)
                .show()
            false
        } else super.onKeyDown(keyCode, event)
    }
}