package com.qiang.workout.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.qiang.workout.Models.Profile;

import java.util.ArrayList;
import java.util.List;

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

    /*
        Profile table methods
    */

	public void addProfile(Profile profile)
	{
		ContentValues values = new ContentValues();

		// Adds values set in Profile object
		values.put(PROFILES_COLUMN_NAME, profile.getName());
		values.put(PROFILES_COLUMN_MINUTES, profile.getMinutes());
		values.put(PROFILES_COLUMN_SECONDS, profile.getSeconds());
		values.put(PROFILES_COLUMN_REPEAT, profile.isRepeat());
		values.put(PROFILES_COLUMN_REPEAT_NUMBER, profile.getRepeatNumber());

		SQLiteDatabase db = getWritableDatabase();
		db.insert(TABLE_PROFILES, null, values);
	}

	public List<Profile> allProfiles()
	{
		List<Profile> profileList = new ArrayList<>();

		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROFILES, null);

		if (cursor.moveToFirst())
		{
			// Adds each profile to profilesList
			do
			{
				// Creates a new Profile object with data from database
				Profile profile = new Profile(cursor.getString(1));
				profile.setID(cursor.getInt(0));
				profile.setMinutes(cursor.getInt(2));
				profile.setSeconds(cursor.getInt(3));
				profile.setRepeat(cursor.getInt(4) == 1);
				profile.setRepeatNumber(cursor.getInt(5));

				profileList.add(profile);
			}
			while (cursor.moveToNext());
		}

		cursor.close();

		return profileList;
	}

	public Profile getProfile(int profileID)
	{
		SQLiteDatabase db = getWritableDatabase();
		String[] whereArgs = new String[]
				{
						Integer.toString(profileID)
				};

		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROFILES + " WHERE id = ?", whereArgs);
		cursor.moveToFirst();

		// Creates a new Profile object with data from database
		Profile profile = new Profile(cursor.getString(1));
		profile.setID(cursor.getInt(0));
		profile.setMinutes(cursor.getInt(2));
		profile.setSeconds(cursor.getInt(3));
		profile.setRepeat(cursor.getInt(4) == 1);
		profile.setRepeatNumber(cursor.getInt(5));

		cursor.close();

		return profile;
	}

	public void deleteProfile(int profileID)
	{
		SQLiteDatabase db = getWritableDatabase();
		String[] whereArgs = new String[]
				{
						Integer.toString(profileID)
				};
		db.delete(TABLE_PROFILES, "id = ?", whereArgs);
	}
}