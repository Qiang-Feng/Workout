package com.qiang.workout.Utilities;

import android.app.DialogFragment;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.qiang.workout.Fragments.TimeSelectFragment;

public class TextTimeClickListener implements View.OnClickListener
{
	private boolean isFragment = false;
	private boolean isMinutes;
	private Fragment targetFragment;
	private AppCompatActivity targetActivity;

	public TextTimeClickListener(boolean isMinutes, Fragment targetFragment)
	{
		this.isMinutes = isMinutes;
		this.targetFragment = targetFragment;
		this.isFragment = true;
	}

	public TextTimeClickListener(boolean isMinutes, AppCompatActivity targetActivity)
	{
		this.isMinutes = isMinutes;
		this.targetActivity = targetActivity;
	}

	@Override
	public void onClick(View v)
	{
		DialogFragment dialog = TimeSelectFragment.newInstance(isMinutes);

		if (isFragment)
		{
			dialog.setTargetFragment(targetFragment, 0);
			dialog.show(targetFragment.getFragmentManager(), "TimeSelect");
		}
		else
		{
			dialog.show(targetActivity.getFragmentManager(), "TimeSelect");
		}
	}
}