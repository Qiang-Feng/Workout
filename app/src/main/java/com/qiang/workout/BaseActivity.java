package com.qiang.workout;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BaseActivity extends AppCompatActivity
{
	private ListView drawerView;
	private DrawerLayout drawerLayout;
	private String[] drawerItemStrings;
	private ActionBarDrawerToggle drawerToggle;

	/*
		Sets up the BaseActivity (called when the activity is first created)
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Displays the "activity_base" layout
		setContentView(R.layout.activity_base);

		// Initialising variables
		drawerView = (ListView) findViewById(R.id.navigation_drawer);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerItemStrings = getResources().getStringArray(R.array.navigation_drawer_items);

		/*
            Initialising the drawerToggle listener:
            Controls the drawer toggle icon in the action bar
        */
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close)
		{
			@Override
			public void onDrawerClosed(View view)
			{
				super.onDrawerClosed(view);
			}

			@Override
			public void onDrawerOpened(View drawerView)
			{
				super.onDrawerOpened(drawerView);
			}
		};

		// Uses drawerToggle as listener for drawerLayout
		drawerLayout.setDrawerListener(drawerToggle);

		// Enables shadow under the navigation drawer (when open)
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		// Removes warning about java.lang.NullPointerException
		assert getSupportActionBar() != null;

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		// Displays the drawerItemStrings array in the navigation drawer list
		drawerView.setAdapter(new ArrayAdapter<>(this, R.layout.navigation_drawer_item, drawerItemStrings));
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);

		// Sync the toggle state after onRestoreInstanceState has occurred
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present
		getMenuInflater().inflate(R.menu.menu_base, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handles item selection in navigation drawer
		if (drawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}

		// Handles item selection in action bar
		switch (item.getItemId())
		{
			case R.id.action_settings:
				return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
