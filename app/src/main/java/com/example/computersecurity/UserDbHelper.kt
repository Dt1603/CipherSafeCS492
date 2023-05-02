package com.example.computersecurity

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import java.security.MessageDigest

class UserDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        const val DATABASE_NAME = "auth.db"
        const val DATABASE_VERSION = 1

        const val TABLE_NAME = "user_table"

        const val COLUMN_ID = "id"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val SQL_CREATE_TABLE =
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY," +
                    "$COLUMN_EMAIL TEXT UNIQUE," +
                    "$COLUMN_PASSWORD TEXT)"

        db?.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(SQL_DELETE_TABLE)
        onCreate(db)
    }

    fun checkUserCredentials(email: String, password: String): Boolean {

            val db = this.readableDatabase
            val cursor = db.rawQuery(
                "SELECT * FROM $TABLE_NAME WHERE $COLUMN_EMAIL=?",
                arrayOf(email)
            )

            var isUserValid = false

            if (cursor.moveToFirst()) {
                val storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
                val hashedPassword = hashPassword(password)

                if (hashedPassword == storedPassword) {
                    isUserValid = true
                }
            }

            cursor.close()
            return isUserValid
        }



        fun createUser(email: String, password: String, confirmPassword: String): Long {
        val db = this.writableDatabase

        if (password != confirmPassword) {
            throw Exception("Passwords do not match.")
        }

        val hashedPassword = hashPassword(password)

        val contentValues = ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, hashedPassword)
        }

        return db.insert(TABLE_NAME, null, contentValues)
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

    fun getUserByEmail(email: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_EMAIL=?", arrayOf(email))
    }
}

