package org.kruemelopment.de.bewecker

import android.content.ContentValues


import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SchlafendeBaseHelper(var context: Context?) :
    SQLiteOpenHelper(context, Database_Name, null, 1) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("Create Table $Table_Name (ID INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT,Absender TEXT,Gruppe TEXT,Package TEXT,Nachricht TEXT,Schlafzeit TEXT)")
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $Table_Name")
    }

    fun insertData(
        name: String?,
        absender: String?,
        gruppe: String?,
        packageName: String?,
        nachricht: String?,
        zeit: String?
    ): Boolean {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("Name", name)
        contentValues.put("Absender", absender)
        contentValues.put("Gruppe", gruppe)
        contentValues.put("Package", packageName)
        contentValues.put("Nachricht", nachricht)
        contentValues.put("Schlafzeit", zeit)
        val result = database.insert(Table_Name, null, contentValues)
        database.close()
        return result != -1L
    }

    val allData: Cursor
        get() {
            val sqLiteDatabase = this.writableDatabase
            return sqLiteDatabase.rawQuery("Select * from $Table_Name", null)
        }

    fun checkactiv(id: String?): Boolean {
        val sqLiteDatabase = this.writableDatabase
        val r = sqLiteDatabase.rawQuery("Select * from $Table_Name WHERE ID = $id", null)
        val a = r.moveToFirst()
        r.close()
        return a
    }

    fun deleteData(id: String?) {
        val db = this.writableDatabase
        db.delete(Table_Name, "ID=?", arrayOf(id))
    }

    fun deleteAllData() {
        val db = this.writableDatabase
        db.execSQL("delete from $Table_Name")
    }

    fun getlastid(): String {
        val sqLiteDatabase = this.writableDatabase
        val r = sqLiteDatabase.rawQuery("Select * from $Table_Name", null)
        r.moveToLast()
        val a = r.getString(0)
        r.close()
        return a
    }

    fun resetincrement() {
        val db = this.writableDatabase
        db.delete(Table_Name, null, null)
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '$Table_Name'")
    }

    fun updatezeit(zeit: String, id: String?) {
        val db = this.writableDatabase
        db.execSQL("UPDATE $Table_Name SET Schlafzeit='$zeit' WHERE ID=$id")
    }

    companion object {
        private const val Database_Name = "Schlafende.db"
        private const val Table_Name = "default_table"
    }
}
