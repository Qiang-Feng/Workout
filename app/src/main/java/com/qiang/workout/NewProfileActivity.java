package com.qiang.workout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.qiang.workout.Interfaces.TimeSelectFragmentListener;
import com.qiang.workout.Utilities.TextTimeClickListener;

public class NewProfileActivity extends AppCompatActivity implements TimeSelectFragmentListener
{
	private TextView textTimeMinutes;
	private TextView textTimeSeconds;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_profile);

		textTimeMinutes = (TextView) findViewById(R.id.new_profile_text_time_minutes);  // Initialises textTimeMinutes
		textTimeSeconds = (TextView) findViewById(R.id.new_profile_text_time_seconds);  // Initialises textTimeSeconds

		textTimeMinutes.setOnClickListener(new TextTimeClickListener(true, this));      // Sets the onClickListener for textTimeMinutes
		textTimeSeconds.setOnClickListener(new TextTimeClickListener(false, this));     // Sets the onClickListener for textTimeSeconds
	}

	private void setTextTime(TextView textTime, int timeChosen)
	{
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
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_new_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onFinishTimeSelectFragment(boolean isMinutes, int timeChosen)
	{
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