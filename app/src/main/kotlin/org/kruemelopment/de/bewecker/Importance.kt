package org.kruemelopment.de.bewecker

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rey.material.widget.FloatingActionButton

class Importance : Fragment(), ImportanceAdapter.IProcessFilter {
    var adapter: ImportanceAdapter? = null
    private var list = ArrayList<ImportanceList>()
    var myDB: PrioDataBaseHelper? = null
    private var menuItem: MenuItem? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.importance, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.dynamic)
        val fab = view.findViewById<FloatingActionButton>(R.id.floating)
        fab.setOnClickListener {
            val level = (myDB!!.lastlevel() + 1).toString()
            myDB!!.insertData(level, null)
            if (list[0].prio == null) list.removeAt(0)
            list.add(ImportanceList(myDB!!.getid(level).toString(), level, null))
            if(list.size==1)adapter!!.notifyItemChanged(0)
            else adapter!!.notifyItemInserted(list.size-1)
            menuItem!!.setVisible(true)
        }
        myDB = PrioDataBaseHelper(context)
        val res = myDB!!.allData
        if (res.count > 0) {
            while (res.moveToNext()) {
                list.add(ImportanceList(res.getString(0), res.getString(1), res.getString(2)))
            }
        }
        res.close()
        if (list.isEmpty()) list.add(ImportanceList(null, null, null))
        for (element in list){
            if(element.text==null) element.text=getString(R.string.nowords)
            else element.text=element.text!!.replace(";","\n")
        }
        adapter = ImportanceAdapter(context, list, myDB!!, this, this)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(100)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.recycleChildrenOnDetach = true
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter=adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.deleteall, menu)
        menuItem = menu.findItem(R.id.deleteall)
        if (list[0].prio == null) {
            menuItem!!.setVisible(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteall) {
            val alertDialog = AlertDialog.Builder(context)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.deleteallimportances))
                .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                    myDB!!.deleteAllData()
                    val oldsize=list.size
                    list.clear()
                    dialog.dismiss()
                    list.add(ImportanceList(null, null, null))
                    adapter!!.notifyItemChanged(0)
                    adapter!!.notifyItemRangeRemoved(1,oldsize-1)
                    menuItem!!.setVisible(false)
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                .setCancelable(true)
                .show()
            alertDialog.show()
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 3) {
            val helpList=ArrayList<ImportanceList>()
            val res = myDB!!.allData
            if (res.count > 0) {
                while (res.moveToNext()) {
                    helpList.add(ImportanceList(res.getString(0), res.getString(1), res.getString(2)))
                }
            }
            res.close()
            for(i in list.indices){
                if(list[i].text!=helpList[i].text){
                    if(helpList[i].text!=null) list[i].text=helpList[i].text!!.replace(";","\n")
                    else list[i].text=null
                    adapter!!.notifyItemChanged(i)
                    break
                }
            }
        }
    }

    override fun onProcessFilter() {
        menuItem!!.setVisible(false)
    }
}
