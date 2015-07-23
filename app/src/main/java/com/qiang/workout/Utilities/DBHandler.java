package com.qiang.workout.Utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper
{
	// Common column names
	public static final String COLUMN_ID = "id";
	// Profiles table column names
	public static final String TABLE_PROFILES = "profiles";
	public static final String PROFILES_COLUMN_NAME = "name";
	public static final String PROFILES_COLUMN_MINUTES = "minutes";
	public static final String PROFILES_COLUMN_SECONDS = "seconds";
	public static final String PROFILES_COLUMN_REPEAT = "repeat";
	public static final String PROFILES_COLUMN_REPEAT_NUMBER = "repeatNumber";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "workout.db";

	public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
	{
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String queryProfiles = "CREATE TABLE " + TABLE_PROFILES + "("
				+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ PROFILES_COLUMN_NAME + " TEXT, "
				+ PROFILES_COLUMN_MINUTES + " INTEGER, "
				+ PROFILES_COLUMN_SECONDS + " INTEGER, "
				+ PROFILES_COLUMN_REPEAT + " INTEGER DEFAULT 0, "
				+ PROFILES_COLUMN_REPEAT_NUMBER + " INTEGER "
				+ ")";

		db.execSQL(queryProfiles);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
		onCreate(db);
	}
}