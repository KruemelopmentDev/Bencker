package org.kruemelopment.de.bewecker

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.ListView
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rengwuxian.materialedittext.MaterialEditText
import com.rey.material.widget.CheckBox
import com.rey.material.widget.Slider
import com.rey.material.widget.Spinner
import es.dmoral.toasty.Toasty
import java.text.Collator
import java.util.Calendar
import java.util.Collections
import java.util.Locale
import java.util.Objects

class NewAlarm : Fragment() {
    private var name1 = ""
    private var nachricht1 = ""
    private var absender1 = ""
    private var gruppe1 = ""
    private var app1: String? = ""
    private var vibration1 = -1
    private var volume1 = -1
    private var increase1 = -1
    private var songuri: String? = ""
    private var nwv1 = ""
    private var nwn1 = ""
    private var aktiviert1 = 0
    private var schlummerzeit1 = ""
    private var wartezeit1 = 0
    private var importance1 = 0
    private var songtitel: String? = ""
    private var klingeltontext: MaterialEditText? = null
    private var rese: MutableList<AppInfo> = ArrayList()
    private var app: MaterialEditText? = null
    private var name: MaterialEditText? = null
    private var nachricht: MaterialEditText? = null
    private var absender: MaterialEditText? = null
    private var gruppe: MaterialEditText? = null
    private var vibration: Slider? = null
    private var volume: Slider? = null
    private var increase: Slider? = null
    private var wartezeit: Slider? = null
    private var importance: Slider? = null
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
    private var loadedappdialog = false
    private var th: Thread? = null
    private var permission=Manifest.permission.READ_EXTERNAL_STORAGE

