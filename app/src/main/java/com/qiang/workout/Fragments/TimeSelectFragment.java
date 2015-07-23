package com.qiang.workout.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.NumberPicker;

import com.qiang.workout.Interfaces.TimeSelectFragmentListener;
import com.qiang.workout.R;

public class TimeSelectFragment extends DialogFragment
{
	private static boolean isMinutes = false;

	public static TimeSelectFragment newInstance(boolean isMinutes)
	{
		TimeSelectFragment.isMinutes = isMinutes;

		return new TimeSelectFragment();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());   // Initialise builder variable as a new AlertDialog object
		final NumberPicker picker = new NumberPicker(getActivity());            // Initialises picker variable

		// Sets appropriate maximum values for seconds and minutes
		if (isMinutes)
		{
			picker.setMaxValue(99);
		}
		else
		{
			picker.setMaxValue(59);
		}

		// Sets the time picker as the display ("view") for the dialog
		builder.setView(picker);

		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int id)
			{
				// Returns either minutes or seconds (depending on what the user initially chose to change)
				if (isMinutes)
				{
					returnResult(picker.getValue());
				}
				else
				{
					returnResult(picker.getValue());
				}
			}
		});

		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int id)
			{
				// User clicked Cancel button
			}
		});

		return builder.create();
	}

	private void returnResult(int timeChosen)
	{
		TimeSelectFragmentListener returnObject;

		// Gets calling object to return time value to
		if (getTargetFragment() == null)
		{
			returnObject = (TimeSelectFragmentListener) getActivity();
		}
		else
		{
			returnObject = (TimeSelectFragmentListener) getTargetFragment();
		}

		// Returns time selected by user to the calling object
		returnObject.onFinishTimeSelectFragment(isMinutes, timeChosen);
	}
}