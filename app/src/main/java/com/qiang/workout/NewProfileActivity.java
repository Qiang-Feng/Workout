package com.qiang.workout;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class NewProfileActivity extends AppCompatActivity implements TimeSelectFragmentListener
{
	public static final int PROFILE_ADDED = 1;

	private EditText name;
	private EditText repeatNumber;

	private TextView textTimeMinutes;
	private TextView textTimeSeconds;
	private TextView timeError;

	private CheckBox repeat;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_profile);

		// Initialising variables
		name = (EditText) findViewById(R.id.new_profile_text_name);
		repeatNumber = (EditText) findViewById(R.id.new_profile_text_repeat);

		textTimeMinutes = (TextView) findViewById(R.id.new_profile_text_time_minutes);
		textTimeSeconds = (TextView) findViewById(R.id.new_profile_text_time_seconds);
		timeError = (TextView) findViewById(R.id.new_profile_text_time_error);

		repeat = (CheckBox) findViewById(R.id.new_profile_checkbox_repeat);

		// Sets onClick listeners for minutes and seconds TextViews
		textTimeMinutes.setOnClickListener(new TextTimeClickListener(true, this));
		textTimeSeconds.setOnClickListener(new TextTimeClickListener(false, this));
	}

	private void setTextTime(TextView textTime, int timeChosen)
	{
		if (timeChosen < 10)
		{
			textTime.setText("0" + Integer.toString(timeChosen));
		} else
		{
			textTime.setText(Integer.toString(timeChosen));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_new_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handles item selection in action bar
		if (item.getItemId() == R.id.action_save_profile)
		{
			boolean isError = false;

			if (name.getText().length() == 0)
			{
				isError = true;
				name.setError("Name is required");
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

			if (!isError)
			{
				addProfile();
				setResult(PROFILE_ADDED);
				finish();
			}
		} else if (item.getItemId() == android.R.id.home)
		{
			displayCancelDialog();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed()
	{
		displayCancelDialog();
	}

	private void addProfile()
	{
		Profile profile = new Profile(name.getText().toString());
		profile.setMinutes(Integer.parseInt(textTimeMinutes.getText().toString()));
		profile.setSeconds(Integer.parseInt(textTimeSeconds.getText().toString()));
		profile.setRepeat(repeat.isChecked());
		profile.setRepeatNumber((repeat.isChecked()) ? Integer.parseInt(repeatNumber.getText().toString()) : 0);    // Only set user's repeatNumber if repeat is checked

		DBHandler dbHandler = new DBHandler(this, null, null, 1);
		dbHandler.addProfile(profile);
	}

	private void displayCancelDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Cancel");
		builder.setMessage("Are you sure you want to discard this profile?");

		builder.setPositiveButton(R.string.keep_editing, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{

			}
		});

		builder.setNegativeButton(R.string.discard, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// Closes this activity and directs users back to previous activity
				finish();
			}
		});

		builder.create().show();
	}

	@Override
	public void onFinishTimeSelectFragment(boolean isMinutes, int timeChosen)
	{
		if (timeChosen != 0)
		{
			timeError.setError(null);
		}

		if (isMinutes)
		{
			setTextTime(textTimeMinutes, timeChosen);
		} else
		{
			setTextTime(textTimeSeconds, timeChosen);
		}
	}
}