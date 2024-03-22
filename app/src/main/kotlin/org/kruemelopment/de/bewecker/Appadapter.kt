package org.kruemelopment.de.bewecker

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rey.material.widget.ImageView
import com.rey.material.widget.TextView
import java.util.Locale

class Appadapter(var context: Context?, private var items: MutableList<AppInfo>, var dialog: Dialog) :
    RecyclerView.Adapter<Appadapter.MyViewHolder>() {
    private var alle: MutableList<AppInfo> = ArrayList()

    init {
        alle.addAll(items)
    }

    class MyViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView? = null
        var packagename: TextView? = null
        var appicon: ImageView? = null
        var parentView=view

        init {
            name = view.findViewById(R.id.textView)
            packagename = view.findViewById(R.id.textView2)
            appicon = view.findViewById(R.id.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.appitem, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val rowItem = items[position]
        holder.name!!.text = rowItem.appname
        holder.packagename!!.text = rowItem.pname
        holder.appicon!!.setImageDrawable(rowItem.icon)
        holder.parentView.setOnClickListener {
            val lol = items[position]
            items.clear()
            items.add(lol)
            dialog.dismiss()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return items[position].pname.hashCode().toLong()
    }

    fun filter(charText: String) {
        val size=items.size
        if (charText.isEmpty()) {
            items.clear()
            items.addAll(alle)
        } else {
            val chart = charText.lowercase(Locale.getDefault())
            items.clear()
            for (lol in alle) {
                if (lol.pname.lowercase(Locale.getDefault())
                        .contains(chart) || lol.appname.lowercase(
                        Locale.getDefault()
                    ).contains(chart)
                ) {
                    items.add(lol)
                }
            }
        }
        if(size<items.size) notifyItemRangeInserted(items.size,items.size-size)
        else if (size>items.size) notifyItemRangeRemoved(items.size,size-items.size)
        notifyItemRangeChanged(0,items.size)
    }
}