package org.kruemelopment.de.bewecker

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class ImportanceAdapter(
    var context: Context?,
    var importance: MutableList<ImportanceList>,
    var myDB: PrioDataBaseHelper,
    private val mCallback: IProcessFilter,
    private var startcontext: Fragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface IProcessFilter {
        fun onProcessFilter()
    }

    class MyViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var titel: TextView? = null
        var text: TextView? = null
        var imageView: ImageView? = null
        var parentView=view

        init {
            titel = view.findViewById(R.id.textView11)
            text = view.findViewById(R.id.textView12)
            imageView = view.findViewById(R.id.imageView3)
        }
    }


    class EmptyViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var text: com.rey.material.widget.TextView? = null

        init {
            text = view.findViewById(R.id.nothing)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(if(viewType==0)R.layout.emptyitem else R.layout.importance_item, parent, false)
        return if(viewType==0) EmptyViewHolder(v) else MyViewHolder(v)
    }

    override fun onBindViewHolder(generalholder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position)==0){
            (generalholder as EmptyViewHolder).text!!.text=context!!.getString(R.string.nopriorities)
            return
        }
        val holder = (generalholder as MyViewHolder)
        val liste = importance[position]
        holder.titel!!.visibility = View.VISIBLE
        if (liste.text != null) {
            holder.text!!.text = liste.text
            if (liste.text!!.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray().size <= 2) {
                holder.imageView!!.visibility = View.GONE
            } else {
                holder.imageView!!.visibility = View.VISIBLE
                holder.imageView!!.setOnClickListener {
                    if (holder.imageView!!.rotation == 0f || holder.imageView!!.rotation == 359.99f) {
                        holder.imageView!!.rotation = 0f
                        holder.imageView!!.animate().rotation(180f).setDuration(200)
                            .start()
                        holder.text!!.maxLines = Int.MAX_VALUE
                    } else {
                        holder.imageView!!.animate().rotation(359.99f).setDuration(200)
                            .start()
                        holder.text!!.maxLines = 2
                    }
                }
            }
            holder.text!!.maxLines = 2
        } else {
            holder.text!!.setText(R.string.nowords)
            holder.imageView!!.visibility = View.GONE
        }
        val text = context!!.getString(R.string.importanceadapter) + " " + liste.prio
        holder.titel!!.text = text
        holder.parentView.setOnLongClickListener {
            val alertDialog = AlertDialog.Builder(
                context
            )
                .setTitle(context!!.getString(R.string.confirm_delete)) //.setMessage()
                .setPositiveButton(context!!.getString(R.string.delete)) { dialog, _ ->
                    dialog.dismiss()
                    myDB.deletePrio(liste.prio)
                    val prio = importance[position].prio!!.toInt()
                    importance.removeAt(position)
                    notifyItemRemoved(position)
                    if (importance.isEmpty()) {
                        importance.add(ImportanceList(null, null, null))
                        mCallback.onProcessFilter()
                        notifyItemInserted(0)
                    } else {
                        for (a in importance) {
                            if (a.prio!!.toInt() > prio) {
                                myDB.updateprios(a.prio!!.toInt())
                                a.prio = (a.prio!!.toInt() - 1).toString()
                            }
                        }
                    }
                }
                .setNegativeButton(context!!.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                .setCancelable(true)
                .show()
            alertDialog.show()
            true
        }
        holder.parentView.setOnClickListener {
            val intent = Intent(context, ImportanceItemShow::class.java)
            intent.putExtra("level", liste.prio)
            intent.putExtra("id",liste.id)
            startcontext.startActivityForResult(intent, 3)
        }
    }

    override fun getItemCount(): Int {
        return importance.size
    }

    override fun getItemId(position: Int): Long {
        return importance[position].id!!.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return if(importance[position].id==null) 0 else 1
    }
}