package org.kruemelopment.de.bewecker

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PrioDataBaseHelper(var context: Context?) :
    SQLiteOpenHelper(context, Database_Name, null, 1) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("Create Table $Table_Name (ID INTEGER PRIMARY KEY AUTOINCREMENT, Level INTEGER,Words TEXT)")
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $Table_Name")
    }

    fun insertData(level: String?, words: String?): Boolean {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("Level", level)
        contentValues.put("Words", words)
        val result = database.insert(Table_Name, null, contentValues)
        database.close()
        return result != -1L
    }

    val allData: Cursor
        get() {
            val sqLiteDatabase = this.writableDatabase
            return sqLiteDatabase.rawQuery(
                "Select * from $Table_Name ORDER BY Level ASC",
                null
            )
        }

    fun getPriosbelow(level: String): Cursor {
        val sqLiteDatabase = this.writableDatabase
        return sqLiteDatabase.rawQuery(
            "Select * from $Table_Name WHERE Level > $level",
            null
        )
    }
    fun getPrioData(level: String): Cursor {
        val sqLiteDatabase = this.writableDatabase
        return sqLiteDatabase.rawQuery(
            "Select * from $Table_Name WHERE Level = $level",
            null
        )
    }
    fun deleteNullItem(id: String?) {
        val db = this.writableDatabase
        db.delete(Table_Name, "ID=?", arrayOf(id))
    }

    fun deletePrio(prio: String?) {
        val db = this.writableDatabase
        db.delete(Table_Name, "Level=?", arrayOf(prio))
    }

    fun updateData(id: String?, level: String?, words: String?): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("Level", level)
        contentValues.put("Words", words)
        val result = db.update(Table_Name, contentValues, "ID=?", arrayOf(id))
        return result > 0
    }

    fun lastlevel(): Int {
        val sqLiteDatabase = this.writableDatabase
        val cur =
            sqLiteDatabase.rawQuery("Select * from $Table_Name ORDER BY Level ASC", null)
                ?: return 0
        if (!cur.moveToLast()) {
            return 0
        }
        val last = cur.getInt(1)
        cur.close()
        return last
    }

    fun lastid(): Int {
        val sqLiteDatabase = this.writableDatabase
        val cur = sqLiteDatabase.rawQuery("Select * from $Table_Name ORDER BY ID ASC", null)
        cur.moveToLast()
        val last = cur.getInt(0)
        cur.close()
        return last
    }

    fun getid(level: String?): Int {
        val sqLiteDatabase = this.writableDatabase
        val cur =
            sqLiteDatabase.rawQuery("Select * from $Table_Name WHERE Level=$level", null)
        cur.moveToFirst()
        val last = cur.getInt(0)
        cur.close()
        return last
    }

    fun updateprios(level: Int) {
        val sqLiteDatabase = this.writableDatabase
        sqLiteDatabase.execSQL("UPDATE " + Table_Name + " SET Level = " + (level - 1) + " WHERE Level = " + level.toString())
    }

    fun deleteAllData() {
        val sqLiteDatabase = this.writableDatabase
        sqLiteDatabase.execSQL("delete from $Table_Name")
    }

    companion object {
        private const val Database_Name = "Importances.db"
        private const val Table_Name = "default_table"
    }
}
