package com.tejasdev.bunkbuddy.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class MigrationFrom1to2: Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS history (" +
            "id INTEGER PRIMARY KEY NOT NULL AUTOINCREMENT," +
            "actionType INTEGER NOT NULL," +
            "message TEXT NOT NULL," +
            "time TEXT NOT NULL," +
            "date TEXT NOT NULL" +
            ")"
        )
    }

}