    interface IProcessFilter {
        fun safedtransfer()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT>32) permission=Manifest.permission.READ_MEDIA_AUDIO
        loadapps()
        name = view.findViewById(R.id.editText1)
        nachricht = view.findViewById(R.id.editText2)
        absender = view.findViewById(R.id.editText3)
        gruppe = view.findViewById(R.id.editText4)
        app = view.findViewById(R.id.editText5)
        vibration = view.findViewById(R.id.slider1)
        volume = view.findViewById(R.id.slider2)
        increase = view.findViewById(R.id.slider3)
        nwv = view.findViewById(R.id.editText7)
        nwn = view.findViewById(R.id.editText8)
        aktiviert = view.findViewById(R.id.spinner)
        wartezeit = view.findViewById(R.id.slider4)
        importance = view.findViewById(R.id.slider5)
        checkBox14 = view.findViewById(R.id.checkbox14)
        val textView = view.findViewById<TextView>(R.id.textView6)
        val prios = PrioDataBaseHelper(context).lastlevel()
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
            requireContext(), android.R.layout.simple_spinner_item, set
        )
        spinnerArrayAdapter.setDropDownViewResource(R.layout.adapterbackground)
        aktiviert!!.adapter = spinnerArrayAdapter
        aktiviert!!.setSelection(1)
        schlummerzeit = view.findViewById(R.id.editText9)
        checkBox2 = view.findViewById(R.id.checkbox2)
        checkBox3 = view.findViewById(R.id.checkbox3)
        checkBox4 = view.findViewById(R.id.checkbox4)
        checkBox5 = view.findViewById(R.id.checkbox5)
        checkBox6 = view.findViewById(R.id.checkbox6)
        checkBox7 = view.findViewById(R.id.checkbox7)
        checkBox8 = view.findViewById(R.id.checkbox8)
        checkBox9 = view.findViewById(R.id.checkbox9)
        checkBox10 = view.findViewById(R.id.checkbox10)
        checkBox11 = view.findViewById(R.id.checkbox11)
        checkBox12 = view.findViewById(R.id.checkbox12)
        checkBox13 = view.findViewById(R.id.checkbox13)
        klingeltontext = view.findViewById(R.id.editText6)
        klingeltontext!!.setOnClickListener {
            val result = ContextCompat.checkSelfPermission(
                requireContext(),
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
                    requireActivity(),
                    arrayOf(permission),
                    101
                )
            }
        }
        app!!.isClickable = false
        app!!.setOnClickListener { pickapp() }
        checkBox2!!.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            nachricht!!.isFocusableInTouchMode = isChecked
        }
        checkBox3!!.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            absender!!.isFocusableInTouchMode = isChecked
        }
        checkBox4!!.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            gruppe!!.isFocusableInTouchMode = isChecked
        }
        checkBox9!!.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
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
        checkBox6!!.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            vibration!!.isFocusableInTouchMode = isChecked
        }
        checkBox7!!.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            volume!!.isFocusableInTouchMode = isChecked
        }
        checkBox8!!.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            increase!!.isFocusableInTouchMode = isChecked
        }
        nwv!!.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker = TimePickerDialog(
                context,
                { _: TimePicker?, selectedHour: Int, selectedMinute: Int ->
                    val a: String = if (selectedHour < 10 && selectedMinute < 10) {
                        "0$selectedHour:0$selectedMinute"
                    } else if (selectedHour < 10) {
                        "0$selectedHour:$selectedMinute"
                    } else if (selectedMinute < 10) {
                        "$selectedHour:0$selectedMinute"
                    } else "$selectedHour:$selectedMinute"
                    nwv!!.setText(a)
                },
                hour,
                minute,
                true
            )
            mTimePicker.setTitle(getString(R.string.timepick))
            mTimePicker.show()
        }
        nwn!!.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker = TimePickerDialog(
                context,
                { _: TimePicker?, selectedHour: Int, selectedMinute: Int ->
                    val a: String = if (selectedHour < 10 && selectedMinute < 10) {
                        "0$selectedHour:0$selectedMinute"
                    } else if (selectedHour < 10) {
                        "0$selectedHour:$selectedMinute"
                    } else if (selectedMinute < 10) {
                        "$selectedHour:0$selectedMinute"
                    } else "$selectedHour:$selectedMinute"
                    nwn!!.setText(a)
                },
                hour,
                minute,
                true
            )
            mTimePicker.setTitle(getString(R.string.timepick))
            mTimePicker.show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_new_alarm, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 34 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                val songurid = data.data
                songuri = data.data.toString()
                val cursor = requireContext().contentResolver.query(
                    songurid!!, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    val titleCol = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    songtitel = cursor.getString(titleCol)
                    cursor.close()
                }
                if (songtitel != null) {
                    klingeltontext!!.setText(songtitel)
                }
                requireContext().contentResolver.takePersistableUriPermission(
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
                Toasty.error(requireContext(), getString(R.string.permission_denied)).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun pickapp() {
        if (loadedappdialog) {
            val dialog = Dialog(requireContext(), R.style.Dialog)
            dialog.setContentView(R.layout.appdialog)
            val recyclerView = dialog.findViewById<RecyclerView>(R.id.listeapps)
            recyclerView.setHasFixedSize(true)
            recyclerView.setItemViewCacheSize(100)
            val layoutManager = LinearLayoutManager(context)
            layoutManager.recycleChildrenOnDetach = true
            recyclerView.layoutManager = layoutManager
            val appadapter = Appadapter(context, rese, dialog)
            recyclerView.adapter = appadapter
            val searchtext = dialog.findViewById<MaterialEditText>(R.id.searchbox)
            searchtext.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

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
            dialog.show()
        }
    }

    private fun loadapps() {
        th = Thread(Runnable {
            val packs = requireContext().packageManager.getInstalledPackages(0)
            for (i in packs.indices) {
                val p = packs[i]
                if (th!!.isInterrupted) {
                    return@Runnable
                }
                if (requireContext().packageManager.getLaunchIntentForPackage(p.packageName) != null) {
                    val appName =
                        p.applicationInfo!!.loadLabel(requireContext().packageManager).toString()
                    val icon = p.applicationInfo!!.loadIcon(requireContext().packageManager)
                    rese.add(AppInfo(appName, p.packageName, icon))
                }
            }
            rese.sortWith { o1: AppInfo, o2: AppInfo ->
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
            loadedappdialog = true
        })
        th!!.start()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save) {
            speichern()
        }
        return true
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
            if (checkBox9!!.isChecked) {
                if (songtitel == null) songtitel = ""
                if (songuri == null || songuri!!.isEmpty()) songuri = "-"
            }
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
            val myDB = DataBaseHelper(requireContext())
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
                songtitel
            )
            if (MainActivity.notificationsmake) makenotification()
            MainActivity.callback!!.safedtransfer()
        } else {
            if (name!!.text.toString().isEmpty()) name!!.error =
                getString(R.string.name_alarm) else if (
                    absender!!.text
                .toString().isEmpty() &&
                    gruppe!!.text
                .toString().isEmpty() &&
                    nachricht!!.text
                .toString().isEmpty() &&
                    app!!.text
                .toString().isEmpty()
            ) {
                Toasty.warning(requireContext(), getString(R.string.parameter_hint)).show()
            } else if (!checkBox2!!.isChecked && !checkBox3!!.isChecked && !checkBox4!!.isChecked && !checkBox5!!.isChecked) {
                Toasty.warning(requireContext(), getString(R.string.activate_parameter)).show()
            } else if (schlummerzeit!!.error == "") Toasty.warning(
                requireContext(),
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
        return if (Objects.requireNonNull(name!!.text).toString().isNotEmpty()) {
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
        val myDB = DataBaseHelper(context)
        val res = myDB.allData
        if (res.count > 0) {
            while (res.moveToNext()) {
                if (res.getInt(4) == 1) b++
            }
        }
        res.close()
        val text: String = if (b > 1) "Es sind aktuell $b Bencker aktiv" else "Es ist aktuell ein Bencker aktiv"
        val logo = R.drawable.logow
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.setFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    or Intent.FLAG_ACTIVITY_SINGLE_TOP
        )
        val intent = PendingIntent.getActivity(
            context, 0,
            notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val action = Intent(context, ActionReceiverAktive::class.java)
        val actionIntent = PendingIntent.getBroadcast(
            context,
            0,
            action,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel("1", "Bencker", importance)
        mChannel.setSound(null, null)
        val mNotificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.createNotificationChannel(mChannel)
        val naction = Notification.Action(0, getString(R.string.finishall), actionIntent)
        val notification = Notification.Builder(context, "1")
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentText(text)
            .setSmallIcon(logo)
            .addAction(naction)
            .setColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.colorPrimaryDark,
                    requireActivity().theme
                )
            )
            .setContentTitle("Bencker")
            .setContentIntent(intent)
            .build()
        mNotificationManager.notify(103, notification)
    }

    override fun onDestroy() {
        th!!.interrupt()
        super.onDestroy()
    }
}
