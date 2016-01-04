package com.qiang.workout.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Spinner;
import android.widget.TextView;

import com.qiang.workout.Models.Category;
import com.qiang.workout.Models.StopwatchTime;
import com.qiang.workout.R;
import com.qiang.workout.Utilities.DBHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StopwatchFragment extends Fragment
{
	private Chronometer stopwatch;

	private Button buttonStart;
	private Button buttonPause;
	private Button buttonResume;
	private Button buttonStop;
	private Button buttonSave;
	private Button buttonReset;

	private View resumeStopContainer;
	private View saveResetContainer;

	private Spinner spinner;
	private CardView categorySpinnerCard;
	private Map<Integer, Integer> categoryIDMap;
	private Category selectedCategory;
	private TextView textCategorySpinner;

	private DBHandler dbHandler;

	private long timeWhenPaused;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Inflates the view
		View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);

		// Initialise variables
		stopwatch = (Chronometer) view.findViewById(R.id.stopwatch);

		buttonStart = (Button) view.findViewById(R.id.button_start);
		buttonPause = (Button) view.findViewById(R.id.button_pause);
		buttonResume = (Button) view.findViewById(R.id.button_resume);
		buttonStop = (Button) view.findViewById(R.id.button_stop);
		buttonSave = (Button) view.findViewById(R.id.button_save);
		buttonReset = (Button) view.findViewById(R.id.button_reset);

		textCategorySpinner = (TextView) view.findViewById(R.id.category_spinner_text);
		spinner = (Spinner) view.findViewById(R.id.category_spinner);
		categorySpinnerCard = (CardView) view.findViewById(R.id.category_spinner_card);

		resumeStopContainer = view.findViewById(R.id.resume_stop_container);
		saveResetContainer = view.findViewById(R.id.save_reset_container);

		categoryIDMap = new HashMap<>();

		dbHandler = new DBHandler(getActivity(), null, null, 1);

		// Handles start button clicks
		buttonStart.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Sets time to 00:00 and starts the stopwatch
				stopwatch.setBase(SystemClock.elapsedRealtime());
				stopwatch.start();

				// Hides start button; show pause button
				buttonStart.setVisibility(View.INVISIBLE);
				buttonPause.setVisibility(View.VISIBLE);
			}
		});

		// Handles pause button clicks
		buttonPause.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Makes note of stopwatch time when paused
				timeWhenPaused = SystemClock.elapsedRealtime() - stopwatch.getBase();
				stopwatch.stop();

				// Hides pause button; show resume and stop buttons
				buttonPause.setVisibility(View.INVISIBLE);
				resumeStopContainer.setVisibility(View.VISIBLE);
			}
		});

		// Handles resume button clicks
		buttonResume.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Starts stopwatch at paused time
				stopwatch.setBase(SystemClock.elapsedRealtime() - timeWhenPaused);
				stopwatch.start();

				// Hide resume and stop buttons; show pause button
				resumeStopContainer.setVisibility(View.INVISIBLE);
				buttonPause.setVisibility(View.VISIBLE);
			}
		});

		// Handles stop button clicks
		buttonStop.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Hide resume and stop buttons; show save and reset buttons
				resumeStopContainer.setVisibility(View.INVISIBLE);
				saveResetContainer.setVisibility(View.VISIBLE);

				// Show category selection spinner card
				categorySpinnerCard.setVisibility(View.VISIBLE);
			}
		});

		// Handles save button clicks
		buttonSave.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Saves the stopwatch time to database
				StopwatchTime stopwatchTime = new StopwatchTime();
				stopwatchTime.setTime((int) timeWhenPaused / 1000);
				stopwatchTime.setCategory(selectedCategory.getID());
				stopwatchTime.setRecordDate((int) System.currentTimeMillis() / 1000);

				dbHandler.addStopwatchTime(stopwatchTime);

				// Resets the stopwatch
				buttonReset.performClick();
			}
		});

		// Handles reset button clicks
		buttonReset.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Reset stopwatch time
				stopwatch.setBase(SystemClock.elapsedRealtime());

				// Hide save and reset buttons; show start button
				saveResetContainer.setVisibility(View.INVISIBLE);
				buttonStart.setVisibility(View.VISIBLE);

				// Hide category selection spinner card
				categorySpinnerCard.setVisibility(View.INVISIBLE);
			}
		});

		// Sets the onClickListener for the category selection card
		categorySpinnerCard.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				spinner.performClick();
			}
		});

		// Handles category selection
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				// Create selected category
				selectedCategory = dbHandler.getCategory(categoryIDMap.get(position));

				// Set category card text to category name
				textCategorySpinner.setText(selectedCategory.getName());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});

		// Load the categories in the spinner if they exist
		if (dbHandler.getCategoriesCount() != 0)
		{
			loadCategorySpinnerItems();
		}

		return view;
	}

	private void loadCategorySpinnerItems()
	{
		List<Category> categoryList = dbHandler.allCategories();
		List<String> categoryString = new ArrayList<>();

        /*
            Add each category name to a list (to show in spinner)
            Also updates the categoryIDMap
        */
		for (int i = 0; i < categoryList.size(); i++)
		{
			categoryString.add(i, categoryList.get(i).getName());
			categoryIDMap.put(i, categoryList.get(i).getID());
		}

		spinner.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_dropdown_item, categoryString));
	}
}
