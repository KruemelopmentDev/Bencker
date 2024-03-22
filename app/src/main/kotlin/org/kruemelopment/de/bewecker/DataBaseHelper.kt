package org.kruemelopment.de.bewecker

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(var context: Context?) : SQLiteOpenHelper(context, Database_Name, null, 6) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("Create Table $Table_Name (ID INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT,Absender TEXT,Gruppe TEXT,Aktiv INTEGER,Package TEXT,Nachricht TEXT,Klingelton TEXT,Volume INTEGER,Volume_inrease INTEGER,Vibration INTEGER,NWV TEXT,NWN TEXT,Wiederholung INTEGER,Schlummerzeit TEXT,Wartezeit INTEGER,Exitsleep INTEGER,importance INTEGER,songtitel TEXT)")
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $Table_Name")
    }

    fun insertData(
        name: String?,
        absender: String?,
        gruppe: String?,
        aktiv: Int,
        packageName: String?,
        nachricht: String?,
        klingelton: String?,
        volume: Int,
        increase: Int,
        vibrate: Int,
        nwv: String?,
        nwn: String?,
        wdh: Int,
        schlummer: String?,
        wartezeit: Int,
        exitsleep: Int,
        prio: Int,
        songtitel: String?
    ): Boolean {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("Name", name)
        contentValues.put("Absender", absender)
        contentValues.put("Gruppe", gruppe)
        contentValues.put("Aktiv", aktiv)
        contentValues.put("Package", packageName)
        contentValues.put("Nachricht", nachricht)
        contentValues.put("Klingelton", klingelton)
        contentValues.put("Volume", volume)
        contentValues.put("Volume_inrease", increase)
        contentValues.put("Vibration", vibrate)
        contentValues.put("NWV", nwv)
        contentValues.put("NWN", nwn)
        contentValues.put("Wiederholung", wdh)
        contentValues.put("Schlummerzeit", schlummer)
        contentValues.put("Wartezeit", wartezeit)
        contentValues.put("Exitsleep", exitsleep)
        contentValues.put("importance", prio)
        contentValues.put("songtitel", songtitel)
        val result = database.insert(Table_Name, null, contentValues)
        database.close()
        return result != -1L
    }

    val allData: Cursor
        get() {
            val sqLiteDatabase = this.writableDatabase
            return sqLiteDatabase.rawQuery("Select * from $Table_Name", null)
        }

    fun getData(id: String?): Cursor {
        val sqLiteDatabase = this.writableDatabase
        return sqLiteDatabase.rawQuery("Select * from $Table_Name WHERE ID=?", arrayOf(id))
    }

    val lastData: Cursor
        get() {
            val sqLiteDatabase = this.writableDatabase
            return sqLiteDatabase.rawQuery(
                "Select * from $Table_Name  ORDER BY ID DESC",
                null
            )
        }

    fun deleteData(id: String?) {
        val db = this.writableDatabase
        db.delete(Table_Name, "ID=?", arrayOf(id))
    }

    fun updateData(
        id: String?,
        name: String?,
        absender: String?,
        gruppe: String?,
        packageName: String?,
        nachricht: String?,
        klingelton: String?,
        volume: Int,
        increase: Int,
        vibrate: Int,
        nwv: String?,
        nwn: String?,
        wdh: Int,
        schlummer: String?,
        wartezeit: Int,
        exitsleep: Int,
        prio: Int,
        songtitel: String?
    ): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("Name", name)
        contentValues.put("Absender", absender)
        contentValues.put("Gruppe", gruppe)
        contentValues.put("Aktiv", 1)
        contentValues.put("Package", packageName)
        contentValues.put("Nachricht", nachricht)
        contentValues.put("Klingelton", klingelton)
        contentValues.put("Volume", volume)
        contentValues.put("Volume_inrease", increase)
        contentValues.put("Vibration", vibrate)
        contentValues.put("NWV", nwv)
        contentValues.put("NWN", nwn)
        contentValues.put("Wiederholung", wdh)
        contentValues.put("Schlummerzeit", schlummer)
        contentValues.put("Wartezeit", wartezeit)
        contentValues.put("Exitsleep", exitsleep)
        contentValues.put("importance", prio)
        contentValues.put("songtitel", songtitel)
        val result = db.update(Table_Name, contentValues, "ID=?", arrayOf(id))
        return result > 0
    }

    fun updateAktiv(id: String?, aktiv: Int) {
        val db = this.writableDatabase
        db.execSQL("UPDATE $Table_Name SET Aktiv=$aktiv WHERE ID=$id")
    }

    fun getPrio(id: String?): Int {
        val sqLiteDatabase = this.writableDatabase
        val cursor =
            sqLiteDatabase.rawQuery("Select * from $Table_Name Where ID=$id", null)
        cursor.moveToFirst()
        val a = cursor.getInt(17)
        cursor.close()
        return a
    }

    fun deleteAllData() {
        val db = this.writableDatabase
        db.execSQL("delete from $Table_Name")
    }

    companion object {
        private const val Database_Name = "Alarms.db"
        private const val Table_Name = "default_table"
    }
}
