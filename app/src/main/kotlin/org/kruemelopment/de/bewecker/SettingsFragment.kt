package org.kruemelopment.de.bewecker

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import es.dmoral.toasty.Toasty
import java.util.Calendar

class SettingsFragment : PreferenceFragmentCompat() {

    private var ringtone: Preference? = null
    private var permission=Manifest.permission.READ_EXTERNAL_STORAGE


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        if (Build.VERSION.SDK_INT>32) permission=Manifest.permission.READ_MEDIA_AUDIO
        val nwv = findPreference<Preference>("nwvw")
        val nwn = findPreference<Preference>("nwnw")
        val schlummer = findPreference<EditTextPreference>("schlummertime")
        nwv!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val sp2 = requireContext().getSharedPreferences("Settings", 0)
            val help = sp2.getString("nwv", "")
            val hour: Int
            val minute: Int
            if (help.isNullOrEmpty()) {
                val mcurrentTime = Calendar.getInstance()
                hour = mcurrentTime[Calendar.HOUR_OF_DAY]
                minute = mcurrentTime[Calendar.MINUTE]
            } else {
                hour = help.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[0].toInt()
                minute = help.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[1].toInt()
            }
            val mTimePicker = TimePickerDialog(
            context,
                { _, selectedHour, selectedMinute ->
                    val a: String = if (selectedHour < 10 && selectedMinute < 10) {
                        "0$selectedHour:0$selectedMinute"
                    } else if (selectedHour < 10) {
                        "0$selectedHour:$selectedMinute"
                    } else if (selectedMinute < 10) {
                        "$selectedHour:0$selectedMinute"
                    } else "$selectedHour:$selectedMinute"
                    val sp8 = requireContext().getSharedPreferences("Settings", 0)
                    val ed = sp8.edit()
                    ed.putString("nwv", a)
                    ed.apply()
                    nwv.summary = getString(R.string.stnwv) + "\n" + a
                },
            hour,
            minute,
            true
        )
            mTimePicker.setTitle(getString(R.string.timepick))
            mTimePicker.show()
            false
        }
        nwn!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val sp2 = requireContext().getSharedPreferences("Settings", 0)
            val help = sp2.getString("nwn", "")
            val hour: Int
            val minute: Int
            if (help.isNullOrEmpty()) {
                val mcurrentTime = Calendar.getInstance()
                hour = mcurrentTime[Calendar.HOUR_OF_DAY]
                minute = mcurrentTime[Calendar.MINUTE]
            } else {
                hour = help.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[0].toInt()
                minute = help.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[1].toInt()
            }
            val mTimePicker = TimePickerDialog(context,
                { _, selectedHour, selectedMinute ->
                    val a: String = if (selectedHour < 10 && selectedMinute < 10) {
                            "0$selectedHour:0$selectedMinute"
                        } else if (selectedHour < 10) {
                            "0$selectedHour:$selectedMinute"
                        } else if (selectedMinute < 10) {
                            "$selectedHour:0$selectedMinute"
                        } else "$selectedHour:$selectedMinute"
                    val sp8 = requireContext().getSharedPreferences("Settings", 0)
                    val ed = sp8.edit()
                    ed.putString("nwn", a)
                    ed.apply()
                    nwn.summary = getString(R.string.stnwn) + "\n" + a
                }, hour, minute, true)
            mTimePicker.setTitle(getString(R.string.timepick))
            mTimePicker.show()
            false
        }
        schlummer!!.onPreferenceChangeListener = object : Preference.OnPreferenceChangeListener {
            override fun onPreferenceChange(preference: Preference, o: Any): Boolean {
                if (o.toString().replace(":", "")
                        .matches("[0-9]".toRegex()) && o.toString().length - o.toString()
                        .replace(":", "").length == 1
                ) {
                    if (!o.toString().contains(":")) {
                        Toasty.warning(
                            requireContext(),
                            getString(R.string.schlummertime_warning),
                            Toast.LENGTH_SHORT
                        ).show()
                        return false
                    } else {
                        return if (o.toString().split(":".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()[1].toInt() > 60) {
                            Toasty.warning(
                                requireContext(),
                                getString(R.string.notover60),
                                Toast.LENGTH_SHORT
                            ).show()
                            false
                        } else {
                            schlummer.summary =
                                getString(R.string.stschlummern3) + "\n" + o.toString() + " " + getString(
                                    R.string.minuten
                                )
                            true
                        }
                    }
                } else {
                    Toasty.warning(
                        requireContext(),
                        "Bitte gib die Zeit im richtigen Format ein!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
            }
        }
        val notification = findPreference<Preference>("notificae")!!
        notification.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
            false
        }
        ringtone = findPreference("ringt")
        ringtone!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val result = ContextCompat.checkSelfPermission(
                requireContext(),permission
            )
            if (result == PackageManager.PERMISSION_GRANTED) {
                val data = Intent(Intent.ACTION_OPEN_DOCUMENT)
                data.addCategory(Intent.CATEGORY_OPENABLE)
                data.setType("audio/*")
                val intent = Intent.createChooser(data, "Klingelton auswählen")
                startActivityForResult(intent, 34)
            } else {
                ActivityCompat.requestPermissions(
                    (requireActivity()),
                    arrayOf(permission),
                    101
                )
            }
            false
        }
        val switchPreference = findPreference<SwitchPreference>("statusbaricon")!!
        switchPreference.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, o ->
                if(Build.VERSION.SDK_INT>32){
                    val result = ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.POST_NOTIFICATIONS)
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        if (o as Boolean) {
                            makenotification()
                            MainActivity.notificationsmake = true
                        } else {
                            cancelall()
                            MainActivity.notificationsmake = false
                        }
                        true
                    }
                    else{
                        ActivityCompat.requestPermissions(
                            (requireActivity()),
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            102
                        )
                        false
                    }
                }
                else {
                    if (o as Boolean) {
                        makenotification()
                        MainActivity.notificationsmake = true
                    } else {
                        cancelall()
                        MainActivity.notificationsmake = false
                    }
                    true
                }
            }
        val catPref = findPreference<PreferenceCategory>("category")
        val switchPreference2 = findPreference<SwitchPreference>("drawapps")!!
        switchPreference2.isChecked = Settings.canDrawOverlays(requireActivity())
        switchPreference2.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + requireActivity().packageName)
                )
                startActivityForResult(intent, 1003)
                true
            }
        val switchPreference3 = findPreference<SwitchPreference>("schlummerbaricon")!!
        switchPreference3.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, o ->
                if(Build.VERSION.SDK_INT>32){
                val result = ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.POST_NOTIFICATIONS)
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        if (o as Boolean) {
                            sleepnotification()
                        } else {
                            removesleepnotification()
                        }
                        true
                    }
                    else{
                        ActivityCompat.requestPermissions(
                            (requireActivity()),
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            102
                        )
                        false
                    }
                }
                else {
                    if (o as Boolean) {
                        sleepnotification()
                    } else {
                        removesleepnotification()
                    }
                    true
                }

            }
        val switchPreference4 = findPreference<SwitchPreference>("reminder")!!
        switchPreference4.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, o ->
                MainActivity.showtipp = o as Boolean
                true
            }
        val sp2 = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val help = sp2.getString("schlummertime", "5:00")
        if (!help.isNullOrEmpty()) schlummer.summary =
            getString(R.string.stschlummern3) + "\n" + help + " " + getString(R.string.minuten)
        val sp8 = requireContext().getSharedPreferences("Settings", 0)
        val vor = sp8.getString("nwv", "")
        val nach = sp8.getString("nwn", "")
        val ring = sp8.getString("ringtonedefaulttitel", "")
        if (!nach.isNullOrEmpty()) nwn.summary = getString(R.string.stnwn) + "\n" + nach
        if (!vor.isNullOrEmpty()) nwv.summary = getString(R.string.stnwv) + "\n" + vor
        if (!ring.isNullOrEmpty()) ringtone!!.summary =
            getString(R.string.ringtonedes) + "\n" + ring
        val darkmode = findPreference<SwitchPreference>("darkmode")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            catPref!!.removePreference(darkmode!!)
        } else {
            darkmode!!.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    if (newValue as Boolean) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    MainActivity.navigationView!!.setCheckedItem(R.id.allbewecker)
                    true
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 34 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                val songuri = data.data
                var songtitel: String? = null
                val cursor = requireContext().contentResolver.query(
                    songuri!!, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    val titleCol = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    songtitel = cursor.getString(titleCol)
                    cursor.close()
                }
                if (songtitel != null) {
                    val sp8 = requireContext().getSharedPreferences("Settings", 0)
                    val ed = sp8.edit()
                    ed.putString("ringtonedefault", songuri.toString())
                    ed.putString("ringtonedefaulttitel", songtitel)
                    ed.apply()
                    ringtone!!.summary = getString(R.string.ringtonedes) + "\n" + songtitel
                }
                requireContext().contentResolver.takePersistableUriPermission(
                    songuri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        } else if (requestCode == 1003) {
            val switchPreference2 = findPreference<SwitchPreference>("drawapps")
            switchPreference2!!.isChecked = Settings.canDrawOverlays(requireActivity())
        }
    }

    private fun cancelall() {
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(103)
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
        if (b == 0) return
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
        val actionIntent =
            PendingIntent.getBroadcast(context, 0, action, PendingIntent.FLAG_IMMUTABLE)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel("1", "Bencker", importance)
        val mNotificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.createNotificationChannel(mChannel)
        val naction = NotificationCompat.Action(0, getString(R.string.finishall), actionIntent)
        val notification = NotificationCompat.Builder(requireContext(), "1")
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

    private fun sleepnotification() {
        val myDB = SchlafendeBaseHelper(context)
        val res = myDB.allData
        if (res.count > 0) {
            while (res.moveToNext()) {
                val schlafzeit = res.getString(6)
                val text = "Bencker schlummert bis $schlafzeit"
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
                val action = Intent(context, ActionReceiver::class.java)
                action.putExtra("schlummerid", res.getString(0))
                val actionIntent = PendingIntent.getBroadcast(
                    context,
                    res.getInt(0),
                    action,
                    PendingIntent.FLAG_IMMUTABLE
                )
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val mChannel = NotificationChannel(res.getString(0), "Bencker", importance)
                mChannel.setSound(null, null)
                val mNotificationManager =
                    requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                mNotificationManager.createNotificationChannel(mChannel)
                val naction = NotificationCompat.Action(0, getString(R.string.finish), actionIntent)
                val notification = NotificationCompat.Builder(requireContext(), res.getString(0))
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
                    .setContentTitle(res.getString(1) + " schlummert")
                    .setContentIntent(intent)
                    .build()
                mNotificationManager.notify(res.getInt(0), notification)
            }
        }
        res.close()
    }

    private fun removesleepnotification() {
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val myDB = SchlafendeBaseHelper(context)
        val res = myDB.allData
        if (res.count > 0) {
            while (res.moveToNext()) {
                notificationManager.cancel(res.getInt(0))
            }
        }
        res.close()
    }
}