package com.qiang.workout.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

import com.qiang.workout.R;

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

	private CardView categorySpinnerCard;

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

		categorySpinnerCard = (CardView) view.findViewById(R.id.category_spinner_card);

		resumeStopContainer = view.findViewById(R.id.resume_stop_container);
		saveResetContainer = view.findViewById(R.id.save_reset_container);

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
				timeWhenPaused = stopwatch.getBase() - SystemClock.elapsedRealtime();
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
				stopwatch.setBase(SystemClock.elapsedRealtime() + timeWhenPaused);
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

		return view;
	}
}
