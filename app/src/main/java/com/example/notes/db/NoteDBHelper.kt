package com.example.notes.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.util.*
import kotlin.collections.ArrayList

class NoteDBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?)
    : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_NOTE_TABLE = "CREATE TABLE " +
                TABLE_NAME +
                "(" + ColumnId + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                ColumnNoteText + " TEXT" + ")"
        db?.execSQL(CREATE_NOTE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addNote(noteText:String){
        val values = ContentValues()
        values.put(ColumnNoteText, noteText)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
    }

    fun getAllNotes(): ArrayList<String>?{
        val db = this.readableDatabase
        var list_data : ArrayList<String> = ArrayList()
        var c = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        var i : Int = 0
//        var list: MutableList<String> = mutableListOf<String>()
        c!!.moveToFirst()
        if (c?.count != 0) {
            while (c.moveToNext()) {
                i++
                var n = c.getString(c.getColumnIndex(NoteDBHelper.ColumnNoteText))
                var id = c.getString(c.getColumnIndex(NoteDBHelper.ColumnId))
//                noet.text = noet.text.toString() + n
                list_data.add(n)
            }
            c.close()
        }
        return list_data
    }

    fun getSearchData(query : String): ArrayList<String>?
    {
        val db = this.readableDatabase
        var list_data : ArrayList<String> = ArrayList()
//        var c = db.query(TABLE_NAME, arrayOf<String>(ColumnNoteText),"$ColumnNoteText = ?", arrayOf<String>("Ha bhai"), null,null,null)
        var c = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $ColumnNoteText LIKE '%$query%'", null)
        var i : Int = 0
//        var list: MutableList<String> = mutableListOf<String>()
        c!!.moveToFirst()
        if (c?.count != 0) {
            while (c.moveToNext()) {
                i++
                var n = c.getString(c.getColumnIndex(NoteDBHelper.ColumnNoteText))
                var id = c.getString(c.getColumnIndex(NoteDBHelper.ColumnId))
//                noet.text = noet.text.toString() + n
                list_data.add(n)
            }
            c.close()
        }
        return list_data
    }

    fun deleteNote(i: String):Boolean {
        val db = this.writableDatabase
        val c = db.delete(TABLE_NAME, "$ColumnNoteText='$i'",null )
        return c > 0
    }

    companion object{
        private val DATABASE_VERSION: Int = 1
        private val DATABASE_NAME: String = "NotesDB"
        val TABLE_NAME = "NotesTable"
        var ColumnId = "id"
        var ColumnNoteText = "noteText"
    }

}