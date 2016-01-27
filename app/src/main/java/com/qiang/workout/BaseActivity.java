package com.qiang.workout;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.qiang.workout.Fragments.CountdownFragment;
import com.qiang.workout.Fragments.CountdownProfilesFragment;
import com.qiang.workout.Fragments.StopwatchCategoriesFragment;
import com.qiang.workout.Fragments.StopwatchFragment;

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

		// Handles item selection in the navigation drawer
		drawerView.setOnItemClickListener(new ListView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				selectItem(position);
			}
		});

		// Selects first item (default) in navigation drawer to be displayed
		selectItem(0);
	}

	private void selectItem(int position)
	{
		Fragment fragment = null;
		String itemSelected = drawerItemStrings[position];

		// Sets up fragment object corresponding to the chosen item
		if (itemSelected.equals(getString(R.string.navigation_item_countdown)))
		{
			fragment = new CountdownFragment();
		}
		else if (itemSelected.equals(getString(R.string.navigation_item_countdown_profiles)))
		{
			fragment = new CountdownProfilesFragment();
		}
		else if (itemSelected.equals(getString(R.string.navigation_item_stopwatch)))
		{
			fragment = new StopwatchFragment();
		}
		else if (itemSelected.equals(getString(R.string.navigation_item_stopwatch_categories)))
		{
			fragment = new StopwatchCategoriesFragment();
		}

		// Replaces content_frame with the fragment corresponding to item selected in navigation drawer
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment)
				.commit();

		setTitle(drawerItemStrings[position]);      // Changes the action bar title to corresponding item user has selected
		drawerView.setItemChecked(position, true);  // Sets item selected by user as checked
		drawerLayout.closeDrawer(drawerView);       // Closes the navigation drawer
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