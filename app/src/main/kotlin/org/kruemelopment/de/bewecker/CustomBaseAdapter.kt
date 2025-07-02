package org.kruemelopment.de.bewecker

import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.RecyclerView
import com.rey.material.widget.ImageView
import com.rey.material.widget.SnackBar
import com.rey.material.widget.Switch
import com.rey.material.widget.TextView


class CustomBaseAdapter(
    var context: Context?,
    private var alarmliste: ArrayList<Alarmliste>,
    var myDB: DataBaseHelper,
    private var snackBar: SnackBar,
    private val mCallback: IProcessFilter
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    interface IProcessFilter {
        fun onProcessFilter()
    }

    class MyViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView? = null
        var absender: TextView? = null
        var imageView: ImageView? = null
        var aSwitch: Switch? = null
        private var sleep: ImageView? = null
        var parentView=view

        init {
            name = view.findViewById(R.id.name)
            absender = view.findViewById(R.id.absender)
            imageView = view.findViewById(R.id.imageView)
            aSwitch = view.findViewById(R.id.switcher)
            sleep = view.findViewById(R.id.imageView2)
        }
    }

    class EmptyViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var text: TextView? = null

        init {
            text = view.findViewById(R.id.nothing)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(if(viewType==0) R.layout.emptyitem else R.layout.alarmitem, parent, false)
        return if(viewType==0) EmptyViewHolder(v) else MyViewHolder(v)
    }

    override fun onBindViewHolder(generalholder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position)==0){
            (generalholder as EmptyViewHolder).text!!.text=context!!.getString(R.string.keine_items)
            return
        }
        val holder = (generalholder as MyViewHolder)
        val alarml: Alarmliste = alarmliste[position]
        holder.name!!.text = alarml.name!!.replace("3deak8jfns}e4³[", "")
        var text = ""
        if ( alarml.absender != null &&!alarml.absender!!.contains("3deak8jfns}e4³[") && alarml.absender != "") text =
            text + alarml.absender + ","
        if (alarml.nachricht != null &&!alarml.nachricht.contains("3deak8jfns}e4³[") &&  alarml.nachricht != "") text =
            text + alarml.nachricht + ","
        if (alarml.gruppe != null &&!alarml.gruppe.contains("3deak8jfns}e4³[") &&  alarml.gruppe != "") text =
            text + alarml.gruppe + ","
        if (alarml.packageName != null &&!alarml.packageName!!.contains("3deak8jfns}e4³[") &&  alarml.packageName != ""
        ) text = text + alarml.packageName + ","
        if (text.isNotEmpty()) if (text.endsWith(",")) text = text.substring(0, text.length - 1)
        holder.absender!!.text = text
        holder.aSwitch!!.isChecked = alarml.aktiv == 1
        try {
            val icon = alarml.packageName?.let { context!!.packageManager.getApplicationIcon(it) }
            holder.imageView!!.setImageDrawable(icon)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            holder.imageView!!.setImageResource(R.drawable.default_icon)
        }
        holder.aSwitch!!.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                if (MainActivity.showtipp) {
                    snackBar.show(context as Activity?)
                }
                alarml.aktiv = 1
                if (MainActivity.notificationsmake) makenotification()
            } else {
                alarml.aktiv = 0
                if (MainActivity.notificationsmake) checkfornotification()
            }
            myDB.updateAktiv(alarml.id, alarml.aktiv)
        }
        holder.parentView.setOnClickListener {
            val intent = Intent(context, EditAlarm::class.java)
            intent.putExtra("id", alarml.id)
            (context as Activity?)!!.startActivityForResult(intent, 2)
        }
        holder.parentView.setOnLongClickListener {
            val alertDialog = AlertDialog.Builder(
                context
            )
                .setTitle(context!!.getString(R.string.confirm_delete))
                .setMessage(context!!.getString(R.string.delete_question))
                .setPositiveButton(context!!.getString(R.string.delete)) { dialog, _ ->
                    dialog.dismiss()
                    myDB.deleteData(alarml.id)
                    alarmliste.removeAt(position)
                    if (MainActivity.notificationsmake) checkfornotification()
                    if (alarmliste.isEmpty()) {
                        mCallback.onProcessFilter()
                        alarmliste.add(
                            Alarmliste(
                                null,
                                null,
                                null,
                                null,
                                0,
                                null,
                                null,
                                "-",
                                0,
                                0,
                                0,
                                null,
                                null,
                                0,
                                null,
                                0,
                                0,
                                0,
                                null
                            )
                        )
                        notifyItemChanged(0)
                    }
                    notifyItemRemoved(position)
                }
                .setNeutralButton(context!!.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                .setNegativeButton(context!!.getString(R.string.copy)) { dialog, _ ->
                    dialog.dismiss()
                    var res = myDB.getData(alarml.id)
                    res.moveToFirst()
                    myDB.insertData(
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
                    res = myDB.lastData
                    res.moveToFirst()
                    alarmliste.add(
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
                    res.close()
                    notifyItemInserted(alarmliste.size)
                }
                .setCancelable(true)
                .show()
            alertDialog.show()
            true
        }
    }

    override fun getItemCount(): Int {
        return alarmliste.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(alarmliste[position].id==null) 0 else 1
    }

    override fun getItemId(position: Int): Long {
        return alarmliste[position].id!!.toLong()
    }
    private fun makenotification() {
        var b = 0
        for (a in alarmliste) {
            if (a.aktiv == 1) {
                b++
            }
        }
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
        mChannel.setSound(null, null)
        val mNotificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.createNotificationChannel(mChannel)
        val naction: NotificationCompat.Action =
            NotificationCompat.Action.Builder(0, context!!.getString(R.string.finishall), actionIntent)
                .build()

        val notification = NotificationCompat.Builder(context!!, "1")
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentText(text)
            .setSmallIcon(logo)
            .addAction(naction)
            .setContentTitle("Bencker")
            .setContentIntent(intent)
            .build()
        mNotificationManager.notify(103, notification)
    }

    private fun checkfornotification() {
        for (a in alarmliste) if (a.aktiv == 1) {
            makenotification()
            return
        }
        val notificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(103)
    }
}