package org.kruemelopment.de.bewecker

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rey.material.widget.FloatingActionButton
import com.rey.material.widget.SnackBar

class AllAlarms : Fragment(), CustomBaseAdapter.IProcessFilter {
    private var alarme: ArrayList<Alarmliste>? = null
    private var adapter: CustomBaseAdapter? = null
    var myDB: DataBaseHelper? = null
    private var snackBar: SnackBar? = null
    private var menuItem: MenuItem? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_main, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        snackBar = SnackBar(context)
        snackBar!!.padding(15, 15)
        snackBar!!.backgroundColor(ResourcesCompat.getColor(resources,R.color.colorPrimary,requireContext().theme))
        snackBar!!.textColor(ResourcesCompat.getColor(resources,R.color.white,requireContext().theme))
        snackBar!!.actionTextColor(ResourcesCompat.getColor(resources,R.color.white,requireContext().theme))
        snackBar!!.actionText(getString(R.string.verstanden))
        snackBar!!.actionClickListener { sb, _ -> sb.dismiss() }
        snackBar!!.text(getString(R.string.read_notifications))
        snackBar!!.singleLine(false)
        snackBar!!.duration(8000)
        val recyclerView = view.findViewById<RecyclerView>(R.id.uebersicht)
        alarme = ArrayList()
        myDB = DataBaseHelper(context)
        val res = myDB!!.allData
        if (res.count > 0) {
            while (res.moveToNext()) {
                val item = Alarmliste(
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
        )
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(100)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.recycleChildrenOnDetach = true
        recyclerView.layoutManager = layoutManager
        adapter = CustomBaseAdapter(context, alarme!!, myDB!!, snackBar!!, this)
        recyclerView.adapter = adapter
        val newalarm = view.findViewById<FloatingActionButton>(R.id.floating)
        newalarm.setOnClickListener {
            requireActivity().startActivityForResult(
                Intent(
                    requireActivity(),
                    FabNewAlarm::class.java
                ), 1
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.deleteallbewecker, menu)
        menuItem = menu.findItem(R.id.deleteall4)
        if (alarme!![0].id == null) {
            menuItem!!.setVisible(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteall4) {
            val alertDialog = AlertDialog.Builder(context)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage("Sollen alle Bencker gelöscht werden?")
                .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                    myDB!!.deleteAllData()
                    val oldsize= alarme!!.size
                    alarme!!.clear()
                    adapter!!.notifyItemRangeRemoved(0, oldsize)
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
                    adapter!!.notifyItemInserted(0)
                    dialog.dismiss()
                    menuItem!!.setVisible(false)
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                .setCancelable(true)
                .show()
            alertDialog.show()
        }
        return true
    }

    override fun onProcessFilter() {
        menuItem!!.setVisible(false)
    }

    fun update(type:Int) {
        if(type==1) {
            val sp2 = PreferenceManager.getDefaultSharedPreferences(requireContext())
            MainActivity.showtipp = sp2.getBoolean("reminder", true)
            val res = myDB!!.lastData
            res.moveToFirst()
            val item = Alarmliste(
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
            alarme!!.add(item)
            if (alarme!![0].id == null) {
                alarme!!.removeAt(0)
                adapter!!.notifyItemInserted(alarme!!.size)
            } else adapter!!.notifyItemInserted(alarme!!.size)
            res.close()
            menuItem!!.setVisible(true)
            if (MainActivity.showtipp) {
                snackBar!!.show(context as Activity?)
            }
        }
        else{
            alarme!!.clear()
            val res = myDB!!.allData
            if (res.count > 0) {
                while (res.moveToNext()) {
                    val item = Alarmliste(
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
                    alarme!!.add(item)
                }
            }
            res.close()
            adapter!!.notifyItemRangeChanged(0,alarme!!.size)
        }
    }
}