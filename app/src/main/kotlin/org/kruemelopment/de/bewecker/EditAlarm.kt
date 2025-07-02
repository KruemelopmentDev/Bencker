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
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
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
import java.util.Collections
import java.util.Locale
import java.util.Objects
import kotlin.math.min

class EditAlarm : AppCompatActivity() {
    var id: String? = null
    private var name: String? = null
    private var absender: String? = null
    private var gruppe: String? = null

    //private int aktiv;
    private var app: String? = null
    private var nachricht: String? = null
    private var songuri: String? = null
    private var volume = 0
    private var increase = 0
    private var vibrate = 0
    private var nwv = ""
    private var nwn = ""
    private var aktiviert = 0
    private var schlummerzeit = ""
    private var exitsleep = false
    private var wartezeit = 0
    var importance = 0
    private var songtitel: String? = ""
    private var klingeltontext: MaterialEditText? = null
    private var app1: MaterialEditText? = null
    private var rese: MutableList<AppInfo> = ArrayList()
    private var name1: MaterialEditText? = null
    private var nachricht1: MaterialEditText? = null
    private var absender1: MaterialEditText? = null
    private var gruppe1: MaterialEditText? = null
    private var vibration1: Slider? = null
    private var volume1: Slider? = null
    private var increase1: Slider? = null
    private var wartezeit1: Slider? = null
    private var importance1: Slider? = null
    private var nwv1: MaterialEditText? = null
    private var nwn1: MaterialEditText? = null
    private var aktiviert1: Spinner? = null
    private var schlummerzeit1: MaterialEditText? = null
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
        setTheme(R.style.AppTheme2)
        setContentView(R.layout.activity_new_alarm)
        if (Build.VERSION.SDK_INT>32) permission=Manifest.permission.READ_MEDIA_AUDIO
        loadapps()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = getString(R.string.edit_alarm)
        id = intent.getStringExtra("id")
        val myDB = DataBaseHelper(this)
        val res = myDB.allData
        if (res.count > 0) {
            while (res.moveToNext()) {
                if (res.getString(0) == id) {
                    name = res.getString(1)
                    absender = res.getString(2)
                    gruppe = res.getString(3)
                    //aktiv=res.getInt(4);
                    app = res.getString(5)
                    nachricht = res.getString(6)
                    songuri = res.getString(7)
                    volume = res.getInt(8)
                    increase = res.getInt(9)
                    vibrate = res.getInt(10)
                    nwv = res.getString(11)
                    nwn = res.getString(12)
                    aktiviert = res.getInt(13)
                    schlummerzeit = res.getString(14)
                    wartezeit = res.getInt(15)
                    exitsleep = res.getInt(16) == 1
                    importance = res.getInt(17)
                    songtitel = res.getString(18)
                }
            }
        }
        res.close()
        name1 = findViewById(R.id.editText1)
        nachricht1 = findViewById(R.id.editText2)
        absender1 = findViewById(R.id.editText3)
        gruppe1 = findViewById(R.id.editText4)
        app1 = findViewById(R.id.editText5)
        vibration1 = findViewById(R.id.slider1)
        volume1 = findViewById(R.id.slider2)
        increase1 = findViewById(R.id.slider3)
        nwv1 = findViewById(R.id.editText7)
        nwn1 = findViewById(R.id.editText8)
        aktiviert1 = findViewById(R.id.spinner)
        wartezeit1 = findViewById(R.id.slider4)
        importance1 = findViewById(R.id.slider5)
        checkBox14 = findViewById(R.id.checkbox14)
        val textView = findViewById<TextView>(R.id.textView6)
        val prios = PrioDataBaseHelper(this).lastlevel()
        if (prios > 0) {
            importance1!!.setValueRange(0, prios, false)
            if (importance == -100) {
                importance1!!.setValue(0f, false)
            } else {
                if (importance < 0) {
                    importance1!!.setValue(min(prios, importance * -1).toFloat(), false)
                } else {
                    if (prios < importance) {
                        importance1!!.setValue(prios.toFloat(), false)
                    } else {
                        importance1!!.setValue(importance.toFloat(), false)
                        checkBox14!!.isChecked = true
                    }
                }
            }
        } else {
            importance1!!.visibility = View.GONE
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
        aktiviert1!!.adapter = spinnerArrayAdapter
        aktiviert1!!.setSelection(aktiviert)
        schlummerzeit1 = findViewById(R.id.editText9)
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
        checkBox14 = findViewById(R.id.checkbox14)
        klingeltontext = findViewById(R.id.editText6)
        name1!!.setText(name)
        if (nachricht!!.contains("3deak8jfns}e4³[")) {
            nachricht1!!.setText(nachricht!!.replace("3deak8jfns}e4³[", ""))
            checkBox2!!.isChecked = false
            nachricht1!!.isFocusableInTouchMode = false
        } else {
            nachricht1!!.setText(nachricht)
            checkBox2!!.isChecked = true
        }
        if (absender!!.contains("3deak8jfns}e4³[")) {
            absender1!!.setText(absender!!.replace("3deak8jfns}e4³[", ""))
            checkBox3!!.isChecked = false
            absender1!!.isFocusableInTouchMode = false
        } else {
            absender1!!.setText(absender)
            checkBox3!!.isChecked = true
        }
        if (gruppe!!.contains("3deak8jfns}e4³[")) {
            gruppe1!!.setText(gruppe!!.replace("3deak8jfns}e4³[", ""))
            checkBox4!!.isChecked = false
            gruppe1!!.isFocusableInTouchMode = false
        } else {
            gruppe1!!.setText(gruppe)
            checkBox4!!.isChecked = true
        }
        if (app!!.contains("3deak8jfns}e4³[")) {
            checkBox5!!.isChecked = false
            app1!!.isFocusableInTouchMode = false
        } else {
            checkBox5!!.isChecked = true
        }
        try {
            val appName = packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(
                    app!!.replace("3deak8jfns}e4³[", ""), PackageManager.GET_META_DATA
                )
            ) as String
            app1!!.setText(appName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (vibrate < 0) {
            vibration1!!.setValue((vibrate * -1).toFloat(), true)
            checkBox6!!.isChecked = false
            vibration1!!.isFocusableInTouchMode = false
        } else {
            vibration1!!.setValue(vibrate.toFloat(), true)
            checkBox6!!.isChecked = true
        }
        safe1 = if (volume < 0) {
            volume1!!.setValue((volume * -1).toFloat(), true)
            checkBox7!!.isChecked = false
            volume1!!.isFocusableInTouchMode = false
            false
        } else {
            volume1!!.setValue(volume.toFloat(), true)
            checkBox7!!.isChecked = true
            true
        }
        safe2 = if (increase < 0) {
            increase1!!.setValue((increase * -1).toFloat(), true)
            checkBox8!!.isChecked = false
            increase1!!.isFocusableInTouchMode = false
            false
        } else {
            increase1!!.setValue(increase.toFloat(), true)
            checkBox8!!.isChecked = true
            true
        }
        if (songuri == null || songuri!!.startsWith("deaktiviert:")) {
            if (songuri == null || songuri == "") klingeltontext!!.setText("") else klingeltontext!!.setText(
                songtitel
            )
            checkBox9!!.isChecked = false
            volume1!!.isFocusableInTouchMode = false
            increase1!!.isFocusableInTouchMode = false
            checkBox7!!.isChecked = false
            checkBox8!!.isChecked = false
            checkBox7!!.isClickable = false
            checkBox8!!.isClickable = false
        } else {
            klingeltontext!!.setText(songtitel)
            checkBox9!!.isChecked = true
        }
        if (nwv.contains("3deak8jfns}e4³[")) {
            checkBox10!!.isChecked = false
            nwv1!!.isFocusableInTouchMode = false
        } else {
            checkBox10!!.isChecked = true
        }
        nwv1!!.setText(nwv.replace("3deak8jfns}e4³[", ""))
        if (nwn.contains("3deak8jfns}e4³[")) {
            checkBox11!!.isChecked = false
            nwn1!!.isFocusableInTouchMode = false
        } else {
            checkBox11!!.isChecked = true
        }
        nwn1!!.setText(nwn.replace("3deak8jfns}e4³[", ""))
        schlummerzeit1!!.setText(schlummerzeit)
        if (wartezeit < 0) {
            checkBox12!!.isChecked = false
            wartezeit1!!.setValue((wartezeit * -1).toFloat(), true)
        } else {
            checkBox12!!.isChecked = true
            wartezeit1!!.setValue(wartezeit.toFloat(), true)
        }
        checkBox13!!.isChecked = exitsleep
        klingeltontext!!.setOnClickListener {
            val result = ContextCompat.checkSelfPermission(
                this@EditAlarm,
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
                    this@EditAlarm,
                    arrayOf(permission),
                    101
                )
            }
        }
        app1!!.isClickable = false
        app1!!.setOnClickListener { pickapp() }
        checkBox2!!.setOnCheckedChangeListener { _, isChecked ->
            nachricht1!!.isFocusableInTouchMode = isChecked
        }
        checkBox3!!.setOnCheckedChangeListener { _, isChecked ->
            absender1!!.isFocusableInTouchMode = isChecked
        }
        checkBox4!!.setOnCheckedChangeListener { _, isChecked ->
            gruppe1!!.isFocusableInTouchMode = isChecked
        }
        checkBox6!!.setOnCheckedChangeListener { _, isChecked ->
            vibration1!!.isFocusableInTouchMode = isChecked
        }
        checkBox7!!.setOnCheckedChangeListener { _, isChecked ->
            volume1!!.isFocusableInTouchMode = isChecked
        }
        checkBox8!!.setOnCheckedChangeListener { _, isChecked ->
            increase1!!.isFocusableInTouchMode = isChecked
        }
        checkBox9!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                songuri = songuri!!.replace("deaktiviert:", "")
                volume1!!.isFocusableInTouchMode = true
                increase1!!.isFocusableInTouchMode = true
                checkBox7!!.isClickable = true
                checkBox8!!.isClickable = true
                checkBox8!!.isChecked = safe2
                checkBox7!!.isChecked = safe1
            } else {
                songuri = "deaktiviert:" + songuri!!.replace("deaktiviert:", "")
                volume1!!.isFocusableInTouchMode = false
                increase1!!.isFocusableInTouchMode = false
                safe1 = checkBox7!!.isChecked
                safe2 = checkBox8!!.isChecked
                checkBox7!!.isChecked = false
                checkBox8!!.isChecked = false
                checkBox7!!.isClickable = false
                checkBox8!!.isClickable = false
            }
        }
        nwv1!!.setOnClickListener(View.OnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker =
                TimePickerDialog(this@EditAlarm, { _, selectedHour, selectedMinute ->
                    val a: String = if (selectedHour < 10 && selectedMinute < 10) {
                        "0$selectedHour:0$selectedMinute"
                    } else if (selectedHour < 10) {
                        "0$selectedHour:$selectedMinute"
                    } else if (selectedMinute < 10) {
                        "$selectedHour:0$selectedMinute"
                    } else "$selectedHour:$selectedMinute"
                    nwv1!!.setText(a)
                }, hour, minute, true)
            mTimePicker.setTitle(getString(R.string.timepick))
            mTimePicker.show()
        })
        nwn1!!.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker =
                TimePickerDialog(this@EditAlarm, { _, selectedHour, selectedMinute ->
                    val a: String = if (selectedHour < 10 && selectedMinute < 10) {
                        "0$selectedHour:0$selectedMinute"
                    } else if (selectedHour < 10) {
                        "0$selectedHour:$selectedMinute"
                    } else if (selectedMinute < 10) {
                        "$selectedHour:0$selectedMinute"
                    } else "$selectedHour:$selectedMinute"
                    nwn1!!.setText(a)
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
                    songtitel = cursor.getString(titleCol)
                    cursor.close()
                }
                if (songtitel != null) {
                    klingeltontext!!.setText(songtitel)
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
                Toasty.error(this, getString(R.string.permission_denied)).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun pickapp() {
        val dialog = Dialog(this, R.style.Dialog)
        dialog.setContentView(R.layout.appdialog)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.listeapps)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(100)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.recycleChildrenOnDetach = true
        recyclerView.layoutManager = layoutManager
        val appadapter = Appadapter(this, rese, dialog)
        recyclerView.adapter = appadapter
        dialog.setOnCancelListener { appadapter.filter("") }
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
        dialog.setOnDismissListener {
            app = rese[0].pname
            app1!!.setText(rese[0].appname)
            appadapter.filter("")
        }
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            val suche = dialog.findViewById<ImageView>(R.id.suchbild)
            suche.setImageResource(R.drawable.search2)
        }
        dialog.show()
    }

    private fun loadapps() {
        val th = Thread {
            val packs = packageManager.getInstalledPackages(0)
            for (i in packs.indices) {
                val p = packs[i]
                if (packageManager.getLaunchIntentForPackage(p.packageName) != null) {
                    val appName = p.applicationInfo!!.loadLabel(packageManager).toString()
                    val icon = p.applicationInfo!!.loadIcon(packageManager)
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
            app1!!.isClickable = true
        }
        th.start()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder(this)
                .setPositiveButton(getString(R.string.leave)) { dialog, which ->
                    this@EditAlarm.setResult(RESULT_CANCELED)
                    dialog.dismiss()
                    finish()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, which -> dialog.dismiss() }
                .setTitle(getString(R.string.delete_changes))
                .setMessage(getString(R.string.delete_values))
                .setCancelable(true)
                .show()
            false
        } else super.onKeyDown(keyCode, event)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) // Press Back Icon
        {
            AlertDialog.Builder(this)
                .setPositiveButton(getString(R.string.leave)) { dialog, _ ->
                    this@EditAlarm.setResult(RESULT_CANCELED)
                    dialog.dismiss()
                    finish()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                .setTitle(getString(R.string.delete_changes))
                .setMessage(getString(R.string.delete_values))
                .setCancelable(true)
                .show()
        } else {
            speichern()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun speichern() {
        if (save()) {
            name = Objects.requireNonNull(name1!!.text).toString()
            nachricht = if (checkBox2!!.isChecked) {
                Objects.requireNonNull(nachricht1!!.text).toString()
            } else Objects.requireNonNull(nachricht1!!.text).toString() + "3deak8jfns}e4³["
            absender = if (checkBox3!!.isChecked) {
                Objects.requireNonNull(absender1!!.text).toString()
            } else Objects.requireNonNull(absender1!!.text).toString() + "3deak8jfns}e4³["
            gruppe = if (checkBox4!!.isChecked) {
                Objects.requireNonNull(gruppe1!!.text).toString()
            } else Objects.requireNonNull(gruppe1!!.text).toString() + "3deak8jfns}e4³["
            if (!checkBox5!!.isChecked) {
                app += "3deak8jfns}e4³["
            }
            vibrate = if (checkBox6!!.isChecked) {
                vibration1!!.value
            } else vibration1!!.value * -1
            volume = if (checkBox7!!.isChecked) {
                volume1!!.value
            } else volume1!!.value * -1
            increase = if (checkBox8!!.isChecked) {
                increase1!!.value
            } else increase1!!.value * -1
            if (checkBox9!!.isChecked) {
                if (songtitel == null) songtitel = ""
                if (songuri == null || songuri!!.isEmpty()) songuri = "-"
            }
            nwv = if (checkBox10!!.isChecked) {
                Objects.requireNonNull(nwv1!!.text).toString()
            } else Objects.requireNonNull(nwv1!!.text).toString() + "3deak8jfns}e4³["
            nwn = if (checkBox11!!.isChecked) {
                Objects.requireNonNull(nwn1!!.text).toString()
            } else Objects.requireNonNull(nwn1!!.text).toString() + "3deak8jfns}e4³["
            schlummerzeit = Objects.requireNonNull(schlummerzeit1!!.text).toString()
            if (!schlummerzeit.contains(":") && !schlummerzeit1!!.text.toString().isEmpty()) {
                schlummerzeit1!!.error = getString(R.string.hint_schlummertime)
                return
            }
            aktiviert = if (aktiviert1!!.selectedItem.toString() == getString(R.string.onetime)) {
                0
            } else 1
            wartezeit = if (checkBox12!!.isChecked) wartezeit1!!.value else wartezeit1!!.value * -1
            var uebergabe = 0
            if (checkBox13!!.isChecked) uebergabe = 1
            importance = if (importance1!!.visibility != View.GONE) {
                if (checkBox14!!.isChecked) importance1!!.value else importance1!!.value * -1
            } else -100
            val myDB = DataBaseHelper(this@EditAlarm)
            myDB.updateData(
                id,
                name,
                absender,
                gruppe,
                app,
                nachricht,
                songuri,
                volume,
                increase,
                vibrate,
                nwv,
                nwn,
                aktiviert,
                schlummerzeit,
                wartezeit,
                uebergabe,
                importance,
                songtitel
            )
            setResult(RESULT_OK)
            if (MainActivity.notificationsmake) makenotification()
            finish()
        } else {
            if (Objects.requireNonNull(name1!!.text).toString().isEmpty()) name1!!.error =
                getString(R.string.name_alarm) else if (Objects.requireNonNull(
                    absender1!!.text
                ).toString().isEmpty() && Objects.requireNonNull(
                    gruppe1!!.text
                ).toString().isEmpty() && Objects.requireNonNull(
                    nachricht1!!.text
                ).toString().isEmpty() && Objects.requireNonNull(
                    app1!!.text
                ).toString().isEmpty()
            ) {
                Toasty.warning(this@EditAlarm, getString(R.string.parameter_hint)).show()
            } else if (!checkBox2!!.isChecked && !checkBox3!!.isChecked && !checkBox4!!.isChecked && !checkBox5!!.isChecked) {
                Toasty.warning(this@EditAlarm, getString(R.string.activate_parameter)).show()
            } else if (schlummerzeit1!!.error == "") Toasty.warning(
                this@EditAlarm,
                getString(R.string.activate_right_one)
            ).show()
        }
    }

    private fun save(): Boolean {
        if (!Objects.requireNonNull(schlummerzeit1!!.text).toString().isEmpty()) {
            if (!schlummerzeit1!!.text.toString().contains(":")) {
                schlummerzeit1!!.error = getString(R.string.sensesleeptime)
                return false
            }
            val a =
                schlummerzeit1!!.text.toString().split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[0].toInt()
            val b =
                schlummerzeit1!!.text.toString().split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[1].toInt()
            if (a < 0) {
                schlummerzeit1!!.error = getString(R.string.sensesleeptime)
                return false
            }
            if (b < 0) {
                schlummerzeit1!!.error = getString(R.string.sensesleeptime)
                return false
            }
            if (a == 0 && b == 0) {
                schlummerzeit1!!.error = getString(R.string.sensesleeptime)
                return false
            }
            schlummerzeit1!!.error = ""
        }
        return if (name1!!.text.toString().isNotEmpty()) {
            if (absender1!!.text.toString().isNotEmpty() && checkBox3!!.isChecked
            ) {
                true
            } else {
                if (gruppe1!!.text.toString().isNotEmpty() && checkBox4!!.isChecked
                ) {
                    true
                } else {
                    if (nachricht1!!.text.toString().isNotEmpty() && checkBox2!!.isChecked
                    ) {
                        true
                    } else {
                        app1!!.text.toString().isNotEmpty() && checkBox5!!.isChecked
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
            .setColor(ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, theme))
            .addAction(naction)
            .setContentTitle("Bencker")
            .setContentIntent(intent)
            .build()
        mNotificationManager.notify(103, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        setTheme(R.style.AppTheme)
    }
}
