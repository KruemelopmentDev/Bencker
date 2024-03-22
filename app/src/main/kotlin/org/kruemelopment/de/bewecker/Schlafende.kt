package org.kruemelopment.de.bewecker

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rey.material.widget.SnackBar

class Schlafende : Fragment() {
    private var alarme: MutableList<Alarmliste>? = null
    var adapter: CustomSleepAdapter? = null
    var myDB: SchlafendeBaseHelper? = null
    private var snackBar: SnackBar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sleepactivity, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        snackBar = SnackBar(context)
        snackBar!!.padding(15, 15)
        snackBar!!.backgroundColor(ResourcesCompat.getColor(resources,R.color.colorPrimary,requireContext().theme))
        snackBar!!.textColor(ResourcesCompat.getColor(resources,R.color.white,requireContext().theme))
        snackBar!!.actionText(getString(R.string.yes))
        snackBar!!.actionTextColor(ResourcesCompat.getColor(resources,R.color.white,requireContext().theme))
        snackBar!!.actionClickListener { sb, _ ->
            deaktivieren()
            sb.dismiss()
        }
        snackBar!!.text(getString(R.string.sleepalarmsde))
        snackBar!!.lines(1)
        snackBar!!.textAppearance(View.TEXT_ALIGNMENT_CENTER)
        snackBar!!.singleLine(true)
        snackBar!!.duration(3000)
        val recyclerView = view.findViewById<RecyclerView>(R.id.dynamic)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(100)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.recycleChildrenOnDetach = true
        recyclerView.layoutManager = layoutManager
        alarme = ArrayList()
        myDB = SchlafendeBaseHelper(context)
        val res = myDB!!.allData
        if (res.count > 0) {
            while (res.moveToNext()) {
                val item = Alarmliste(
                    res.getString(0),
                    res.getString(1),
                    res.getString(2),
                    res.getString(3),
                    0,
                    res.getString(4),
                    res.getString(5),
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
                    res.getString(6)
                )
                alarme!!.add(item)
            }
        }
        res.close()
        if (alarme!!.isEmpty()) alarme!!.add(
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
        ) else snackBar!!.show(
            context as Activity?
        )
        adapter = CustomSleepAdapter(context, alarme!!)
        recyclerView.adapter = adapter
    }

    private fun deaktivieren() {
        val mNotificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val sp2 = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val make = sp2.getBoolean("schlummerbaricon", false)
        for (a in alarme!!) {
            if (make) mNotificationManager.cancel(a.id!!.toInt())
        }
        myDB!!.deleteAllData()
        myDB!!.resetincrement()
        val oldsize=alarme!!.size
        alarme!!.clear()
        alarme!!.add(
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
        adapter!!.notifyItemRangeRemoved(1,oldsize-1)
        adapter!!.notifyItemChanged(0)
        //TODO alle löschen geht ne korrekt
    }
}