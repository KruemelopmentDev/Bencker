package org.kruemelopment.de.bewecker

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.rengwuxian.materialedittext.MaterialEditText
import com.rey.material.widget.Button
import es.dmoral.toasty.Toasty

class ImportanceItemAdapter(
    var context: Context,
    private var texte: ArrayList<ImportanceList>,
    var myDB: PrioDataBaseHelper,
    private val mCallback: IProcessFilter
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    interface IProcessFilter {
        fun onProcessFilter()
    }

    class MyViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var text: TextView? = null
        var imageView: ImageView? = null
        var parentView=view

        init {
            text = view.findViewById(R.id.textView15)
            imageView = view.findViewById(R.id.imageView4)
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
            .inflate(if(viewType==0)R.layout.emptyitem else R.layout.importanceitemadapter, parent, false)
        return if(viewType==0) EmptyViewHolder(v) else MyViewHolder(v)
    }

    override fun onBindViewHolder(generalholder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position)==0){
            (generalholder as EmptyViewHolder).text!!.text=context.getString(R.string.nowords)
            return
        }
        val holder = (generalholder as MyViewHolder)
        val liste=texte[position]
        holder.text!!.text = liste.text
        if (holder.text!!.lineCount <= 2) {
            holder.imageView!!.visibility = View.GONE
        } else {
            holder.imageView!!.visibility = View.VISIBLE
            holder.imageView!!.setOnClickListener {
                if (holder.imageView!!.rotation == 0f || holder.imageView!!.rotation == 359.99f) {
                    holder.imageView!!.rotation = 0f
                    holder.imageView!!.animate().rotation(180f).setDuration(200).start()
                    holder.text!!.maxLines = Int.MAX_VALUE
                } else {
                    holder.imageView!!.animate().rotation(359.99f).setDuration(200).start()
                    holder.text!!.maxLines = 2
                }
            }
        }
        holder.text!!.maxLines = 2
        holder.parentView.setOnClickListener {
            val dialog = Dialog(context, R.style.Dialog)
            dialog.setContentView(R.layout.newtext_dialog)
            val editText = dialog.findViewById<MaterialEditText>(R.id.editText34)
            editText.floatingLabelText = context.getString(R.string.editwords)
            editText.setText(liste.text)
            val safe = dialog.findViewById<TextView>(R.id.textView17)
            val cancel = dialog.findViewById<TextView>(R.id.textView18)
            val info = dialog.findViewById<ImageView>(R.id.imageView5)
            safe.setOnClickListener {
                if (editText.text.toString().isNotEmpty()) {
                    myDB.updateData(liste.id, liste.prio, editText.text.toString())
                    liste.text = editText.text.toString()
                    notifyItemChanged(position)
                    dialog.dismiss()
                } else Toasty.error(context, R.string.insertsomething, Toast.LENGTH_SHORT)
                    .show()
            }
            cancel.setOnClickListener { dialog.dismiss() }
            info.setOnClickListener {
                val dialog1 = Dialog(context, R.style.Dialog)
                dialog1.setContentView(R.layout.infodialog)
                val btn = dialog1.findViewById<Button>(R.id.btn)
                btn.setOnClickListener { dialog1.dismiss() }
                dialog1.show()
            }
            dialog.setCancelable(true)
            dialog.show()
        }
        holder.parentView.setOnLongClickListener {
            val alertDialog = AlertDialog.Builder(
                context
            )
                .setTitle(context.getString(R.string.confirm_delete)) //.setMessage()
                .setPositiveButton(context.getString(R.string.delete)) { dialog, _ ->
                    dialog.dismiss()
                    myDB.deleteNullItem(liste.id)//change
                    if (texte.size == 1) {
                        myDB.insertData(liste.prio, null)
                        texte.add(
                            ImportanceList(
                                myDB.getid(liste.prio).toString(),
                                liste.prio,
                                null
                            )
                        )
                        notifyItemChanged(position)
                        mCallback.onProcessFilter()
                    }
                    else notifyItemRemoved(position)
                    texte.removeAt(position)

                }
                .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                .setCancelable(true)
                .show()
            alertDialog.show()
            true
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(texte[position].id==null) 0 else 1
    }

    override fun getItemCount(): Int {
        return texte.size
    }

    override fun getItemId(position: Int): Long {
        return texte[position].id!!.toLong()
    }
}