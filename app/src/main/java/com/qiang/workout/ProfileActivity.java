package com.qiang.workout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.qiang.workout.Interfaces.TimeSelectFragmentListener;
import com.qiang.workout.Models.Profile;
import com.qiang.workout.Utilities.DBHandler;
import com.qiang.workout.Utilities.TextTimeClickListener;

public class ProfileActivity extends AppCompatActivity implements TimeSelectFragmentListener
{
	// Values for the actions add and edit
	public static final int PROFILE_ADDED = 1;
	public static final int PROFILE_EDITED = 2;

	// Input area components
	private EditText name;
	private EditText repeatNumber;
	private TextView textTimeMinutes;
	private TextView textTimeSeconds;
	private TextView timeError;
	private CheckBox repeat;

	// Database
	private DBHandler dbHandler;

	// Profile management
	private Profile profile;
	private boolean isEditingProfile = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Sets the activity_profile as this activity's display
		setContentView(R.layout.activity_profile);

		// Initialising components
		// Input area components
		name = (EditText) findViewById(R.id.profile_text_name);
		repeatNumber = (EditText) findViewById(R.id.profile_text_repeat);
		textTimeMinutes = (TextView) findViewById(R.id.profile_text_time_minutes);
		textTimeSeconds = (TextView) findViewById(R.id.profile_text_time_seconds);
		timeError = (TextView) findViewById(R.id.profile_text_time_error);
		repeat = (CheckBox) findViewById(R.id.profile_checkbox_repeat);

		// Database
		dbHandler = new DBHandler(this, null, null, 1);

		// Handles click events for textTimeMinutes and textTimeSeconds
		textTimeMinutes.setOnClickListener(new TextTimeClickListener(true, this));
		textTimeSeconds.setOnClickListener(new TextTimeClickListener(false, this));

		if (getIntent().hasExtra("profileID"))
		{
			isEditingProfile = true;

			// Changes activity's title to @string/title_edit_profile
			setTitle(R.string.title_edit_profile);

			// Creates new chosen profile object
			profile = dbHandler.getProfile(getIntent().getIntExtra("profileID", 1));

			// Sets input box values for profile loaded from database
			name.setText(profile.getName());
			setTextTime(textTimeMinutes, profile.getMinutes());
			setTextTime(textTimeSeconds, profile.getSeconds());
			repeat.setChecked(profile.isRepeat());
			repeatNumber.setText(Integer.toString(profile.getRepeatNumber()));
		}
		else
		{
			// Changes activity's title to @string/title_new_profile
			setTitle(R.string.title_new_profile);
		}
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handles item selection in action bar
		if (item.getItemId() == R.id.action_save_profile)
		{
	        /*
                Performs validation of input data
            */
			boolean isError = false;

			if (name.getText().length() == 0)
			{
				isError = true;
				name.setError("Name is required");
			}

			if (name.getText().toString().trim().length() > 50)
			{
				isError = true;
				name.setError("Name can't be longer than 50 characters");
			}

			if (textTimeMinutes.getText().equals("00") && textTimeSeconds.getText().equals("00"))
			{
				isError = true;
				timeError.setError("Time is required");
			}

			if (repeat.isChecked() && repeatNumber.getText().length() == 0)
			{
				isError = true;
				repeatNumber.setError("Number of laps is required");
			}

            /*
                Produces an error if:
                - name of profile is taken and adding a new profile
                - changed profile name is taken
            */
			if (dbHandler.profileExists(name.getText().toString().trim()))
			{
				if (!isEditingProfile || !name.getText().toString().trim().equals(profile.getName()))
				{
					isError = true;
					name.setError("Name already exists");
				}
			}


			if (!isError)
			{
				// Either adds or edits the profile depending on what's needed
				if (isEditingProfile)
				{
					setResult(PROFILE_EDITED);
					updateProfile();
				}
				else
				{
					setResult(PROFILE_ADDED);
					addProfile();
				}

				finish();
			}
		}

		return super.onOptionsItemSelected(item);
	}

	private void addProfile()
	{
		// Creates a profile object with user's settings (to be inserted to the database)
		Profile newProfile = new Profile(name.getText().toString().trim());
		newProfile.setMinutes(Integer.parseInt(textTimeMinutes.getText().toString()));
		newProfile.setSeconds(Integer.parseInt(textTimeSeconds.getText().toString()));
		newProfile.setRepeat(repeat.isChecked());

		// Only set user's repeatNumber if repeat is checked
		newProfile.setRepeatNumber((repeat.isChecked()) ? Integer.parseInt(repeatNumber.getText().toString()) : 0);

		dbHandler.addProfile(newProfile);
	}

	private void updateProfile()
	{
		// Updates profile object with user's settings
		profile.setName(name.getText().toString().trim());
		profile.setMinutes(Integer.parseInt(textTimeMinutes.getText().toString()));
		profile.setSeconds(Integer.parseInt(textTimeSeconds.getText().toString()));
		profile.setRepeat(repeat.isChecked());

		// Only set user's repeatNumber if repeat is checked
		profile.setRepeatNumber((repeat.isChecked()) ? Integer.parseInt(repeatNumber.getText().toString()) : 0);

		dbHandler.saveEditedProfile(profile);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_profile, menu);
		return true;
	}

	@Override
	public void onFinishTimeSelectFragment(boolean isMinutes, int timeChosen)
	{
		// Removes time error if user has selected a non-zero value
		if (timeChosen != 0)
		{
			timeError.setError(null);
		}

		// Determines whether minutes or seconds should be updated
		if (isMinutes)
		{
			setTextTime(textTimeMinutes, timeChosen);
		}
		else
		{
			setTextTime(textTimeSeconds, timeChosen);
		}
	}
}