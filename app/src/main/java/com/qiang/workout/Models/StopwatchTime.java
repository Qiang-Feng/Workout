package com.qiang.workout.Models;

public class StopwatchTime
{
	private int id;
	private int recordDate;
	private int time;

	public int getID()
	{
		return id;
	}

	public void setID(int id)
	{
		this.id = id;
	}

	public int getRecordDate()
	{
		return recordDate;
	}

	public void setRecordDate(int recordDate)
	{
		this.recordDate = recordDate;
	}

	public int getTime()
	{
		return time;
	}

	public void setTime(int time)
	{
		this.time = time;
	}

	public String getTimeAsString()
	{
		int minutes = time / 60;
		int seconds = time % 60;

		String timeString = "";

		// Prepends leading 0 to minutes if needed
		if (minutes < 10)
		{
			timeString += "0";
		}
		timeString += minutes;

		timeString += ":";

		// Prepends leading 0 to seconds if needed
		if (seconds < 10)
		{
			timeString += "0";
		}
		timeString += seconds;

		return timeString;
	}
}