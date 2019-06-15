package com.fortin.carpool.helper;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBhelper {

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static final String DATABASE_NAME = "carpool.db";
	private static final int DATABASE_VERSION = 1;

	private final Context mCtx;


	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {

			//1. Create table Find History
			db.execSQL("Create table if not exists tblhistory(_id integer primary key autoincrement,source Text(30),source_lat Text(30),source_long Text(30),destination Text(30),dest_lat Text(30), dest_long Text(30))");
			Log.d("cuisine table","create");

		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			db.execSQL("DROP TABLE IF EXISTS tblhistory");

			onCreate(db);
		}
	}

	public void Reset() {
		mDbHelper.onUpgrade(this.mDb, 1, 1);
	}

	public DBhelper(Context ctx) {
		mCtx = ctx;
		mDbHelper = new DatabaseHelper(mCtx);
	}

	public SQLiteDatabase open() throws SQLException {
		mDb = mDbHelper.getWritableDatabase();
		mDbHelper.onCreate(mDb);
		return mDb;
	}

	public void close() {
		mDbHelper.close();
	}

}
