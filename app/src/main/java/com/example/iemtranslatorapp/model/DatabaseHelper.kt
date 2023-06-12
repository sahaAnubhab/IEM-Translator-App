package com.example.iemtranslatorapp.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "translator.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_HISTORY = "history"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_SOURCE = "source"
        private const val COLUMN_TRANSLATION = "translation"
        private const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_HISTORY " +
                "($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_SOURCE TEXT, " + "$COLUMN_TRANSLATION TEXT,"+ " $COLUMN_TIMESTAMP TEXT)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
        onCreate(db)
    }

    fun onAdd(translation: String, source : String, timestamp:String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SOURCE, source)
            put(COLUMN_TRANSLATION, translation)
            put(COLUMN_TIMESTAMP, timestamp)
        }
        db.insert(TABLE_HISTORY, null, values)
        db.close()
    }

    fun onDelete(translation: String) {
        val db = writableDatabase
        db.delete(TABLE_HISTORY, "$COLUMN_TRANSLATION=?", arrayOf(translation))
        db.close()
    }

    fun deleteAll(){
        val db = writableDatabase
        db.execSQL("delete from $TABLE_HISTORY");

    }
}
