package org.kruemelopment.de.bewecker

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rengwuxian.materialedittext.MaterialEditText
import com.rey.material.widget.Button
import com.rey.material.widget.FloatingActionButton
import com.rey.material.widget.TextView
import es.dmoral.toasty.Toasty

class ImportanceItemShow : AppCompatActivity(), ImportanceItemAdapter.IProcessFilter {

    var myDB: PrioDataBaseHelper? = null
    private var liste = ArrayList<ImportanceList>()
    private var level: String? = null
    private var adapter: ImportanceItemAdapter? = null
    private var menuitem: MenuItem? = null
    private var id:String? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme2)
        setContentView(R.layout.importanceitemshow)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val recyclerView = findViewById<RecyclerView>(R.id.dynamic)
        val fab = findViewById<FloatingActionButton>(R.id.floats)
        level = intent.getStringExtra("level")
        id = intent.getStringExtra("id")
        supportActionBar!!.title = getString(R.string.importanceadapter) + " " + level
        myDB = PrioDataBaseHelper(this)
        val res = myDB!!.getPrioData(level!!)
        res.moveToFirst()
        val list=ImportanceList(
            res.getString(0),
            res.getString(1),
            res.getString(2)
        )
        if(list.text!=null) {
            val help = list.text!!.split(";")
            for (el in help) {
                liste.add(ImportanceList(list.id, list.prio, el))
            }
        }
        res.close()
        if (liste.isEmpty()) liste.add(ImportanceList(null, level, null))
        adapter = ImportanceItemAdapter(this, liste, myDB!!, this)
        fab.setOnClickListener {
            val dialog = Dialog(this, R.style.Dialog)
            dialog.setContentView(R.layout.newtext_dialog)
            val editText = dialog.findViewById<MaterialEditText>(R.id.editText34)
            val safe = dialog.findViewById<TextView>(R.id.textView17)
            val cancel = dialog.findViewById<TextView>(R.id.textView18)
            val info = dialog.findViewById<ImageView>(R.id.imageView5)
            safe.setOnClickListener {
                if (editText.text.toString().isNotEmpty()) {
                    dialog.dismiss()
                    liste.add(
                        ImportanceList(
                            myDB!!.lastid().toString(),
                            level,
                            editText.text.toString()
                        )
                    )
                    if (liste[0].text == null) {
                        liste.removeAt(0)
                        myDB!!.updateData(id,level, editText.text.toString())
                        adapter!!.notifyItemChanged(0)
                    }
                    else {
                        var help =""
                        for (element in liste) help+=element.text+";"
                        help=help.substring(0, help.length-1)
                        myDB!!.updateData(id,level,help)
                        adapter!!.notifyItemInserted(liste.size)
                    }
                    menuitem!!.setVisible(true)
                } else Toasty.error(
                    this,
                    R.string.insertsomething,
                    Toast.LENGTH_SHORT
                ).show()
            }
            cancel.setOnClickListener { dialog.dismiss() }
            info.setOnClickListener {
                val dialog1 = Dialog(this, R.style.Dialog)
                dialog1.setContentView(R.layout.infodialog)
                val btn = dialog1.findViewById<Button>(R.id.btn)
                btn.setOnClickListener { dialog1.dismiss() }
                dialog1.show()
            }
            dialog.setCancelable(true)
            dialog.show()
        }
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(100)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.recycleChildrenOnDetach = true
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.deleteall2, menu)
        menuitem = menu.findItem(R.id.deleteall2)
        if (liste[0].text == null) {
            menuitem!!.setVisible(false)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) // Press Back Icon
        {
            this.setResult(RESULT_OK)
            finish()
        } else if (item.itemId == R.id.deleteall2) {
            val alertDialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.deleteallimportancewords))
                .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                    myDB!!.updateData(id,level,null)
                    val oldsize=liste.size
                    liste.clear()
                    adapter!!.notifyItemRangeRemoved(1,oldsize)
                    liste.add(ImportanceList(null, level, null))
                    adapter!!.notifyItemChanged(0)
                    dialog.dismiss()
                    menuitem!!.setVisible(false)
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                .setCancelable(true)
                .show()
            alertDialog.show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onProcessFilter() {
        menuitem!!.setVisible(false)
    }
}
