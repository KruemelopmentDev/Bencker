package org.kruemelopment.de.bewecker

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.rey.material.widget.ImageView
import com.rey.material.widget.Switch
import com.rey.material.widget.TextView

class CustomSleepAdapter(var context: Context?, private var alarmliste: MutableList<Alarmliste>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var myDB: SchlafendeBaseHelper = SchlafendeBaseHelper(context)

    class MyViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView? = null
        var absender: TextView? = null
        var imageView: ImageView? = null
        var aSwitch: Switch? = null
        private var sleep: ImageView? = null
        var klingeltum: TextView? = null

        init {
            name = view.findViewById(R.id.name)
            absender = view.findViewById(R.id.absender)
            imageView = view.findViewById(R.id.imageView)
            aSwitch = view.findViewById(R.id.switcher)
            sleep = view.findViewById(R.id.imageView2)
            klingeltum = view.findViewById(R.id.textView19)
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
            .inflate(if(viewType==0)R.layout.emptyitem else R.layout.sleepitem, parent, false)
        return if(viewType==0) EmptyViewHolder(v) else MyViewHolder(v)
    }

    override fun onBindViewHolder(generalholder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position)==0){
            (generalholder as EmptyViewHolder).text!!.text=context!!.getString(R.string.nosnoozing)
            return
        }
        val holder = (generalholder as MyViewHolder)
        holder.name!!.text = alarmliste[position].name!!.replace("3deak8jfns}e4³[", "")
        var text = ""
        val alarml = alarmliste[position]
        if (alarmliste[position].absender != null && !alarml.absender!!.contains("3deak8jfns}e4³[") && alarmliste[position].absender != "") text =
            text + alarmliste[position].absender + ","
        if (alarmliste[position].nachricht != null &&!alarml.nachricht!!.contains("3deak8jfns}e4³[") &&  alarmliste[position].nachricht != "") text =
            text + alarmliste[position].nachricht + ","
        if (alarmliste[position].gruppe != null &&!alarml.gruppe!!.contains("3deak8jfns}e4³[") &&  alarmliste[position].gruppe != "") text =
            text + alarmliste[position].gruppe + ","
        if (alarmliste[position].packageName != null &&!alarml.packageName!!.contains("3deak8jfns}e4³[") &&  alarmliste[position].packageName != ""
        ) text = text + alarmliste[position].packageName + ","
        if (text.isNotEmpty()) if (text.endsWith(",")) text = text.substring(0, text.length - 1)
        holder.absender!!.text = text
        holder.aSwitch!!.isChecked = true
        try {
            val icon = context!!.packageManager.getApplicationIcon(alarml.packageName!!)
            holder.imageView!!.setImageDrawable(icon)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            holder.imageView!!.setImageResource(R.drawable.default_icon)
        }
        val helptext= "Bencker schlummert bis: ${alarml.songtitel} Uhr"
        holder.klingeltum!!.text =helptext
        holder.aSwitch!!.setOnCheckedChangeListener { _, checked ->
            if (!checked) {
                myDB.deleteData(alarmliste[position].id)
                val mNotificationManager =
                    context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val sp2 = PreferenceManager.getDefaultSharedPreferences(
                    context!!
                )
                if (sp2.getBoolean("schlummerbaricon", false)) mNotificationManager.cancel(
                    alarml.id!!.toInt()
                )
                alarmliste.removeAt(position)
                if (alarmliste.isEmpty()) {
                    myDB.resetincrement()
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
                }
                if (alarmliste.size==1){
                    notifyItemChanged(0)
                }
                else notifyItemRemoved(position)
            }
        }
    }

    override fun getItemCount(): Int {
       return alarmliste.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(alarmliste[position].id==null) 0 else 1
    }

    override fun getItemId(position: Int): Long {
        return alarmliste[position].hashCode().toLong()
    }
}