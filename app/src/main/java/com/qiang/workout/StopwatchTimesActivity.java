package com.qiang.workout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.qiang.workout.Models.Category;
import com.qiang.workout.Models.StopwatchTime;
import com.qiang.workout.Utilities.DBHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StopwatchTimesActivity extends AppCompatActivity
{
	private ListView stopwatchTimesView;
	private List<String> stopwatchTimesStrings;
	private Map<Integer, Integer> stopwatchTimesIDMap;
	private Category selectedCategory;

	private DBHandler dbHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Sets the activity_stopwatch_times as this activity's display
		setContentView(R.layout.activity_stopwatch_times);

		// Initialise variables
		stopwatchTimesView = (ListView) findViewById(R.id.stopwatch_times_list);
		dbHandler = new DBHandler(this, null, null, 1);
		stopwatchTimesIDMap = new HashMap<>();
		selectedCategory = dbHandler.getCategory(getIntent().getIntExtra("stopwatchCategoryID", 0));

		loadStopwatchTimesList();
		registerForContextMenu(stopwatchTimesView);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
	{
		// Create delete menu item if user selected from stopwatch times list
		if (view.getId() == R.id.stopwatch_times_list)
		{
			menu.add(getResources().getString(R.string.delete));
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		if (item.getTitle().toString().equals(getResources().getString(R.string.delete)))
		{
			// Deletes time and updates the stopwatch times list
			dbHandler.deleteStopwatchTime(stopwatchTimesIDMap.get(info.position));
			loadStopwatchTimesList();
		}

		return super.onContextItemSelected(item);
	}

	private void loadStopwatchTimesList()
	{
		/*
	        Puts all stopwatch times in a list: stopwatchTimesStrings
            Also updates the stopwatchTimesIDMap
        */
		List<StopwatchTime> stopwatchTimeList = dbHandler.allStopwatchTimes(selectedCategory);
		stopwatchTimesStrings = new ArrayList<>();

		for (int i = 0; i < stopwatchTimeList.size(); i++)
		{
			stopwatchTimesStrings.add(i, stopwatchTimeList.get(i).getTimeAsString());
			stopwatchTimesIDMap.put(i, stopwatchTimeList.get(i).getID());
		}

		// Displays the profileStrings array in the profiles list
		stopwatchTimesView.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, stopwatchTimesStrings));
	}
}
