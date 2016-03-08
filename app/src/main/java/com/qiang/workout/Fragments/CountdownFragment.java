package com.qiang.workout.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.qiang.workout.Models.Profile;
import com.qiang.workout.ProfileActivity;
import com.qiang.workout.R;
import com.qiang.workout.Utilities.CountDown;
import com.qiang.workout.Utilities.DBHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountdownFragment extends Fragment
{
	// Countdown timer buttons
	private Button buttonStart;
	private Button buttonPause;
	private Button buttonStop;
	private Button buttonResume;
	private View pauseStopContainer;

	// Countdown timer
	private TextView textTimeMinutes;
	private TextView textTimeSeconds;
	private TextView textRoundNumber;
	private ProgressBar progressBar;
	private CountDown countDownTimer;
	private boolean timerStarted = false;
	private int roundNumber = 1;

	// Profile selection
	private TextView textProfileSpinner;
	private CardView profileSpinnerCard;
	private Spinner spinner;
	private Profile selectedProfile;
	private Map<Integer, Integer> profileIDMap;

	// Database
	private DBHandler dbHandler;

	// Beep sound components
	private ToneGenerator toneGenerator;
	public static final int INTERVAL_BEEP = ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD;
	public static final int FINISHED_BEEP = ToneGenerator.TONE_CDMA_CALLDROP_LITE;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Inflates the view
		final View view = inflater.inflate(R.layout.fragment_countdown, container, false);

		// Initialising components
		// Countdown timer buttons
		buttonStart = (Button) view.findViewById(R.id.button_start);
		buttonPause = (Button) view.findViewById(R.id.button_pause);
		buttonStop = (Button) view.findViewById(R.id.button_stop);
		buttonResume = (Button) view.findViewById(R.id.button_resume);
		pauseStopContainer = view.findViewById(R.id.pause_stop_container);

		// Countdown timer
		progressBar = (ProgressBar) view.findViewById(R.id.progress_circular);
		textTimeMinutes = (TextView) view.findViewById(R.id.text_time_minutes);
		textTimeSeconds = (TextView) view.findViewById(R.id.text_time_seconds);
		textRoundNumber = (TextView) view.findViewById(R.id.text_round_number);

		// Profile selection
		spinner = (Spinner) view.findViewById(R.id.profile_spinner);
		profileSpinnerCard = (CardView) view.findViewById(R.id.profile_spinner_card);
		textProfileSpinner = (TextView) view.findViewById(R.id.profile_spinner_text);
		profileIDMap = new HashMap<>();

		// Database
		dbHandler = new DBHandler(getActivity(), null, null, 1);

		// Beep sound generator
		toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

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
				timerStarted = false;

				// Resets time shown and the progress bar
				setTextTime(textTimeMinutes, selectedProfile.getMinutes());
				setTextTime(textTimeSeconds, selectedProfile.getSeconds());
				progressBar.setProgress(0);

				// Reset round number
				roundNumber = 1;
				setRoundNumber(roundNumber, selectedProfile.getRepeatNumber());

				// Hide resume button and show pause button
				buttonResume.setVisibility(View.INVISIBLE);
				buttonPause.setVisibility(View.VISIBLE);

				// Hide pause and stop button; Show start button
				pauseStopContainer.setVisibility(View.INVISIBLE);
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
				pauseStopContainer.setVisibility(View.VISIBLE);

				startTimer();
			}
		});

		// Sets the onClickListener for the profile selection card
		profileSpinnerCard.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Shows profile selection dropdown only if timer has not been started
				if (!timerStarted)
				{
					spinner.performClick();
				}
			}
		});

		// Handles profile selection
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				// Create selected profile
				selectedProfile = dbHandler.getProfile(profileIDMap.get(position));

				// Set profile card text to profile name
				textProfileSpinner.setText(selectedProfile.getName());

				// Set round number text; If not repeated, set text to "Lap 1 of 1"
				if (selectedProfile.isRepeat())
				{
					setRoundNumber(1, selectedProfile.getRepeatNumber());
				}
				else
				{
					setRoundNumber(1, 1);
				}

				setTextTime(textTimeMinutes, selectedProfile.getMinutes());
				setTextTime(textTimeSeconds, selectedProfile.getSeconds());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});

		// If the user has no profiles
		if (dbHandler.getProfilesCount() == 0)
		{
			textProfileSpinner.setText("No profiles exist");

			// Alerts user to create a new profile
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Looks like you don't have any profiles!");

			// Sets up the "New Profile" button
			builder.setPositiveButton(R.string.new_profile, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					// Launches new profile activity
					Intent intent = new Intent(getActivity(), ProfileActivity.class);
					startActivityForResult(intent, 1);
				}
			});

			builder.create().show();
		}
		else
		{
			// Load all countdown profiles into the spinner
			loadProfileSpinnerItems();
		}

		return view;
	}

	private void loadProfileSpinnerItems()
	{
		List<Profile> profileList = dbHandler.allProfiles();
		List<String> profilesString = new ArrayList<>();

        /*
            Add each profile name to a list (to show in spinner)
            Also updates the profileIDMap
        */
		for (int i = 0; i < profileList.size(); i++)
		{
			profilesString.add(i, profileList.get(i).getName());
			profileIDMap.put(i, profileList.get(i).getID());
		}

		// Adds the countdown profiles list to the spinner
		spinner.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_dropdown_item, profilesString));
	}

	private void setTextTime(TextView textTime, int timeChosen)
	{
		// Appends 0 in front of timeChosen if less than 10 before setting textTime
		if (timeChosen < 10)
		{
			textTime.setText("0" + Integer.toString(timeChosen));
		}
		else
		{
			textTime.setText(Integer.toString(timeChosen));
		}
	}

	private void startTimer()
	{
		long millisTemp = 0;
		timerStarted = true;

		// Gets the countdown starting time in milliseconds
		millisTemp += Integer.parseInt((String) textTimeMinutes.getText()) * 60 * 1000;
		millisTemp += Integer.parseInt((String) textTimeSeconds.getText()) * 1000;

		// Allows inner classes to access the countdown starting time
		final long millis = millisTemp;

		countDownTimer = new CountDown(millis, 10)
		{
			@Override
			public void onTick(long millisUntilFinished)
			{
				// Updates the progress bar percentage
				progressBar.setProgress((int) Math.round(((millis - millisUntilFinished) / (double) millis) * 10000));

                /*
                    Calculates (pseudo) minutes and seconds left:
                    As milliseconds aren't displayed, the user may see:
                        00:10 change to 00:09 immediately as soon as they click the start button
                        00:00 for 1 second when the timer finishes
                */
				int pseudoMinutesLeft = (int) millisUntilFinished / (60 * 1000);
				int pseudoSecondsLeft = (int) (((millisUntilFinished / 1000) + 0.5) % 60) + 1;
				if (pseudoSecondsLeft == 60)
				{
					pseudoMinutesLeft += 1;
					pseudoSecondsLeft = 0;
				}

				// Updates displayed time on screen
				setTextTime(textTimeMinutes, pseudoMinutesLeft);
				setTextTime(textTimeSeconds, pseudoSecondsLeft);
			}

			@Override
			public void onFinish()
			{
				// Resets displayed time and progress bar
				setTextTime(textTimeMinutes, selectedProfile.getMinutes());
				setTextTime(textTimeSeconds, selectedProfile.getSeconds());
				progressBar.setProgress(0);

				// Hide pause and stop button; Show start button
				pauseStopContainer.setVisibility(View.INVISIBLE);
				buttonStart.setVisibility(View.VISIBLE);

				// If there are still more rounds to go
				if (roundNumber < selectedProfile.getRepeatNumber())
				{
					// Updates round number and display
					roundNumber += 1;
					setRoundNumber(roundNumber, selectedProfile.getRepeatNumber());

					// Plays interval beep sound
					toneGenerator.startTone(INTERVAL_BEEP, 125);

					// Start next round
					buttonStart.performClick();
				}
				else
				{
					timerStarted = false;

					// Plays finished beep sound
					toneGenerator.startTone(FINISHED_BEEP, 125);

					// Reset round number
					roundNumber = 1;

					if (selectedProfile.isRepeat())
					{
						setRoundNumber(roundNumber, selectedProfile.getRepeatNumber());
					}
					else
					{
						setRoundNumber(roundNumber, 1);
					}
				}
			}
		};

		countDownTimer.start();
	}

	private void setRoundNumber(int roundNumber, int ofNumber)
	{
		textRoundNumber.setText("Round " + roundNumber+ " of " + ofNumber);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// Only update spinner items if profile has been added
		if (resultCode == ProfileActivity.PROFILE_ADDED)
		{
			loadProfileSpinnerItems();

			// Only reset profile selected text if user has only 1 profile
			if (dbHandler.getProfilesCount() == 1)
			{
				textProfileSpinner.setText(getString(R.string.profile_spinner_default_text));
			}
		}
	}
}