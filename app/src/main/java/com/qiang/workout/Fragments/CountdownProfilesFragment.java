package com.qiang.workout.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.qiang.workout.Models.Profile;
import com.qiang.workout.ProfileActivity;
import com.qiang.workout.R;
import com.qiang.workout.Utilities.DBHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountdownProfilesFragment extends Fragment
{
	// Profiles list components
	private ListView profilesView;
	private Map<Integer, Integer> profileIDMap;

	// Database
	private DBHandler dbHandler;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Inflates the view
		View view = inflater.inflate(R.layout.fragment_countdown_profiles, container, false);

		// Initialising components
		// Profiles list
		profilesView = (ListView) view.findViewById(R.id.profiles_list);
		profileIDMap = new HashMap<>();

		// Database
		dbHandler = new DBHandler(getActivity(), null, null, 1);

		loadProfileList();

		// Creates popup dialog with actions when an item has been selected in the list
		registerForContextMenu(profilesView);

		// Handles add profile button click
		view.findViewById(R.id.button_add_profile).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Launches new profile activity
				Intent intent = new Intent(getActivity(), ProfileActivity.class);
				startActivityForResult(intent, 1);
			}
		});

		return view;
	}

	private void loadProfileList()
	{
	    /*
	        Puts all profile names in the profileStrings list
            Updates the profileIDMap - keeps track of which profile is in which position in the list
        */
		List<Profile> profileList = dbHandler.allProfiles();
		List<String> profileStrings = new ArrayList<>();

		for (int i = 0; i < profileList.size(); i++)
		{
			profileStrings.add(i, profileList.get(i).getName());
			profileIDMap.put(i, profileList.get(i).getID());
		}

		// Displays the profileStrings array in the profiles list
		profilesView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.list_item, profileStrings));
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
	{
		// Create edit and delete menu items if user selected from profile list
		if (view.getId() == R.id.profiles_list)
		{
			menu.add(getResources().getString(R.string.edit));
			menu.add(getResources().getString(R.string.delete));
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		if (item.getTitle().toString().equals(getResources().getString(R.string.delete)))
		{
			// Deletes profile and updates the profile list
			dbHandler.deleteProfile(profileIDMap.get(info.position));
			loadProfileList();
		}
		else if (item.getTitle().toString().equals(getResources().getString(R.string.edit)))
		{
			// Redirects user to the ProfileActivity screen (for editing of the chosen profile)
			Intent intent = new Intent(getActivity(), ProfileActivity.class);
			intent.putExtra("profileID", profileIDMap.get(info.position));
			startActivityForResult(intent, 1);
		}

		return super.onContextItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// Only reload profile list if profile has been added or edited
		if (resultCode == ProfileActivity.PROFILE_ADDED || resultCode == ProfileActivity.PROFILE_EDITED)
		{
			// Reloads profile list
			loadProfileList();
		}
	}
}