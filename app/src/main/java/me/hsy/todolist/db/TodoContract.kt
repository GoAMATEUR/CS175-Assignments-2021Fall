package me.hsy.todolist.db

import android.provider.BaseColumns

object TodoContract {
    // SQL command to create table
    const val SQL_CREATE_NOTES = ("CREATE TABLE " + TodoNote.TABLE_NAME
            + "(" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TodoNote.COLUMN_DATE + " INTEGER, "
            + TodoNote.COLUMN_STATE + " INTEGER, "
            + TodoNote.COLUMN_CONTENT + " TEXT, "
            + TodoNote.COLUMN_PRIORITY + " INTEGER)")

    // add priority column to
    const val SQL_ADD_PRIORITY_COLUMN =
        "ALTER TABLE " + TodoNote.TABLE_NAME + " ADD " + TodoNote.COLUMN_PRIORITY + " INTEGER"

    // Database information
    object TodoNote : BaseColumns {
        const val TABLE_NAME = "note"
        const val COLUMN_DATE = "date"
        const val COLUMN_STATE = "state"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_PRIORITY = "priority"
        const val _ID = BaseColumns._ID // cannot directly use ._ID?
    }
}