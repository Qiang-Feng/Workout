package com.qiang.workout.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qiang.workout.NewProfileActivity;
import com.qiang.workout.R;
import com.qiang.workout.Utilities.CountDown;
import com.qiang.workout.Utilities.DBHandler;

public class TimerFragment extends Fragment
{
	private ProgressBar progressBar;

	private Button buttonStart;
	private Button buttonPause;
	private Button buttonStop;
	private Button buttonResume;

	private TextView textTimeMinutes;
	private TextView textTimeSeconds;

	private CountDown countDownTimer;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Inflates the view
		final View view = inflater.inflate(R.layout.fragment_timer, container, false);

		// Initialising variables
		progressBar = (ProgressBar) view.findViewById(R.id.progress_circular);

		buttonStart = (Button) view.findViewById(R.id.button_start);
		buttonPause = (Button) view.findViewById(R.id.button_pause);
		buttonStop = (Button) view.findViewById(R.id.button_stop);
		buttonResume = (Button) view.findViewById(R.id.button_resume);

		textTimeMinutes = (TextView) view.findViewById(R.id.text_time_minutes);
		textTimeSeconds = (TextView) view.findViewById(R.id.text_time_seconds);

		// Handles pause button clicks
		buttonPause.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Hide pause button; Show resume button
				buttonPause.setVisibility(View.INVISIBLE);
				buttonResume.setVisibility(View.VISIBLE);

				countDownTimer.pause();
			}
		});

		// Handles resume button clicks
		buttonResume.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Hide resume button; Show pause button
				buttonResume.setVisibility(View.INVISIBLE);
				buttonPause.setVisibility(View.VISIBLE);

				countDownTimer.resume();
			}
		});

		// Handles stop button clicks
		buttonStop.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				countDownTimer.stop();

				// Resets time shown and the progress bar
				setTextTime(textTimeMinutes, 0);
				setTextTime(textTimeSeconds, 0);
				progressBar.setProgress(0);

				// Hide pause and stop button; Show start button
				view.findViewById(R.id.pause_stop_container).setVisibility(View.INVISIBLE);
				buttonStart.setVisibility(View.VISIBLE);
			}
		});

		// Sets the onClickListener for the start button
		buttonStart.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Don't start timer if time selected is 00:00
				if (textTimeMinutes.getText().equals("00") && textTimeSeconds.getText().equals("00"))
				{
					return;
				}

				// Hide start button; Show pause and stop button
				buttonStart.setVisibility(View.INVISIBLE);
				view.findViewById(R.id.pause_stop_container).setVisibility(View.VISIBLE);

				startTimer();
			}
		});

		// If the user has no profiles...
		SQLiteDatabase db = new DBHandler(getActivity(), null, null, 1).getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT '1' FROM " + DBHandler.TABLE_PROFILES, null);
		cursor.moveToFirst();

		if (cursor.getCount() == 0)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Looks like you don't have any profiles!");

			builder.setPositiveButton(R.string.new_profile, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					Intent intent = new Intent(getActivity(), NewProfileActivity.class);
					startActivity(intent);
				}
			});

			builder.create().show();
		}

		cursor.close();

		return view;
	}

	private void setTextTime(TextView textTime, int timeChosen)
	{
		// Formats the time text (appends 0 if necessary)
		if (timeChosen < 10)
		{
			textTime.setText("0" + Integer.toString(timeChosen));
		} else
		{
			textTime.setText(Integer.toString(timeChosen));
		}
	}

	private void startTimer()
	{
		long millisTemp = 0;

		millisTemp += Integer.parseInt((String) textTimeMinutes.getText()) * 60 * 1000;
		millisTemp += Integer.parseInt((String) textTimeSeconds.getText()) * 1000;

		final long millis = millisTemp;

		countDownTimer = new CountDown(millis, 1)
		{
			@Override
			public void onTick(long millisUntilFinished)
			{
				progressBar.setProgress((int) Math.round(((millis - millisUntilFinished) / (double) millis) * 10000));

				int minutesLeft = (int) millisUntilFinished / (60 * 1000);
				int secondsLeft = (int) ((millisUntilFinished / 1000) + 0.5) % 60;

				// Appends 0 in front of minutes if less than 10 before updating the minutes text on screen
				if (minutesLeft < 10)
				{
					textTimeMinutes.setText("0" + Long.toString(minutesLeft));
				} else
				{
					textTimeMinutes.setText(Long.toString(minutesLeft));
				}

				// Appends 0 in front of seconds if less than 10 before updating the seconds text on screen
				if (secondsLeft < 10)
				{
					textTimeSeconds.setText("0" + Long.toString(secondsLeft));
				} else
				{
					textTimeSeconds.setText(Long.toString(secondsLeft));
				}
			}

			@Override
			public void onFinish()
			{
				// Resets time shown and the progress bar
				textTimeSeconds.setText("00");
				progressBar.setProgress(0);
			}
		};

		countDownTimer.start();
	}
}