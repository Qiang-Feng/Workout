package com.qiang.workout.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.qiang.workout.Models.Category;
import com.qiang.workout.Models.Profile;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper
{
	// Common column names
	public static final String COLUMN_ID = "id";

	// Profiles table
	public static final String TABLE_PROFILES = "profiles";
	public static final String PROFILES_COLUMN_NAME = "name";
	public static final String PROFILES_COLUMN_MINUTES = "minutes";
	public static final String PROFILES_COLUMN_SECONDS = "seconds";
	public static final String PROFILES_COLUMN_REPEAT = "repeat";
	public static final String PROFILES_COLUMN_REPEAT_NUMBER = "repeatNumber";
	private static final int DATABASE_VERSION = 1;

	// Categories table
	public static final String TABLE_CATEGORIES = "categories";
	public static final String CATEGORIES_COLUMN_NAME = "name";

	private static final String DATABASE_NAME = "workout.db";

	public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
	{
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// Create the profiles table
		String queryProfiles = "CREATE TABLE " + TABLE_PROFILES + "("
				+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ PROFILES_COLUMN_NAME + " TEXT, "
				+ PROFILES_COLUMN_MINUTES + " INTEGER, "
				+ PROFILES_COLUMN_SECONDS + " INTEGER, "
				+ PROFILES_COLUMN_REPEAT + " INTEGER DEFAULT 0, "
				+ PROFILES_COLUMN_REPEAT_NUMBER + " INTEGER "
				+ ")";

		db.execSQL(queryProfiles);

		// Create the categories table
		String queryCategories = "CREATE TABLE " + TABLE_CATEGORIES + "("
				+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ CATEGORIES_COLUMN_NAME + " TEXT "
				+ ")";

		db.execSQL(queryCategories);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
		onCreate(db);
	}

    /*
        Profiles table methods
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

	public int getProfilesCount()
	{
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT '1' FROM " + TABLE_PROFILES, null);

		int profilesCount = cursor.getCount();

		cursor.close();

		return profilesCount;
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
		String[] whereArgs = { Integer.toString(profileID) };

		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROFILES + " WHERE " + COLUMN_ID + " = ?", whereArgs);
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

	public void saveEditedProfile(Profile profile)
	{
		SQLiteDatabase db = getWritableDatabase();
		String[] profileExistsArgs = { Integer.toString(profile.getID()) };
		Cursor profileExists = db.rawQuery("SELECT '1' FROM " + TABLE_PROFILES + " WHERE " + COLUMN_ID + " = ?", profileExistsArgs);

		// Only update if profile exists
		if (profileExists.getCount() != 0)
		{
			String[] updateQueryArgs =
			{
				profile.getName(),
				Integer.toString(profile.getMinutes()),
				Integer.toString(profile.getSeconds()),
				Integer.toString((profile.isRepeat()) ? 1 : 0),
				Integer.toString(profile.getRepeatNumber()),
				Integer.toString(profile.getID())
			};

			String updateQuery = "UPDATE " + TABLE_PROFILES + " SET "
					+ PROFILES_COLUMN_NAME + " = ?,"
					+ PROFILES_COLUMN_MINUTES + " = ?,"
					+ PROFILES_COLUMN_SECONDS + " = ?,"
					+ PROFILES_COLUMN_REPEAT + " = ?,"
					+ PROFILES_COLUMN_REPEAT_NUMBER + " = ?"
					+ " WHERE " + COLUMN_ID + " = ?";

			db.execSQL(updateQuery, updateQueryArgs);
		}
		else
		{
			// Just add the profile
			addProfile(profile);
		}

		profileExists.close();
	}

	public void deleteProfile(int profileID)
	{
		SQLiteDatabase db = getWritableDatabase();
		String[] whereArgs = { Integer.toString(profileID) };
		db.delete(TABLE_PROFILES, COLUMN_ID + " = ?", whereArgs);
	}

	/*
		Categories table methods
	*/
	public void addCategory(Category category)
	{
		ContentValues values = new ContentValues();

		// Adds values set in Category object
		values.put(CATEGORIES_COLUMN_NAME, category.getName());

		SQLiteDatabase db = getWritableDatabase();
		db.insert(TABLE_CATEGORIES, null, values);
	}

	public int getCategoriesCount()
	{
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT '1' FROM " + TABLE_CATEGORIES, null);

		int categoriesCount = cursor.getCount();

		cursor.close();

		return categoriesCount;
	}

	public List<Category> allCategories()
	{
		List<Category> categoryList = new ArrayList<>();

		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES, null);

		if (cursor.moveToFirst())
		{
			// Adds each category to categoryList
			do
			{
				// Creates a new Category object with data from database
				Category category = new Category(cursor.getString(1));
				category.setID(cursor.getInt(0));

				categoryList.add(category);
			}
			while (cursor.moveToNext());
		}

		cursor.close();

		return categoryList;
	}

	public Category getCategory(int categoryID)
	{
		SQLiteDatabase db = getWritableDatabase();
		String[] whereArgs = { Integer.toString(categoryID) };

		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_ID + " = ?", whereArgs);
		cursor.moveToFirst();

		// Creates a new Category object with data from database
		Category category = new Category(cursor.getString(1));
		category.setID(cursor.getInt(0));

		cursor.close();

		return category;
	}

	public void saveEditedCategory(Category category)
	{
		SQLiteDatabase db = getWritableDatabase();
		String[] categoryExistsArgs = { Integer.toString(category.getID()) };
		Cursor categoryExists = db.rawQuery("SELECT '1' FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_ID + " = ?", categoryExistsArgs);

		// Only update if category exists
		if (categoryExists.getCount() != 0)
		{
			String[] updateQueryArgs = { category.getName() };

			String updateQuery = "UPDATE " + CATEGORIES_COLUMN_NAME + " SET "
					+ CATEGORIES_COLUMN_NAME + " = ?,"
					+ " WHERE " + COLUMN_ID + " = ?";

			db.execSQL(updateQuery, updateQueryArgs);
		}
		else
		{
			// Just add the category
			addCategory(category);
		}

		categoryExists.close();
	}

	public void deleteCategory(int categoryID)
	{
		SQLiteDatabase db = getWritableDatabase();
		String[] whereArgs = { Integer.toString(categoryID) };
		db.delete(TABLE_CATEGORIES, COLUMN_ID + " = ?", whereArgs);
	}
}